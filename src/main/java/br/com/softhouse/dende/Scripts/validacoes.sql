-- Execução
INSERT INTO usuario (nome, data_nascimento, sexo, email, senha, ativo)
VALUES ('Ana Silva', '1995-07-20', 'Feminino', 'ana@email.com', 'senha123', DEFAULT);

-- Verificação
SELECT id, nome, email, ativo FROM usuario WHERE email = 'ana@email.com';
-- Esperado: 1 linha com ativo = TRUE e nome 'Ana Silva'


-- Execução (deve falhar)
INSERT INTO usuario (nome, data_nascimento, sexo, email, senha)
VALUES ('Outra Ana', '1990-01-15', 'Feminino', 'ana@email.com', 'outrasenha');

-- Verificação de erro: erro 1062 (Duplicate entry) por UNIQUE(email)
-- ou verificação de contagem:
SELECT COUNT(*) FROM usuario WHERE email = 'ana@email.com';
-- Esperado: 1 (apenas o primeiro registro)

-- 1º 
//////////////////////////////////////////////////////////////////////////////

INSERT INTO organizador (nome, data_nascimento, sexo, email, senha, ativo)
VALUES ('Carlos Organizador', '1988-03-10', 'Masculino', 'carlos@org.com', 'org123', DEFAULT);

-- Verificação
SELECT id, nome, email, ativo FROM organizador WHERE email = 'carlos@org.com';
-- Esperado: 1 linha

-- Inserir empresa vinculada ao organizador
INSERT INTO empresa (cnpj, razao_social, nome_fantasia, organizador_id)
VALUES ('12.345.678/0001-90', 'Eventos Carlos Ltda', 'Carlos Eventos', 1);

-- Verificação
SELECT e.cnpj, o.nome FROM empresa e JOIN organizador o ON e.organizador_id = o.id
WHERE o.email = 'carlos@org.com';
-- Esperado: 1 linha com cnpj correto

-- A coluna organizador_id é NOT NULL, portanto:
INSERT INTO empresa (cnpj, razao_social, nome_fantasia) -- não informa organizador_id
VALUES ('98.765.432/0001-11', 'Sem Dono', 'S.D.');
-- Erro esperado: Field 'organizador_id' doesn't have a default value

-- 2º
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-- Supõe-se que a aplicação não permita alterar o e-mail (regra de negócio) – o UPDATE será feito apenas com outros campos.
-- Teste: atualizar nome e senha.
UPDATE usuario SET nome = 'Ana Maria Silva', senha = 'novaSenha456' WHERE id = 1;

-- Verificação: e-mail permanece inalterado.
SELECT nome, email, senha FROM usuario WHERE id = 1;
-- Esperado: nome = 'Ana Maria Silva', email = 'ana@email.com', senha = 'novaSenha456'

-- 3º
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-- Query de visualização (cálculo de idade)
SELECT 
    nome,
    data_nascimento,
    CONCAT(
        TIMESTAMPDIFF(YEAR, data_nascimento, CURDATE()), ' anos, ',
        TIMESTAMPDIFF(MONTH, data_nascimento, CURDATE()) % 12, ' meses, ',
        DATEDIFF(CURDATE(), DATE_ADD(data_nascimento, INTERVAL TIMESTAMPDIFF(YEAR, data_nascimento, CURDATE()) YEAR)) 
        - (TIMESTAMPDIFF(MONTH, data_nascimento, CURDATE()) % 12) * 30, ' dias'
    ) AS idade,
    email
FROM usuario WHERE id = 1;
-- A verificação exata depende da data de execução, mas a lógica deve retornar algo como '30 anos, 9 meses, 22 dias'.

SELECT o.nome, o.email, e.cnpj, e.razao_social, e.nome_fantasia
FROM organizador o
LEFT JOIN empresa e ON e.organizador_id = o.id
WHERE o.id = 1;
-- Esperado: 1 linha contendo os dados da empresa.

-- 4º 
////////////////////////////////////////////////////////////////////////////////////////////////////////////

UPDATE usuario SET ativo = FALSE WHERE id = 1;
SELECT ativo FROM usuario WHERE id = 1;
-- Esperado: 0 (false)

-- Tentativa de desativar (regra de negócio a ser implementada na aplicação, mas testamos a integridade)
-- Este teste depende da lógica da aplicação; no banco, podemos simular a validação:
SELECT COUNT(*) AS eventos_ativos FROM evento 
WHERE organizador_id = 1 AND evento_ativo = TRUE AND data_fim >= NOW();
-- Se count > 0, a aplicação deve impedir a desativação.
-- Para teste positivo, primeiro garantir que não haja eventos ativos:
UPDATE evento SET evento_ativo = FALSE WHERE organizador_id = 1;
UPDATE organizador SET ativo = FALSE WHERE id = 1;
SELECT ativo FROM organizador WHERE id = 1;
-- Esperado: 0 (false)

-- 5º
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-- Autenticação via e-mail/senha (feita pela aplicação) – no banco testamos a reativação após validação bem-sucedida.
UPDATE organizador SET ativo = TRUE WHERE id = 1;
SELECT ativo FROM organizador WHERE id = 1;
-- Esperado: 1 (true)

-- 6º
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

INSERT INTO evento (organizador_id, nome, descricao, pagina_web, data_inicio, data_fim,
                    tipo_evento, modalidade, preco_unitario_ingresso, taxa_cancelamento,
                    evento_estorno, capacidade_maxima, local_evento)
VALUES (1, 'Workshop Spring', 'Aprenda Spring Boot', 'http://workshop.com',
        '2026-06-10 09:00:00', '2026-06-10 12:00:00',
        'OFICINA', 'PRESENCIAL', 150.00, 10.00, TRUE, 50, 'Sala 101');

-- Verificação
SELECT nome, capacidade_maxima, evento_ativo FROM evento WHERE nome = 'Workshop Spring';
-- Esperado: 1 linha, evento_ativo = FALSE (default)

INSERT INTO evento (organizador_id, nome, data_inicio, data_fim, ...)
VALUES (1, 'Evento Passado', '2020-01-01 10:00:00', '2020-01-01 12:00:00', ...);
-- Verificação: o banco não impõe essa restrição por si só; a aplicação deve validar.
-- Teste: SELECT da data atual e comparar com a data inserida; o INSERT não deve ocorrer.
-- Podemos testar a lógica simulando a validação:
SELECT (data_inicio >= NOW()) AS valido FROM evento WHERE nome = 'Evento Passado';
-- Se o insert for permitido indevidamente, o resultado será 0.
-- Para garantir, antes do insert, a aplicação verifica e rejeita.
-- Após a tentativa, verificar que não foi inserido:
SELECT COUNT(*) FROM evento WHERE nome = 'Evento Passado';
-- Esperado: 0

-- Inserir com data_fim - data_inicio = 10 minutos (inválido)
INSERT INTO evento (...) VALUES (..., '2026-06-10 09:00:00', '2026-06-10 09:10:00', ...);
-- Validação via query:
SELECT TIMESTAMPDIFF(MINUTE, data_inicio, data_fim) >= 30 AS valido FROM evento WHERE nome = 'Curto';
-- Espera-se que a aplicação impeça, então SELECT COUNT(*) deve retornar 0.

-- Inserir com data_fim < data_inicio (inválido)
-- Verificação: CHECK( data_fim > data_inicio ) não existe no schema, então a aplicação valida.
-- Teste: após tentativa de insert, count = 0.

-- 7º
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

UPDATE evento 
SET nome = 'Workshop Spring Boot Avançado', descricao = 'Nova descrição', preco_unitario_ingresso = 200.00
WHERE id = 1;

-- Verificação: nome e preço alterados, evento_ativo permanece igual.
SELECT nome, preco_unitario_ingresso, evento_ativo FROM evento WHERE id = 1;
-- Esperado: nome atualizado, ativo mantido (TRUE se estava ativo)

-- 8º
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

UPDATE evento SET evento_ativo = TRUE WHERE id = 1;
SELECT evento_ativo FROM evento WHERE id = 1;
-- Esperado: TRUE

-- 9º
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

UPDATE evento SET evento_ativo = FALSE WHERE id = 1;
-- Verificar
SELECT evento_ativo FROM evento WHERE id = 1;
-- Esperado: FALSE

-- Simulação: criar ingressos ativos.
INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago)
VALUES (1, 1, 'ACEITO', 'ana@email.com', 150.00);
INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago)
VALUES (2, 1, 'ACEITO', 'joao@email.com', 150.00); -- ESSE DEU ERRO

-- Executar a desativação (aplicação fará os passos abaixo)
START TRANSACTION;
-- Cancelar todos os ingressos e registrar estorno conforme taxa
UPDATE ingresso 
SET status = 'CANCELADO', 
    valor_estornado = CASE 
        WHEN EXISTS (SELECT 1 FROM evento WHERE id = evento_id AND evento_estorno = TRUE) 
        THEN valor_pago * (1 - (SELECT taxa_cancelamento FROM evento WHERE id = evento_id) / 100)
        ELSE 0 
    END
WHERE evento_id = 1;

-- Atualizar evento para inativo
UPDATE evento SET evento_ativo = FALSE WHERE id = 1;
COMMIT;

-- Verificações:
SELECT status, valor_estornado FROM ingresso WHERE evento_id = 1;
-- Esperado: ambos 'CANCELADO' e valor_estornado > 0 (se evento_estorno = TRUE)
SELECT evento_ativo FROM evento WHERE id = 1;
-- Esperado: FALSE

-- 10º
///////////////////////////////////////////////////////////////////////////

SELECT nome, data_inicio, data_fim, local_evento, preco_unitario_ingresso, capacidade_maxima
FROM evento
WHERE organizador_id = 1
ORDER BY data_inicio;
-- Esperado: lista com os dados solicitados.

-- 11º
//////////////////////////////////////////////////////////////////////////////

-- Evento ativo, com vagas, futuro
-- Evento finalizado (data_fim passada) – não deve aparecer.
-- Evento sem ingressos disponíveis (capacidade máxima = total de ingressos ACEITO) – não deve aparecer.

SELECT e.nome, e.data_inicio, e.data_fim, e.local_evento,
       e.preco_unitario_ingresso, e.capacidade_maxima,
       e.tipo_evento, e.modalidade,
       (e.capacidade_maxima - COUNT(i.id)) AS vagas_disponiveis
FROM evento e
LEFT JOIN ingresso i ON i.evento_id = e.id AND i.status = 'ACEITO'
WHERE e.evento_ativo = TRUE
  AND e.data_fim >= NOW()
GROUP BY e.id
HAVING vagas_disponiveis > 0
ORDER BY e.data_inicio, e.nome;

-- Evento finalizado não deve estar no feed:
SELECT id, nome, data_fim FROM evento WHERE data_fim < NOW();

-- Evento lotado:
SELECT e.id, e.nome, e.capacidade_maxima, COUNT(i.id) AS vendidos
FROM evento e
LEFT JOIN ingresso i ON i.evento_id = e.id AND i.status = 'ACEITO'
GROUP BY e.id
HAVING vendidos >= e.capacidade_maxima;

-- 12º
//////////////////////////////////////////////////////////////////////////////////////////////////////

INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago, data_compra)
VALUES (1, 1, 'ACEITO', 'ana@email.com', 150.00, NOW());
-- Verificar ingresso criado e decremento indireto da capacidade (via verificação de vagas).
SELECT COUNT(*) AS total_aceito FROM ingresso WHERE evento_id = 1 AND status = 'ACEITO';
-- Deve ser ≤ capacidade_maxima.

-- **

-- Aplicação calculará valorTotal = (SELECT preco_unitario_ingresso FROM evento WHERE id = 1) + preco_sub.
INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago)
VALUES (1, 1, 'ACEITO', 'ana@email.com', (SELECT preco_unitario_ingresso FROM evento WHERE id = 1));
INSERT INTO ingresso (usuario_id, evento_id, status, email, valor_pago)
VALUES (1, 2, 'ACEITO', 'ana@email.com', (SELECT preco_unitario_ingresso FROM evento WHERE id = 2));

-- Verificação: dois ingressos para o mesmo usuario na mesma compra (relacionados).
SELECT COUNT(*) FROM ingresso WHERE usuario_id = 1 AND evento_id IN (1,2) AND status = 'ACEITO';
-- Esperado: 2

