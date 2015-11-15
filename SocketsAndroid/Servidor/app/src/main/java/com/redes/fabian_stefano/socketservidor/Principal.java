package com.redes.fabian_stefano.socketservidor;

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

    private TextView ip_asignada;
    private String mensaje = "";
    private TextView resultado;
    private Button btn_empezar;
    private Button btn_cerrar;
    private ServerSocket socket_servidor;
    SocketServerThread hilo_servidor;

    private static final int TAMANO_ARCHIVO = 2000000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ip_asignada = (TextView)findViewById(R.id.label_ip_asignada);
        ip_asignada.setText(obtener_direccion_IP());
        resultado = (TextView) findViewById(R.id.label_resultado);
        btn_cerrar = (Button)findViewById(R.id.btn_cerrar);
        btn_cerrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                vaciar_campos();
            }
        });

        btn_empezar = (Button) findViewById(R.id.btn_correr);
        btn_empezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hilo_servidor = new SocketServerThread();
                hilo_servidor.start();  //Pone a correr el servidor
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
            return true;
        }
        if(id== R.id.action_reestablecer){
            vaciar_campos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void vaciar_campos(){
        resultado.setText("");
        mensaje="";
        if(socket_servidor != null){
            try {
                socket_servidor.close();
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

        if (socket_servidor != null) {
            try {
                socket_servidor.close();
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

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            try {
                socket_servidor = new ServerSocket(SocketServerPORT);

                while (true) {
                    Socket socket = socket_servidor.accept();
                    count++;
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
            }
        }

    }

    /**
     * Clase que se encarga de hacer las comunicaciones con el socket cliente.
     * @author Fabián Rodríguez
     * @author Stefano Del Vecchio
     */
    private class SocketServerReplyThread extends Thread {

        private Socket socket_servidor;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            this.socket_servidor = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Mensaje #" + cnt + " recibido";

            try {
                //Lee lo que le envia el cliente
                byte[] buffer_lectura = new byte[TAMANO_ARCHIVO];
                InputStream input_stream = socket_servidor.getInputStream();
                int bytes_leidos = input_stream.read(buffer_lectura, 0, buffer_lectura.length); //Lee el archivo recibido

                //Procede a guardar el archivo recibido
                File archivo_guardar = new File(Environment.getExternalStorageDirectory(), "recibido_"+cnt+".txt");
                FileOutputStream salida_archivo = new FileOutputStream(archivo_guardar);

                BufferedOutputStream buffer_archivo = new BufferedOutputStream(salida_archivo);
                buffer_archivo.write(buffer_lectura, 0, bytes_leidos);
                buffer_archivo.close();

                //Envia el mensaje de recibido
                outputStream = socket_servidor.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();
                outputStream.close();
                socket_servidor.close();

                mensaje += "respuesta: " + msgReply + "\n";

                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resultado.setText(mensaje);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                mensaje += "¡Error! " + e.toString() + "\n";
            }

            Principal.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    resultado.setText(mensaje);
                }
            });
        }

    }

    private String obtener_direccion_IP() {
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
            ip += "¡Error! " + e.toString() + "\n";

        }

        return ip;
    }
}
