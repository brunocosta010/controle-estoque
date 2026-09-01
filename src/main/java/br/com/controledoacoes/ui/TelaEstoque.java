package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.ItemDAO;
import br.com.controledoacoes.model.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class TelaEstoque extends JFrame {

    private final JTextField txtPesquisa = new JTextField(24);
    private final JLabel lblTotalItens = new JLabel("Total de itens cadastrados: 0");
    private final JLabel lblQuantidadeTotal = new JLabel("Quantidade total em estoque: 0");
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
        setSize(760, 520);
        setMinimumSize(new Dimension(650, 450));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel conteudo = new JPanel(new BorderLayout(12, 12));
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        conteudo.add(criarPesquisa(), BorderLayout.NORTH);
        conteudo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        conteudo.add(criarResumo(), BorderLayout.SOUTH);
        configurarTabela();
        setContentPane(conteudo);
        carregarTabela("");
    }

    private JPanel criarPesquisa() {
        JPanel topo = new JPanel(new GridBagLayout());
        topo.setBorder(BorderFactory.createTitledBorder("Consulta de estoque"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topo.add(new JLabel("Pesquisar:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topo.add(txtPesquisa, gbc);
        JButton btnPesquisar = new JButton("Pesquisar");
        JButton btnAtualizar = new JButton("Atualizar");
        btnPesquisar.addActionListener(e -> carregarTabela(txtPesquisa.getText().trim()));
        btnAtualizar.addActionListener(e -> {
            txtPesquisa.setText("");
            carregarTabela("");
            txtPesquisa.requestFocusInWindow();
        });
        txtPesquisa.addActionListener(e -> btnPesquisar.doClick());
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2;
        topo.add(btnPesquisar, gbc);
        gbc.gridx = 3;
        topo.add(btnAtualizar, gbc);
        return topo;
    }

    private JPanel criarResumo() {
        JPanel resumo = new JPanel(new GridLayout(1, 2, 20, 0));
        resumo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(190, 190, 190)),
                BorderFactory.createEmptyBorder(12, 5, 2, 5)));
        Font fonte = new Font("SansSerif", Font.BOLD, 14);
        lblTotalItens.setFont(fonte);
        lblQuantidadeTotal.setFont(fonte);
        resumo.add(lblTotalItens);
        resumo.add(lblQuantidadeTotal);
        return resumo;
    }

    private void configurarTabela() {
        tabela.setRowHeight(28);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(360);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(230);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(110);
    }

    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);
        try {
            List<Item> itens = dao.listar();
            int quantidadeTotal = 0;
            String termo = filtro.toLowerCase(Locale.ROOT);
            for (Item item : itens) {
                quantidadeTotal += item.getQuantidadeAtual();
                if (termo.isBlank()
                        || item.getNome().toLowerCase(Locale.ROOT).contains(termo)
                        || item.getCategoria().toLowerCase(Locale.ROOT).contains(termo)) {
                    modelo.addRow(new Object[]{item.getNome(), item.getCategoria(), item.getQuantidadeAtual()});
                }
            }
            lblTotalItens.setText("Total de itens cadastrados: " + itens.size());
            lblQuantidadeTotal.setText("Quantidade total em estoque: " + quantidadeTotal);
        } catch (SQLException e) {
            String mensagem = e.getMessage() == null ? "Não foi possível carregar o estoque." : e.getMessage();
            JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
