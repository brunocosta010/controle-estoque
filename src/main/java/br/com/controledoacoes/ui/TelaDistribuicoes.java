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
    private final JLabel lblDisponivel = new JLabel("0");
    private final JSpinner spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextField txtDestino = new JTextField();
    private final JTextField txtData = new JTextField(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    );

    private final ItemDAO itemDAO = new ItemDAO();
    private final DistribuicaoDAO distribuicaoDAO = new DistribuicaoDAO();

    public TelaDistribuicoes() {
        setTitle("Distribuição");
        setSize(460, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new GridLayout(6, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        painel.add(new JLabel("Item:"));
        painel.add(cbItem);
        painel.add(new JLabel("Quantidade disponível:"));
        painel.add(lblDisponivel);
        painel.add(new JLabel("Quantidade a distribuir:"));
        painel.add(spQuantidade);
        painel.add(new JLabel("Destino:"));
        painel.add(txtDestino);
        painel.add(new JLabel("Data:"));
        painel.add(txtData);

        JButton btnRegistrar = new JButton("Registrar Distribuição");
        JButton btnAtualizar = new JButton("Atualizar lista");

        painel.add(btnRegistrar);
        painel.add(btnAtualizar);

        cbItem.addActionListener(e -> atualizarDisponivel());
        btnRegistrar.addActionListener(e -> registrar());
        btnAtualizar.addActionListener(e -> carregarItens());

        add(painel);

        carregarItens();
    }

    private void carregarItens() {
        cbItem.removeAllItems();

        try {
            for (Item item : itemDAO.listar()) {
                cbItem.addItem(item);
            }

            atualizarDisponivel();
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void atualizarDisponivel() {
        Item item = (Item) cbItem.getSelectedItem();
        lblDisponivel.setText(item == null ? "0" : String.valueOf(item.getQuantidadeAtual()));
    }

    private void registrar() {
        Item item = (Item) cbItem.getSelectedItem();

        if (item == null) {
            JOptionPane.showMessageDialog(this, "Cadastre pelo menos um item.");
            return;
        }

        int quantidade = (int) spQuantidade.getValue();
        String destino = txtDestino.getText().trim();
        String data = txtData.getText().trim();

        try {
            distribuicaoDAO.registrar(item.getId(), quantidade, destino, data);
            JOptionPane.showMessageDialog(this, "Distribuição registrada com sucesso.");
            spQuantidade.setValue(1);
            txtDestino.setText("");
            carregarItens();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void mostrarErro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
