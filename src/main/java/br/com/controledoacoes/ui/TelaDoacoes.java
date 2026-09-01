package br.com.controledoacoes.ui;

import br.com.controledoacoes.dao.DoacaoDAO;
import br.com.controledoacoes.dao.DoadorDAO;
import br.com.controledoacoes.dao.ItemDAO;
import br.com.controledoacoes.model.Doador;
import br.com.controledoacoes.model.Item;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelaDoacoes extends JFrame {

    private final JComboBox<Doador> cbDoador = new JComboBox<>();
    private final JComboBox<Item> cbItem = new JComboBox<>();
    private final JSpinner spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextField txtData = new JTextField(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 12);
    private final DoadorDAO doadorDAO = new DoadorDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final DoacaoDAO doacaoDAO = new DoacaoDAO();

    public TelaDoacoes() {
        setTitle("Nova Doação");
        setSize(560, 390);
        setMinimumSize(new Dimension(500, 350));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel conteudo = new JPanel(new BorderLayout(12, 18));
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JLabel titulo = new JLabel("Registrar nova doação");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        conteudo.add(titulo, BorderLayout.NORTH);
        conteudo.add(criarFormulario(), BorderLayout.CENTER);
        setContentPane(conteudo);
        carregarCombos();
    }

    private JPanel criarFormulario() {
        JPanel area = new JPanel(new BorderLayout(10, 18));
        area.setBorder(BorderFactory.createTitledBorder("Dados da entrada"));
        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampo(campos, gbc, 0, "Doador:", cbDoador);
        adicionarCampo(campos, gbc, 1, "Item:", cbItem);
        adicionarCampo(campos, gbc, 2, "Quantidade:", spQuantidade);
        adicionarCampo(campos, gbc, 3, "Data:", txtData);

        JButton btnRegistrar = new JButton("Registrar Doação");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrar.setPreferredSize(new Dimension(180, 36));
        JButton btnAtualizar = new JButton("Atualizar listas");
        btnRegistrar.addActionListener(e -> registrar());
        btnAtualizar.addActionListener(e -> carregarCombos());
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

    private void carregarCombos() {
        Object doadorSelecionado = cbDoador.getSelectedItem();
        Object itemSelecionado = cbItem.getSelectedItem();
        cbDoador.removeAllItems();
        cbItem.removeAllItems();
        try {
            for (Doador doador : doadorDAO.listar()) cbDoador.addItem(doador);
            for (Item item : itemDAO.listar()) cbItem.addItem(item);
            restaurarSelecao(cbDoador, doadorSelecionado);
            restaurarSelecao(cbItem, itemSelecionado);
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private <T> void restaurarSelecao(JComboBox<T> combo, Object anterior) {
        if (anterior == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).toString().equals(anterior.toString())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void registrar() {
        Doador doador = (Doador) cbDoador.getSelectedItem();
        Item item = (Item) cbItem.getSelectedItem();
        if (doador == null || item == null) {
            mostrarAviso("Cadastre pelo menos um doador e um item antes de registrar a doação.");
            return;
        }
        String data = txtData.getText().trim();
        if (data.isBlank()) {
            mostrarAviso("Informe a data da doação.");
            txtData.requestFocusInWindow();
            return;
        }
        try {
            doacaoDAO.registrar(doador.getId(), item.getId(), (int) spQuantidade.getValue(), data);
            JOptionPane.showMessageDialog(this, "Doação registrada com sucesso.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            spQuantidade.setValue(1);
            carregarCombos();
            cbDoador.requestFocusInWindow();
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
