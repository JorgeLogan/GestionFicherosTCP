package clases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

public class DAOArchivos {

	// Para cargar un archivo con un flie chooser
	public static File getArchivo() {
		File archivo = null;
		JFileChooser selector = new JFileChooser();
		if(selector.showDialog(null, "Selecciona archivo...") == JFileChooser.APPROVE_OPTION) {
			archivo = selector.getSelectedFile();
		}
		return archivo;
	}
	
	// Para grabar un archivo en una carpeta con un jfilechooser
	public static void grabarEnDirectorio(byte[] datos, String nombreArchivo) {
		// Buscamos donde queremos guardarlo y con que nombre
		JFileChooser dialogo = new JFileChooser();
		dialogo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File fArchivo = null;
		if(dialogo.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println("Aceptamos guardar en " + dialogo.getSelectedFile()+ File.separator + nombreArchivo);
			fArchivo = new File(dialogo.getSelectedFile() + File.separator + nombreArchivo);
			
			if(datos == null) System.out.println("DATOS NULOS");
			
			try {
				FileOutputStream flujo = new FileOutputStream(fArchivo);
				flujo.write(datos);
				flujo.close();
				System.out.println("Archivo guardado");
			}
			catch(Exception e) {
				System.out.println("No se pudo grabar el archivo: " + e.getMessage());
			}
		}
	}
	
	// Para convertir en un file basandose en un array de bytes
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
