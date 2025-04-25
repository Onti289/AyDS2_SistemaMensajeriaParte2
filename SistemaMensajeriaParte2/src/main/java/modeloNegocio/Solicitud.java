package modeloNegocio;

import java.io.Serializable;

public class Solicitud implements Serializable {
	private String tipoSolicitud; // Ej: "OBTENER_USUARIOS"
	private int puerto;
	private String ip;

	public Solicitud(int puerto, String ip,String tipo) {
		this.tipoSolicitud = tipo;
		this.puerto = puerto;
		this.ip=ip;
	}
	
	public int getPuerto() {
		return puerto;
	}

	public String getIp() {
		return ip;
	}

	public String getTipoSolicitud() {
		return tipoSolicitud;
	}
	
}