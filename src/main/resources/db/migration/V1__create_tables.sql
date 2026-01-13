CREATE TABLE enderecos
(
    id         BIGSERIAL PRIMARY KEY,
    cep        VARCHAR(9),
    logradouro VARCHAR(255),
    numero     VARCHAR(10),
    bairro     VARCHAR(100),
    cidade     VARCHAR(100),
    estado     VARCHAR(2)
);

CREATE TABLE imoveis
(
    id              BIGSERIAL PRIMARY KEY,
    titulo          VARCHAR(255) NOT NULL,
    descricao       TEXT,
    tipo_imovel     VARCHAR(50),
    status          VARCHAR(50),
    valor           DECIMAL(12, 2),
    area_total      DECIMAL(10, 2),
    area_construida DECIMAL(10, 2),
    quartos         INT,
    banheiros       INT,
    vagas_garagem   INT,
    endereco_id     BIGINT REFERENCES enderecos (id)
);

CREATE TABLE fotos_imovel
(
    id          BIGSERIAL PRIMARY KEY,
    url_arquivo VARCHAR(255) NOT NULL,
    imovel_id   BIGINT REFERENCES imoveis (id) ON DELETE CASCADE
);