package br.com.controledoacoes.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import br.com.controledoacoes.dao.ItemDAO;
import br.com.controledoacoes.model.Item;

public class TelaItens extends JFrame {

    private final JTextField txtNome = new JTextField();
    private final JComboBox<String> cbCategoria = new JComboBox<>(new String[]{
        "Alimentos",
        "Roupas",
        "Higiene",
        "Limpeza",
        "Móveis",
        "Utensílios",
        "Outros"
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
        setTitle("Itens");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridLayout(3, 2, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formulario.add(new JLabel("Nome:"));
        formulario.add(txtNome);
        formulario.add(new JLabel("Categoria:"));
        formulario.add(cbCategoria);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        formulario.add(btnCadastrar);
        formulario.add(btnAtualizar);

        JButton btnExcluir = new JButton("Excluir selecionado");

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(btnExcluir, BorderLayout.SOUTH);

        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());

        tabela.getSelectionModel().addListSelectionListener(e -> preencherCampos());

        carregarTabela();
    }

    private void cadastrar() {
        String nome = txtNome.getText().trim();
        String categoria = (String) cbCategoria.getSelectedItem();

        if (nome.isBlank() || categoria.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe nome e categoria.");
            return;
        }

        try {
            dao.inserir(new Item(nome, categoria));
            limparCampos();
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Item cadastrado com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível cadastrar o item. Verifique se o nome já existe.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void atualizar() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item.");
            return;
        }

        String nome = txtNome.getText().trim();
        String categoria = (String) cbCategoria.getSelectedItem();

        if (nome.isBlank() || categoria.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe nome e categoria.");
            return;
        }

        int id = (int) modelo.getValueAt(linha, 0);
        int estoque = (int) modelo.getValueAt(linha, 3);

        try {
            dao.atualizar(new Item(id, nome, categoria, estoque));
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Item atualizado com sucesso.");
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item.");
            return;
        }

        int id = (int) modelo.getValueAt(linha, 0);
        int estoque = (int) modelo.getValueAt(linha, 3);

        if (estoque > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não exclua itens que ainda possuem estoque.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja excluir o item selecionado?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.excluir(id);
            carregarTabela();
            limparCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir. O item pode possuir movimentações registradas.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void carregarTabela() {
        modelo.setRowCount(0);

        try {
            for (Item item : dao.listar()) {
                modelo.addRow(new Object[]{
                        item.getId(),
                        item.getNome(),
                        item.getCategoria(),
                        item.getQuantidadeAtual()
                });
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
        cbCategoria.setSelectedIndex(0);
        tabela.clearSelection();
        txtNome.requestFocus();
    }

    private void mostrarErro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
