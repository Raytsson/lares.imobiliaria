-- Criação das tabelas de Autenticação

CREATE TABLE roles
(
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios
(
    user_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(255) NOT NULL,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    ativo        BOOLEAN DEFAULT TRUE,
    ultimo_login DATETIME
);

CREATE TABLE usuarios_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES usuarios (user_id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

-- Carga inicial das Roles
INSERT INTO roles (name)
VALUES ('ADMIN');
INSERT INTO roles (name)
VALUES ('GERENTE');
INSERT INTO roles (name)
VALUES ('COLABORADOR');

-- Usuário Admin Padrão
-- ATENÇÃO: Se a senha não funcionar, use o endpoint de cadastro depois
INSERT INTO usuarios (nome, username, email, password, ativo)
VALUES ('Admin Master', 'admin', 'admin@imob.com.br', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8qkrPm',
        TRUE);

-- Vincula o Admin à Role de ADMIN
INSERT INTO usuarios_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM usuarios u,
     roles r
WHERE u.username = 'admin'
  AND r.name = 'ADMIN';