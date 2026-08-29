package br.com.controledoacoes.dao;

import br.com.controledoacoes.config.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoricoDAO {

    public List<Object[]> listar() throws SQLException {
        List<Object[]> historico = new ArrayList<>();

        String sql = """
                SELECT data, 'ENTRADA' AS tipo, i.nome AS item, d.quantidade AS quantidade,
                       doador.nome AS detalhe
                FROM doacao d
                JOIN item i ON i.id = d.item_id
                JOIN doador ON doador.id = d.doador_id

                UNION ALL

                SELECT data, 'SAÍDA' AS tipo, i.nome AS item, dist.quantidade AS quantidade,
                       dist.destino AS detalhe
                FROM distribuicao dist
                JOIN item i ON i.id = dist.item_id

                ORDER BY data DESC
                """;

        try (Connection conn = Database.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                historico.add(new Object[]{
                        rs.getString("data"),
                        rs.getString("tipo"),
                        rs.getString("item"),
                        rs.getInt("quantidade"),
                        rs.getString("detalhe")
                });
            }
        }

        return historico;
    }
}
