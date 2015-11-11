package com.redes.fabian_stefano.sockets;

/**
 * Método que se encarga de hacer las operaciones con los sockets de android.
 * @author Fabian Rodriguez
 * @author Stefano Del Vecchio
 */

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ControladoraSocket {

    private Socket m_socket;

    /**
     * Constructor por defecto de la clase. Crea un socket vacio
     */
    public ControladoraSocket(){
        m_socket = new Socket();
    }

    /**
     * Constructor que recibe la direccion IP a la que se quiere conectar.
     * @param direccion la direccion IP del servidor donde se quiere conectar.
     */
    public ControladoraSocket(String direccion){
        try{
            m_socket = new Socket(direccion, 9876);
        }catch(UnknownHostException e){
        //Excepcion por si no logra resolver el host.
            e.printStackTrace();
        }catch (IOException e) {
        //Excepcion por si ocurre un error creando el Socket.
            e.printStackTrace();
        }
    }
}
