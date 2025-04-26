package vistas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

import util.Util;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Box;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class VentanaInicial extends JFrame implements IVista, ActionListener {

    private JButton botonRegistro;
    private JButton botonInicioSesion;

    public VentanaInicial() {
        setTitle("Sistema de Mensajeria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 250);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayout(4, 1, 0, 10));
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);

        JLabel titulo = new JLabel("Sistema de Mensajeria", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(titulo);

        botonRegistro = new JButton(Util.CTEREGISTRO);
        panelPrincipal.add(botonRegistro);

        botonInicioSesion = new JButton(Util.CTEINICIARSESION);
        panelPrincipal.add(botonInicioSesion);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    @Override
    public void setActionListener(ActionListener controlador) {
        botonRegistro.addActionListener(controlador);
        botonInicioSesion.addActionListener(controlador);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Lógica para manejar eventos si es necesario
    }
}