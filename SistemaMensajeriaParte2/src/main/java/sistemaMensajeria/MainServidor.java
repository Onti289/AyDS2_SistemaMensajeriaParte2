package sistemaMensajeria;

import controlador.*;
import modeloNegocio.SistemaServidor;

public class MainServidor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SistemaServidor servidor = SistemaServidor.get_Instancia();
		ControladorServer controladorServer = new ControladorServer(servidor);
		
	}

}