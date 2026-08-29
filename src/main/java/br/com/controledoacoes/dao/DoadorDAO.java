package br.com.controledoacoes.dao;

import br.com.controledoacoes.config.Database;
import br.com.controledoacoes.model.Doador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoadorDAO {

    public void inserir(Doador doador) throws SQLException {
        String sql = "INSERT INTO doador (nome, telefone) VALUES (?, ?)";

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getTelefone());
            ps.executeUpdate();
        }
    }

    public void atualizar(Doador doador) throws SQLException {
        String sql = "UPDATE doador SET nome = ?, telefone = ? WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getTelefone());
            ps.setInt(3, doador.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM doador WHERE id = ?";

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Doador> listar() throws SQLException {
        List<Doador> doadores = new ArrayList<>();
        String sql = "SELECT id, nome, telefone FROM doador ORDER BY nome";

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                doadores.add(new Doador(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone")
                ));
            }
        }

        return doadores;
    }
}
