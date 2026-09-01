package br.com.controledoacoes.ui;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private static final Font FONTE_BOTAO = new Font("SansSerif", Font.PLAIN, 16);

    public TelaPrincipal() {
        setTitle("Sistema de Controle de Doações");
        setSize(680, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(620, 430));

        JPanel painel = new JPanel(new BorderLayout(20, 28));
        painel.setBorder(BorderFactory.createEmptyBorder(35, 45, 40, 45));

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sistema de Controle de Doações");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Cadastro, estoque e distribuição de itens");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitulo.setForeground(new Color(80, 80, 80));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(8));
        cabecalho.add(subtitulo);

        JPanel botoes = new JPanel(new GridLayout(3, 2, 16, 16));
        JButton btnDoadores = criarBotao("Doadores");
        JButton btnItens = criarBotao("Itens");
        JButton btnDoacoes = criarBotao("Nova Doação");
        JButton btnDistribuicoes = criarBotao("Distribuição");
        JButton btnEstoque = criarBotao("Estoque");
        JButton btnHistorico = criarBotao("Histórico");

        botoes.add(btnDoadores);
        botoes.add(btnItens);
        botoes.add(btnDoacoes);
        botoes.add(btnDistribuicoes);
        botoes.add(btnEstoque);
        botoes.add(btnHistorico);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(botoes, BorderLayout.CENTER);

        btnDoadores.addActionListener(e -> new TelaDoadores().setVisible(true));
        btnItens.addActionListener(e -> new TelaItens().setVisible(true));
        btnDoacoes.addActionListener(e -> new TelaDoacoes().setVisible(true));
        btnDistribuicoes.addActionListener(e -> new TelaDistribuicoes().setVisible(true));
        btnEstoque.addActionListener(e -> new TelaEstoque().setVisible(true));
        btnHistorico.addActionListener(e -> new TelaHistorico().setVisible(true));

        setContentPane(painel);
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_BOTAO);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(240, 72));
        return botao;
    }
}
