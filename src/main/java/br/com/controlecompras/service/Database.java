package br.com.controlecompras.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class Database {

    private static String url;

    @Value("${app.database.url}")
    private String configuredUrl;

    @PostConstruct
    public void init() {
        url = configuredUrl;
        inicializarBanco();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void inicializarBanco() {
        String sql = """
                CREATE TABLE IF NOT EXISTS compras (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    item TEXT NOT NULL,
                    valor NUMERIC NOT NULL,
                    data TEXT NOT NULL,
                    solicitado_por TEXT NOT NULL,
                    setor TEXT NOT NULL
                )
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco de dados.", e);
        }
    }
}