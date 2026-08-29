package br.com.controledoacoes;

import br.com.controledoacoes.config.Database;
import br.com.controledoacoes.ui.TelaPrincipal;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Database.inicializar();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new TelaPrincipal().setVisible(true);
        });
    }
}
