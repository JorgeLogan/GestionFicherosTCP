package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public abstract class VistaGestor extends JFrame  implements ActionListener{

	/**
	 * Pedido por el IDE
	 */
	private static final long serialVersionUID = 1L;

	// Atributos
	protected JList<String> listadoFicheros;
	protected DefaultListModel<String> modeloFicheros;
	
	private JLabel lblCarpetaServer;
	private JButton btnConectar;
	private JButton btnDesconectar;
	private JButton btnBajar;
	private JButton btnSubir;
	private JButton btnSalir;
	
	public VistaGestor() {
		super();
		this.colocarComponentes();
	}
	
	public void colocarComponentes() {
		// Creamos un panel general
		JPanel pGeneral = new JPanel(new BorderLayout());
		this.add(pGeneral);
		pGeneral.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		// Creo un panel para los botones de conexion/desconexion
		
		// Creo un panel para la zona de ficheros que tendra la etiqueta, y el listado ficheros
		
		
		// Creo el panel superior para conectar/desconectar
		JPanel pSuperior = new JPanel(new GridLayout(1,3));
		pGeneral.add(pSuperior, BorderLayout.NORTH);
		pSuperior.setBorder(this.bordePersonalizado());
		this.btnConectar = new JButton("Conectar");
		this.btnDesconectar = new JButton("Desconectar");
		this.btnConectar.addActionListener(this);
		this.btnDesconectar.addActionListener(this);

		pSuperior.add(this.btnConectar);
		pSuperior.add(new JLabel(" "));
		pSuperior.add(this.btnDesconectar);

		
		JPanel pCentral = new JPanel();
		pGeneral.add(pCentral, BorderLayout.CENTER);
		
		
		JPanel pFicheros = new JPanel();
		pFicheros.setLayout(new BoxLayout(pFicheros, BoxLayout.Y_AXIS));
		pCentral.add(pFicheros);
		
		this.lblCarpetaServer =new JLabel("Carpeta Servidor");
		pFicheros.add(this.lblCarpetaServer);
		
		// Para el listado, debemos asignarle su modelo
		this.listadoFicheros = new JList<String>();
		this.modeloFicheros = new DefaultListModel<String>();
		this.listadoFicheros.setModel(modeloFicheros);
		this.listadoFicheros.setMinimumSize(new Dimension(200,200));
		
		// Y creamos un scroll que contenga el listado
		JScrollPane scroll = new JScrollPane(this.listadoFicheros);
		scroll.setPreferredSize(new Dimension(200, 200));
		scroll.setMinimumSize(new Dimension(200,200));
		pFicheros.add(scroll);
		
		// Creamos un panel para la zona derecha donde tendremos los botones
		JPanel pBotones = new JPanel(new GridLayout(6,1));
		pCentral.add(pBotones);
		pBotones.setBorder(this.bordePersonalizado());
		this.btnSubir = new JButton("Subir ficheros");
		this.btnBajar = new JButton("Bajar fichero");
		this.btnSalir = new JButton("Salir");
		
		pBotones.add(btnSubir);
		pBotones.add(new JLabel(" "));
		pBotones.add(btnBajar);
		pBotones.add(new JLabel(" "));
		pBotones.add(new JLabel(" "));
		pBotones.add(btnSalir);

		// Damos funcionamiento a los botones
		this.btnSubir.addActionListener(this);
		this.btnBajar.addActionListener(this);
		this.btnSalir.addActionListener(this);
		
		// Damos el funcionamiento a la ventana
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
	}
	
	// Para generar un borde un poco bonito
	private Border bordePersonalizado() {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.blue),
				BorderFactory.createEmptyBorder(5,5,5,5)
				);
	}
	
	// Clases abstractas para implementar en los hijos
	protected abstract void clickSubir();
	protected abstract void clickDescargar();
	protected abstract void clickSalir();
	protected abstract void clickConectar();
	protected abstract void clickDesconectar();
	
	// Funcion para el control de eventos
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "Subir ficheros":
			System.out.println("Pulsado subir");
			this.clickSubir();
			break;
		case "Bajar fichero":
			System.out.println("Pulsado bajar");
			this.clickDescargar();
			break;
		case "Salir":
			System.out.println("Pulsado salir");
			this.clickSalir();
			break;
		case "Conectar":
			System.out.println("Pulsado conectar");
			this.clickConectar();
			break;
		case "Desconectar":
			System.out.println("Pulsado desconectar");
			// Limpiamos el listado antes de nada
			this.modeloFicheros.clear();
			this.clickDesconectar();
			break;
		}
	}
}
