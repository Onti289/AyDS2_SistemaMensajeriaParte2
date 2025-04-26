package vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaServidor extends JFrame implements IVista {

    private JLabel etiquetaEstado;

    public VentanaServidor() {
        setTitle("Servidor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null); // Centrar la ventana

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        getContentPane().add(panelPrincipal);

        etiquetaEstado = new JLabel("Servidor en funcionamiento");
        etiquetaEstado.setFont(new Font("Arial", Font.PLAIN, 14));
        panelPrincipal.add(etiquetaEstado);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    @Override
    public void setActionListener(ActionListener controlador) {
        // Esta ventana no tiene botones, así que no es necesario.
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}