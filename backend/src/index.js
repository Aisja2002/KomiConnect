import { Hono } from "hono";
import { hashPassword, verifyPassword } from "./lib/pbkdf2";
import * as jose from "jose";
import { bearerAuth } from 'hono/bearer-auth'
import imageSize from "image-size";

const app = new Hono();

const auth = bearerAuth({
  verifyToken: async (token, c) => {
    try {
      const secret = new TextEncoder().encode(c.env.JWT_SECRET);
      const { payload } = await jose.jwtVerify(token, secret);
      c.set("user", payload);
      return true;
    } catch (err) {
      return false;
    }
  }
});

app.post("/register", async (c) => {
  const args = await c.req.json();
  if (args["secret"] != c.env.REGISTRATION_SECRET_TOKEN) {
    return c.text("Invalid secret", 401);
  }
  const username = args["username"];
  const password = args["password"];
  if (!username || !password) {
    return c.text("Invalid username or password", 400);
  }
  const hashed_password = await hashPassword(password);
  const db = c.env.DB;
  const result = await db.prepare("INSERT INTO user(username, password) VALUES(?, ?) RETURNING id").bind(username, hashed_password).run();
  if (!result.success) {
    return c.text("Cannot create user", 500);
  }
  return c.text("User created successfully");
});

app.post("/login", async (c) => {
  const args = await c.req.json();
  const username = args["username"];
  const password = args["password"];
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, username, password, admin, data FROM user WHERE username = ?").bind(username).run();
  if (!result.success || result.results.length !== 1) {
    return c.text("Invalid username", 404);
  }
  const user = result.results[0];
  const isPasswordCorrect = await verifyPassword(user.password, password);
  if (!isPasswordCorrect) {
    return c.text("Invalid password", 401);
  }
  const token = await new jose.SignJWT({ id: user.id, username: user.username, admin: user.admin, data: user.data })
    .setProtectedHeader({ alg: "HS256" })
    .setIssuedAt()
    .setExpirationTime("60d")
    .sign(new TextEncoder().encode(c.env.JWT_SECRET));
  return c.text(token);
});

// User routes

app.get("/me", auth, async (c) => {
  const user = c.get("user");
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, username, admin, data FROM user WHERE id = ?").bind(user.id).run();
  if (!result.success || result.results.length !== 1) {
    return c.text("User not found", 404);
  }
  const userData = result.results[0];
  return c.json({
    id: userData.id,
    username: userData.username,
    admin: userData.admin,
    data: JSON.parse(userData.data),
  });
});

app.get("/user", auth, async (c) => {
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, username, admin, data FROM user").run();
  if (!result.success) {
    return c.text("Cannot fetch users", 500);
  }
  return c.json(result.results.map((user) => ({
    id: user.id,
    username: user.username,
    admin: user.admin,
    data: JSON.parse(user.data),
  })));
});

app.get("/user/:id", auth, async (c) => {
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, username, admin, data FROM user WHERE id = ?").bind(id).run();
  if (!result.success || result.results.length !== 1) {
    return c.text("User not found", 404);
  }
  const user = result.results[0];
  return c.json({
    id: user.id,
    username: user.username,
    admin: user.admin,
    data: JSON.parse(user.data),
  });
});

app.put("/user", auth, async (c) => {
  const user = c.get("user");
  const args = await c.req.json();
  const db = c.env.DB;
  const data = JSON.stringify(args["data"] || {});
  const result = await db.prepare("UPDATE user SET data = json(?) WHERE id = ?").bind(data, user.id).run();
  if (!result.success) {
    return c.text("Cannot update user data", 500);
  }
  return c.text("User data updated successfully");
});

app.delete("/user", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const db = c.env.DB;
  const result = await db.prepare("DELETE FROM user WHERE id = ?").bind(user.id).run();
  if (!result.success) {
    return c.text("Cannot delete user", 500);
  }
  return c.text("User deleted successfully");
});

const STATS_QUERY = `
SELECT
    (SELECT COUNT(*) FROM post WHERE post.user = ?) AS total_posts,
    (SELECT COUNT(DISTINCT convention) FROM post WHERE post.user = ?) AS tagged_conventions,
    (SELECT COUNT(*) FROM post WHERE post.label = 'Eventi' AND post.user = ?) AS events_posts,
    IFNULL((SELECT MAX(cnt) FROM (SELECT COUNT(*) AS cnt FROM like LEFT JOIN post ON like.post = post.id WHERE post.user = ? GROUP BY post.id)), 0) AS most_likes
`;

app.get("/stats/user/:id", auth, async (c) => {
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare(STATS_QUERY).bind(id, id, id, id).run();
  if (!result.success || result.results.length !== 1) {
    return c.text("User not found", 404);
  }
  const row = result.results[0];
  return c.json(row);
});

app.post("/picture/user/:id", auth, async (c) => {
  const user = c.get("user");
  if (user.id != c.req.param("id") && !user.admin) {
    return c.text("Unauthorized", 403);
  }
  const path = `user/${user.id}`;
  return await uploadPicture(c, path);
});

app.delete("/picture/user/:id", auth, async (c) => {
  const user = c.get("user");
  if (user.id != c.req.param("id") && !user.admin) {
    return c.text("Unauthorized", 403);
  }
  const path = `user/${user.id}`;
  return await deletePicture(c, path);
});

app.get("/picture/user/:id", auth, async (c) => {
  const id = c.req.param("id");
  const path = `user/${id}`;
  return await getPicture(c, path);
});

// Convention routes

app.get("/convention", auth, async (c) => {
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, data FROM convention").run();
  if (!result.success) {
    return c.text("Cannot fetch conventions", 500);
  }
  return c.json(result.results.map((conv) => ({
    id: conv.id,
    data: JSON.parse(conv.data),
  })));
});

app.get("/convention/:id", auth, async (c) => {
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, data FROM convention WHERE id = ?").bind(id).run();
  if (!result.success || result.results.length !== 1) {
    return c.text("Convention not found", 404);
  }
  const conv = result.results[0];
  return c.json({
    id: conv.id,
    data: JSON.parse(conv.data),
  });
});

app.post("/convention", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const args = await c.req.json();
  const data = JSON.stringify(args["data"] || {});
  const db = c.env.DB;
  const result = await db.prepare("INSERT INTO convention(data) VALUES(json(?)) RETURNING id").bind(data).run();
  if (!result.success) {
    return c.text("Cannot create convention", 500);
  }
  return c.json({ id: result.results[0].id });
});

app.put("/convention/:id", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const id = c.req.param("id");
  const args = await c.req.json();
  const data = JSON.stringify(args["data"] || {});
  const db = c.env.DB;
  const result = await db.prepare("UPDATE convention SET data = json(?) WHERE id = ?").bind(data, id).run();
  if (!result.success) {
    return c.text("Cannot update convention", 500);
  }
  return c.text("Convention updated successfully");
});

app.delete("/convention/:id", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("DELETE FROM convention WHERE id = ?").bind(id).run();
  if (!result.success) {
    return c.text("Cannot delete convention", 500);
  }
  return c.text("Convention deleted successfully");
});

app.post("/picture/convention/:id", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const id = c.req.param("id");
  const path = `convention/${id}`;
  return await uploadPicture(c, path);
});

app.delete("/picture/convention/:id", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const id = c.req.param("id");
  const path = `convention/${id}`;
  return await deletePicture(c, path);
});

app.get("/picture/convention/:id", auth, async (c) => {
  const id = c.req.param("id");
  const path = `convention/${id}`;
  return await getPicture(c, path);
});

// Label routes

app.get("/label", auth, async (c) => {
  const db = c.env.DB;
  const result = await db.prepare("SELECT name FROM label").run();
  if (!result.success) {
    return c.text("Cannot fetch labels", 500);
  }
  return c.json(result.results.map((label) => label.name));
});

app.post("/label", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const args = await c.req.json();
  const name = args["name"];
  if (!name) {
    return c.text("Invalid label name", 400);
  }
  const db = c.env.DB;
  const result = await db.prepare("INSERT INTO label(name) VALUES(?)").bind(name).run();
  if (!result.success) {
    return c.text("Cannot create label", 500);
  }
  return c.text("Label created successfully");
});

app.delete("/label/:name", auth, async (c) => {
  const user = c.get("user");
  if (!user.admin) {
    return c.text("Unauthorized", 403);
  }
  const name = c.req.param("name");
  const db = c.env.DB;
  const result = await db.prepare("DELETE FROM label WHERE name = ?").bind(name).run();
  if (!result.success) {
    return c.text("Cannot delete label", 500);
  }
  return c.text("Label deleted successfully");
});

// Post routes

app.get("/post", auth, async (c) => {
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, user, convention, label, data FROM post").run();
  if (!result.success) {
    return c.text("Cannot fetch posts", 500);
  }
  return c.json(result.results.map((post) => ({
    id: post.id,
    user: post.user,
    convention: post.convention,
    label: post.label,
    data: JSON.parse(post.data),
  })));
});

app.get("/post/:id", auth, async (c) => {
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, user, convention, label, data FROM post WHERE id = ?").bind(id).run();
  if (!result.success || result.results.length !== 1) {
    return c.text("Post not found", 404);
  }
  const post = result.results[0];
  return c.json({
    id: post.id,
    user: post.user,
    convention: post.convention,
    label: post.label,
    data: JSON.parse(post.data),
  });
});

app.get("/post-user/:id", auth, async (c) => {
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("SELECT id, user, convention, label, data FROM post WHERE user = ?").bind(id).run();
  if (!result.success) {
    return c.text("Cannot fetch posts by user", 500);
  }
  return c.json(result.results.map((post) => ({
    id: post.id,
    user: post.user,
    convention: post.convention,
    label: post.label,
    data: JSON.parse(post.data),
  })));
});

app.post("/post", auth, async (c) => {
  const user = c.get("user");
  const args = await c.req.json();
  const convention = args["convention"];
  const label = args["label"];
  const data = JSON.stringify(args["data"] || {});
  if (!convention || !label) {
    return c.text("Invalid convention or label", 400);
  }
  const db = c.env.DB;
  const result = await db.prepare("INSERT INTO post(user, convention, label, data) VALUES(?, ?, ?, json(?)) RETURNING id").bind(user.id, convention, label, data).run();
  if (!result.success) {
    return c.text("Cannot create post", 500);
  }
  return c.json({ id: result.results[0].id });
});

app.delete("/post/:id", auth, async (c) => {
  const user = c.get("user");
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("DELETE FROM post WHERE id = ? AND (user = ? OR ?)").bind(id, user.id, user.admin).run();
  if (!result.success) {
    return c.text("Cannot delete post", 500);
  }
  if (result.changes === 0) {
    return c.text("Post not found or not owned by user", 404);
  }
  return c.text("Post deleted successfully");
});

app.post("/picture/post/:id", auth, async (c) => {
  const user = c.get("user");
  const id = c.req.param("id");
  const db = c.env.DB;
  const postResult = await db.prepare("SELECT user FROM post WHERE id = ?").bind(id).run();
  if (!postResult.success || postResult.results.length !== 1) {
    return c.text("Post not found", 404);
  }
  const post = postResult.results[0];
  if (post.user != user.id && !user.admin) {
    return c.text("Unauthorized", 403);
  }
  const path = `post/${id}`;
  return await uploadPicture(c, path);
});

app.delete("/picture/post/:id", auth, async (c) => {
  const user = c.get("user");
  const id = c.req.param("id");
  const db = c.env.DB;
  const postResult = await db.prepare("SELECT user FROM post WHERE id = ?").bind(id).run();
  if (!postResult.success || postResult.results.length !== 1) {
    return c.text("Post not found", 404);
  }
  const post = postResult.results[0];
  if (post.user != user.id && !user.admin) {
    return c.text("Unauthorized", 403);
  }
  const path = `post/${id}`;
  return await deletePicture(c, path);
});

app.get("/picture/post/:id", auth, async (c) => {
  const id = c.req.param("id");
  const path = `post/${id}`;
  return await getPicture(c, path);
});

// Like routes

app.post("/like/:id", auth, async (c) => {
  const user = c.get("user");
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("INSERT INTO like(post, user) VALUES(?, ?)").bind(id, user.id).run();
  if (!result.success) {
    return c.text("Cannot like post", 500);
  }
  return c.text("Post liked successfully");
});

app.delete("/like/:id", auth, async (c) => {
  const user = c.get("user");
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("DELETE FROM like WHERE post = ? AND user = ?").bind(id, user.id).run();
  if (!result.success) {
    return c.text("Cannot unlike post", 500);
  }
  if (result.changes === 0) {
    return c.text("Like not found", 404);
  }
  return c.text("Post unliked successfully");
});

app.get("/like/post/:id", auth, async (c) => {
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("SELECT user FROM like WHERE post = ?").bind(id).run();
  if (!result.success) {
    return c.text("Cannot fetch likes", 500);
  }
  return c.json(result.results.map((like) => like.user));
});

app.get("/like/user/:id", auth, async (c) => {
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("SELECT post FROM like WHERE user = ?").bind(id).run();
  if (!result.success) {
    return c.text("Cannot fetch likes", 500);
  }
  return c.json(result.results.map((like) => like.post));
});

// Favorite routes

app.post("/favorite/:id", auth, async (c) => {
  const user = c.get("user");
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("INSERT INTO favorite(post, user) VALUES(?, ?)").bind(id, user.id).run();
  if (!result.success) {
    return c.text("Cannot favorite post", 500);
  }
  return c.text("Post favorited successfully");
});

app.delete("/favorite/:id", auth, async (c) => {
  const user = c.get("user");
  const id = c.req.param("id");
  const db = c.env.DB;
  const result = await db.prepare("DELETE FROM favorite WHERE post = ? AND user = ?").bind(id, user.id).run();
  if (!result.success) {
    return c.text("Cannot unfavorite post", 500);
  }
  if (result.changes === 0) {
    return c.text("Favorite not found", 404);
  }
  return c.text("Post unfavorited successfully");
});

app.get("/favorite/user", auth, async (c) => {
  const user = c.get("user");
  const db = c.env.DB;
  const result = await db.prepare("SELECT post FROM favorite WHERE user = ?").bind(user.id).run();
  if (!result.success) {
    return c.text("Cannot fetch favorites", 500);
  }
  return c.json(result.results.map((fav) => fav.post));
});

app.get("/favorite/user/post", auth, async (c) => {
  const user = c.get("user");
  const db = c.env.DB;
  const result = await db.prepare("SELECT post.id AS id, post.user AS user, post.convention AS convention, post.label AS label, post.data AS data FROM favorite LEFT JOIN post ON favorite.post = post.id WHERE favorite.user = ?").bind(user.id).run();
  if (!result.success) {
    return c.text("Cannot fetch favorite posts", 500);
  }
  return c.json(result.results.map((post) => ({
    id: post.id,
    user: post.user,
    convention: post.convention,
    label: post.label,
    data: JSON.parse(post.data),
  })));
});

// Utility functions

async function uploadPicture(c, path) {
  const formData = await c.req.formData();
  const file = formData.get("file");
  if (!file || !(file instanceof File)) {
    return c.text("Invalid file", 400);
  }
  if (file.size > 5 * 1024 * 1024) { // 5MB limit
    return c.text("File too large", 413);
  }
  if (file.type !== "image/png" && file.type !== "image/jpeg") {
    return c.text("Invalid file type", 415);
  }
  const fileBuffer = await file.arrayBuffer();
  if (fileBuffer.byteLength === 0) {
    return c.text("File is empty", 400);
  }
  const dimensions = imageSize(new Uint8Array(fileBuffer));
  if (!dimensions || dimensions.width !== dimensions.height) {
    return c.text("Image must be square", 400);
  }
  const uploadResult = await c.env.STORAGE.put(path, fileBuffer, {
    httpMetadata: {
      contentType: file.type,
    },
  });
  if (!uploadResult) {
    return c.text("Failed to upload file", 500);
  }
  return c.text("File uploaded successfully");
}

async function getPicture(c, path) {
  const user = c.get("user");
  const storage = c.env.STORAGE;
  const file = await storage.get(path);
  if (!file) {
    return c.text("Picture not found", 404);
  }
  const headers = new Headers();
  file.writeHttpMetadata(headers);
  headers.set("etag", file.httpEtag);
  return new Response(file.body, {
    headers
  });
}

async function deletePicture(c, path) {
  const storage = c.env.STORAGE;
  const deleteResult = await storage.delete(path);
  if (!deleteResult) {
    return c.text("Failed to delete picture", 500);
  }
  return c.text("Picture deleted successfully");
}

export default app;