CREATE TABLE enderecos
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    cep        VARCHAR(9),
    logradouro VARCHAR(255),
    numero     VARCHAR(10),
    bairro     VARCHAR(100),
    cidade     VARCHAR(100),
    estado     VARCHAR(2)
);

CREATE TABLE imoveis
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo          VARCHAR(255) NOT NULL,
    cidade          VARCHAR(100),
    descricao       VARCHAR(500),
    tipo_imovel     VARCHAR(50),
    status          VARCHAR(50),
    valor           DECIMAL(12, 2),
    area_total      DECIMAL(10, 2),
    area_construida DECIMAL(10, 2),
    quartos         INT,
    banheiros       INT,
    vagas_garagem   INT,
    endereco_id     BIGINT,
    movel_active    BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_imoveis_endereco FOREIGN KEY (endereco_id) REFERENCES enderecos (id)
);

CREATE TABLE fotos_imovel
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    url_arquivo VARCHAR(255) NOT NULL,
    imovel_id   BIGINT,
    CONSTRAINT fk_fotos_imovel FOREIGN KEY (imovel_id) REFERENCES imoveis (id) ON DELETE CASCADE
);