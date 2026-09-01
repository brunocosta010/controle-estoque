package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.DoadorDAO;
import br.com.controledoacoes.model.Doador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TelaDoadores extends JFrame {

    private final JTextField txtNome = new JTextField(28);
    private final JTextField txtTelefone = new JTextField(18);
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
        setTitle("Cadastro de Doadores");
        setSize(760, 520);
        setMinimumSize(new Dimension(680, 460));
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
        area.setBorder(BorderFactory.createTitledBorder("Dados do doador"));

        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        campos.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        campos.add(txtNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        campos.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        campos.add(txtTelefone, gbc);

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
        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(390);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(210);
    }

    private void cadastrar() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        if (nome.isBlank()) {
            mostrarAviso("Informe o nome do doador.");
            txtNome.requestFocusInWindow();
            return;
        }
        try {
            dao.inserir(new Doador(nome, telefone));
            carregarTabela();
            limparCampos();
            mostrarSucesso("Doador cadastrado com sucesso.");
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void atualizar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarAviso("Selecione um doador na tabela.");
            return;
        }
        String nome = txtNome.getText().trim();
        if (nome.isBlank()) {
            mostrarAviso("Informe o nome do doador.");
            txtNome.requestFocusInWindow();
            return;
        }
        int id = (int) modelo.getValueAt(linha, 0);
        try {
            dao.atualizar(new Doador(id, nome, txtTelefone.getText().trim()));
            carregarTabela();
            limparCampos();
            mostrarSucesso("Doador atualizado com sucesso.");
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarAviso("Selecione um doador na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o doador selecionado?", "Confirmação",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.excluir((int) modelo.getValueAt(linha, 0));
            carregarTabela();
            limparCampos();
            mostrarSucesso("Doador excluído com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível excluir. O doador pode possuir doações registradas.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            for (Doador doador : dao.listar()) {
                modelo.addRow(new Object[]{doador.getId(), doador.getNome(), doador.getTelefone()});
            }
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
            Object telefone = modelo.getValueAt(linha, 2);
            txtTelefone.setText(telefone == null ? "" : telefone.toString());
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtTelefone.setText("");
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
