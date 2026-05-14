package br.com.softhouse.dende.repositories.util;

import br.com.dende.softhouse.annotations.Value; // O import exato que você achou!
import lombok.Getter;

@Getter
public class ConfigProperties {

    @Value(key = "datasource.url")
    private String url;

    @Value(key = "datasource.username")
    private String username;

    @Value(key = "datasource.password")
    private String password;

    @Value(key = "datasource.driver-class-name")
    private String driverClassName;

    @Value(key = "datasource.hikari.maximum-pool-size")
    private int maximumPoolSize;

    @Value(key = "datasource.hikari.minimum-idle")
    private int minimumIdle;

    @Value(key = "datasource.hikari.connection-timeout")
    private long connectionTimeout;
}