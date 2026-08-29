package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.ItemDAO;
import br.com.controledoacoes.model.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TelaEstoque extends JFrame {

    private final JTextField txtPesquisa = new JTextField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Item", "Categoria", "Quantidade"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);
    private final ItemDAO dao = new ItemDAO();

    public TelaEstoque() {
        setTitle("Estoque Atual");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topo = new JPanel(new BorderLayout(8, 8));
        topo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnPesquisar = new JButton("Pesquisar");
        JButton btnAtualizar = new JButton("Atualizar");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnPesquisar);
        botoes.add(btnAtualizar);

        topo.add(new JLabel("Pesquisar item:"), BorderLayout.WEST);
        topo.add(txtPesquisa, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnPesquisar.addActionListener(e -> carregarTabela(txtPesquisa.getText().trim()));
        btnAtualizar.addActionListener(e -> {
            txtPesquisa.setText("");
            carregarTabela("");
        });

        carregarTabela("");
    }

    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);

        try {
            for (Item item : dao.listar()) {
                if (filtro.isBlank()
                        || item.getNome().toLowerCase().contains(filtro.toLowerCase())
                        || item.getCategoria().toLowerCase().contains(filtro.toLowerCase())) {

                    modelo.addRow(new Object[]{
                            item.getNome(),
                            item.getCategoria(),
                            item.getQuantidadeAtual()
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
