package clases;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import paquetes.Paquete;
import paquetes.Paquete.OPCIONES;
import zona_critica.CarpetaArchivos;

/**
 * Clase para ser creada por el servidor y ser un hilo puente entre este y el cliente
 * @author Jorge
 *
 */
public class HiloCliente extends Thread{
	
	private int numId;
	private Socket sCliente;
	private CarpetaArchivos carpeta;
	private boolean salir = false;
	private ServidorFicheros servidor; // Para actualizar el servidor cuando se sube algo
	
	private ObjectInputStream objEntrada;
	private ObjectOutputStream objSalida;
	
	public HiloCliente(int numId, Socket sCliente, CarpetaArchivos carpeta, ServidorFicheros servidor){
		this.numId = numId;
		this.sCliente = sCliente;
		this.carpeta = carpeta;
		this.servidor = servidor;
		
		// Preparamos el objeto de salida
		try {
			this.objSalida = new ObjectOutputStream(this.sCliente.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Error creando el hilo de Servidor/Cliente. No se creara hilo");
		}
	}
	
	/**
	 * Creo una funcion para actualizar el servidor.
	 * @param archivos los archivos a pasar
	 */
	private void actualizarServidor() {
		this.servidor.actualizarListado();
	}
	
	@Override
	public void run() {
		System.out.println("Hilo cliente " + this.numId + " creado e iniciado!!");
		while(this.salir == false) {
			// Nos protegemos con un try catch
			try {
				System.out.println("Esperamos peticion del cliente...");
				
				// Nos ponemos a la espera de informacion del cliente
				this.objEntrada = new ObjectInputStream(this.sCliente.getInputStream());
				
				// Ya la recibimos, ahora tenemos que transformarla en el paquete
				Paquete paquete = (Paquete)this.objEntrada.readObject();
				
				System.out.println("Recibimos el paquete " + paquete.toString());
				
				// Trabajamos con el paquete, segun lo que pida (en el primero turno sabemos que quiere.. 
				//en el resto, no
				switch(paquete.getOpcion()) {
				case LEER:
					paquete.setArchivos(this.carpeta.leerCarpeta());
					// Al leer la carpeta no necesitamos usar nombreArchivo en el paquete, asi que lo reutilizo
					// para mandar el nombre del cliente, y no meter mas atributos
					paquete.setNombreArchivo("Cliente " + this.numId);
					paquete.setOperacionOK(true);
					System.out.println("Paquete con los archivos preparado");
					break;
				case SUBIR:
					paquete.setOperacionOK(this.carpeta.grabarArchivo(paquete.getBuffer(), paquete.getNombreArchivo()));
					// Como se ha grabado el archivo, tambien se ha cambiado la estructura de la carpeta, asi que la pasamos tb
					paquete.setArchivos(this.carpeta.leerCarpeta());
					this.actualizarServidor(); // Tambien hacemos que se actualice el servidor
					System.out.println("Archivo grabado e interfaz servidor actualizada. Preparado paquete respuesta con los archivos actuales");
					break;
				case DESCARGAR:
					byte[] datos = this.carpeta.cargarArchivo(paquete.getNombreArchivo());
					paquete.setBuffer(datos);
					System.out.println("Preparado paquete con los datos a descargar. Tamanio: " + paquete.getBuffer().length);
					break;
				case SALIR:
					this.salir = true;
					System.out.println("Recibimos peticion de cierre del cliente");
				}

				// Ahora ya tenemos el paquete preparado, asi que lo enviamos si no queremos salir
				if(this.salir == false) {
					
					this.objSalida.writeObject(paquete);	
					System.out.println("Paquete enviado al cliente con los datos solicitados");
				}
			}
			catch(Exception e) {
				System.out.println("Error en el hilo del cliente " + this.numId + ": " + e.getMessage());
				this.salir = true;
			}
		}
		// Cerramos flujos
		try {
			this.objSalida.close();
			this.objEntrada.close();
			System.out.println("Cerramos flujos");
		} catch (IOException e) {
			System.out.println("Error cerrando los flujos del hilo cliente/servidor (servidor)");
		}
		
		System.out.println("Cerrado hilo del cliente/servidor num. " + this.numId);
	}
	
	public String[] leerArchivos(){
		String[] lista = null;
				
		lista = this.carpeta.leerCarpeta();
		
		return lista;
	}
	
	// Creo este metodo para enviar desde el servidor un paquete de actualizacion
	public void actualizar() {
		Paquete paquete = new Paquete(OPCIONES.LEER);
		String[] archivos = this.carpeta.leerCarpeta();
		paquete.setArchivos(archivos);
		paquete.setOperacionOK(true);
		try {
			this.objSalida.writeObject(paquete);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
