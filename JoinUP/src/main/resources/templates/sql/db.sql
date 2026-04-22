CREATE DATABASE IF NOT EXISTS eventos_db;
USE eventos_db;

CREATE TABLE Usuarios (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    d_nombre VARCHAR(45),
    d_ap1 VARCHAR(45),
    d_ap2 VARCHAR(45),
    d_email VARCHAR(45),
    d_password VARCHAR(45),
    d_numTelefono VARCHAR(45),

    fec_nac DATE,

    dir_provin VARCHAR(45),
    dir_pobla VARCHAR(45),
    dir_infoExtra VARCHAR(255),

    int_v1 VARCHAR(45),
    int_v2 VARCHAR(45),
    int_v3 VARCHAR(45),

    imagen LONGTEXT,

    url_twitter VARCHAR(255),
    url_facebook VARCHAR(255),
    url_linkedin VARCHAR(255),
    url_instagram VARCHAR(255),

    id_interes INT,
    d_rol ENUM('GRATUITO','PREMIUM'),

    FOREIGN KEY (id_interes) REFERENCES Intereses(id_interes)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE Eventos (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    ev_organ INT,
    ev_fecha DATE,
    ev_titulo VARCHAR(80),
    ev_desc VARCHAR(200),
    ev_maxPartic INT,
    ev_pro TINYINT,
    ev_tag1 VARCHAR(45),
    ev_tag2 VARCHAR(45),
    ev_tag3 VARCHAR(45),
    ev_precio VARCHAR(45),
    ev_imagen LONGTEXT,  

    dir_tipoVia VARCHAR(45),
    dir_via VARCHAR(45),
    dir_numVia VARCHAR(45),
    dir_piso VARCHAR(45),
    dir_puerta VARCHAR(45),
    dir_codigo VARCHAR(45),
    dir_provin VARCHAR(45),
    dir_pobla VARCHAR(45),
    dir_infoExtra VARCHAR(255),

    FOREIGN KEY (ev_organ) REFERENCES Usuarios(id_cliente)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Usuarios_Eventos (
    id_usuarios_eventos INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_evento INT NOT NULL,

    CONSTRAINT fk_usuario_evento_cliente
        FOREIGN KEY (id_cliente) REFERENCES Usuarios(id_cliente)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_usuario_evento_evento
        FOREIGN KEY (id_evento) REFERENCES Eventos(id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT unique_cliente_evento UNIQUE (id_cliente, id_evento)
);

CREATE TABLE Usuarios_Deseados (
    id_usuario_deseado INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_evento INT NOT NULL,

    CONSTRAINT fk_deseado_cliente
        FOREIGN KEY (id_cliente) REFERENCES Usuarios(id_cliente)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_deseado_evento
        FOREIGN KEY (id_evento) REFERENCES Eventos(id_evento)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT unique_deseado_user UNIQUE (id_cliente, id_evento)
);

CREATE TABLE pagos (
    id_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    monto DOUBLE NOT NULL,
    moneda VARCHAR(10) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha DATETIME NOT NULL,
    id_transaccion VARCHAR(100),

    CONSTRAINT fk_pago_usuario
        FOREIGN KEY (id_cliente)
        REFERENCES Usuarios(id_cliente)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
