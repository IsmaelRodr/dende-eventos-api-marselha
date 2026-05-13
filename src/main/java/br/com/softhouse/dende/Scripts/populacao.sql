-- ============================================================
-- INSERTS PARA O BANCO DE DADOS
-- ============================================================

-- ------------------------------------------------------------
-- 1. USUARIOS (10+ registros)
-- ------------------------------------------------------------
INSERT INTO usuario (nome, data_nascimento, sexo, email, senha, ativo) VALUES
('Ana Silva',      '1995-07-20', 'Feminino',  'ana.silva@email.com',      'senha123', TRUE),
('João Pereira',   '1990-03-15', 'Masculino', 'joao.pereira@email.com',   'senha123', TRUE),
('Maria Santos',   '1988-11-28', 'Feminino',  'maria.santos@email.com',   'senha123', TRUE),
('Carlos Oliveira','1993-01-05', 'Masculino', 'carlos.oliveira@email.com','senha123', TRUE),
('Fernanda Lima',  '2000-09-12', 'Feminino',  'fernanda.lima@email.com',  'senha123', FALSE),
('Rafael Costa',   '1985-06-30', 'Masculino', 'rafael.costa@email.com',   'senha123', TRUE),
('Juliana Alves',  '1997-02-14', 'Feminino',  'juliana.alves@email.com',  'senha123', TRUE),
('Pedro Rocha',    '1992-12-01', 'Masculino', 'pedro.rocha@email.com',    'senha123', FALSE),
('Larissa Mendes', '1987-04-18', 'Feminino',  'larissa.mendes@email.com', 'senha123', TRUE),
('Marcos Souza',   '1999-08-07', 'Masculino', 'marcos.souza@email.com',   'senha123', TRUE),
('Beatriz Cunha',  '1996-10-22', 'Feminino',  'beatriz.cunha@email.com',  'senha123', TRUE);

-- ------------------------------------------------------------
-- 2. ORGANIZADORES (10 registros)
-- ------------------------------------------------------------
INSERT INTO organizador ( nome, data_nascimento, sexo, email, senha, ativo) VALUES
('Ricardo Eventos',   '1980-05-10', 'Masculino', 'ricardo.eventos@org.com',   'org123', TRUE),
('Camila Produções',  '1983-11-25', 'Feminino',  'camila.producoes@org.com',  'org123', TRUE),
('Fernando Congressos','1975-02-14', 'Masculino', 'fernando.congressos@org.com','org123', TRUE),
('Patrícia Shows',    '1988-07-30', 'Feminino',  'patricia.shows@org.com',     'org123', TRUE),
('Lucas Tecnologia',  '1990-01-20', 'Masculino', 'lucas.tecnologia@org.com',   'org123', TRUE),
('Tatiana Educação',  '1982-09-15', 'Feminino',  'tatiana.educacao@org.com',   'org123', FALSE),
('Bruno Esportes',    '1979-06-08', 'Masculino', 'bruno.esportes@org.com',     'org123', TRUE),
('Amanda Cultural',   '1991-03-12', 'Feminino',  'amanda.cultural@org.com',    'org123', TRUE),
('Gabriel Feiras',    '1986-12-05', 'Masculino', 'gabriel.feiras@org.com',     'org123', TRUE),
('Isabela Religião',  '1984-08-24', 'Feminino',  'isabela.religiao@org.com',   'org123', TRUE);

-- ------------------------------------------------------------
-- 3. EMPRESAS (10 registros, todos vinculados a organizadores)
-- ------------------------------------------------------------
INSERT INTO empresa (cnpj, razao_social, nome_fantasia, organizador_id) VALUES
('23.456.789/0001-02', 'Produções Camila Eireli',     'Camila Produções',      2),
('34.567.890/0001-03', 'Fernando Congressos S.A.',    'Fernando Congressos',   3),
('45.678.901/0001-04', 'Patrícia Shows ME',           'Patrícia Shows',        4),
('56.789.012/0001-05', 'Lucas Tech Eventos Ltda',     'Lucas Tech',            5),
('67.890.123/0001-06', 'Tatiana Educação Corporativa','Tatiana Educação',      6),
('78.901.234/0001-07', 'Bruno Esportes e Lazer Ltda', 'Bruno Esportes',        7),
('89.012.345/0001-08', 'Amanda Produções Culturais',  'Amanda Cultural',       8),
('90.123.456/0001-09', 'Gabriel Feiras e Eventos ME', 'Gabriel Feiras',        9),
('01.234.567/0001-10', 'Isabela Eventos Religiosos',  'Isabela Religião',     10);

-- ------------------------------------------------------------
-- 4. EVENTOS (10 registros, variando entre ativos/inativos e futuros/passados)
-- Data de referência: 2026-05-11
-- ------------------------------------------------------------
INSERT INTO evento (organizador_id, nome, descricao, pagina_web, data_inicio, data_fim,
                    tipo_evento, evento_principal_id, modalidade, preco_unitario_ingresso,
                    taxa_cancelamento, evento_estorno, capacidade_maxima, local_evento, evento_ativo) VALUES
(1, 'Congresso de TI 2026',
    'O maior congresso de tecnologia da região.',
    'http://congressoti2026.com',
    '2026-09-10 09:00:00', '2026-09-12 18:00:00',
    'CONGRESSO', NULL, 'PRESENCIAL', 350.00, 20.00, TRUE, 500, 'Centro de Convenções Norte', TRUE),
(1, 'Workshop: IA na Prática',
    'Workshop intensivo sobre inteligência artificial.',
    'http://congressoti2026.com/workshop-ia',
    '2026-09-11 14:00:00', '2026-09-11 18:00:00',
    'OFICINA', 1, 'PRESENCIAL', 120.00, 10.00, TRUE, 50, 'Sala 101', TRUE),
(2, 'Curso Online de Design',
    'Aprenda design gráfico do zero ao avançado.',
    'http://cursodesign.com',
    '2026-06-01 19:00:00', '2026-06-30 21:00:00',
    'CURSO', NULL, 'REMOTO', 200.00, 0.00, FALSE, 200, 'Plataforma Zoom', TRUE),
(3, 'Seminário de Marketing Digital',
    'Tendências do marketing para 2025.',
    'http://seminariomkt.com',
    '2025-11-20 08:00:00', '2025-11-20 12:00:00',
    'SEMINÁRIO', NULL, 'PRESENCIAL', 80.00, 0.00, FALSE, 100, 'Auditório Central', FALSE),
(7, 'Corrida Beneficente Esperança',
    'Corrida de 5km em prol de instituições de caridade.',
    'http://corridaesperanca.com',
    '2026-07-25 07:00:00', '2026-07-25 11:00:00',
    'CORRIDA', NULL, 'PRESENCIAL', 50.00, 100.00, TRUE, 1000, 'Parque Municipal', TRUE),
(4, 'Show Nacional: Banda Sol Maior',
    'Show da banda Sol Maior com seus maiores sucessos.',
    'http://shownacional.com',
    '2026-08-15 21:00:00', '2026-08-16 01:00:00',
    'SHOW', NULL, 'PRESENCIAL', 150.00, 50.00, TRUE, 3000, 'Estádio Municipal', TRUE),
(10, 'Retiro Espiritual Anual',
    'Retiro de renovação da fé.',
    'http://retiroespiritual.com',
    '2026-10-03 10:00:00', '2026-10-05 18:00:00',
    'RETIRO', NULL, 'PRESENCIAL', 0.00, 0.00, TRUE, 150, 'Mosteiro São Bento', TRUE),
(5, 'Treinamento Avançado Java',
    'Treinamento prático de Java para equipes.',
    'http://treinamentojava.com',
    '2026-11-10 08:00:00', '2026-11-12 17:00:00',
    'TREINAMENTO', NULL, 'PRESENCIAL', 750.00, 30.00, TRUE, 20, 'Sala de Treinamentos Tech', FALSE),
(2, 'Mesa Redonda: Futuro da Educação',
    'Discussão sobre os rumos da educação superior.',
    'http://congressoti2026.com/mesa-educacao',
    '2026-09-11 10:00:00', '2026-09-11 12:00:00',
    'PALESTRA', 1, 'HIBRIDO', 0.00, 0.00, TRUE, 80, 'Sala 202', TRUE),
(6, 'Feira de Artesanato Regional',
    'Exposição e venda de artesanato local.',
    'http://feiraartesanato.com',
    '2026-02-10 09:00:00', '2026-02-12 20:00:00',
    'FEIRA', NULL, 'PRESENCIAL', 10.00, 0.00, FALSE, 200, 'Praça da Matriz', FALSE);

-- ------------------------------------------------------------
-- 5. INGRESSOS (10 registros, com situações variadas)
-- ------------------------------------------------------------
INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago, valor_estornado, data_compra) VALUES
-- Ingresso 1: ACEITO para evento futuro (1)
(1, 1, 'ACEITO',  'ana.silva@email.com',       350.00, 0.00,  '2026-05-10 15:30:00'),
-- Ingresso 2: ACEITO para subevento (2) – usuário 1 também compra do principal? A regra pode exigir dois, mas aqui inserimos individualmente.
(1, 2, 'ACEITO',  'ana.silva@email.com',       120.00, 0.00,  '2026-05-10 15:31:00'),
-- Ingresso 3: ACEITO para evento futuro (3)
(6, 3, 'ACEITO',  'joao.pereira@email.com',    200.00, 0.00,  '2026-04-20 10:00:00'),
-- Ingresso 4: CANCELADO para evento passado (4) – estorno total
(7, 4, 'CANCELADO', 'maria.santos@email.com',   80.00,  80.00, '2025-11-10 14:00:00'),
-- Ingresso 5: ACEITO para corrida (5)
(6, 5, 'ACEITO',  'rafael.costa@email.com',     50.00, 0.00,  '2026-05-01 09:00:00'),
-- Ingresso 6: ACEITO para show (6)
(7, 6, 'ACEITO',  'juliana.alves@email.com',   150.00, 0.00,  '2026-05-05 12:00:00'),
-- Ingresso 7: CANCELADO (estorno parcial) para evento 1 (usando evento 1, mas cancelado)
(9, 1, 'CANCELADO','larissa.mendes@email.com',  350.00, 280.00, '2026-04-15 16:45:00'),
-- Ingresso 8: ACEITO para retiro (7) – gratuito
( 4, 7, 'ACEITO',  'carlos.oliveira@email.com',  0.00, 0.00,   '2026-05-10 18:00:00'),
-- Ingresso 9: ACEITO para treinamento (8) – evento inativo, mas permitido no banco
( 10, 8, 'ACEITO', 'marcos.souza@email.com',     750.00, 0.00,  '2026-05-11 08:30:00'),
-- Ingresso 10: CANCELADO para feira (10) que já passou
( 5, 10, 'CANCELADO','fernanda.lima@email.com',  10.00, 0.00,   '2026-02-01 09:00:00'),
-- Ingresso extra (11): ACEITO para evento 9 (palestra vinculada ao 1)
( 1, 9, 'ACEITO',  'joao.pereira@email.com',    0.00, 0.00,   '2026-05-10 20:00:00');

SELECT id, email FROM usuario ORDER BY id;