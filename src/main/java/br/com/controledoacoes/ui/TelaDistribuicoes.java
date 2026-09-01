package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.DistribuicaoDAO;
import br.com.controledoacoes.dao.ItemDAO;
import br.com.controledoacoes.model.Item;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelaDistribuicoes extends JFrame {

    private final JComboBox<Item> cbItem = new JComboBox<>();
    private final JLabel lblDisponivel = new JLabel("0 unidade(s)");
    private final JSpinner spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextField txtDestino = new JTextField(24);
    private final JTextField txtData = new JTextField(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 12);
    private final ItemDAO itemDAO = new ItemDAO();
    private final DistribuicaoDAO distribuicaoDAO = new DistribuicaoDAO();

    public TelaDistribuicoes() {
        setTitle("Distribuição");
        setSize(590, 440);
        setMinimumSize(new Dimension(520, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel conteudo = new JPanel(new BorderLayout(12, 18));
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JLabel titulo = new JLabel("Registrar distribuição de itens");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        conteudo.add(titulo, BorderLayout.NORTH);
        conteudo.add(criarFormulario(), BorderLayout.CENTER);
        setContentPane(conteudo);
        carregarItens();
    }

    private JPanel criarFormulario() {
        JPanel area = new JPanel(new BorderLayout(10, 18));
        area.setBorder(BorderFactory.createTitledBorder("Dados da saída"));
        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampo(campos, gbc, 0, "Item:", cbItem);
        lblDisponivel.setFont(new Font("SansSerif", Font.BOLD, 14));
        adicionarCampo(campos, gbc, 1, "Quantidade disponível:", lblDisponivel);
        adicionarCampo(campos, gbc, 2, "Quantidade a distribuir:", spQuantidade);
        adicionarCampo(campos, gbc, 3, "Destino:", txtDestino);
        adicionarCampo(campos, gbc, 4, "Data:", txtData);

        JButton btnRegistrar = new JButton("Registrar Distribuição");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrar.setPreferredSize(new Dimension(205, 36));
        JButton btnAtualizar = new JButton("Atualizar lista");
        btnRegistrar.addActionListener(e -> registrar());
        btnAtualizar.addActionListener(e -> carregarItens());
        cbItem.addActionListener(e -> atualizarDisponivel());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        botoes.add(btnAtualizar);
        botoes.add(btnRegistrar);

        area.add(campos, BorderLayout.CENTER);
        area.add(botoes, BorderLayout.SOUTH);
        return area;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha,
                                String rotulo, JComponent componente) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.weightx = 0;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        painel.add(componente, gbc);
    }

    private void carregarItens() {
        Object anterior = cbItem.getSelectedItem();
        cbItem.removeAllItems();
        try {
            for (Item item : itemDAO.listar()) cbItem.addItem(item);
            if (anterior != null) {
                for (int i = 0; i < cbItem.getItemCount(); i++) {
                    if (cbItem.getItemAt(i).toString().equals(anterior.toString())) {
                        cbItem.setSelectedIndex(i);
                        break;
                    }
                }
            }
            atualizarDisponivel();
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void atualizarDisponivel() {
        Item item = (Item) cbItem.getSelectedItem();
        int quantidade = item == null ? 0 : item.getQuantidadeAtual();
        lblDisponivel.setText(quantidade + " unidade(s)");
    }

    private void registrar() {
        Item item = (Item) cbItem.getSelectedItem();
        if (item == null) {
            mostrarAviso("Cadastre pelo menos um item antes de registrar a distribuição.");
            return;
        }
        String destino = txtDestino.getText().trim();
        String data = txtData.getText().trim();
        if (destino.isBlank()) {
            mostrarAviso("Informe o destino da distribuição.");
            txtDestino.requestFocusInWindow();
            return;
        }
        if (data.isBlank()) {
            mostrarAviso("Informe a data da distribuição.");
            txtData.requestFocusInWindow();
            return;
        }
        try {
            distribuicaoDAO.registrar(item.getId(), (int) spQuantidade.getValue(), destino, data);
            JOptionPane.showMessageDialog(this, "Distribuição registrada com sucesso.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            spQuantidade.setValue(1);
            txtDestino.setText("");
            carregarItens();
            cbItem.requestFocusInWindow();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErro(Exception e) {
        String mensagem = e.getMessage() == null ? "Ocorreu um erro inesperado." : e.getMessage();
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
