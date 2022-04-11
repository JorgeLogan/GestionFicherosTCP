package clases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class HiloCliente extends Thread{
	private Socket sCliente;
	private ServidorFicheros servidor;
	private boolean salir = false;
	
	private boolean ocupado; // Para los metodos sincronizados. para Leer no es necesario, pero lo hago con todo
	
	private DataInputStream datosEntrada;
	private DataOutputStream datosSalida;
	
	public HiloCliente(Socket sCliente, ServidorFicheros servidor){
		this.sCliente = sCliente;
		this.servidor = servidor;
		
		this.start();
	}
	
	@Override
	public void run() {
		while(this.salir == false) {
			
		}
	}
	
	public synchronized String[] leerArchivos(){
		String[] lista = null;
		while(this.ocupado == true) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.ocupado = true;
		
		lista = this.servidor.leerCarpeta();
		
		notifyAll();
		
		return lista;
	}
	
}
