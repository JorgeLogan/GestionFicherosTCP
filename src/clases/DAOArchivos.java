package clases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

public class DAOArchivos {

	public static File getArchivo() {
		File archivo = null;
		JFileChooser selector = new JFileChooser();
		if(selector.showDialog(null, "Selecciona archivo...") == JFileChooser.APPROVE_OPTION) {
			archivo = selector.getSelectedFile();
		}
		return archivo;
	}
	
	// Para grabar un archivo basado en un array de bytes
	public static File convertirBytesToArchivo(String nombreArchivo, byte[] buffer) {
		File archivo = null;
			
		try {
			FileOutputStream flujoArchivo = new FileOutputStream(nombreArchivo);
			flujoArchivo.write(buffer);
			flujoArchivo.close();
		}
		catch(Exception e) {
			System.out.println("Error al convertir el flujo de bytes a un archivo: " + e.getMessage());
		}
		
		return archivo;
	}
	
	// Para convertir un objeto tipo File a array de bytes
	public static byte[] convertirFileToBytes(File archivo) {
		byte[] buffer = null;
		
		FileInputStream salida;
		try {
			salida = new FileInputStream(archivo);
			buffer = salida.readAllBytes();
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
		
		return buffer;
	}
}
