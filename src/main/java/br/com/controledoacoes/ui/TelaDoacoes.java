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
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    );

    private final DoadorDAO doadorDAO = new DoadorDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final DoacaoDAO doacaoDAO = new DoacaoDAO();

    public TelaDoacoes() {
        setTitle("Nova Doação");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new GridLayout(5, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        painel.add(new JLabel("Doador:"));
        painel.add(cbDoador);
        painel.add(new JLabel("Item:"));
        painel.add(cbItem);
        painel.add(new JLabel("Quantidade:"));
        painel.add(spQuantidade);
        painel.add(new JLabel("Data:"));
        painel.add(txtData);

        JButton btnRegistrar = new JButton("Registrar Doação");
        JButton btnAtualizar = new JButton("Atualizar listas");

        painel.add(btnRegistrar);
        painel.add(btnAtualizar);

        btnRegistrar.addActionListener(e -> registrar());
        btnAtualizar.addActionListener(e -> carregarCombos());

        add(painel);

        carregarCombos();
    }

    private void carregarCombos() {
        cbDoador.removeAllItems();
        cbItem.removeAllItems();

        try {
            for (Doador doador : doadorDAO.listar()) {
                cbDoador.addItem(doador);
            }

            for (Item item : itemDAO.listar()) {
                cbItem.addItem(item);
            }
        } catch (SQLException e) {
            mostrarErro(e);
        }
    }

    private void registrar() {
        Doador doador = (Doador) cbDoador.getSelectedItem();
        Item item = (Item) cbItem.getSelectedItem();

        if (doador == null || item == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cadastre pelo menos um doador e um item antes de registrar a doação."
            );
            return;
        }

        int quantidade = (int) spQuantidade.getValue();
        String data = txtData.getText().trim();

        try {
            doacaoDAO.registrar(doador.getId(), item.getId(), quantidade, data);
            JOptionPane.showMessageDialog(this, "Doação registrada com sucesso.");
            spQuantidade.setValue(1);
            carregarCombos();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void mostrarErro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
