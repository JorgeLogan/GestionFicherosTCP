package clases;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import paquetes.Paquete;
import paquetes.Paquete.OPCIONES;

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
	protected void clickSalir() {
		this.clickDesconectar();
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

	@Override
	protected void subir() {
		// Primero eligo que archivo quiero subir
		File archivo = DAOArchivos.getArchivo();
		
		// Ahora, compruebo que no exista ese archivo en el listado
		if(this.modeloFicheros.contains(archivo.getName())) {
			
			System.out.println("Ya existe el archivo " + archivo.getName() + "... pido confirmacion");
			if(JOptionPane.showConfirmDialog(this, "El archivo " + archivo.getName() + 
					" ya existe... ¿ Quieres sobreescribir?") == JOptionPane.OK_OPTION) {
			
				System.out.println("Acepta sobreescribir");
				// Mandamos grabar el archivo
				this.enviarServidorSubir(archivo);
			}
			else {
				System.out.println("No acepta sobreescribir, asi que no hacemos nada");
			}
		}
		else { // No existia el archivo, asi que lo grabo sin medias tintas :)
			this.enviarServidorSubir(archivo);
		}
	}

	// Funcion para enviar un archivo al servidor para grabarlo en la zona critica.
	// Tambien recibira paquete de vuelta con la info de la carpeta actualizada
	public void enviarServidorSubir(File archivo) {
		// Preparo un paquete para subir el fichero
		byte[] buffer = DAOArchivos.convertirFileToBytes(archivo);
		Paquete paquete = new Paquete(OPCIONES.SUBIR, buffer, archivo.getName());
		
		// Y ahora usamos nuestro propio amazon y enviamos el paquete y esperamos respuesta
		Paquete respuesta = this.enviarPaqueteParaRecibirRespuesta(paquete);
		
		if(respuesta != null) {
			// Limpiamos el listado, y pasamos los elementos recibidos en el paquete
			this.modeloFicheros.clear();
			for(String nombre : paquete.getArchivos()) this.modeloFicheros.addElement(nombre);
			System.out.println("recibida respuesta y actualizados los datos");	
		}
	}
	
	// Funcion para descargar el fichero seleccionado del JList
	@Override
	protected void descargar() {
		String archivo = this.listadoFicheros.getSelectedValue();
		
		if(archivo == null) return;
		System.out.println("Intentamos descargar el fichero " + archivo);
		
		// Praparamos el paquete de envio
		Paquete paquete = new Paquete(OPCIONES.DESCARGAR, archivo);
		
		// Lo enviamos y recibimos respuesta
		Paquete respuesta = this.enviarPaqueteParaRecibirRespuesta(paquete);
	
		// Si la respuesta no es null, ha ido bien, e intentaremos guardar el fichero
		if(respuesta != null) {
			DAOArchivos.grabarEnDirectorio(respuesta.getBuffer(), respuesta.getNombreArchivo());
		}
	}
	
	private Paquete enviarPaqueteParaRecibirRespuesta(Paquete paquete) {
		Paquete respuesta = null;
		
		try {
			// Preparamos y enviamos el paquete.
			this.objSalida = new ObjectOutputStream(this.socketCliente.getOutputStream());
			this.objSalida.writeObject(paquete);
			
			// Ya lo enviamos, ahora esperamos respuesta			
			respuesta = (Paquete)this.objEntrada.readObject();
		}
		catch(Exception e) {
			
		}
		
		// Devolvemos la respuesta o null si fallo algo
		return respuesta;
	}
}
