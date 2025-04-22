package sistemaMensajeria;

import controlador.Controlador;
import modeloNegocio.SistemaMensajeria;
import vistas.*;

public class Main {

    public static void main(String[] args) {
        SistemaMensajeria sMensajeria=SistemaMensajeria.get_Instancia();
        Controlador controlador=new Controlador(sMensajeria);

    }

}