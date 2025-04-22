package sistemaMensajeria;

import controlador.Controlador;
import modeloNegocio.SistemaUsuario;
import vistas.*;

public class Main {

    public static void main(String[] args) {
        SistemaUsuario sMensajeria=SistemaUsuario.get_Instancia();
        Controlador controlador=new Controlador(sMensajeria);

    }

}