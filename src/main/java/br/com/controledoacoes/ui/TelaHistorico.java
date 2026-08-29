package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.HistoricoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TelaHistorico extends JFrame {

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Data", "Tipo", "Item", "Quantidade", "Doador/Destino"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);
    private final HistoricoDAO dao = new HistoricoDAO();

    public TelaHistorico() {
        setTitle("Histórico de Movimentações");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarTabela());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnAtualizar);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        carregarTabela();
    }

    private void carregarTabela() {
        modelo.setRowCount(0);

        try {
            for (Object[] linha : dao.listar()) {
                modelo.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
