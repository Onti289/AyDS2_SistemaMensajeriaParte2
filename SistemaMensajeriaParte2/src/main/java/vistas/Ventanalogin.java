package vistas;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import controlador.*;
import util.Util;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JButton;

public class Ventanalogin extends JFrame implements IVista, ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsuario;
	private JButton botonRegistrar;
	private JTextField textFieldPuerto;
	private ControladorUsuario controlador;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public Ventanalogin(ControladorUsuario controlador) {
		this.controlador = controlador;
		setTitle("Sistema de mensajer a");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 308, 227);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(2, 2, 0, 0));

		JLabel lblNewLabel_1 = new JLabel("NickName:");
		panel.add(lblNewLabel_1);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setVgap(25);
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_2);

		txtUsuario = new JTextField();
		panel_2.add(txtUsuario);
		txtUsuario.setColumns(10);
		this.txtUsuario.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validarCampos();
			}
		});

		JLabel lblNewLabel = new JLabel("Puerto(1024<P<65536):");
		panel.add(lblNewLabel);

		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_3.getLayout();
		flowLayout_1.setVgap(25);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel.add(panel_3);

		this.textFieldPuerto = new JTextField();
		panel_3.add(this.textFieldPuerto);
		this.textFieldPuerto.setColumns(10);
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

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.botonRegistrar = new JButton("Registrar");
		this.botonRegistrar.setActionCommand(Util.CTEREGISTRAR);
		this.botonRegistrar.setToolTipText("Registrar");
		this.botonRegistrar.setEnabled(false);
		panel_1.add(this.botonRegistrar);
	}

	// Lo de abajo posiblemente se borra
	public String getUsuario() {
		return txtUsuario.getText();
	}

	public String getPuerto() {
		return this.textFieldPuerto.getText();
	}

	private void validarCampos() {
		String usuario = getUsuario();
		String puerto = getPuerto();
		boolean habilitar = !usuario.isEmpty() && !puerto.isEmpty();

		try {
			int p = Integer.parseInt(puerto);
			habilitar = habilitar && p > 1024 && p < 65536;
		} catch (NumberFormatException e) {
			habilitar = false;
		}

		botonRegistrar.setEnabled(habilitar);
	}

	public void muestraErrorPuertoEnUso() {
		JOptionPane.showMessageDialog(null,
				"El puerto ingresado ya est  en uso.\nPor favor, eleg  otro puerto entre 1025 y 65535.",
				"Error: Puerto en uso", JOptionPane.ERROR_MESSAGE);

	}

	public void vaciarTextFieldPuerto() {
		this.textFieldPuerto.setText("");
	}

	public void deshabilitarBoton() {
		this.botonRegistrar.setEnabled(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setActionListener(ActionListener controlador) {
		// TODO Auto-generated method stub
		this.botonRegistrar.addActionListener(controlador);
	}

}
