package br.com.controledoacoes.dao;

import br.com.controledoacoes.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoacaoDAO {

    private final ItemDAO itemDAO = new ItemDAO();

    public void registrar(int doadorId, int itemId, int quantidade, String data) throws SQLException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        String sql = """
                INSERT INTO doacao (doador_id, item_id, quantidade, data)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);

            try {
                int quantidadeAtual = itemDAO.obterQuantidadeAtual(itemId, conn);

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, doadorId);
                    ps.setInt(2, itemId);
                    ps.setInt(3, quantidade);
                    ps.setString(4, data);
                    ps.executeUpdate();
                }

                itemDAO.atualizarQuantidade(itemId, quantidadeAtual + quantidade, conn);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();

                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }

                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
