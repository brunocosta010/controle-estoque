package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.DoadorDAO;
import br.com.controledoacoes.model.Doador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TelaDoadores extends JFrame {

    private final JTextField txtNome = new JTextField();
    private final JTextField txtTelefone = new JTextField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Telefone"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modelo);
    private final DoadorDAO dao = new DoadorDAO();

    public TelaDoadores() {
        setTitle("Doadores");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridLayout(3, 2, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formulario.add(new JLabel("Nome:"));
        formulario.add(txtNome);
        formulario.add(new JLabel("Telefone:"));
        formulario.add(txtTelefone);

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
        String telefone = txtTelefone.getText().trim();

        if (nome.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do doador.");
            return;
        }

        try {
            dao.inserir(new Doador(nome, telefone));
            limparCampos();
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Doador cadastrado com sucesso.");
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void atualizar() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um doador.");
            return;
        }

        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do doador.");
            return;
        }

        int id = (int) modelo.getValueAt(linha, 0);

        try {
            dao.atualizar(new Doador(id, nome, telefone));
            carregarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Doador atualizado com sucesso.");
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um doador.");
            return;
        }

        int id = (int) modelo.getValueAt(linha, 0);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja excluir o doador selecionado?",
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
                    "Não foi possível excluir. O doador pode possuir doações registradas.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void carregarTabela() {
        modelo.setRowCount(0);

        try {
            List<Doador> doadores = dao.listar();

            for (Doador d : doadores) {
                modelo.addRow(new Object[]{d.getId(), d.getNome(), d.getTelefone()});
            }
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();

        if (linha != -1) {
            txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
            txtTelefone.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtTelefone.setText("");
        tabela.clearSelection();
        txtNome.requestFocus();
    }

    private void mostrarErro(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
