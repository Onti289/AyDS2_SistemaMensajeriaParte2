package modeloNegocio;

import java.io.Serializable;

public class Solicitud implements Serializable {
	private String tipo; // Ej: "OBTENER_USUARIOS"
	private Usuario usuario;

	public Solicitud(Usuario usuario, String tipo) {
		this.tipo = tipo;
		this.usuario = usuario;
	}

	public String getTipo() {
		return tipo;
	}

	public Usuario getUsuario() {
		return usuario;
	}
}