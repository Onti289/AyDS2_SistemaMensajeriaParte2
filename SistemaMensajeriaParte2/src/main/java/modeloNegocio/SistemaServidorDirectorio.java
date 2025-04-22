package modeloNegocio;

import java.util.ArrayList;
import java.util.List;

public class SistemaServidorDirectorio {

	private ArrayList <Usuario> listaUsuarios;
	
	public boolean existeUsuarioPorNombre(List<Usuario> lista, String nombreBuscado) {
	    for (Usuario u : lista) {
	        if (u.getNickName().equalsIgnoreCase(nombreBuscado)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void agregarUsuario(String nickName,int puerto) {
		
		if (!existeUsuarioPorNombre(listaUsuarios,nickName))
		   this.listaUsuarios.add(new Usuario(nickName,puerto));
		//else
		   //lanzar una exepcion nicknameExistenteException	
	}

	
}
