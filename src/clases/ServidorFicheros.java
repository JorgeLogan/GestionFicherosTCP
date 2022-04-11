package clases;

import vistas.VistaGestor;

public class ServidorFicheros extends VistaGestor {
	
	/**
	 * Pedido por el IDE
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		System.out.println("Servidor de ficheros por TCP");
		new ServidorFicheros();
	}

	@Override
	protected void subir() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bajar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void salir() {
		// TODO Auto-generated method stub
		
	}
}
