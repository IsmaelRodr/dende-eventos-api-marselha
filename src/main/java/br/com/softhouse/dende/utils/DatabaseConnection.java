package br.com.softhouse.dende.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária responsável pelo gerenciamento de conexões JDBC com o MySQL.
 * Adota o padrão Singleton/Factory para fornecer conexões seguras e isoladas.
 */
public class DatabaseConnection {

    // 1. DADOS DE CONEXÃO (A string de URL define o driver, host, porta e schema)
    // O parâmetro serverTimezone=America/Sao_Paulo evita bugs de datas ao salvar eventos!
    private static final String URL = "jdbc:mysql://localhost:3306/dende_eventos?useSSL=false&serverTimezone=America/Sao_Paulo";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Ajustem para a senha da máquina de vocês

    // 2. CONSTRUTOR PRIVADO: Uma regra de ouro da arquitetura. 
    // Classes utilitárias contêm apenas métodos estáticos. Ninguém deve fazer "new DatabaseConnection()".
    private DatabaseConnection() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada.");
    }

    // 3. O MÉTODO DE FÁBRICA (Factory Method)
    public static Connection getConnection() {
        try {
            // Garante que a JVM (Java Virtual Machine) carregue a classe do driver do MySQL para a memória.
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // O DriverManager é o maestro do JDBC. Ele gerencia o socket TCP/IP com o banco de dados.
            return DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            // Se o driver não estiver no build.gradle.kts, o sistema morre aqui com clareza.
            throw new RuntimeException("CRÍTICO: Driver do MySQL (com.mysql.cj.jdbc.Driver) não encontrado no Classpath.", e);
        } catch (SQLException e) {
            // Se o banco estiver desligado ou a senha errada, capturamos o erro e informamos o motivo.
            throw new RuntimeException("CRÍTICO: Falha de autenticação ou conexão recusada pelo MySQL. Verifique se o serviço está rodando.", e);
        }
    }
} 