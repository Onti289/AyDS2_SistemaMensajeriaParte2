package vistas;

import java.awt.Component;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import dto.UsuarioDTO;

public class UsuarioRenderer extends JLabel implements ListCellRenderer<UsuarioDTO> {

	private Set<UsuarioDTO> usuariosConMensajesNuevos;

	public UsuarioRenderer(Set<UsuarioDTO> usuariosConMensajesNuevos) {
		this.usuariosConMensajesNuevos = usuariosConMensajesNuevos;
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends UsuarioDTO> list, UsuarioDTO usuarioDTO, int index,
			boolean isSelected, boolean cellHasFocus) {

		// Icono de notificaci�n si tiene mensaje nuevo
		String texto = usuarioDTO.toString(); // o value.toString()
		if (usuariosConMensajesNuevos.contains(usuarioDTO)) {
			texto = "? " + texto;
		}

		setText(texto);

		// Colores de selecci�n normales
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		return this;
	}
}