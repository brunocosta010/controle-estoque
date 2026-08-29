package br.com.controledoacoes.dao;

import br.com.controledoacoes.config.Database;
import br.com.controledoacoes.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public void inserir(Item item) throws SQLException {
        String sql = "INSERT INTO item (nome, categoria, quantidade_atual) VALUES (?, ?, 0)";

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getNome());
            ps.setString(2, item.getCategoria());
            ps.executeUpdate();
        }
    }

    public void atualizar(Item item) throws SQLException {
        String sql = "UPDATE item SET nome = ?, categoria = ? WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getNome());
            ps.setString(2, item.getCategoria());
            ps.setInt(3, item.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM item WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Item> listar() throws SQLException {
        List<Item> itens = new ArrayList<>();
        String sql = """
                SELECT id, nome, categoria, quantidade_atual
                FROM item
                ORDER BY nome
                """;

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                itens.add(new Item(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getInt("quantidade_atual")
                ));
            }
        }

        return itens;
    }

    public int obterQuantidadeAtual(int itemId, Connection conn) throws SQLException {
        String sql = "SELECT quantidade_atual FROM item WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantidade_atual");
                }
            }
        }

        throw new SQLException("Item não encontrado.");
    }

    public void atualizarQuantidade(int itemId, int novaQuantidade, Connection conn) throws SQLException {
        String sql = "UPDATE item SET quantidade_atual = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, novaQuantidade);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }
}
