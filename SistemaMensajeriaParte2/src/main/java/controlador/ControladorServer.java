package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import modeloNegocio.*;
import util.Util;
import vistas.IVista;
import vistas.VentanaAgregarContacto;
import vistas.Ventanalogin;

public class ControladorServer implements ActionListener, Observer{
	protected IVista ventana;
	protected SistemaServidor sistemaServidor;
	public ControladorServer(SistemaServidor sistemaServidor) {
		this.sistemaServidor = sistemaServidor;
		this.sistemaServidor.addObserver(this);
		this.sistemaServidor.iniciaServidor();
	}
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Solicitud) {
			Solicitud solicitud=(Solicitud)arg;
			if(solicitud.getTipo()==Util.SOLICITA_LISTA_USUARIO) {
				this.ventana = new VentanaAgregarContacto(this);
				this.ventana.setVisible(true);
				this.ventana.setActionListener(this);
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
	
}
