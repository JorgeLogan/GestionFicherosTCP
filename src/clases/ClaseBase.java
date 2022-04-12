package clases;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;

import paquetes.Paquete;
import vistas.VistaGestor;

public abstract class ClaseBase extends VistaGestor{
	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos
	public final static String CARPETA = "C:\\Pruebas\\";
	public final static int TAM_MAX_PAQUETE = 1000;
	public final static int PUERTO = 5678;
	public final static String IP = "192.168.0.103";
	

	/**
	 * Funcion para leer datos de la carpeta
	 * @return devuelve los nombres de los archivos
	 */
	public String[] leerCarpeta() {
		String listado[] = null;
		System.out.println("Intentamos leer la carpeta: " + CARPETA);
		try {
			File carpeta = new File(CARPETA);
			listado = carpeta.list();
		}
		catch(Exception e) {
			System.out.println("Error leyendo la carpeta. Error: " + e.getMessage());
		}
		System.out.println("Encontramos " + listado.length + " archivos y directorios");
		return listado;
	}
	
	// Cada vez que alguien haga algun cambio deberia notificar a los clientes/servidor para que se
	// actualize el listado de nuevo:
	//		El servidor debe avisar a los clientes
	//		Los clientes al servidor para que éste actualize al resto
	protected abstract void avisarCambios();
	
	
	// Funciones de conversion
	// Funcion para convertir un conjunto de bytes en un tipo de paquete de datos
	protected File convertirBytesToFichero(byte[] datos) {
		File paquete = null;
		
		return paquete;
	}
	
	/**
	 * Creo una funcion que me convierta el paquete de datos a un array de bytes
	 * @param paquete e paquete a transfomar
	 * @return el array de bytes que forma el objeto
	 */
	protected byte[] convertirPaquetesToByte(Paquete paquete) {
		byte[] buffer;
		
		// Preparamos el flujo de array de bytes
		ByteArrayOutputStream salidaBytes = new ByteArrayOutputStream();
		try {
			// Ahora creamos el flujo de objeto con el flujo de bytes como parametros
			ObjectOutputStream salidaPaquete = new ObjectOutputStream(salidaBytes);
			
			// Y grabamos en el flujo de bytes el objeto
			salidaPaquete.writeObject(paquete);
			
			// Cerramos flujos
			salidaPaquete.close();
			salidaBytes.close();
			
		} catch (IOException e) {
			System.out.println("Error al convertir el objeto a array de bytes: " + e.getMessage());
		}
		buffer = salidaBytes.toByteArray();
		
		return buffer;
	}
	
	// Para grabar un archivo basado en un array de bytes
	protected File convertirBytesToArchivo(String nombreArchivo, byte[] buffer) {
		File archivo = null;
		
		try {
			FileOutputStream flujoArchivo = new FileOutputStream(CARPETA + "\\" + archivo);
			flujoArchivo.write(buffer);
			flujoArchivo.close();
		}
		catch(Exception e) {
			System.out.println("Error al convertir el flujo de bytes a un archivo: " + e.getMessage());
		}
		
		return archivo;
	}
	
	
	// Para pasar un listado de strings al listado de la vista
	protected void pasarListadoToVentana(String[] array) {
		
		this.modeloFicheros.clear();
		
		for(int i=0; i<array.length; i++) {
			this.modeloFicheros.addElement(array[i].toString());
			System.out.println(i + " --> " + array[i]);
		}
		System.out.println("Elementos en el modelo: " + this.modeloFicheros.capacity());
		this.listadoFicheros.updateUI();
	}

	protected File abrirCarpetaLocal() {
		File f = null;
		JFileChooser selector = new JFileChooser();
		if(selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			f = selector.getSelectedFile();
		}
		
		return f;
	}
	
	protected abstract boolean conectarTCP();
	protected abstract void desconectarTCP();
}
