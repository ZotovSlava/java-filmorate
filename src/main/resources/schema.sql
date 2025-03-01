create TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100),
    birthday DATE
);

create TABLE IF NOT EXISTS follows (
    id_user_following INT NOT NULL,
    id_user_followed INT NOT NULL,
    PRIMARY KEY (id_user_following, id_user_followed),
    FOREIGN KEY (id_user_following) REFERENCES users(id) ON delete CASCADE ON update CASCADE,
    FOREIGN KEY (id_user_followed) REFERENCES users(id) ON delete CASCADE ON update CASCADE
);

create TABLE IF NOT EXISTS genres (
    id_genre INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS mpa (
    id_mpa INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS films (
    id INT AUTO_INCREMENT PRIMARY KEY,
    duration INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    id_mpa INT,
    FOREIGN KEY (id_mpa) REFERENCES mpa(id_mpa) ON delete CASCADE ON update CASCADE
);

create TABLE IF NOT EXISTS films_genres (
    id_film INT NOT NULL,
    id_genre INT NOT NULL,
    PRIMARY KEY (id_film, id_genre),
    FOREIGN KEY (id_film) REFERENCES films(id) ON delete CASCADE ON update CASCADE,
    FOREIGN KEY (id_genre) REFERENCES genres(id_genre) ON delete CASCADE ON update CASCADE
);

create TABLE IF NOT EXISTS likes (
    id_user INT NOT NULL,
    id_film INT NOT NULL,
    PRIMARY KEY (id_film, id_user),
    FOREIGN KEY (id_user) REFERENCES users(id) ON delete CASCADE ON update CASCADE,
    FOREIGN KEY (id_film) REFERENCES films(id) ON delete CASCADE ON update CASCADE
);
