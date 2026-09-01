package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.HistoricoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TelaHistorico extends JFrame {

    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Todos", "Entradas", "Saídas"});
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
        setSize(900, 540);
        setMinimumSize(new Dimension(760, 460));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel conteudo = new JPanel(new BorderLayout(12, 12));
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        conteudo.add(criarBarraSuperior(), BorderLayout.NORTH);
        conteudo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        configurarTabela();
        setContentPane(conteudo);
        carregarTabela();
    }

    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        barra.setBorder(BorderFactory.createTitledBorder("Filtros"));
        JButton btnAtualizar = new JButton("Atualizar");
        cbTipo.addActionListener(e -> carregarTabela());
        btnAtualizar.addActionListener(e -> carregarTabela());
        barra.add(new JLabel("Tipo:"));
        barra.add(cbTipo);
        barra.add(btnAtualizar);
        return barra;
    }

    private void configurarTabela() {
        tabela.setRowHeight(28);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(95);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(260);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(280);
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        String filtro = (String) cbTipo.getSelectedItem();
        try {
            for (Object[] linha : dao.listar()) {
                String tipo = String.valueOf(linha[1]);
                if ("Todos".equals(filtro)
                        || ("Entradas".equals(filtro) && "ENTRADA".equals(tipo))
                        || ("Saídas".equals(filtro) && "SAÍDA".equals(tipo))) {
                    modelo.addRow(linha);
                }
            }
        } catch (SQLException e) {
            String mensagem = e.getMessage() == null ? "Não foi possível carregar o histórico." : e.getMessage();
            JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
