package br.com.controledoacoes.dao;

import br.com.controledoacoes.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DistribuicaoDAO {

    private final ItemDAO itemDAO = new ItemDAO();

    public void registrar(int itemId, int quantidade, String destino, String data) throws SQLException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("Informe o destino da distribuição.");
        }

        String sql = """
                INSERT INTO distribuicao (item_id, quantidade, destino, data)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = Database.conectar()) {
            conn.setAutoCommit(false);

            try {
                int quantidadeAtual = itemDAO.obterQuantidadeAtual(itemId, conn);

                if (quantidade > quantidadeAtual) {
                    throw new IllegalArgumentException(
                            "Quantidade insuficiente em estoque. Disponível: " + quantidadeAtual
                    );
                }

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, itemId);
                    ps.setInt(2, quantidade);
                    ps.setString(3, destino);
                    ps.setString(4, data);
                    ps.executeUpdate();
                }

                itemDAO.atualizarQuantidade(itemId, quantidadeAtual - quantidade, conn);

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
