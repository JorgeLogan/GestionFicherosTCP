package vistas;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class VistaGestor extends JFrame  implements ActionListener{

	/**
	 * Pedido por el IDE
	 */
	private static final long serialVersionUID = 1L;

	// Atributos
	private JList<String> listadoFicheros;
	private DefaultListModel<String> modeloFicheros;
	
	private JLabel lblCarpetaServer;
	private JButton btnBajar;
	private JButton btnSubir;
	private JButton btnSalir;
	
	public VistaGestor() {
		super();
		this.colocarComponentes();
	}
	
	public void colocarComponentes() {
		// Creamos un panel general
		JPanel pGeneral = new JPanel();
		this.add(pGeneral);
		
		// Creo un panel para la zona de ficheros que tendra la etiqueta, y el listado ficheros
		JPanel pFicheros = new JPanel();
		pFicheros.setLayout(new BoxLayout(pFicheros, BoxLayout.Y_AXIS));
		pGeneral.add(pFicheros);
		
		this.lblCarpetaServer =new JLabel("Carpeta Servidor");
		pFicheros.add(this.lblCarpetaServer);
		
		// Para el listado, debemos asignarle su modelo
		this.listadoFicheros = new JList<String>();
		this.modeloFicheros = new DefaultListModel<String>();
		this.listadoFicheros.setModel(modeloFicheros);
		
		// Y creamos un scroll que contenga el listado
		JScrollPane scroll = new JScrollPane();
		scroll.add(this.listadoFicheros);
		scroll.setPreferredSize(new Dimension(200, 200));
		pFicheros.add(scroll);
		
		// Creamos un panel para la zona derecha donde tendremos los botones
		JPanel pBotones = new JPanel(new GridLayout(8,1));
		pGeneral.add(pBotones);
		
		this.btnSubir = new JButton("Subir ficheros");
		this.btnBajar = new JButton("Bajar fichero");
		this.btnSalir = new JButton("Salir");
		
		pBotones.add(new JLabel(" "));
		pBotones.add(btnSubir);
		pBotones.add(new JLabel(" "));
		pBotones.add(btnBajar);
		pBotones.add(new JLabel(" "));
		pBotones.add(new JLabel(" "));
		pBotones.add(btnSalir);
		pBotones.add(new JLabel(" "));
		// Damos funcionamiento a los botones
		this.btnSubir.addActionListener(this);
		this.btnBajar.addActionListener(this);
		this.btnSalir.addActionListener(this);
		
		// Damos el funcionamiento a la ventana
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	// Clases abstractas para implementar en los hijos
	protected abstract void subir();
	protected abstract void bajar();
	protected abstract void salir();

	// Funcion para el control de eventos
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "Subir ficheros":
			System.out.println("Pulsado subir");
			break;
		case "Bajar fichero":
			System.out.println("Pulsado bajar");
			break;
		case "Salir":
			System.out.println("Pulsado salir");
			break;
		}
	}
}
