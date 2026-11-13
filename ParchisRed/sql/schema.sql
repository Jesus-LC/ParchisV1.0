-- Ejecutar una vez en MySQL (Workbench o consola): mysql -u root -p < sql/schema.sql
CREATE DATABASE IF NOT EXISTS parchis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE parchis;

CREATE TABLE IF NOT EXISTS perfil (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(40) NOT NULL UNIQUE,
    creado  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
