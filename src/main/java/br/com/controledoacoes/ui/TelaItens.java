package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.ItemDAO;
import br.com.controledoacoes.model.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TelaItens extends JFrame {

    private final JTextField txtNome = new JTextField(28);
    private final JComboBox<String> cbCategoria = new JComboBox<>(new String[]{
            "Alimentos", "Roupas", "Higiene", "Limpeza", "Móveis", "Utensílios", "Outros"
    });
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Categoria", "Estoque"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final ItemDAO dao = new ItemDAO();

    public TelaItens() {
        setTitle("Cadastro de Itens");
        setSize(800, 540);
        setMinimumSize(new Dimension(700, 470));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel conteudo = new JPanel(new BorderLayout(12, 12));
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        conteudo.add(criarFormulario(), BorderLayout.NORTH);
        conteudo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        configurarTabela();

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });
        setContentPane(conteudo);
        carregarTabela();
    }

    private JPanel criarFormulario() {
        JPanel area = new JPanel(new BorderLayout(10, 12));
        area.setBorder(BorderFactory.createTitledBorder("Dados do item"));
        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; campos.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; campos.add(txtNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; campos.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; campos.add(cbCategoria, gbc);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir selecionado");
        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        botoes.add(btnCadastrar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);

        area.add(campos, BorderLayout.CENTER);
        area.add(botoes, BorderLayout.SOUTH);
        return area;
    }

    private void configurarTabela() {
        tabela.setRowHeight(28);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(350);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);
    }

    private void cadastrar() {
        String nome = txtNome.getText().trim();
        String categoria = (String) cbCategoria.getSelectedItem();
        if (!camposValidos(nome, categoria)) return;
        try {
            dao.inserir(new Item(nome, categoria));
            carregarTabela();
            limparCampos();
            mostrarSucesso("Item cadastrado com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível cadastrar o item. Verifique se o nome já existe.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarAviso("Selecione um item na tabela.");
            return;
        }
        String nome = txtNome.getText().trim();
        String categoria = (String) cbCategoria.getSelectedItem();
        if (!camposValidos(nome, categoria)) return;
        int id = (int) modelo.getValueAt(linha, 0);
        int estoque = (int) modelo.getValueAt(linha, 3);
        try {
            dao.atualizar(new Item(id, nome, categoria, estoque));
            carregarTabela();
            limparCampos();
            mostrarSucesso("Item atualizado com sucesso.");
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private boolean camposValidos(String nome, String categoria) {
        if (nome.isBlank()) {
            mostrarAviso("Informe o nome do item.");
            txtNome.requestFocusInWindow();
            return false;
        }
        if (categoria == null || categoria.isBlank()) {
            mostrarAviso("Selecione uma categoria.");
            cbCategoria.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarAviso("Selecione um item na tabela.");
            return;
        }
        int estoque = (int) modelo.getValueAt(linha, 3);
        if (estoque > 0) {
            mostrarAviso("Não é possível excluir um item que ainda possui estoque.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o item selecionado?", "Confirmação",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir((int) modelo.getValueAt(linha, 0));
            carregarTabela();
            limparCampos();
            mostrarSucesso("Item excluído com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível excluir. O item pode possuir movimentações registradas.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            for (Item item : dao.listar()) {
                modelo.addRow(new Object[]{item.getId(), item.getNome(), item.getCategoria(), item.getQuantidadeAtual()});
            }
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
            cbCategoria.setSelectedItem(String.valueOf(modelo.getValueAt(linha, 2)));
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        if (cbCategoria.getItemCount() > 0) cbCategoria.setSelectedIndex(0);
        tabela.clearSelection();
        txtNome.requestFocusInWindow();
    }

    private void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErro(Exception e) {
        String mensagem = e.getMessage() == null ? "Ocorreu um erro inesperado." : e.getMessage();
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
