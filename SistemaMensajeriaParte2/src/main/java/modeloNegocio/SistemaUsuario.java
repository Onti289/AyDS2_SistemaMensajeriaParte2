package modeloNegocio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import dto.ContactoDTO;
import dto.MensajeDTO;
import dto.UsuarioDTO;
import excepciones.ErrorEnvioMensajeException;
import util.Util;

public class SistemaUsuario extends Observable {
	private Usuario usuario;
	private static SistemaUsuario sistema_instancia = null;

	private SistemaUsuario() {

	}

	public static SistemaUsuario get_Instancia() {
		if (sistema_instancia == null)
			sistema_instancia = new SistemaUsuario();
		return sistema_instancia;
	}

	public String getnickName() {
		return this.usuario.getNickName();
	}

	public int getPuerto() {
		return this.usuario.getPuerto();
	}

	public void pedirListaUsuarios(int puerto, String ip) {
		try (Socket socket = new Socket(Util.IPLOCAL, Util.PUERTO_SERVIDOR)) {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			Solicitud solicitud = new Solicitud(puerto,ip, Util.SOLICITA_LISTA_USUARIO);
			oos.writeObject(solicitud);
			oos.flush();
			oos.close();
		} catch (IOException e) {
		}
	}

	public void setUsuario(String nickname, int puerto, String ip) {
		this.usuario = new Usuario(nickname, puerto, ip);
	}

	public boolean existeContactoPorNombre(PriorityQueue<Usuario> lista, String nombreBuscado) {
		for (Usuario u : lista) {
			if (u.getNickName().equalsIgnoreCase(nombreBuscado)) {
				return true;
			}
		}
		return false;
	}

	public int agregarContacto(String nickName, String ip, int puerto) {
		int condicion = 1; // contacto ya agendado
		if (!existeContactoPorNombre(this.usuario.getAgenda(), nickName)) {
			this.usuario.agregaContacto(new Usuario(nickName, puerto, ip));
			condicion = 2;
		}
		return condicion;
	}

	public PriorityQueue<Usuario> getAgenda() {
		return this.usuario.getAgenda();
	}

	public ArrayList<MensajeDTO> getChat(int puerto, String ip) {
		return usuario.getChat(puerto, ip);
	}

	public ArrayList<Mensaje> getMensajes() {
		return usuario.getMensajes();
	}

	public void setContactoActual(int puerto, String ip) {
		Usuario contacto = usuario.getBuscaContacto(puerto);
		if (contacto != null) {
			this.usuario.agregarConversacion(contacto);
		}
	}

	public void iniciarServidor(int puerto) {
		Thread serverThread = new Thread(() -> {
			try (ServerSocket serverSocket = new ServerSocket(puerto)) {
				while (true) {
					Socket socket = serverSocket.accept();
					try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
						Object recibido = ois.readObject();
						if (recibido instanceof Mensaje) {
							Mensaje mensaje = (Mensaje) recibido;
							this.usuario.recibirMensaje(mensaje);
							setChanged(); // importante
							notifyObservers(mensaje);

						} else {// si llega aca es por que el server lo pudo registrar
							if (recibido instanceof UsuarioDTO) {
								UsuarioDTO usuariodto = (UsuarioDTO) recibido;
								setUsuario(usuariodto.getNombre(), usuariodto.getPuerto(), usuariodto.getIp());
							} else {
								if (recibido instanceof ContactoDTO) {
									ContactoDTO contactoDTO = (ContactoDTO) recibido;

								}
								else {
									if(recibido instanceof List<?>) {
										List<?> lista = (List<?>) recibido;
									    if (!lista.isEmpty() && lista.get(0) instanceof UsuarioDTO) {
									        List<UsuarioDTO> usuarios = (List<UsuarioDTO>) lista;
									    }
									    setChanged(); // importante
										notifyObservers(lista);
									}
								}
							}
						}

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		serverThread.start();
	}

	public Usuario buscarUsuarioPorDTO(UsuarioDTO dto) {
		for (Usuario u : usuario.getAgenda()) {
			if (u.getPuerto() == dto.getPuerto() && u.getIp() == dto.getIp()) {
				return u;
			}
		}
		return null; // o lanzar excepci�n si quer�s asegurarte que est�
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void enviarMensajeServidor(UsuarioDTO contacto, String mensaje) {
		try (Socket socket = new Socket(contacto.getIp(), contacto.getPuerto())) {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			Usuario ureceptor = this.buscarUsuarioPorDTO(contacto);
			Mensaje msg;
			if (ureceptor != null) {
				msg = new Mensaje(mensaje, LocalDateTime.now(), this.usuario, ureceptor);
				oos.writeObject(msg);
				oos.flush();
				oos.close();
				this.usuario.guardarMensaje(msg);
				setChanged(); // importante
				notifyObservers(msg);
			}

		} catch (IOException e) {
			ErrorEnvioMensajeException error = new ErrorEnvioMensajeException(
					"Error de conexion: el destinario se encuentra desconectado");
			setChanged(); // importante
			notifyObservers(error);
		}
	}

	public void RegistrarUsuarioEnServidor(String nickName, int puerto, String ip) {
		try (Socket socket = new Socket(Util.IPLOCAL, Util.PUERTO_SERVIDOR)) {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			UsuarioDTO usuariodto = new UsuarioDTO(nickName, puerto, ip);
			oos.writeObject(usuariodto);
			oos.flush();
			oos.close();
		} catch (IOException e) {

		}
	}

	public static boolean puertoDisponible(int puerto) {
		try (ServerSocket socket = new ServerSocket(puerto)) {
			socket.setReuseAddress(true);
			return true; // El puerto est� disponible
		} catch (IOException e) {
			return false; // El puerto ya est� en uso
		}
	}

	public List<Usuario> getListaConversaciones() {
		return this.usuario.getListaConversaciones();
	}

	public String getAlias(int puerto) {
		String name;
		PriorityQueue<Usuario> lista = this.usuario.getAgenda();
		while (!lista.isEmpty()) {
			Usuario contacto = lista.poll();
			if (contacto.getPuerto() == puerto) {
				return contacto.getNickName();
			}
		}
		return null;
	}

}
