package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;
import dto.MensajeDTO;
import dto.UsuarioDTO;
import modeloNegocio.*;
import util.Util;
import vistas.*;

@SuppressWarnings("deprecation")
public class Controlador implements ActionListener, Observer {
	protected IVista ventana;
	protected IVista ventana2;
	protected SistemaUsuario sistemaUsuario;

	public Controlador(SistemaUsuario sistemaUsuario) {
		this.ventana = new Ventanalogin(this);
		ventana.setVisible(true);
		this.sistemaUsuario = sistemaUsuario;
		this.ventana.setActionListener(this);
		sistemaUsuario.addObserver(this);
	}

	public IVista getVentana() {
		return ventana;
	}

	public SistemaUsuario getSistemaMensajeria() {
		return sistemaUsuario;
	}

	public void setVentana(IVista ventana) {
		this.ventana = ventana;
		this.ventana.setActionListener(this);
		this.ventana.setVisible(true);
	}

	public void setUser(String nickName, int puerto, String ip) {
		this.sistemaUsuario.RegistrarUsuarioEnServidor(nickName, puerto, ip);
		this.ventana.setVisible(false);
		this.setVentana(new VentanaPrincipal(this));
		this.ventana.setVisible(true);
		this.ventana.setActionListener(this);
	}

	public String getNickNamePuerto() {
		return "Nickname:" + sistemaUsuario.getnickName() + "\nPuerto:" + sistemaUsuario.getPuerto();
	}

	public List<UsuarioDTO> getAgenda() {
		List<UsuarioDTO> lista = new ArrayList<UsuarioDTO>();
		PriorityQueue<Usuario> copia = new PriorityQueue<>(sistemaUsuario.getAgenda());
		Usuario user;
		while (!copia.isEmpty()) {
			user = copia.poll();
			lista.add(new UsuarioDTO(user.getNickName(), user.getPuerto(), user.getIp()));
		}

		return lista;
	}

	public List<UsuarioDTO> getListaConversaciones() {
		List<UsuarioDTO> lista = new ArrayList<UsuarioDTO>();
		List<Usuario> copia = new ArrayList<>(sistemaUsuario.getListaConversaciones());
		for (Usuario user : copia) {
			lista.add(new UsuarioDTO(user.getNickName(), user.getPuerto(), user.getIp()));
		}

		return lista;
	}

	public IVista getVentana2() {
		return ventana2;
	}

	public void setVentana2(IVista ventana2) {
		this.ventana2 = ventana2;
		this.ventana2.setActionListener(this);
		this.ventana2.setVisible(true);
	}

	public int agregaContacto(String nickName, String ip, int puerto) {
		int nroCondicionAgregado;
		// Si puerto esta disponible es por que no existe ningun usuario con ese puerto
		if (this.sistemaUsuario.puertoDisponible(puerto))
			nroCondicionAgregado = 1;
		else {
			nroCondicionAgregado = this.sistemaUsuario.agregarContacto(nickName, ip, puerto);
		}
		return nroCondicionAgregado;
	}

	public void cargaChat(int puerto, String ip) {
		String alias = this.sistemaUsuario.getAlias(puerto);
		for (MensajeDTO msg : this.sistemaUsuario.getChat(puerto, ip)) {
			if (ventana instanceof VentanaPrincipal) {
				if (msg.getEmisor().getPuerto() == puerto) // el msg lo mando el contacto
					((VentanaPrincipal) ventana).agregarMensajeAchat(msg.getContenido(), msg.getFechayhora(), alias);
				else { // el msg lo manda el usuario
					((VentanaPrincipal) ventana).agregarMensajeAchat(msg.getContenido(), msg.getFechayhora(),
							msg.getEmisor().getNickName());
				}
			}
		}
	}

	public void actualizaListaConversacion(int puerto, String ip) {
		this.sistemaUsuario.setContactoActual(puerto, ip);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int puerto;
		switch (e.getActionCommand()) {
		case Util.CTEREGISTRAR:

			if (this.ventana instanceof Ventanalogin) {
				Ventanalogin ventanalogin = (Ventanalogin) this.ventana;
				puerto = Integer.parseInt(ventanalogin.getPuerto());
				if (!this.sistemaUsuario.puertoDisponible(puerto)) {
					((Ventanalogin) this.ventana).muestraErrorPuertoEnUso();
					((Ventanalogin) this.ventana).vaciarTextFieldPuerto();
					((Ventanalogin) this.ventana).deshabilitarBoton();
				} else {
					this.sistemaUsuario.iniciarServidor(puerto);
					// en 3er parametro NO VA IPLOCAL iria ip que recibe de
					// vista pero tenemos que hacerlo
					setUser(ventanalogin.getUsuario(), puerto, Util.IPLOCAL);
					this.ventana.setVisible(false);
					this.setVentana(new VentanaPrincipal(this));
					((VentanaPrincipal) ventana).TitulonameUsuario(ventanalogin.getUsuario());
				}
			}

			break;
		case Util.CTEAGREGARCONTACTO:

			this.sistemaUsuario.obtenerListaUsuarios();
			this.setVentana2(new VentanaAgregarContacto(this));

			break;
		case Util.CTENUEVACONVER:
			this.setVentana2(new VentanaContactos(this));

			break;
		case Util.CTEENVIAR:
			if (ventana instanceof VentanaPrincipal) {
				String contenidoMensaje;

				contenidoMensaje = ((VentanaPrincipal) ventana).getTextFieldMensaje();
				UsuarioDTO user = ((VentanaPrincipal) ventana).getContactoConversacionActual();
				this.sistemaUsuario.enviarMensajeServidor(user, contenidoMensaje);
			}
			break;
		case Util.CTEINICIARCONVERSACION:
			if (this.ventana2 instanceof VentanaContactos) {
				VentanaContactos ventanaContactos = (VentanaContactos) this.ventana2;

				UsuarioDTO contacto = ventanaContactos.getUsuario();
				this.cargaChat(contacto.getPuerto(), contacto.getIp());
				this.actualizaListaConversacion(contacto.getPuerto(), contacto.getIp());
				// ACTUALIZA la lista en la ventana principal
				if (ventana instanceof VentanaPrincipal) {
					((VentanaPrincipal) ventana).actualizarListaChats(this.getListaConversaciones());
					// pone nombre de user seleccionado en parte de chat

					((VentanaPrincipal) ventana).setTextFieldNameContacto(contacto.getNombre());
					((VentanaPrincipal) ventana).setDejarSeleccionadoContactoNuevaConversacion(contacto);
					((VentanaPrincipal) ventana).vaciarTextFieldMensajes();
				}

				this.ventana2.dispose();
			}
			break;
		case Util.CTEAGREGAR:
			if (this.ventana2 instanceof VentanaAgregarContacto) {
				VentanaAgregarContacto ventanaAgregar = (VentanaAgregarContacto) this.ventana2;
				String nick = ventanaAgregar.getNickname();
				String ip = ventanaAgregar.getIp();
				puerto = Integer.parseInt(ventanaAgregar.getPuerto());
				int nroCondicionAgregado = this.agregaContacto(nick, ip, puerto);
				if (nroCondicionAgregado == 3) {
					((VentanaAgregarContacto) ventana2).mostrarConfirmacionContactoAgregado();
					ventana2.dispose(); // cerrar la ventana luego de agregar
				} else {
					if (nroCondicionAgregado == 2) {
						((VentanaAgregarContacto) ventana2).mostrarErrorContactoYaAgendado();
					} else {
						((VentanaAgregarContacto) ventana2).mostrarErrorUsuarioNoDisponible();
					}
					((VentanaAgregarContacto) ventana2).vaciarTextFieldPuerto();
					((VentanaAgregarContacto) ventana2).deshabilitarBoton();
				}
			}
			break;
		default:
			break;
		}

	}

	public void contactoSeleccionadoDesdeLista(UsuarioDTO contacto) {

		if (ventana instanceof VentanaPrincipal) {
			VentanaPrincipal vp = (VentanaPrincipal) ventana;
			vp.setTextFieldNameContacto(contacto.getNombre());
			vp.limpiarChat();
			this.cargaChat(contacto.getPuerto(), contacto.getIp()); // Mostr s historial
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof Mensaje) {
			Mensaje mensaje = (Mensaje) arg;
			if (mensaje.getEmisor().equals(this.sistemaUsuario.getUsuario())) {
				((VentanaPrincipal) ventana).agregarMensajeAchat(mensaje.getContenido(), LocalDateTime.now(),
						this.sistemaUsuario.getUsuario().getNickName());
				((VentanaPrincipal) ventana).limpiarBuffer();
			} else {
				if (ventana instanceof VentanaPrincipal) {
					VentanaPrincipal vp = (VentanaPrincipal) ventana;
					// chequeo si soy receptor
					if (!(mensaje.getEmisor().getIp().equals(this.sistemaUsuario.getUsuario().getIp())
							&& (mensaje.getEmisor().getPuerto() == this.sistemaUsuario.getUsuario().getPuerto()))) {
						// this.sistemaMensajeria.getUsuario().recibirMensaje(mensaje);
						// Si emisor es el contacto con el que estoy hablando muestra en pantalla
						if ((vp.hayConversaciones()) && (!(vp.getContactoConversacionActual() == null))
								&& mensaje.getEmisor().getIp().equals(vp.getContactoConversacionActual().getIp())
								&& (mensaje.getEmisor().getPuerto() == vp.getContactoConversacionActual()
										.getPuerto())) {
							// String
							// alias=this.sistemaMensajeria.getAlias(mensaje.getEmisor().getPuerto());
							// vp.agregarMensajeAchat(mensaje.getContenido(),mensaje.getFechayhora(),alias);
							vp.limpiarChat();
							this.cargaChat(mensaje.getEmisor().getPuerto(), mensaje.getEmisor().getIp()); // Mostr s
																											// historial
						} // notifica llega cuando no hay conversaciones o no es contacto actual
						else {
							vp.actualizarListaChats(this.getListaConversaciones());
							UsuarioDTO usuarioDTO = new UsuarioDTO(mensaje.getEmisor().getNickName(),
									mensaje.getEmisor().getPuerto(), mensaje.getEmisor().getIp());
							vp.notificarNuevoMensaje(usuarioDTO);
						}
					}
				}
			}

		} else if (arg instanceof IOException) {

			String mensaje = ((IOException) arg).getMessage();
			((VentanaPrincipal) ventana).mostrarErrorEnvioMensaje(mensaje);
		}
	}
}
