package clases;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import paquetes.Paquete;
import zona_critica.CarpetaArchivos;

public class HiloCliente extends Thread{
	private int numId;
	private Socket sCliente;
	private CarpetaArchivos carpeta;
	private boolean salir = false;
	
	private boolean ocupado; // Para los metodos sincronizados. para Leer no es necesario, pero lo hago con todo
	
	private ObjectInputStream objEntrada;
	private ObjectOutputStream objSalida;
	
	public HiloCliente(int numId, Socket sCliente, CarpetaArchivos carpeta){
		this.numId = numId;
		this.sCliente = sCliente;
		this.carpeta = carpeta;
		
		try {
			this.objSalida = new ObjectOutputStream(this.sCliente.getOutputStream());
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void run() {
		System.out.println("Hilo cliente " + this.numId + " creado e iniciado!!");
		while(this.salir == false) {
			// Nos protegemos con un try catch
			try {
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
					break;
				case SUBIR:
					paquete.setOperacionOK(this.carpeta.grabarArchivo(paquete.getBuffer(), paquete.getNombreArchivo()));
					// Como se ha grabado el archivo, tambien se ha cambiado la estructura de la carpeta, asi que la pasamos tb
					paquete.setArchivos(this.carpeta.leerCarpeta());
					break;
				case DESCARGAR:
					break;
				case SALIR:
					this.salir = true;
					System.out.println("Recibimos cierre del cliente");
				}

				// Ahora ya tenemos el paquete preparado, asi que lo enviamos si no queremos salir
				if(this.salir == false) {
					
					this.objSalida.writeObject(paquete);
					//this.objSalida.close();					
				}
			}
			catch(Exception e) {
				System.out.println("Error en el hilo del cliente " + this.numId + ": " + e.getMessage());
				this.salir = true;
			}
		}
		System.out.println("Saliendo de hilo cliente num. " + this.numId);
		// Cerramos flujos
		try {
			this.objSalida.close();
			this.objEntrada.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] leerArchivos(){
		String[] lista = null;
				
		lista = this.carpeta.leerCarpeta();
		
		return lista;
	}
	
	
}
