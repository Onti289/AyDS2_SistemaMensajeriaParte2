package modeloNegocio;

import java.util.ArrayList;
import java.util.List;

public class SistemaServidor {

	private ArrayList <Usuario> listaUsuarios;
	private ArrayList <Usuario> listaConectados;
	
	public boolean existeUsuarioPorNombre(String nombreBuscado) {
	    for (Usuario u : listaUsuarios) {
	        if (u.getNickName().equalsIgnoreCase(nombreBuscado)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void registrarUsuario(String nickName,int puerto) {
		
		if (!existeUsuarioPorNombre(nickName))
		   this.listaUsuarios.add(new Usuario(nickName,puerto));
		//else
		   //lanzar una exepcion nicknameExistenteException	
	}
	
	public boolean estaConectado(String nickName) {
       for (Usuario u : listaConectados) {
	      if (u.getNickName().equalsIgnoreCase(nickName)) {
             return true;
          }    
       }
	   return false;  	
	}
	
	
	public boolean puertoCorrecto(String nickName,int puerto) {
	    for (Usuario u : listaUsuarios) {
		      if (u.getNickName().equalsIgnoreCase(nickName) && u.getPuerto() == puerto) {	  
	             return true;
	          }    
	    }
		return false;  	
	}
	
	public void loginUsuario(String nickName,int puerto) { //agregar ip
		if (this.existeUsuarioPorNombre(nickName) && this.puertoCorrecto(nickName,puerto) && !this.estaConectado(nickName)) {
           System.out.print("hola");	
		   listaConectados.add(new Usuario(nickName, puerto));
	    }
	      //usuario logueado exitosamente
		//else
		   //usuarioInexistente o ya logueado	
	}

	
}
