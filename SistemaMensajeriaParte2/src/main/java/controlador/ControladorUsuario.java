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

public class ControladorUsuario implements ActionListener, Observer {
	protected IVista ventana;
	protected IVista ventana2;
	protected SistemaUsuario sistemaUsuario;

	public ControladorUsuario(SistemaUsuario sistemaUsuario) {
		this.ventana = new VentanaInicial();
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

	public void setUser(String nickName, int puerto, String ip, String tipo) {
		
		this.sistemaUsuario.activaUsuarioEnServidor(nickName, puerto, ip, tipo);
		/*
		this.ventana.setVisible(false);
		this.setVentana(new VentanaPrincipal(this));
		this.ventana.setVisible(true);
		this.ventana.setActionListener(this);
		*/
	}

    /*public void setUser(String nickName, int puerto, String ip) {
		
		this.sistemaUsuario.//RegistrarUsuarioEnServidor(nickName, puerto, ip);
		
		this.ventana.setVisible(false);
		this.setVentana(new VentanaPrincipal(this));
		this.ventana.setVisible(true);
		this.ventana.setActionListener(this);
		
	}
	*/
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

		nroCondicionAgregado = this.sistemaUsuario.agregarContacto(nickName, ip, puerto);
		
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
		
		//llega aca cuado apreta boton registro en VentanaInicial
		case Util.CTEREGISTRO:
			if (this.ventana instanceof VentanaInicial) {
				this.ventana.setVisible(false);
				this.setVentana(new VentanaRegistrarse(this));
				
			}
			
			break;
		case Util.CTEINICIARSESION:
			if (this.ventana instanceof VentanaInicial) {
				this.ventana.setVisible(false);
				this.setVentana(new VentanaLogin(this));
				
			}
			break;
		//llega aca cuando apreta boton registrar en VentanaRegistrarse
		case Util.CTEREGISTRAR:

			if (this.ventana instanceof VentanaRegistrarse) {
				VentanaRegistrarse ventanaRegistrarse = (VentanaRegistrarse) this.ventana;
				puerto = Integer.parseInt(ventanaRegistrarse.getPuerto());
				if (!this.sistemaUsuario.puertoDisponible(puerto)) {
					((VentanaRegistrarse) this.ventana).muestraErrorPuertoEnUso();
					((VentanaRegistrarse) this.ventana).vaciarTextFieldPuerto();
					((VentanaRegistrarse) this.ventana).deshabilitarBoton();
				} else {
		
					this.sistemaUsuario.iniciarServidor(puerto);
					setUser(ventanaRegistrarse.getUsuario(), puerto, ventanaRegistrarse.getIP(), Util.CTEREGISTRAR);
					/*
					this.ventana.setVisible(false);
					this.setVentana(new VentanaPrincipal(this));
					((VentanaPrincipal) ventana).TitulonameUsuario(ventanaRegistrarse.getUsuario());
				*/
				}
			}

			break;
			//llega aca cuando apreta boton iniciar sesion en VentanaRegistrarse
		case Util.CTELOGIN:

			if (this.ventana instanceof VentanaLogin) {
				VentanaLogin ventanaLogin = (VentanaLogin) this.ventana;
				puerto = Integer.parseInt(ventanaLogin.getPuerto());
				if (!this.sistemaUsuario.puertoDisponible(puerto)) {
					((VentanaLogin) this.ventana).muestraErrorPuertoEnUso();
					((VentanaLogin) this.ventana).vaciarTextFieldPuerto();
					((VentanaLogin) this.ventana).deshabilitarBoton();
				} else {
		
					this.sistemaUsuario.iniciarServidor(puerto);
					setUser(ventanaLogin.getUsuario(), puerto, ventanaLogin.getIP(), Util.CTELOGIN);
					/*
					this.ventana.setVisible(false);
					this.setVentana(new VentanaPrincipal(this));
					((VentanaPrincipal) ventana).TitulonameUsuario(ventanaRegistrarse.getUsuario());
				*/
				}
			}

			break;

		case Util.CTEAGREGARCONTACTO:
			this.sistemaUsuario.pedirListaUsuarios();
			break;
		case Util.CTENUEVACONVER:
			this.setVentana2(new VentanaDirectorio(this));

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
			if (this.ventana2 instanceof VentanaDirectorio) {
				VentanaDirectorio ventanaContactos = (VentanaDirectorio) this.ventana2;

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
			if (this.ventana2 instanceof VentanaDirectorio) {
				VentanaDirectorio ventanaDirectorio = (VentanaDirectorio) this.ventana2;
				UsuarioDTO usuario = ventanaDirectorio.getUsuario();
	
				puerto = usuario.getPuerto();
				int nroCondicionAgregado = this.agregaContacto(usuario.getNombre(), usuario.getIp(), puerto);
				if (nroCondicionAgregado == 2) {
					((VentanaDirectorio) ventana2).mostrarConfirmacionContactoAgregado();
					ventana2.dispose(); // cerrar la ventana luego de agregar
				} else {
					if (nroCondicionAgregado == 1) {
						((VentanaDirectorio) ventana2).mostrarErrorContactoYaAgendado();
					} 
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
		else {
			if(arg instanceof UsuarioDTO) { //pudo registrar o loguear a usuario

				UsuarioDTO usuarioDTO=(UsuarioDTO)arg;
				this.ventana.setVisible(false);
				this.setVentana(new VentanaPrincipal(this));
				((VentanaPrincipal) ventana).TitulonameUsuario(usuarioDTO.getNombre());
			}
			else {
				if(arg instanceof List<?>) {
					//Aca hacer que vista con algun metodo tome esa lista
					//y lo muestre por pantalla
				}
			}
		}
	}
}
