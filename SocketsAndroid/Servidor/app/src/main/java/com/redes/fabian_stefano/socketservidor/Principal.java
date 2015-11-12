package com.redes.fabian_stefano.socketservidor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ip_asignada = (TextView)findViewById(R.id.label_ip_asignada);
        ip_asignada.setText(obtener_direccion_IP());
        resultado = (TextView) findViewById(R.id.label_resultado);
        btn_empezar = (Button) findViewById(R.id.btn_correr);
        btn_empezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread socketServerThread = new Thread(new SocketServerThread());
                socketServerThread.start();
                mensaje += "*** El servidor esta corriendo ***\n";
                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resultado.setText(mensaje);
                    }
                });

            }
        });

        btn_cerrar = (Button)findViewById(R.id.btn_cerrar);
        btn_cerrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(socket_servidor != null){
                    try {
                        socket_servidor.close();
                        mensaje+="*** Se cerró el servidor ***";
                        Principal.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                resultado.setText(mensaje);
                            }
                        });
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket_servidor != null) {
            try {
                socket_servidor.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
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

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket, count);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * Clase que se encarga de hacer las comunicaciones con el socket cliente.
     * @author @author Andr.oid Eric http://android-er.blogspot.com/2014/02/android-sercerclient-example-client.html
     */
    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Android, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                mensaje += "replayed: " + msgReply + "\n";

                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resultado.setText(mensaje);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mensaje += "Something wrong! " + e.toString() + "\n";
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
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";

        }

        return ip;
    }
}
