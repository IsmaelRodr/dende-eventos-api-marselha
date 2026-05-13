create database Dende_Eventos_Marselha;

use Dende_Eventos_Marselha;

-- MySQL database schema (v2) – Organizador opcionalmente ligado a Empresa,
-- mas Empresa obrigatoriamente ligada a um Organizador.
-- Engine: InnoDB | Charset: utf8mb4

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE organizador (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE empresa (
    cnpj VARCHAR(18) PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    organizador_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_empresa_organizador FOREIGN KEY (organizador_id)
        REFERENCES organizador(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE evento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizador_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    pagina_web VARCHAR(500),
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    tipo_evento ENUM('SOCIAL','CORPORATIVO','ACADÊMICO','CULTURAL','ENTRETENIMENTO',
                     'RELIGIOSOS','ESPORTIVOS','FEIRA','CONGRESSO','OFICINA','CURSO',
                     'TREINAMENTO','AULA','SEMINÁRIO','PALESTRA','SHOW','FESTIVAL',
                     'EXPOSIÇÃO','RETIRO','CULTO','CELEBRAÇÃO','CAMPEONATO','CORRIDA') NOT NULL,
    evento_principal_id BIGINT NULL,
    modalidade ENUM('PRESENCIAL','REMOTO','HIBRIDO') NOT NULL,
    preco_unitario_ingresso DECIMAL(10,2) NOT NULL,
    taxa_cancelamento DECIMAL(10,2) NOT NULL DEFAULT 0,
    evento_estorno BOOLEAN NOT NULL DEFAULT FALSE,
    capacidade_maxima INT NOT NULL,
    local_evento VARCHAR(255) NOT NULL,
    evento_ativo BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_evento_organizador FOREIGN KEY (organizador_id)
        REFERENCES organizador(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_evento_principal FOREIGN KEY (evento_principal_id)
        REFERENCES evento(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ingresso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    status ENUM('ACEITO','CANCELADO') NOT NULL DEFAULT 'ACEITO',
    email VARCHAR(255) NOT NULL,
    valor_pago DECIMAL(10,2) NOT NULL,
    valor_estornado DECIMAL(10,2) NOT NULL DEFAULT 0,
    data_compra DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- vírgula aqui
    CONSTRAINT fk_ingresso_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_ingresso_evento FOREIGN KEY (evento_id)
        REFERENCES evento(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Índices para desempenho
CREATE INDEX idx_evento_organizador ON evento(organizador_id);
CREATE INDEX idx_evento_principal ON evento(evento_principal_id);
CREATE INDEX idx_evento_ativo ON evento(evento_ativo);
CREATE INDEX idx_ingresso_usuario ON ingresso(usuario_id);
CREATE INDEX idx_ingresso_evento ON ingresso(evento_id);
CREATE INDEX idx_ingresso_status ON ingresso(status);