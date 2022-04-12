package clases;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import paquetes.Paquete;
import zona_critica.CarpetaArchivos;

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

	// Atributos
	boolean salir = false;
	ServerSocket servidorTCP = null;
	List<Socket> listaClientes = new LinkedList<Socket>();
	CarpetaArchivos carpeta;

	/**
	 * Clase principal que hara el servidor ejecutable
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Servidor de ficheros por TCP");
		new ServidorFicheros();
	}
	
	/*^^
	 * Constructor de la clase
	 */
	public ServidorFicheros() {
		super();
		this.setTitle("Servidor de Ficheros TCP");
		this.carpeta = new CarpetaArchivos();
	}

	/*
	 * Metodo sobreescrito de la clase base para conectar por TCP
	 */
	@Override
	public boolean conectarTCP() {
		boolean resultado = false;
		try {
			this.servidorTCP = new ServerSocket(PUERTO);
			Thread miHilo = new Thread(this);
			miHilo.start();
			
			resultado = true;
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultado;
	}
	
	/**
	 * Metodo sobreescrito de la clase base para desconectar por TCP
	 */
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
	
	/**
	 * El servidor no necesita enviar un paquete, para trabajar en la zona critica, ya que la carpeta esta en su equipo.
	 */
	@Override
	protected void clickSubir() {
		System.out.println("Intento abrir dialogo");
		
		// Abrimos un dialogo para escoger archivos
		JFileChooser d  = new JFileChooser();
		if(d.showDialog(this, "Selecciona un fichero") == JFileChooser.APPROVE_OPTION) {
			File seleccionado = d.getSelectedFile(); 
			
			// Convertimos el archivo a bytes
			byte[] buffer = null;
			try {
				FileInputStream lectorFichero = new FileInputStream(seleccionado.getAbsolutePath());
				buffer = lectorFichero.readAllBytes();
				lectorFichero.close();
				System.out.println("Guardare " + seleccionado.getName());
				this.carpeta.grabarArchivo(buffer,seleccionado.getName());
			} catch (Exception e) {
				System.out.println("No se pudo guardar el archivo: " + e.getMessage());
				buffer = null;
			}
			
			// Si todo fue bien, ya tenemos el buffer para grabarlo
			if(buffer!= null) {
				if(this.carpeta.grabarArchivo(buffer, seleccionado.getName())) {
					JOptionPane.showMessageDialog(this, "Archivo guardado!");
					
					//Actualizamos el listado
					String archivos[] = this.carpeta.leerCarpeta();
					this.pasarListadoToVentana(archivos);
				}else {
					System.out.println("No se pudo guardar el archivo");
				}
			}
			else {
				System.out.println("No se pudo guardar el archivo. Buffer nulo");
			}

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
	public void subirArchivo(File archivo) {
		
	}
	
	@Override
	protected void clickDescargar() {
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
			String archivos[] = this.carpeta.leerCarpeta();
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

	/**************************************************************************************************************
	 * 
	 * Metodo de la interfaz runnable
	 * 
	 * ************************************************************************************************************
	 */
	@Override
	public void run() {
		System.out.println("Inicio del hilo de escucha del servidor.");
		int indiceClientes = 1;
		while(this.salir == false) {
			try {
				System.out.println("Servidor a la escucha de clientes...");
				// Nos ponemos a la espera de clientes
				Socket sCliente = this.servidorTCP.accept();
				
				System.out.println("Cliente recibido!");
				// Si llegamos aqui, es que ya acepto alguno, asi que lo pasamos al listado
				this.listaClientes.add(sCliente);
				
				// Aqui no necesito nicks ni nada, como los acepto directamente, no les pongo filtro
				// Lo que si voy a hacer, es crear un hilo para el con la clase critica
				HiloCliente hilo = new HiloCliente(indiceClientes, sCliente, this.carpeta);
				indiceClientes++;
				
			}
			catch(Exception e) {
				this.salir = true;
				System.out.println("Error en la escucha del servidor dedicada a la espera a clientes: " + e.getMessage());
				
			}
		}
		
	}
}
