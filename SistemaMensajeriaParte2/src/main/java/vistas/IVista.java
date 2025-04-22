package vistas;

import java.awt.event.ActionListener;

import controlador.Controlador;

public interface IVista {

    void setVisible(boolean b);

    void setActionListener(ActionListener controlador);

    void dispose();

}