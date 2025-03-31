-- 1. Criação das Extensões e Tabelas
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabela de Operadoras
CREATE TABLE operadoras (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    registro_ans VARCHAR(20) UNIQUE NOT NULL,
    cnpj VARCHAR(18) NOT NULL,
    razao_social TEXT NOT NULL,
    nome_fantasia TEXT,
    modalidade TEXT,
    uf CHAR(2),
    data_registro DATE,
    fonte_dado TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Demonstrações Contábeis
CREATE TABLE demonstracoes_contabeis (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    registro_ans VARCHAR(20) REFERENCES operadoras(registro_ans),
    ano INT NOT NULL,
    trimestre INT NOT NULL,
    evento_sinistro NUMERIC(18,2) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Criação das Tabelas Temporárias para Importação dos Dados
-- Tabela temporária para Operadoras
CREATE TEMP TABLE temp_operadoras (
    registro_ans VARCHAR(20),
    cnpj VARCHAR(18),
    razao_social TEXT,
    nome_fantasia TEXT,
    modalidade TEXT,
    uf CHAR(2),
    data_registro DATE
);

-- Importando Dados para a Tabela Temporária
\copy temp_operadoras FROM 'TESTE DE NIVELAMENTO/3. TESTE BANCO ANS/Dados_Cadastrais/Relatorio_cadop.csv' 
DELIMITER ';' CSV HEADER;

-- Inserindo Dados da Tabela Temporária para a Tabela Final
INSERT INTO operadoras (registro_ans, cnpj, razao_social, nome_fantasia, modalidade, uf, data_registro, fonte_dado)
SELECT registro_ans, cnpj, razao_social, nome_fantasia, modalidade, uf, data_registro, 'ANS - Dados Abertos' 
FROM temp_operadoras;

-- Removendo Tabela Temporária
DROP TABLE temp_operadoras;

-- Tabela Temporária para Demonstrações Contábeis
CREATE TEMP TABLE temp_demonstracoes (
    registro_ans VARCHAR(20),
    ano INT,
    trimestre INT,
    evento_sinistro NUMERIC(18,2)
);

-- Importando Dados para a Tabela Temporária de Demonstrações
\copy temp_demonstracoes FROM 'TESTE DE NIVELAMENTO/3. TESTE BANCO ANS/2024/1T2024/1T2024.csv' 
DELIMITER ';' CSV HEADER;

-- Inserindo Dados para a Tabela Final de Demonstrações Contábeis
INSERT INTO demonstracoes_contabeis (registro_ans, ano, trimestre, evento_sinistro)
SELECT registro_ans, ano, trimestre, evento_sinistro FROM temp_demonstracoes;

-- Removendo Tabela Temporária
DROP TABLE temp_demonstracoes;

-- 3. Consultas Analíticas

-- Consulta para o último Trimestre
WITH ult_trimestre AS (
    SELECT registro_ans, SUM(evento_sinistro) AS total_despesas
    FROM demonstracoes_contabeis
    WHERE ano = EXTRACT(YEAR FROM CURRENT_DATE)
    AND trimestre = (SELECT MAX(trimestre) FROM demonstracoes_contabeis WHERE ano = EXTRACT(YEAR FROM CURRENT_DATE))
    GROUP BY registro_ans
)
SELECT o.razao_social, u.total_despesas 
FROM ult_trimestre u
JOIN operadoras o ON u.registro_ans = o.registro_ans
ORDER BY u.total_despesas DESC
LIMIT 10;

-- Consulta para o Último Ano
WITH ult_ano AS (
    SELECT registro_ans, SUM(evento_sinistro) AS total_despesas
    FROM demonstracoes_contabeis
    WHERE ano = EXTRACT(YEAR FROM CURRENT_DATE)
    GROUP BY registro_ans
)
SELECT o.razao_social, u.total_despesas 
FROM ult_ano u
JOIN operadoras o ON u.registro_ans = o.registro_ans
ORDER BY u.total_despesas DESC
LIMIT 10;
