package com.redes.fabian_stefano.sockets;

/**
 * Método que se encarga de controlar los eventos de la interfaz.
 * @author Fabian Rodriguez
 * @author Stefano Del Vecchio
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Principal extends AppCompatActivity {

    private TextView text_resultados;
    private EditText input_direccion;
    private EditText input_cant_veces;
    private EditText input_puerto;
    private FloatingActionButton fab;

    private Socket m_socket_cliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text_resultados = (TextView) findViewById(R.id.text_resultados);
        input_direccion = (EditText) findViewById(R.id.input_direccion);
        input_puerto = (EditText) findViewById(R.id.input_puerto);
        input_cant_veces = (EditText) findViewById(R.id.input_cant_veces);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaciones(v)){
                    //Obtiene los datos
                    int cant_veces = Integer.parseInt(input_cant_veces.getText().toString());
                    String direccion = input_direccion.getText().toString();
                    int puerto = Integer.parseInt(input_puerto.getText().toString());

                    //Hace el numero de envios con la cantidad que digito el usuario.
                    for(int i=0; i< cant_veces; ++i){
                        MyClientTask myClientTask = new MyClientTask(direccion, puerto);
                        myClientTask.execute();
                        Principal.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                text_resultados.setText("*** Se inicia la conexión ***\n");
                            }
                        });
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
        if (id == R.id.action_info) {
            return true;
        }
        if(id==R.id.action_clear){
            vaciar_campos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void vaciar_campos(){
        input_direccion.setText("");
        text_resultados.setText("");
        input_cant_veces.setText("");

        input_puerto.setText("");
        if(m_socket_cliente != null){
            try {
                m_socket_cliente.close();
                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        text_resultados.setText("*** Se cerró la conexión ***\n");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método que realiza las validaciones de los datos en la interfaz.
     * @author  Fabian Rodriguez
     * @author Stefano del Vecchio
     * @param v que es el View actual
     * @return true si son validos los campos, false si no
     */
    private boolean validaciones(View v){
        if(input_direccion.getText().length() != 0) {
            if(input_puerto.getText().length() != 0){
                if(input_cant_veces.getText().length() != 0){
                    return true;
                }else{
                    Snackbar alerta = Snackbar.make(v, "Tiene que ingresar la cantidad de envíos", Snackbar.LENGTH_LONG);
                    alerta.setAction("Revisar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            input_cant_veces.requestFocus();
                        }
                    });
                    alerta.show();
                    return false;
                }
            }else{
                Snackbar alerta = Snackbar.make(v, "Tiene que ingresar un puerto", Snackbar.LENGTH_LONG);
                alerta.setAction("Revisar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        input_puerto.requestFocus();
                    }
                });
                alerta.show();
                return false;
            }

        }else{
            Snackbar alerta = Snackbar.make(v, "Tiene que ingresar una dirección IP", Snackbar.LENGTH_LONG);
            alerta.setAction("Revisar", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input_direccion.requestFocus();
                }
            });
            alerta.show();
            return false;
        }
    }

    /**
     * Clase que se encarga de realizar la conexión por medio de los sockets. Lo realiza en un hilo separado ya que todas las
     * operaciones de red se tienen que realizar en un hilo en el "background".
     * @author Fabián Rodríguez
     * @author Stefano Del Vecchio
     */
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                m_socket_cliente = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = m_socket_cliente.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();

            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(m_socket_cliente != null){
                    try {
                        m_socket_cliente.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            text_resultados.append(response+"\n");
            super.onPostExecute(result);
        }

    }
}
