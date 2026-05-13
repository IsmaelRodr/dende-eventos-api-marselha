-- Criar organizador, usuário e eventos de exemplo (ajuste conforme seu banco)
INSERT INTO organizador (nome, data_nascimento, sexo, email, senha)
VALUES ('Org Teste', '1985-05-20', 'Masculino', 'org@teste.com', 'senha123');
-- Suponha que o id gerado seja 1.

INSERT INTO usuario (nome, data_nascimento, sexo, email, senha)
VALUES ('Ana Silva', '1995-07-20', 'Feminino', 'anna@email.com', 'senha123');
-- Suponha que o id gerado seja 1.

-- Evento principal (futuro, ativo, com vagas)
INSERT INTO evento (organizador_id, nome, descricao, pagina_web, data_inicio, data_fim,
                    tipo_evento, modalidade, preco_unitario_ingresso, taxa_cancelamento,
                    evento_estorno, capacidade_maxima, local_evento)
VALUES (1, 'Congresso Anual', 'Evento principal', 'http://congresso.com',
        '2026-08-15 09:00:00', '2026-08-16 18:00:00',
        'CONGRESSO', 'PRESENCIAL', 200.00, 10.00, TRUE, 100, 'Centro de Convenções');
-- Suponha que o id gerado seja 1.

-- Subevento vinculado ao evento principal
INSERT INTO evento (organizador_id, nome, descricao, pagina_web, data_inicio, data_fim,
                    tipo_evento, modalidade, preco_unitario_ingresso, taxa_cancelamento,
                    evento_estorno, capacidade_maxima, local_evento, evento_principal_id)
VALUES (1, 'Workshop IA', 'Subevento', 'http://workshop-ia.com',
        '2026-08-16 14:00:00', '2026-08-16 18:00:00',
        'OFICINA', 'PRESENCIAL', 80.00, 5.00, TRUE, 30, 'Sala 201', 1);
-- Suponha que o id gerado seja 2.

-- ****

-- Comprar ingresso para o subevento (id=2), que tem evento principal (id=1)
-- Regra: valor total = preço do evento principal + preço do subevento.
-- Dois ingressos devem ser criados.

-- Insere ingresso do evento principal
INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago)
VALUES (1, 1, 'ACEITO', 'ana@email.com',
        (SELECT preco_unitario_ingresso FROM evento WHERE id = 1));

-- Insere ingresso do subevento (corrigindo a sintaxe)
INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago)
VALUES (1, 2, 'ACEITO', 'ana@email.com',
        (SELECT preco_unitario_ingresso FROM evento WHERE id = 2));

-- Deve retornar 2 ingressos ACEITO para o usuário 1
SELECT COUNT(*) AS total_ingressos
FROM ingresso
WHERE usuario_id = 1 AND evento_id IN (1,2) AND status = 'ACEITO';

-- Consultar os ingressos gerados
SELECT id, usuario_id, evento_id, status, valor_pago
FROM ingresso
WHERE usuario_id = 1;

-- 13º

////////////////////////////////////////////////////////////////////////////

UPDATE ingresso 
SET status = 'CANCELADO',
    valor_estornado = valor_pago * (1 - (SELECT taxa_cancelamento FROM evento WHERE id = evento_id) / 100)
WHERE id = 1;

-- Verificação:
SELECT status, valor_estornado FROM ingresso WHERE id = 1;
-- status = 'CANCELADO', valor_estornado = 135.00 (se valor_pago=150)
-- Também verificar se a vaga é liberada: o evento agora tem +1 vaga disponível (não diretamente, mas o feed deve refletir).

-- 14º

///////////////////////////////////////////////////////////////////////////////////////////

SELECT i.id, e.nome AS evento_nome, e.data_inicio, i.status, i.valor_pago, i.valor_estornado,
       e.evento_ativo
FROM ingresso i
JOIN evento e ON i.evento_id = e.id
WHERE i.usuario_id = 1
ORDER BY 
    CASE WHEN i.status = 'ACEITO' AND e.evento_ativo = TRUE AND e.data_fim >= NOW() THEN 0 ELSE 1 END,
    e.data_inicio ASC,
    e.nome ASC;
-- Verificação: os primeiros registros devem ser eventos ativos e futuros, depois os cancelados/finalizados.