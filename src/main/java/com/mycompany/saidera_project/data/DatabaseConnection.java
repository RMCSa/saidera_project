package com.mycompany.saidera_project.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gerenciador de conexão com o banco de dados PostgreSQL.
 * Carrega dinamicamente as credenciais a partir do arquivo db.properties.
 */
public class DatabaseConnection {

    private static String url;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Aviso: db.properties nao encontrado. Usando configuracoes default.");
                url = "jdbc:postgresql://localhost:5432/postgres";
                user = "postgres";
                password = "123456";
            } else {
                props.load(input);
                url = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/postgres");
                user = props.getProperty("db.user", "postgres");
                password = props.getProperty("db.password", "123456");
            }
            
            // Garantir que o driver do PostgreSQL esteja carregado
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Fallback defaults
            url = "jdbc:postgresql://localhost:5432/postgres";
            user = "postgres";
            password = "123456";
        }
    }

    /**
     * Estabelece e retorna uma conexao ativa com o banco de dados PostgreSQL.
     * 
     * @return Conexao ativa JDBC
     * @throws SQLException Caso ocorra erro na conexao
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
