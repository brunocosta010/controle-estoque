package br.com.controledoacoes.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String DB_PATH = "data/doacoes.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private Database() {
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializar() {
        File pasta = new File("data");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        String sqlDoador = """
                CREATE TABLE IF NOT EXISTS doador (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    telefone TEXT
                )
                """;

        String sqlItem = """
                CREATE TABLE IF NOT EXISTS item (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL UNIQUE,
                    categoria TEXT NOT NULL,
                    quantidade_atual INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0)
                )
                """;

        String sqlDoacao = """
                CREATE TABLE IF NOT EXISTS doacao (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    doador_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL,
                    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
                    data TEXT NOT NULL,
                    FOREIGN KEY (doador_id) REFERENCES doador(id),
                    FOREIGN KEY (item_id) REFERENCES item(id)
                )
                """;

        String sqlDistribuicao = """
                CREATE TABLE IF NOT EXISTS distribuicao (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    item_id INTEGER NOT NULL,
                    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
                    destino TEXT NOT NULL,
                    data TEXT NOT NULL,
                    FOREIGN KEY (item_id) REFERENCES item(id)
                )
                """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute(sqlDoador);
            stmt.execute(sqlItem);
            stmt.execute(sqlDoacao);
            stmt.execute(sqlDistribuicao);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar o banco de dados.", e);
        }
    }
}
