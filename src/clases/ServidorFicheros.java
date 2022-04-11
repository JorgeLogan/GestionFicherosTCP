package clases;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Creo un servidor de archivos, que creara un hilo por cada cliente que se conecte a su red
 * 
 * @author Jorge
 *
 */
public class ServidorFicheros extends ClaseBase implements Runnable {
	
	/**
	 * Pedido por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	boolean salir = false;
	ServerSocket servidorTCP = null;
	List<Socket> listaClientes = new LinkedList<Socket>();
	

	public static void main(String[] args) {
		System.out.println("Servidor de ficheros por TCP");
		new ServidorFicheros();
	}
	
	public ServidorFicheros() {
		super();
		this.setTitle("Servidor de Ficheros TCP");
	}

	@Override
	public boolean conectarTCP() {
		boolean resultado = false;
		try {
			this.servidorTCP = new ServerSocket(PUERTO);
			resultado = true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultado;
	}
	
	
	@Override
	public void desconectarTCP() {
		if(this.servidorTCP!= null) {
			
			// Cerramos cada cliente
			for(Socket cliente : this.listaClientes) this.desconectarCliente(cliente);
			
			try {				
				// Cerramos el servidor
				this.servidorTCP.close();
			} catch (IOException e) {
				System.out.println("Error al desconectar: " + e.getMessage());
			}
		}
	}
	
	@Override
	protected void clickSubir() {
		System.out.println("Intento abrir dialogo");
		
		// Abrimos un dialogo para escoger archivos
		JFileChooser d  = new JFileChooser(CARPETA);
		d.setMultiSelectionEnabled(true);
		if(d.showDialog(this, "Selecciona un fichero") == JFileChooser.APPROVE_OPTION) {
			File[] seleccionados = d.getSelectedFiles(); 
			for(int i=0; i<seleccionados.length; i++)
				System.out.println("OK!!! " + seleccionados[i].getName());
		}
		
		// Una vez tenemos los archivos, vamos a intentar grabarlos en la carpeta.. siempre que no existan...
		// En ese caso habria que confirmar la grabacion
	}

	// Para saber si existe un archivo
	public boolean existeArchivoEnCarpeta(String archivo) {
		File arch = new File(CARPETA + archivo);
		return arch.exists();
	}
	
	// Para grabar un archivo en la carpeta
	public void grabarArchivo(File archivo) {
		
	}
	
	@Override
	protected void clickBajar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clickSalir() {
		// TODO Auto-generated method stub
		this.dispose();
		
	}

	@Override
	protected void avisarCambios() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clickConectar() {
		if(this.conectarTCP()) {
			JOptionPane.showMessageDialog(this, "Conectado!");
			String archivos[] = this.leerCarpeta();
			this.pasarListadoToVentana(archivos);
		}
		else {
			JOptionPane.showMessageDialog(this,"No se pudo conectar, lo siento");
		}
		
	}

	@Override
	protected void clickDesconectar() {
		this.desconectarTCP();
	}
	
	private void desconectarCliente(Socket socket) {
		try {
			socket.close();
		}catch(Exception e) {
			System.out.println("Error al cerrar el socket cliente: " + e.getMessage());
		}
	}

	/**
	 * Metodo de la interfaz runnable
	 */
	@Override
	public synchronized void run() {
		
		while(this.salir == false) {
			try {
				// Nos ponemos a la espera de clientes
				Socket sCliente = this.servidorTCP.accept();
				

				// Si llegamos aqui, es que ya acepto alguno, asi que lo pasamos al listado
				this.listaClientes.add(sCliente);
				
				// Aqui no necesito nicks ni nada, como los acepto directamente, no les pongo filtro
				// Lo que si voy a hacer, es crear un hilo para el
				
				
			}
			catch(Exception e) {
				
				
			}
		}
		
	}
}
