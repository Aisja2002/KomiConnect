BEGIN TRANSACTION;

DELETE FROM label;

INSERT INTO label (name) VALUES
    ('Acquisti'),
    ('Persone'),
    ('Eventi'),
    ('Cibo');

DELETE FROM convention;

INSERT INTO convention (data) VALUES
    (json_object(
        'name', 'Riminicomix',
        'start', '2025-07-17',
        'end', '2025-07-20',
        'location', 'Rimini (RN) ',
        'website', 'https://www.cartoonclubrimini.com/riminicomix-2025/',
        'coordinates', json_object('latitude', 44.0711902, 'longitude', 12.5739531)
    )),
    (json_object(
        'name', 'Terni Comics',
        'start', '2025-09-06',
        'end', '2025-09-08',
        'location', 'Terni (TR) ',
        'website', 'https://www.ternicomics.it/',
        'coordinates', json_object('latitude', 42.5603111, 'longitude', 12.6318516)
    )),
    (json_object(
        'name', 'Gamics Cesena',
        'start', '2025-11-30',
        'end', '2024-12-02',
        'location', 'Cesena (FC)',
        'website', 'https://www.gamicscesena.it/',
        'coordinates', json_object('latitude', 44.1795803, 'longitude', 12.2130677)
    ));

COMMIT;