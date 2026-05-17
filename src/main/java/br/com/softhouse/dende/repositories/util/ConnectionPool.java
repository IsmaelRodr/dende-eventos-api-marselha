package br.com.softhouse.dende.repositories.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static HikariDataSource dataSource;

    private ConnectionPool() {
        // Construtor privado – classe utilitária
    }

    private static void initializeIfNeeded() {
        if (dataSource != null) return;

        ConfigProperties props = new ConfigProperties();
        // Se a injeção ainda não ocorreu, os campos estarão nulos
        if (props.getUrl() == null || props.getUsername() == null) {
            throw new IllegalStateException(
                    "Configurações de banco de dados não foram injetadas. Verifique o framework Dendê."
            );
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setDriverClassName(props.getDriverClassName());
        config.setMaximumPoolSize(props.getMaximumPoolSize());
        config.setMinimumIdle(props.getMinimumIdle());
        config.setConnectionTimeout(props.getConnectionTimeout());

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        initializeIfNeeded(); // garante que o pool seja criado apenas no primeiro uso
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}