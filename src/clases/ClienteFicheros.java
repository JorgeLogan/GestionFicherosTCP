package clases;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import paquetes.Paquete;

public class ClienteFicheros extends ClaseBase{
	
	/**
	 * Pedido por el IDE
	 */
	private static final long serialVersionUID = 1L;
	private Socket socketCliente;
	private ObjectOutputStream objSalida;
	private ObjectInputStream objEntrada;
	
	
	public ClienteFicheros() {
		super();
		
	}
	
	public static void main(String[] args) {
		System.out.println("Cliente de gestor de ficheros TCP");
		new ClienteFicheros();
	}

	@Override
	protected void avisarCambios() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean conectarTCP() {
		boolean resultado = false;
		
		try {
			// Abrimos la conexion
			this.socketCliente = new Socket(IP, PUERTO);
			this.gestionBotones(true);
			resultado = true;
		}
		catch(Exception e) {
			System.out.println("No se pudo conectar el cliente! " + e.getMessage());
		}
		return resultado;
	}

	@Override
	protected void desconectarTCP() {
		// Como no estamos logeados para nada en especial, podemos salir cuando queramos
		try {
			this.socketCliente.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.gestionBotones(false);
	}

	@Override
	protected void clickSubir() {
		System.out.println("Empezamos buscando un archivo para subir");
		JFileChooser selector = new JFileChooser();
		
	}

	@Override
	protected void clickDescargar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clickSalir() {
		try {
			if(this.socketCliente!= null) this.socketCliente.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dispose();
	}

	/**
	 * Para empezar, el cliente se conectará, y acto seguido, enviara un paquete donde querra recibir
	 * el listado de archivos que tiene en la carpeta compartida
	 */
	@Override
	protected void clickConectar() {
		
		if(this.conectarTCP() == true) {
			JOptionPane.showMessageDialog(this, "Conectado al servidor!");
			
			// Pido al servidor los datos de la carpeta
			this.pedirArchivos();
			
			// Espero respuesta
			
			
		}else {
			JOptionPane.showMessageDialog(this, "No te has podido conectar al servidor");
		}
	}

	private void pedirArchivos() {
		// preparo la informacion a enviar al servidor, pidiendole los nombres de la carpeta de archivos
		Paquete paquete = new Paquete(Paquete.OPCIONES.LEER);
		
		try {
			// Preparo y envio la peticion al hilo del servidor
			this.objSalida = new ObjectOutputStream(this.socketCliente.getOutputStream());
			objSalida.writeObject(paquete);
			
			// Ahora toca recibir el paquete
			this.objEntrada = new ObjectInputStream(this.socketCliente.getInputStream());
			paquete =  (Paquete)this.objEntrada.readObject();
			
			// Comprobamos si el paquete vino bien
			if(paquete.isOperacionOK()) {
				this.modeloFicheros.clear();
				// Tambien puede ser que no tengamos el titulo con nuestro id cliente, asi que usamos
				//   la reutilizacion de la variable de nombre de archivo para ello
				this.setTitle(paquete.getNombreArchivo());
				
				// Ahora si, pasamos los archivos al listado
				for(String archivo : paquete.getArchivos()) this.modeloFicheros.addElement(archivo);
			}
			else {
				JOptionPane.showMessageDialog(this, "No se pudo realizar la operacion!");
			}			
		} 
		catch (Exception e) {
			System.out.println("Error en el cliente al intentar pedir los ficheros de la carpeta: " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
		}
	}
	
	@Override
	protected void clickDesconectar() {
		try {
			this.objSalida = new ObjectOutputStream(this.socketCliente.getOutputStream());
			this.objSalida.writeObject(new Paquete(Paquete.OPCIONES.SALIR));
			this.objSalida.close();
		} catch (IOException e) {
			System.out.println("Error cerrando los flujos del cliente. Ya han sido cerrados por el servidor");
		}
		this.desconectarTCP();		
	}
}
