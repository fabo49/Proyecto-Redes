package com.redes.fabian_stefano.socketservidor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Principal extends AppCompatActivity {

    //Constantes
    private static final int TAMANO_BUFFER = 1000000;
    private static final int puerto_servidor = 8080;

    //Elementos de la interfaz
    private TextView ip_asignada;
    private TextView puerto_asignado;
    private String mensaje = "";
    private TextView resultado;
    private Button btn_empezar;
    private Button btn_cerrar;

    //Miembros de la clase
    private ServerSocket m_socket_servidor;
    private SocketServerThread m_hilo_servidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ip_asignada = (TextView) findViewById(R.id.label_ip_asignada);
        ip_asignada.setText(obtener_direccion_IP());
        resultado = (TextView) findViewById(R.id.label_resultado);
        btn_cerrar = (Button) findViewById(R.id.btn_cerrar);
        btn_cerrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vaciar_campos();
            }
        });

        puerto_asignado = (TextView) findViewById(R.id.label_puerto_asignado);
        puerto_asignado.setText(String.valueOf(puerto_servidor));

        btn_empezar = (Button) findViewById(R.id.btn_correr);
        btn_empezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_hilo_servidor = new SocketServerThread();
                m_hilo_servidor.start();  //Pone a correr el servidor
                mensaje += "*** El servidor esta corriendo ***\n";
                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resultado.setText(mensaje);
                    }
                });

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent cambio_actividad = new Intent(this, Informacion.class);
            startActivity(cambio_actividad);
            return true;
        }
        if (id == R.id.action_reestablecer) {
            vaciar_campos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void vaciar_campos() {
        resultado.setText("");
        mensaje = "";
        if (m_socket_servidor != null) {
            try {
                m_socket_servidor.close();
                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resultado.setText("*** Se cerró el servidor ***");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ip_asignada.setText(obtener_direccion_IP());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_socket_servidor != null) {
            try {
                m_socket_servidor.close();
                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resultado.setText("*** Se cerró el servidor ***");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            Socket socket = null;
            try {
                m_socket_servidor = new ServerSocket(puerto_servidor);

                while (true) {
                    socket = m_socket_servidor.accept();
                    ++count;
                    mensaje += "#" + count + " proveniente de " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";

                    Principal.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            resultado.setText(mensaje);
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        mensaje += "IOException: al cerrar el primer socket " + e.toString() + "\n";
                    }
                }
            }
        }

    }

    /**
     * Clase que se encarga de hacer las comunicaciones con el socket cliente.
     *
     * @author Fabián Rodríguez
     * @author Stefano Del Vecchio
     */
    private class SocketServerReplyThread extends Thread {

        private Socket socket_servidor;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            socket_servidor = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;

            try {
                //Lee lo que le envia el cliente
                byte[] buffer_lectura = new byte[TAMANO_BUFFER];
                InputStream input_stream = socket_servidor.getInputStream();
                int bytes_leidos = input_stream.read(buffer_lectura, 0, buffer_lectura.length); //Lee el archivo recibido

                //Procede a guardar el archivo recibido
                File archivo_guardar = new File(Environment.getExternalStorageDirectory(), "recibido_" + cnt + ".txt");
                FileOutputStream salida_archivo = new FileOutputStream(archivo_guardar);

                BufferedOutputStream buffer_archivo = new BufferedOutputStream(salida_archivo);
                buffer_archivo.write(buffer_lectura, 0, bytes_leidos);
                buffer_archivo.close();

                //Envia el mensaje de recibido (ACK)

                String msgReply = "Mensaje #" + cnt + " ACK";
                outputStream = socket_servidor.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();
                outputStream.close();
                socket_servidor.close();

            } catch (IOException e) {
                e.printStackTrace();
                mensaje += "IOException: " + e.toString() + "\n";
            } finally {
                if (socket_servidor != null) {
                    try {
                        socket_servidor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mensaje += "IOException: al cerrar el socket " + e.toString() + '\n';
                    }
                }
                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resultado.setText(mensaje);
                    }
                });
            }
        }
    }

    private static String obtener_direccion_IP() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "SocketException: " + e.toString() + "\n";

        }

        return ip;
    }
}
