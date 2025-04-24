package modeloNegocio;

import java.io.Serializable;

public class Solicitud implements Serializable {
	private String tipoSolicitud; // Ej: "OBTENER_USUARIOS"
	private Usuario usuario;

	public Solicitud(Usuario usuario, String tipo) {
		this.tipoSolicitud = tipo;
		this.usuario = usuario;
	}

	public String getTipo() {
		return tipoSolicitud;
	}

	public Usuario getUsuario() {
		return usuario;
	}
}