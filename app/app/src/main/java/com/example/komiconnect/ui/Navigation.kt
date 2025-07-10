package com.example.komiconnect.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.komiconnect.screens.convention.ConventionScreen
import com.example.komiconnect.screens.convention.ConventionViewModel
import com.example.komiconnect.screens.post.PostScreen
import com.example.komiconnect.screens.post.PostViewModel
import com.example.komiconnect.screens.add.AddPostScreen
import com.example.komiconnect.screens.add.AddViewModel
import com.example.komiconnect.screens.favorites.FavoritesScreen
import com.example.komiconnect.screens.favorites.FavoritesViewModel
import com.example.komiconnect.screens.settings.SettingsScreen
import com.example.komiconnect.screens.home.HomeScreen
import com.example.komiconnect.screens.home.HomeViewModel
import com.example.komiconnect.screens.login.LoginScreen
import com.example.komiconnect.screens.login.LoginViewModel
import com.example.komiconnect.screens.map.MapScreen
import com.example.komiconnect.screens.map.MapViewModel
import com.example.komiconnect.screens.profile.ProfileScreen
import com.example.komiconnect.screens.profile.ProfileViewModel
import com.example.komiconnect.screens.register.RegistrationScreen
import com.example.komiconnect.screens.search.SearchScreen
import com.example.komiconnect.screens.search.SearchViewModel
import com.example.komiconnect.screens.settings.SettingsViewModel
import com.example.komiconnect.screens.splash.SplashScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel


sealed interface KomiConnectRoute {
    @Serializable data object Login : KomiConnectRoute
    @Serializable data object Register : KomiConnectRoute
    @Serializable data object Home : KomiConnectRoute
    @Serializable data object Settings : KomiConnectRoute
    @Serializable data object Map : KomiConnectRoute
    @Serializable data object Search : KomiConnectRoute
    @Serializable data object Favorites : KomiConnectRoute
    @Serializable data object Add : KomiConnectRoute
    @Serializable data object Splash : KomiConnectRoute
}

@Serializable data class Profile(val id : Int?) : KomiConnectRoute
@Serializable data class Convention(val id : Int?) : KomiConnectRoute
@Serializable data class Post(val id: Int?): KomiConnectRoute

@Composable
fun KomiConnectNavGraph(navController: NavHostController) {
    val loginVm = koinViewModel<LoginViewModel>()
    val homeVm = koinViewModel<HomeViewModel>()
    val settingsVm = koinViewModel<SettingsViewModel>()
    val profileVm = koinViewModel<ProfileViewModel>()
    val conventionVm = koinViewModel<ConventionViewModel>()
    val searchVm = koinViewModel<SearchViewModel>()
    val addVm = koinViewModel<AddViewModel>()
    val postVm = koinViewModel<PostViewModel>()
    val favoritesVm = koinViewModel<FavoritesViewModel>()
    val mapVm = koinViewModel<MapViewModel>()

    NavHost(
        navController = navController,
        startDestination = if(loginVm.state == null){
            KomiConnectRoute.Splash
        } else if(loginVm.state?.token == "") {
            KomiConnectRoute.Login
        } else {
            KomiConnectRoute.Home
        }
    ) {
        composable<KomiConnectRoute.Splash> {
            SplashScreen()
        }
        composable<KomiConnectRoute.Login> {
            LoginScreen(loginVm::setToken, navController)
        }
        composable<KomiConnectRoute.Register> {
            RegistrationScreen(navController)
        }
        composable<KomiConnectRoute.Home> {
            HomeScreen(homeVm.state, homeVm::allPosts, navController)
        }
        composable<KomiConnectRoute.Settings> {
            SettingsScreen(
                settingsVm.state, settingsVm::resetToken,
                settingsVm::changeTheme, navController)
        }
        composable<Profile> { backStackEntry ->
            val profile: Profile = backStackEntry.toRoute()
            ProfileScreen(
                profile.id, profileVm.state,
                profileVm.meResponse, profileVm.error,
                profileVm.postResponse, profileVm.postError,
                profileVm::fetchUserProfile, profileVm::saveDataChanges,
                profileVm::uploadPicture,
                profileVm::fetchProfilePost,
                navController)
        }

        composable<Convention> { backStackEntry ->
            val convention: Convention = backStackEntry.toRoute()
            ConventionScreen(
                convention.id, conventionVm.state,
                conventionVm.meResponse, conventionVm.error,
                conventionVm::fetchConventionProfile,
                navController)
        }

        composable<Post> { backStackEntry ->
            val post: Post = backStackEntry.toRoute()
            PostScreen(
                post.id, postVm.state,
                postVm.imageResponse, postVm.userImageResponse,
                postVm.postResponse, postVm.postError,
                postVm.userResponse,
                postVm.conventionResponse,
                postVm::deletePost,
                postVm::fetchPostProfile, postVm::fetchUserProfile, postVm::fetchConvention,
                postVm::fetchFavorites, postVm::fetchLikes,
                postVm::addFavorite, postVm::deleteFavorite,
                postVm::addLike, postVm::deleteLike,
                navController)
        }

        composable<KomiConnectRoute.Map> {
            MapScreen(mapVm.state, mapVm::allConventions, navController)
        }
        composable<KomiConnectRoute.Search> {
            SearchScreen(searchVm.state, searchVm::allUsers, searchVm::allConventions, navController)
        }
        composable<KomiConnectRoute.Favorites> {
            FavoritesScreen(favoritesVm.state, favoritesVm::favoritePosts, navController)
        }
        composable<KomiConnectRoute.Add> {
            AddPostScreen(addVm.state, addVm::allConventions, addVm::uploadPicture, navController)
        }
    }
}
