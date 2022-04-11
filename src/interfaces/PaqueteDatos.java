package interfaces;

import java.io.Serializable;

public class PaqueteDatos implements Serializable{

	/**
	 * Pedida por el IDE
	 */
	private static final long serialVersionUID = 1L;

	// Atributos
	private byte[] buffer; // Los datos del archivo
	private int tamMaximo; // El tama�o m�ximo del paquete de datos
	private String nombreArchivo; // El nombre del archivo que pasamos
	
	// Por si el archivo es muy grande, nos vendra bien un indice de que paquete es
	// y cuantos son en total.
	private int totalPaquetes;
	private int paqueteActual;
	
	// Constructor vacio
	public PaqueteDatos() {}

	// Constructor con los datos
	public PaqueteDatos(byte[] buffer, int tamMaximo, String nombreArchivo, int totalPaquetes, int paqueteActual) {
		super();
		this.buffer = buffer;
		this.tamMaximo = tamMaximo;
		this.nombreArchivo = nombreArchivo;
		this.totalPaquetes = totalPaquetes;
		this.paqueteActual = paqueteActual;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int getTamMaximo() {
		return tamMaximo;
	}

	public void setTamMaximo(int tamMaximo) {
		this.tamMaximo = tamMaximo;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public int getTotalPaquetes() {
		return totalPaquetes;
	}

	public void setTotalPaquetes(int totalPaquetes) {
		this.totalPaquetes = totalPaquetes;
	}

	public int getPaqueteActual() {
		return paqueteActual;
	}

	public void setPaqueteActual(int paqueteActual) {
		this.paqueteActual = paqueteActual;
	}
	
	// Getters y setters
	
	
	
	
	
	
	
}