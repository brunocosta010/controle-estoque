package br.com.controledoacoes.ui;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Controle de Doações");
        setSize(520, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("SISTEMA DE CONTROLE DE DOAÇÕES", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        painel.add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(3, 2, 12, 12));

        JButton btnDoadores = new JButton("Doadores");
        JButton btnItens = new JButton("Itens");
        JButton btnDoacoes = new JButton("Nova Doação");
        JButton btnDistribuicoes = new JButton("Distribuição");
        JButton btnEstoque = new JButton("Estoque");
        JButton btnHistorico = new JButton("Histórico");

        botoes.add(btnDoadores);
        botoes.add(btnItens);
        botoes.add(btnDoacoes);
        botoes.add(btnDistribuicoes);
        botoes.add(btnEstoque);
        botoes.add(btnHistorico);

        painel.add(botoes, BorderLayout.CENTER);

        btnDoadores.addActionListener(e -> new TelaDoadores().setVisible(true));
        btnItens.addActionListener(e -> new TelaItens().setVisible(true));
        btnDoacoes.addActionListener(e -> new TelaDoacoes().setVisible(true));
        btnDistribuicoes.addActionListener(e -> new TelaDistribuicoes().setVisible(true));
        btnEstoque.addActionListener(e -> new TelaEstoque().setVisible(true));
        btnHistorico.addActionListener(e -> new TelaHistorico().setVisible(true));

        add(painel);
    }
}
