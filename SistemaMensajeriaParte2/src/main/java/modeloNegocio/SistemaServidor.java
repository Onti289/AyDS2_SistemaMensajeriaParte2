package modeloNegocio;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import dto.*;

import util.Util;

public class SistemaServidor   {

	private ArrayList<Usuario> listaUsuarios;
	private ArrayList<Usuario> listaConectados;
	private static SistemaServidor servidor_instancia = null;
	private static final int puerto = Util.PUERTO_SERVIDOR;

	private SistemaServidor() {

	}

	public static SistemaServidor get_Instancia() {
		if (servidor_instancia == null)
			servidor_instancia = new SistemaServidor();
		return servidor_instancia;
	}

	private boolean existeUsuarioPorNombre(List<Usuario> lista, String nombreBuscado) {
		for (Usuario u : lista) {
			if (u.getNickName().equalsIgnoreCase(nombreBuscado)) {
				return true;
			}
		}
		return false;
	}

	private void enviaUsuarioRegistrado(UsuarioDTO usuario) {
		try (Socket socket = new Socket(usuario.getIp(), usuario.getPuerto())) {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(usuario);
			oos.flush();
			oos.close();
		} catch (IOException e) {

		}
	}

	public void iniciaServidor() {
		try (ServerSocket serverSocket = new ServerSocket(this.puerto)) {
			while (true) {
				Socket clienteSocket = serverSocket.accept();
				System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress());

				// Manejamos la recepción en un nuevo hilo (opcional pero recomendable)
				new Thread(() -> {
					try (ObjectInputStream ois = new ObjectInputStream(clienteSocket.getInputStream())) {
						Object recibido = ois.readObject();
						if (recibido instanceof UsuarioDTO) {
							UsuarioDTO usuario = (UsuarioDTO) recibido;
							registrarUsuario(usuario);
							enviaUsuarioRegistrado(usuario);
						} else {// entra si lo que recibe en vez de usuario es mensaje
							if (recibido instanceof Solicitud) {	
								Solicitud solicitud=(Solicitud)recibido;
								if(solicitud.getTipoSolicitud()==Util.SOLICITA_LISTA_USUARIO) {
									retornaLista(solicitud.getIp(),solicitud.getPuerto());
								}
							}
						}
					} catch (Exception e) {
						System.err.println("Error al procesar solicitud del cliente: " + e.getMessage());
					}
				}).start();
			}
		} catch (Exception e) {
			System.err.println("Error en el servidor central: " + e.getMessage());
		}
	}
	private void retornaLista(String ip,int puerto) {
			try (Socket socket = new Socket(ip,puerto)) {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());	
				oos.writeObject(obtenerListaUsuariosDTO());
				oos.flush();
				oos.close();
			} catch (IOException e) {

			}
	}
	
	private List<UsuarioDTO> obtenerListaUsuariosDTO() {
	    List<UsuarioDTO> listaDTO = new ArrayList<>();
	    for (Usuario u : this.listaUsuarios) {
	        listaDTO.add(new UsuarioDTO(u.getNickName(), u.getPuerto(), u.getIp()));
	    }
	    return listaDTO;
	}

	public boolean existeUsuarioPorNombre(String nombreBuscado) {
		for (Usuario u : listaUsuarios) {
			if (u.getNickName().equalsIgnoreCase(nombreBuscado)) {
				return true;
			}
		}
		return false;
	}
	
	public void registrarUsuario(UsuarioDTO usuario) {

		if (!existeUsuarioPorNombre(usuario.getNombre()))
			this.listaUsuarios.add(new Usuario(usuario.getNombre(), usuario.getPuerto(),usuario.getIp()));
		// else
		// lanzar una exepcion nicknameExistenteException
	}

	public boolean estaConectado(String nickName) {
		for (Usuario u : listaConectados) {
			if (u.getNickName().equalsIgnoreCase(nickName)) {
				return true;
			}
		}
		return false;
	}

	public boolean puertoCorrecto(String nickName, int puerto) {
		for (Usuario u : listaUsuarios) {
			if (u.getNickName().equalsIgnoreCase(nickName) && u.getPuerto() == puerto) {
				return true;
			}
		}
		return false;
	}

	public void loginUsuario(Usuario usuario) { // agregar ip
		if (this.existeUsuarioPorNombre(usuario.getNickName()) && this.puertoCorrecto(usuario.getNickName(), usuario.getPuerto())
				&& !this.estaConectado(usuario.getNickName())) {
			listaConectados.add(usuario);
		}
		// usuario logueado exitosamente
		// else
		// usuarioInexistente o ya logueado
	}

}
