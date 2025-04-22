package dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import modeloNegocio.Usuario;

public class MensajeDTO implements Serializable{
    private String contenido;
    private LocalDateTime fechayhora;
    private Usuario emisor;
    private Usuario receptor;
    public MensajeDTO(String contenido, LocalDateTime fechayhora, Usuario emisor, Usuario receptor) {
        super();
        this.contenido = contenido;
        this.fechayhora = fechayhora;
        this.emisor = emisor;
        this.receptor = receptor;
    }
    public String getContenido() {
        return contenido;
    }
    public LocalDateTime getFechayhora() {
        return fechayhora;
    }
    public Usuario getEmisor() {
        return emisor;
    }
    public Usuario getReceptor() {
        return receptor;
    }

}
