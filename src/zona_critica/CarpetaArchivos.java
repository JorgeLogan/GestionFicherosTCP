package zona_critica;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CarpetaArchivos {

	public final static String CARPETA = "C:\\Pruebas\\";
	private boolean ocupado = false; // para gestionar la zona critica
	
	/**
	 * Funcion para extraer los archivos y directorios de la carpeta compartida
	 * @return un array de string que seran los nombres de archivo
	 */
	public synchronized String[] leerCarpeta() {
		String[] archivos = null;
		
		// Esperamos nuestro turno para acceder a la carpeta
		while(this.ocupado) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.ocupado = true;
		
		File lector = new File(CARPETA);
		archivos = lector.list();
		
		// Liberamos el ocupado y notificamos
		this.ocupado = false;
		notify();
		
		return archivos;
	}
	
	/**
	 * Funcion para saber si ya existe un archivo
	 * @param archivo El nombre de archivo a buscar
	 * @return true si existe, false si no
	 */
	public synchronized boolean existeArchivo(String archivo) {
		File fArchivo = new File(archivo);
		return fArchivo.exists();
	}
	
	/**
	 * Funcion para grabar un flujo de bytes en un fichero.
	 * No comprueba si existe, simplemente lo graba (y sobreescribe si existe)
	 * @param archivo el nombre del archivo a grabar
	 * @return true si pudo grabar, false si por lo que sea, no pudo
	 */
	public synchronized boolean grabarArchivo(byte[] datos, String nombreArchivo) {
		boolean resultado = false;
		
		// Me pongo a la espera, o tiro a trabajar si la zona no esta ocupada
		while(this.ocupado) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.ocupado = true;
		
		// Preparo el stream de array de bytes
		ByteArrayOutputStream flujoDatos = new ByteArrayOutputStream();
		try {
			// Preparo el flujo de datos de ficheros de salida para grabar el archivo
			FileOutputStream salida = new FileOutputStream(CARPETA + nombreArchivo);
			
			// Grabo los datos
			salida.write(datos, 0, datos.length);
			
			// Cierro los flujos
			salida.close();
			flujoDatos.close();
			
			// Y pongo resultado a true, porque si llego aqui, no hubo problemas
			resultado = true;
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Ya puedo soltar el ocupado, y notificar al resto de los clientes que acabamos en la Zona Critica
		this.ocupado = false;
		notifyAll();
		
		// Devolvemos el resultado
		return resultado;
	}
	
	/**
	 * Funcion para guardar un archivo de tipo file en la carpeta
	 * @param archivo el archivo a guardar en la zona critica
	 * @return true si puede grabarlo, false si no
	 */
	public synchronized boolean grabarArchivo(File archivo) {
		boolean resultado = false;
		
		while(this.ocupado) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.ocupado = true;
		
		// Ya podemos trabajar
		try {
			// Preparo el flujo de salida
			FileOutputStream flujoSalida = new FileOutputStream(CARPETA + archivo.getName());
			
			// Preparo el archivo pasado
			FileInputStream flujoEntrada = new FileInputStream(archivo);
			
			// Ahora grabamos en la salida los datos del de entrada
			flujoSalida.write(flujoEntrada.readAllBytes());
			
			// Cerramos flujos
			flujoSalida.close();
			flujoEntrada.close();
			
			// Retornariamos true si llegamos aqui, porque saldria todo bien
			resultado = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultado = false;
		}
		
		// Liberamos y avisamos
		this.ocupado = false;
		notifyAll();
		
		return resultado;
	}
	
	/**
	 * Funcion para cargar de la carpeta de zona critica los datos de un fichero
	 * @param archivo el archivo del cual extraer la informacion 
	 * @return el conjunto de bytes del que esta formado el fichero
	 */
	public synchronized byte[] cargarArchivo(String archivo) {
		byte[] buffer = null;
		
		while(this.ocupado) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Si llegamos aqui, cogemos el banderin de ocupado
		this.ocupado = true; 
		
		// Y nos ponemos a trabajr con ell fichero
		try {
			FileInputStream lectorFichero = new FileInputStream(new File(CARPETA + archivo));
			buffer = lectorFichero.readAllBytes();
			lectorFichero.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			buffer = null;
		}
		
		// Ya podemos liberar el ocupado y notificar al resto de clientes
		this.ocupado = false;
		this.notifyAll();
		
		// Devolvemos el buffer
		return buffer;
	}
}
