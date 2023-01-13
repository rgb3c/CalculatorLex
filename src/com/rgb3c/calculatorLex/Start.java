package com.rgb3c.calculatorLex;

import javax.swing.*;

import static com.rgb3c.calculatorLex.Logic.actionMapFilling;

public class Start {

    private JFrame window;

    public Start () {
        window = new JFrame("CalculatorLex");
        ImageIcon img = new ImageIcon("src/com/rgb3c/calculatorLex/ico/logo.png");
        window.setIconImage(img.getImage());
        window.setSize(500,150);
        window.add(new Panel());
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Start();
            }
        });
        actionMapFilling();
    }
}

