package modeloNegocio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.PriorityQueue;

import dto.MensajeDTO;
import dto.UsuarioDTO;
import excepciones.ErrorEnvioMensajeException;

public class SistemaUsuario extends Observable{
	private ArrayList <Usuario> listaContactos;
	private static SistemaUsuario sistema_instancia=null;
	private Usuario usuario;
	
	
	
	private SistemaUsuario() {
		
	}
	public static SistemaUsuario get_Instancia() {
		if(sistema_instancia==null)
			sistema_instancia=new SistemaUsuario();
		return sistema_instancia;
	} 
	
	public boolean existeContactoPorNombre(List<Usuario> lista, String nombreBuscado) {
	    for (Usuario u : lista) {
	        if (u.getNickName().equalsIgnoreCase(nombreBuscado)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void agregarContacto(String nickName,int puerto) {
		
		if (!existeContactoPorNombre(listaContactos,nickName))
		   this.listaContactos.add(new Usuario(nickName,puerto));
		//else
		   //lanzar una exepcion nicknameExistenteException	
	}


	public PriorityQueue<Usuario> getAgenda() {
		return this.listaContactos.getAgenda();
	}

	public ArrayList<MensajeDTO> getChat(int puerto,String ip){
		return listaContactos.getChat(puerto,ip);
	}
	public ArrayList<Mensaje> getMensajes(){
    	return listaContactos.getMensajes();
    }
	
	public void setContactoActual(int puerto,String ip) {
		Usuario contacto=listaContactos.getBuscaContacto(puerto);
		if(contacto!=null) {
			this.listaContactos.agregarConversacion(contacto);
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
	                        this.listaContactos.recibirMensaje(mensaje);
	                        setChanged(); // importante
	     		           	notifyObservers(mensaje);
	                        
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
	    for (Usuario u : listaContactos.getAgenda()) {
	        if (u.getPuerto() == dto.getPuerto() && u.getIp()== dto.getIp()) {
	            return u;
	        }
	    }
	    return null; // o lanzar excepci�n si quer�s asegurarte que est�
	}
	public Usuario getUsuario() {
		return this.listaContactos;
	}
	public void enviarMensaje(UsuarioDTO contacto, String mensaje)  {
	    try (Socket socket = new Socket(contacto.getIp(), contacto.getPuerto())) {
	    	ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	    	Usuario ureceptor=this.buscarUsuarioPorDTO(contacto);
	    	Mensaje msg;
	    	if(ureceptor!=null) {
	    		msg=new Mensaje(mensaje,LocalDateTime.now(),this.listaContactos,ureceptor); 
	    		oos.writeObject(msg);
	    		oos.flush();
		    	oos.close();
		    	this.listaContactos.guardarMensaje(msg);
		    	setChanged(); // importante
			    notifyObservers(msg);
	    	}

	    } catch (IOException e) {
	    	ErrorEnvioMensajeException error=new ErrorEnvioMensajeException("Error de conexion: el destinario se encuentra desconectado");
	    	setChanged(); // importante
	        notifyObservers(error);
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
		return this.listaContactos.getListaConversaciones();
	}
	public String getAlias(int puerto) {
		String name;
		PriorityQueue<Usuario> lista=this.listaContactos.getAgenda();
		while (!lista.isEmpty()) {
	        Usuario contacto = lista.poll();
	        if (contacto.getPuerto() == puerto) {
	            return contacto.getNickName();
	        }
	    }
		return null;
	}

}
