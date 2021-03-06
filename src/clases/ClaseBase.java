package clases;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import paquetes.Paquete;
import vistas.VistaGestor;
import zona_critica.CarpetaArchivos;

/**
 * Clase Base.
 * De esta clase heredaran tanto servidor como clientes
 * Es necesario tener en C:\ la carpeta Pruebas para poder trabajar, ya que es la carpeta compartida
 * 
 * @author Jorge Alvarez Ceñal
 *
 */
public abstract class ClaseBase extends VistaGestor{
	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos
	public final static String CARPETA = "C:\\Pruebas\\";
	public final static int TAM_MAX_PAQUETE = 1000;
	public final static int PUERTO = 5678;
	public final static String IP = "localhost";// "192.168.0.3";
	CarpetaArchivos carpeta; // La zona critica
	
	
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

	
	// Para pasar un listado de strings al listado de la vista
	// Sin el sync, la mayoria de las veces no me pasaba el modelo, porque lo vaciaba una vez
	// rellenado. No se si es un bug. NOTA: Los Modeler estos son un asco
	protected synchronized void pasarListadoToVentana(String[] array) {
		this.modeloFicheros.clear();
		for(int i=0; i<array.length; i++) {
			this.modeloFicheros.addElement(array[i].toString());
			
		}
		System.out.println("Num elementos cargados: "  + array.length);
	}
	
	
	/**
	 * Funcion para el click del boton de subir archivo
	 */
	@Override
	protected void clickSubir() {
		this.subir();
	}

	
	/**
	 * Funcion para el click del boton de descargar archivo
	 */
	@Override
	protected void clickDescargar() {
		this.descargar();		
	}
	
	
	// Funciones abstractas para implementarlas en las clases finales
	protected abstract void subir();
	protected abstract void descargar();
	protected abstract boolean conectarTCP();
	protected abstract void desconectarTCP();
}
