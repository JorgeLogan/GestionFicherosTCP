package clases;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import paquetes.Paquete;
import paquetes.Paquete.OPCIONES;

public class ClienteFicheros extends ClaseBase implements Runnable {

    /**
     * Pedido por el IDE
     */
    private static final long serialVersionUID = 1L;
    private Socket socketCliente;
    private ObjectOutputStream objSalida;
    private ObjectInputStream objEntrada;
    private boolean salir = false; // Para salir de nuestro propio hilo de escucha

    public ClienteFicheros() {
        super();
    }

    public static void main(String[] args) {
        System.out.println("Cliente de gestor de ficheros TCP");
        new ClienteFicheros();
    }

    @Override
    protected boolean conectarTCP() {
        boolean resultado = false;
        this.salir = false; // Puede que ya hubieramos desconectado antes, asi que reiniciamos salir
        try {
            // Abrimos la conexion
            this.socketCliente = new Socket(IP, PUERTO);
            this.gestionBotones(true);
            resultado = true;

            // Nos ponemos a la escucha de respuestas o actualizaciones
            Thread hiloEscucha = new Thread(this);
            hiloEscucha.start();
        } catch (Exception e) {
            System.out.println("No se pudo conectar el cliente! " + e.getMessage());
        }
        return resultado;
    }

    @Override
    protected void desconectarTCP() {
        this.salir = true;
        try {
            this.socketCliente.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar el socket: " + e.getMessage());
        }
        this.gestionBotones(false);
    }

    // Metodo para el click de salir
    @Override
    protected void clickSalir() {
        this.clickDesconectar();
        this.dispose();
    }

    /**
     * Para empezar, el cliente se conectar�, y acto seguido, enviara un paquete
     * donde querra recibir el listado de archivos que tiene en la carpeta
     * compartida
     */
    @Override
    protected void clickConectar() {

        if (this.conectarTCP() == true) {
            JOptionPane.showMessageDialog(this, "Conectado al servidor!");
            this.pedirArchivos();

        } else {
            JOptionPane.showMessageDialog(this, "No te has podido conectar al servidor");
        }
    }

    /**
     * Para pedir el listado de archivos al servidor
     */
    private void pedirArchivos() {
        // preparo la informacion a enviar al servidor, pidiendole los nombres de la carpeta de archivos
        Paquete paquete = new Paquete(Paquete.OPCIONES.LEER);
        this.enviarPaquete(paquete);
    }

    @Override
    protected void clickDesconectar() {
        try {
            this.objSalida = new ObjectOutputStream(this.socketCliente.getOutputStream());
            this.objSalida.writeObject(new Paquete(Paquete.OPCIONES.SALIR));
            this.objSalida.close();
        } catch (IOException e) {
            System.out.println("Error cerrando los flujos del cliente. Ya han sido cerrados por el servidor");
        }
        this.desconectarTCP();
    }

    @Override
    protected void subir() {
        // Primero eligo que archivo quiero subir
        File archivo = DAOArchivos.getArchivo();
        if (archivo == null) {
            return; // En el caso de cancelar, el archivo sera null
        }
        // Ahora, compruebo que no exista ese archivo en el listado
        if (this.modeloFicheros.contains(archivo.getName())) {

            System.out.println("Ya existe el archivo " + archivo.getName() + "... pido confirmacion");
            if (JOptionPane.showConfirmDialog(this, "El archivo " + archivo.getName()
                    + " ya existe... � Quieres sobreescribir?") == JOptionPane.OK_OPTION) {

                System.out.println("Acepta sobreescribir");
                // Mandamos grabar el archivo
                this.enviarServidorSubir(archivo);
            } else {
                System.out.println("No acepta sobreescribir, asi que no hacemos nada");
            }
        } else { // No existia el archivo, asi que lo grabo sin medias tintas :)
            this.enviarServidorSubir(archivo);
        }
    }

    // Funcion para enviar un archivo al servidor para grabarlo en la zona critica.
    // Tambien recibira paquete de vuelta con la info de la carpeta actualizada
    public void enviarServidorSubir(File archivo) {
        // Preparo un paquete para subir el fichero
        byte[] buffer = DAOArchivos.convertirFileToBytes(archivo);
        Paquete paquete = new Paquete(OPCIONES.SUBIR, buffer, archivo.getName());

        // Y ahora usamos nuestro propio amazon y enviamos el paquete y esperamos respuesta
        this.enviarPaquete(paquete);
    }

    // Funcion para descargar el fichero seleccionado del JList
    @Override
    protected void descargar() {
        System.out.println("Solicitamos descargar un fichero");
        String archivo = this.listadoFicheros.getSelectedValue();

        if (archivo == null) {
            return;
        }
        System.out.println("Intentamos descargar el fichero " + archivo);

        // Praparamos el paquete de envio
        Paquete paquete = new Paquete(OPCIONES.DESCARGAR, archivo);

        // Lo enviamos 
        this.enviarPaquete(paquete);
    }

    // Funcion para enviar un paquete. Ya reciiremos respuesta a traves del hilo de escucha esta clase
    private void enviarPaquete(Paquete paquete) {

        try {
            // Preparamos y enviamos el paquete.
            this.objSalida = new ObjectOutputStream(this.socketCliente.getOutputStream());
            this.objSalida.writeObject(paquete);
            System.out.println("Enviado al servidor paquete para " + paquete.getOpcion());
        } catch (Exception e) {
            System.out.println("Error enviando paquete al hilo del servidor");
        }
    }

    // Hilo de escucha de respuestas o actualizaciones
    @Override
    public void run() {
        // Nos pondremos a la escucha del hilo del servidor
        // Recibiremos las peticiones que hagamos, o las actualizaciones
        System.out.println("Inicio del hilo del cliente para la escucha de respuestas");
        Paquete respuesta = null;

        try {
            this.objEntrada = new ObjectInputStream(this.socketCliente.getInputStream());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        while (this.salir == false) {
            try {
                // Para el paso de los archivos. Intento ralentizarlo lo posible, ya que no se porque,
                // siempre me borra el jlist despues de pasarle los datos! No tiene sentido
                String[] listado; // Para el paso de los archivos.

                System.out.println("Bucle de escucha...");
                // Esperamos respuesta del servidor, o actualizaciones

                respuesta = (Paquete) this.objEntrada.readObject();

                System.out.println("Recibida respuesta");
                // Si la respuesta no es null, ha ido bien, asi que miramos que opcion
                // tenemos en el paquete para trabajar segun ella
                if (respuesta != null) {
                    switch (respuesta.getOpcion()) {
                        case LEER:
                            // En este caso, actualizamos nuestra vista
                            if (this.getTitle().length() == 0) {
                                this.setTitle(respuesta.getNombreArchivo());
                            }
                            //this.modeloFicheros.clear();
                            System.out.println("Actualizamos listado de archivos");
                            listado = respuesta.getArchivos();
                            this.pasarListadoToVentana(listado);
                            break;
                        case SUBIR:
                            //this.modeloFicheros.clear();
                            System.out.println("Actualizamos listado de archivos");
                            // Aqui tambien actualizamos la vista
                            listado = respuesta.getArchivos();
                            this.pasarListadoToVentana(listado);
                            break;
                        case DESCARGAR:
                            System.out.println("hemos recibido (descargado) un archivo del servidor");
                            DAOArchivos.grabarEnDirectorio(respuesta.getBuffer(), respuesta.getNombreArchivo());
                            break;
                        case SALIR:
                            System.out.println("Recibimos del servidor que hay que cerrar hilo de escucha");
                            this.salir = true;
                            break;
                    }
                }
            } catch (Exception e) {
                // Si paso algo en este punto, es que se corto conexion, asi que salimos
                this.salir = true;
                System.out.println("Error en la escucha de respuestas desde el servidor -> " + e.getMessage());
            }
        }
        System.out.println("Cerrado hilo de escucha de respuestas del cliente");
    }
}
