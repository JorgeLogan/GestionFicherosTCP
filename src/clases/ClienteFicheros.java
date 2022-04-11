package clases;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ClienteFicheros extends ClaseBase{
	private Socket socketCliente;
	
	public ClienteFicheros() {
		super();
		
	}
	
	public static void main(String[] args) {
		System.out.println("Cliente de gestor de ficheros TCP");
		new ClienteFicheros();
	}

	@Override
	protected void avisarCambios() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean conectarTCP() {
		boolean resultado = false;
		
		try {
			this.socketCliente = new Socket(IP, PUERTO);
			resultado = true;
		}
		catch(Exception e) {
			System.out.println("No se pudo conectar el cliente! " + e.getMessage());
		}
		return false;
	}

	@Override
	protected void desconectarTCP() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clickSubir() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clickBajar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clickSalir() {
		try {
			if(this.socketCliente!= null) this.socketCliente.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dispose();
	}

	@Override
	protected void clickConectar() {
		
		if(this.conectarTCP() == true) {
			JOptionPane.showMessageDialog(this, "Conectado al servidor!");
		}else {
			JOptionPane.showMessageDialog(this, "No te has podido conectar al servidor");
		}
	}

	@Override
	protected void clickDesconectar() {
		// TODO Auto-generated method stub
		
	}
}
