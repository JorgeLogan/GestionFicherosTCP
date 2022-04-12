package paquetes;

import java.io.Serializable;

/**
 * El paquete de envio de datos
 * Lo hago lo mas sencillo que puedo, le pongo un enum para que no sea muy lioso
 * y le paso un array de bytes donde ira o sera recibida la informacion
 * 
 * @author Jorge
 *
 */
public class Paquete implements Serializable{
	/**
	 * Pedido por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	public static enum OPCIONES { LEER, SUBIR, DESCARGAR, SALIR }; 
	// Atributos
	private OPCIONES opcion;
	private byte[] buffer; // Para la lectura/grabacion de ficheros
	private String nombreArchivo; // Para saber el archivo a grabar/descargar
	private String[] archivos; // Para la lectura de la carpeta
	private boolean operacionOK; // Para las respuestas, que el cliente sepa si fue bien o no.
	
	// Creo una serie de constructores un poco especializados para cada operacion, para minimizar un poco el codigo fuera
	// Constructor base
	public Paquete() {}
	
	// Constructor para peticiones
	public Paquete(OPCIONES opcion) {
		this.opcion = opcion;
	}
	
	// Constructor para envio de bytes de fichero de cara a grabar
	public Paquete(OPCIONES opcion, byte[] buffer, String nombre) {
		this.opcion = opcion;
		this.buffer = buffer;
		this.nombreArchivo = nombre;
	}
	
	// Constructor para paquete con listado de archivos
	public Paquete(String[] archivos) {
		this.archivos = archivos;
	}
	
	// Constructor especializado para paquetes de peticion descarga de archvio
	public Paquete(OPCIONES opcion, String nombreArchivo) {
		this.opcion = opcion;
		this.nombreArchivo = nombreArchivo;
	}

	// Getters y Setters
	public OPCIONES getOpcion() {
		return opcion;
	}


	public void setOpcion(OPCIONES opcion) {
		this.opcion = opcion;
	}


	public byte[] getBuffer() {
		return buffer;
	}


	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public String toString() {
		return "Paquete para " + opcion;
	}

	public String[] getArchivos() {
		return archivos;
	}


	public void setArchivos(String[] archivos) {
		this.archivos = archivos;
	}


	public boolean isOperacionOK() {
		return operacionOK;
	}


	public void setOperacionOK(boolean operacionOK) {
		this.operacionOK = operacionOK;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
}
