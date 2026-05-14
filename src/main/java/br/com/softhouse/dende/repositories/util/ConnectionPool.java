package br.com.softhouse.dende.repositories.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static HikariDataSource dataSource;

    // Inicializamos o pool de forma estática garantindo o Singleton
    static {
        ConfigProperties configProps = new ConfigProperties();
        // Nota: Certifique-se de que o framework Dendê injeta os valores antes dessa chamada, 
        // ou adapte a inicialização conforme a documentação do framework.
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configProps.getUrl());
        config.setUsername(configProps.getUsername());
        config.setPassword(configProps.getPassword());
        config.setDriverClassName(configProps.getDriverClassName());
        config.setMaximumPoolSize(configProps.getMaximumPoolSize());
        config.setMinimumIdle(configProps.getMinimumIdle());
        config.setConnectionTimeout(configProps.getConnectionTimeout());

        dataSource = new HikariDataSource(config);
    }

    private ConnectionPool() {
        // Classe utilitária, construtor privado
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}