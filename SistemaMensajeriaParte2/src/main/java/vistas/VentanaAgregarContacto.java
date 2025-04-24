package vistas;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controlador.Controlador;
import util.Util;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;

public class VentanaAgregarContacto extends JFrame implements IVista, ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panelIP;
	private JLabel ipLabel;
	private JPanel panelPuerto;
	private JLabel puertoLabel;
	private JPanel panel_3;
	private JButton btnAgregarButton;
	private JTextField textFieldPuerto;
	private JTextField textFieldIP;
	private JTextField textFieldNickname;
	private Controlador controlador;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public VentanaAgregarContacto(Controlador controlador) {
		this.controlador = controlador;
		setTitle("Agregar contacto");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 367, 284);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 2, 0, 0));

		JPanel panelNickname = new JPanel();
		contentPane.add(panelNickname);
		panelNickname.setLayout(null);

		JLabel nicknameLabel = new JLabel("Nickname");
		nicknameLabel.setBounds(0, 0, 127, 62);
		panelNickname.add(nicknameLabel);

		textFieldNickname = new JTextField();
		textFieldNickname.setBounds(176, 22, 86, 20);
		this.textFieldNickname.addKeyListener(this);
		panelNickname.add(textFieldNickname);
		textFieldNickname.setColumns(10);
		this.textFieldNickname.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validarCampos();
			}
		});

		panelIP = new JPanel();
		contentPane.add(panelIP);
		panelIP.setLayout(null);

		ipLabel = new JLabel("Direcci�n IP");
		ipLabel.setBounds(0, 0, 127, 62);
		panelIP.add(ipLabel);

		textFieldIP = new JTextField();
		textFieldIP.setText("localhost");
		textFieldIP.setBounds(176, 21, 86, 20);
		panelIP.add(textFieldIP);
		this.textFieldIP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validarCampos();
			}
		});

		textFieldIP.setColumns(10);

		panelPuerto = new JPanel();
		contentPane.add(panelPuerto);
		panelPuerto.setLayout(null);

		puertoLabel = new JLabel("Puerto\r\n(1023<P<65536)");
		puertoLabel.setBounds(0, 0, 343, 62);
		panelPuerto.add(puertoLabel);

		textFieldPuerto = new JTextField();
		textFieldPuerto.setBounds(176, 22, 86, 20);
		panelPuerto.add(textFieldPuerto);
		this.textFieldPuerto.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!Character.isDigit(c)) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				validarCampos();
			}
		});
		textFieldPuerto.setColumns(10);

		panel_3 = new JPanel();
		contentPane.add(panel_3);

		btnAgregarButton = new JButton("Agregar");
		this.btnAgregarButton.setActionCommand(Util.CTEAGREGAR);
		btnAgregarButton.setBounds(75, 24, 80, 23);
		this.btnAgregarButton.setToolTipText("Agregar");

		panel_3.setLayout(null);
		this.btnAgregarButton.setEnabled(false);
		panel_3.add(btnAgregarButton);
	}

	public String getNickname() {
		return textFieldNickname.getText();
	}

	public String getIp() {
		return this.textFieldIP.getText();
	}

	public String getPuerto() {
		return this.textFieldPuerto.getText();
	}

	private void validarCampos() {
		String nickname = textFieldNickname.getText().trim();
		String ip = textFieldIP.getText().trim();
		String puertoTexto = textFieldPuerto.getText().trim();

		boolean camposLlenos = !nickname.isEmpty() && !ip.isEmpty() && !puertoTexto.isEmpty();
		boolean puertoValido = false;

		try {
			int puerto = Integer.parseInt(puertoTexto);
			puertoValido = puerto > 1023 && puerto < 65536;
		} catch (NumberFormatException e) {
			puertoValido = false;
		}

		btnAgregarButton.setEnabled(camposLlenos && puertoValido);
	}

	public void mostrarErrorContactoYaAgendado() {
		JOptionPane.showMessageDialog(this, "El contacto que intent�s agregar ya est� en tu agenda.",
				"Contacto ya agendado", JOptionPane.ERROR_MESSAGE);
	}

	public void mostrarErrorUsuarioNoDisponible() {
		JOptionPane.showMessageDialog(this, "Error:Usuario no disponible para agregar", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		validarCampos();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void setActionListener(ActionListener controlador) {
		// TODO Auto-generated method stub
		this.btnAgregarButton.addActionListener(controlador);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public void mostrarConfirmacionContactoAgregado() {
		JOptionPane.showMessageDialog(this, "�Contacto agregado exitosamente!", "Registro exitoso",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarErrorNoPuedoAgregarme() {
		JOptionPane.showMessageDialog(this,
				"El puerto ingresado no puede ser el mismo que el suyo.\nPor favor, ingrese un puerto distinto.",
				"Puerto inv�lido", JOptionPane.WARNING_MESSAGE);
	}

	public void vaciarTextFieldPuerto() {
		this.textFieldPuerto.setText("");
	}

	public void deshabilitarBoton() {
		this.btnAgregarButton.setEnabled(false);
	}
}
