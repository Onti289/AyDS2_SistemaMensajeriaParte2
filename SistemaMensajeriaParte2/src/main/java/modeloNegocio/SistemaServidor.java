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

	private ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
	private ArrayList<Usuario> listaConectados = new ArrayList<Usuario>();
	private static SistemaServidor servidor_instancia = null;

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

	private void enviaRespuestaUsuario(Solicitud usuario) {
		try (Socket socket = new Socket(usuario.getIp(), usuario.getPuerto())) {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(usuario);
			oos.flush();
			oos.close();
		} catch (IOException e) {

		}
	}

	public void iniciaServidor() {
		Thread serverThread = new Thread(() -> {
		try (ServerSocket serverSocket = new ServerSocket(Util.PUERTO_SERVIDOR)) {
			while (true) {
				
				Socket clienteSocket = serverSocket.accept();
				System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress());
				
					try (ObjectInputStream ois = new ObjectInputStream(clienteSocket.getInputStream())) {
						Object recibido = ois.readObject();
						System.out.println("LLEga a servereee");
						/*if (recibido instanceof Sol) {
							UsuarioDTO usuario = (UsuarioDTO) recibido;
							System.out.println(usuario.toString());
							registrarUsuario(usuario);
							enviaUsuarioRegistrado(usuario);
						} else // entra si lo que recibe en vez de usuario es mensaje
							*/
						if (recibido instanceof Solicitud) {	
								Solicitud solicitud=(Solicitud)recibido;
								System.out.println("soli = "+solicitud.getTipoSolicitud());
								if(solicitud.getTipoSolicitud().equalsIgnoreCase(Util.SOLICITA_LISTA_USUARIO)) {
									retornaLista(solicitud.getIp(),solicitud.getPuerto());
								}
								else
									if (solicitud.getTipoSolicitud().equalsIgnoreCase(Util.CTEREGISTRAR)){
										System.out.println("Registrooooooo");
										UsuarioDTO usuario = solicitud.getUsuarioDTO();
										if(registrarUsuario(usuario)) {
											solicitud.setTipoSolicitud(Util.CTEREGISTRO);
										}
										else {
											solicitud.setTipoSolicitud(Util.CTEUSUARIOLOGUEADO);
										}
										enviaRespuestaUsuario(solicitud);
									}
									else
										if (solicitud.getTipoSolicitud().equalsIgnoreCase(Util.CTELOGIN)) {
											System.out.println("Loginnnnn");
											UsuarioDTO usuario = solicitud.getUsuarioDTO();
											int tipo=loginUsuario(usuario);
											if(tipo==1) {
												solicitud.setTipoSolicitud(Util.CTEUSUARIOLOGUEADO);
											}
											else {
												//usuario Existe pero esta logueado
												if(tipo==2) {
													solicitud.setTipoSolicitud(Util.CTEUSUARIOLOGUEADO);
												}
												else {
													solicitud.setTipoSolicitud(Util.CTEUSUERINEXISTENTE);
												}
												
											}
											enviaRespuestaUsuario(solicitud);
										}
							}
						
					} catch (Exception e) {
						System.err.println("Error al procesar solicitud del cliente: " + e.getMessage());
					}
					clienteSocket.close();
				}
		} catch (Exception e) {
			System.err.println("Error en el servidor central: " + e.getMessage());
			//cerrarServidor();
		}
		});
		serverThread.start();
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
	
	public boolean registrarUsuario(UsuarioDTO usuariodto) {
		boolean registro=true;
		if (!existeUsuarioPorNombre(usuariodto.getNombre())) {
			Usuario usuario=new Usuario(usuariodto.getNombre(), usuariodto.getPuerto(),usuariodto.getIp());
			this.listaUsuarios.add(usuario);
			this.listaConectados.add(usuario);
		}
		else {
			registro=false;
		 }
		return registro;
	}

	public boolean estaConectado(String nickName) {
		for (Usuario u : listaConectados) {
			if (u.getNickName().equalsIgnoreCase(nickName)) {
				return true;
			}
		}
		return false;
	}

	public boolean puertoEIpCorrecto(String nickName, int puerto, String IP) {
		for (Usuario u : listaUsuarios) {
			if (u.getNickName().equalsIgnoreCase(nickName) && u.getPuerto() == puerto && u.getIp().equalsIgnoreCase(IP)) {
				return true;
			}
		}
		return false;
	}
	public int loginUsuario(UsuarioDTO usuario) {
		int tipo=1;//usuario logueado exitosamente
		//usuario Existe pero esta logueado
		if(this.estaConectado(usuario.getNombre())) {
			tipo=2;
		}
		// usuario logueado exitosamente
		if (this.existeUsuarioPorNombre(usuario.getNombre()) && this.puertoEIpCorrecto(usuario.getNombre(), usuario.getPuerto(), usuario.getIp())
				&& !this.estaConectado(usuario.getNombre())) {
			listaConectados.add(new Usuario(usuario.getNombre(), usuario.getPuerto(), usuario.getIp()));
		}
		else {// usuarioInexistente 
			tipo=3;
		}	
		return tipo;
		
	}

}
