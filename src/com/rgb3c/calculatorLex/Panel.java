package com.rgb3c.calculatorLex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Panel extends JPanel {

    private final Font fontMain = new Font("SanSerif", Font.BOLD, 20);
    private final Font fontInfo = new Font("SanSerif", Font.BOLD, 13);
    private final JTextField output = new JTextField("0");
    private final JLabel info = new JLabel("Only 0-9 numbers, /*-+(), and function:");
    private final JLabel info2 = new JLabel("min(several), pow(x,y), rand(), avg(several)");
    private final JButton backspace = new JButton("C"), equ = new JButton("="), infoButton = new JButton("i");
    boolean infoVisible = true;


    public Panel() {
        setLayout(null);
        setFocusable(true);
        grabFocus();

        backspace.setBounds(10, 50, 50, 50);
        backspace.setFont(fontMain);
        add(backspace);
        backspace.setFocusable(false);

        equ.setBounds(425, 50, 50, 50);
        equ.setFont(fontMain);
        add(equ);
        equ.setFocusable(false);

        infoButton.setBounds(365, 50, 50, 50);
        infoButton.setFont(fontMain);
        add(infoButton);
        infoButton.setFocusable(false);

        info.setBounds(70,50, 400, 30);
        info.setFont(fontInfo);
        add(info);

        info2.setBounds(70,70, 400, 30);
        info2.setFont(fontInfo);
        add(info2);

        output.setBounds(10,10, 465, 30);
        output.setFont(fontMain);
        output.setEditable(true);
        add(output);
        output.setFocusable(true);
        output.grabFocus();

        ActionListener equAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Logic logic = new Logic(output.getText());
                output.setText(logic.getResult());
            }
        };
        equ.addActionListener(equAction);

        ActionListener backSpaceAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                output.setText("");
                grabFocus();
            }
        };
        backspace.addActionListener(backSpaceAction);

        ActionListener infoButtonAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (infoVisible) {
                    setInfoVisibleFalse();
                    infoVisible = false;
                } else {
                    setInfoVisibleTrue();
                    infoVisible = true;
                }
                grabFocus();
            }
        };
        infoButton.addActionListener(infoButtonAction);

        output.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char symbol = e.getKeyChar();

                if (symbol == '\n') {
                    equ.doClick();
                }

                if (symbol == 27) {
                    backspace.doClick();
                }
            }
        });
    }

    private void setInfoVisibleFalse() {
        info.setVisible(false);
        info2.setVisible(false);
    }

    private void setInfoVisibleTrue() {
        info.setVisible(true);
        info2.setVisible(true);
    }
}