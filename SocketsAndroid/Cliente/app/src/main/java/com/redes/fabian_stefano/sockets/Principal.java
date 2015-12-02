package com.redes.fabian_stefano.sockets;

/**
 * Método que se encarga de controlar los eventos de la interfaz.
 *
 * @author Fabian Rodriguez
 * @author Stefano Del Vecchio
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Principal extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Constantes
    private static final int TAMANO_BUFFER = 1024;
    private static final int TIEMPO_ESPERA = 600;

    //Elementos de la interfaz
    private TextView text_resultados;
    private EditText input_direccion;
    private EditText input_cant_veces;
    private EditText input_puerto;
    private FloatingActionButton fab;
    private Spinner m_opciones_tamano;

    //Miembros de la clase
    private String m_tamano_seleccionado;
    private String m_respuesta;
    private double m_vec_tiempos[];
    private int m_index_vec_tiempos;
    private boolean m_exito;

    //Para sincronizacion de los hilos
    private final Lock m_semaforo_respuesta = new ReentrantLock();

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
        m_opciones_tamano = (Spinner) findViewById(R.id.drop_tamanos);
        llena_spiner();
        m_respuesta = "";
        m_exito = true;

        fab = (FloatingActionButton) findViewById(R.id.fab);

        m_opciones_tamano.setOnItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validaciones(v)) {

                    m_respuesta = "*** Se inicia la conexión ***\n";

                    //Hace el numero de envios con la cantidad que digito el usuario.
                    Principal.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            text_resultados.setText(m_respuesta);
                        }
                    });

                    //Obtiene los datos
                    m_index_vec_tiempos = 0;
                    m_exito = true;
                    int cant_veces = Integer.parseInt(input_cant_veces.getText().toString());
                    String direccion = input_direccion.getText().toString();
                    int puerto = Integer.parseInt(input_puerto.getText().toString());
                    m_vec_tiempos = new double[cant_veces];

                    HiloPreparador hilo_preparador = new HiloPreparador(direccion, puerto, cant_veces, m_tamano_seleccionado);
                    hilo_preparador.start(); //Corre el hilo
                    try {
                        hilo_preparador.join(); //Espera a que termine el hilo
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (m_exito) { //Verifica que la conexión se hizo bien.
                        m_respuesta += "*** Se cierra la conexión ***\n";
                        String vector = crea_csv();
                        String nombre_archivo = "resultados_" + m_tamano_seleccionado + ".csv";
                        File archivo_resultados = new File(Environment.getExternalStorageDirectory(), nombre_archivo);
                        try {
                            FileOutputStream salida_archivo = new FileOutputStream(archivo_resultados);
                            PrintStream flujo = new PrintStream(salida_archivo);
                            flujo.print(vector);
                            flujo.close();
                            salida_archivo.close();
                            Snackbar alerta = Snackbar.make(v, "Archivo \"" + nombre_archivo + "\" creado", Snackbar.LENGTH_LONG);
                            alerta.show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            m_respuesta += "FileNotFoundException: archivo a guardar " + e.toString() + '\n';
                        } catch (IOException e) {
                            e.printStackTrace();
                            m_respuesta += "IOException: archivo a guardar" + e.toString() + '\n';
                        }
                    } else {
                        m_respuesta += "*** Se cierra la conexión ***\n";
                        Snackbar alerta = Snackbar.make(v, "Se presentó un error en la conexión", Snackbar.LENGTH_LONG);
                        alerta.show();
                    }
                    Principal.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            text_resultados.setText(m_respuesta);
                        }
                    });
                }
            }
        });

    }

    private String crea_csv() {
        String a_retornar = "Iteracion,Tamaño del archivo,Tiempo\n";
        for (int i = 0; i < m_vec_tiempos.length; ++i) {
            double tmp = m_vec_tiempos[i];
            a_retornar += String.valueOf(i + 1) + "," + m_tamano_seleccionado + "," + String.valueOf(tmp) + "\n";
        }
        return a_retornar.substring(0, a_retornar.length() - 1);
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
            Intent cambio_actividad = new Intent(this, Informacion.class);
            startActivity(cambio_actividad);
            return true;
        }
        if (id == R.id.action_clear) {
            vaciar_campos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Método que llena las opciones disponibles para los tamaños de los archivos a enviar.
     */
    private void llena_spiner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.opciones_spiner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        m_opciones_tamano.setAdapter(adapter);
    }

    /**
     * Método que limpia todos los campos de la interfaz y cierra el socket si está abierto.
     *
     * @author Fabián Rodríguez
     * @author Stefano Del Vecchio
     */
    private void vaciar_campos() {
        input_direccion.setText("");
        text_resultados.setText("");
        input_cant_veces.setText("");
        m_respuesta = "";
        input_puerto.setText("");
    }

    /**
     * Método que realiza las validaciones de los datos en la interfaz.
     *
     * @param v que es el View actual
     * @return true si son validos los campos, false si no
     * @author Fabian Rodriguez
     * @author Stefano del Vecchio
     */
    private boolean validaciones(View v) {
        if (input_direccion.getText().length() != 0) {
            if (input_puerto.getText().length() != 0) {
                if (input_cant_veces.getText().length() != 0) {
                    return true;
                } else {
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
            } else {
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

        } else {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        m_tamano_seleccionado = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //No hace nada
    }

    /**
     * Se encarga de preparar las cosas para el hilo, carga el archivo una vez y se lo envía a cada hilo para la ejecución.
     *
     * @author Fabián Rodríguez
     * @author Stefano del Vecchio
     */
    public class HiloPreparador extends Thread {
        private String direccion_destino;
        private int puerto_destino;
        private int num_ejecuciones;
        private String tam_seleccionado;

        HiloPreparador(String direccion, int puerto, int repeticiones, String tamano) {
            direccion_destino = direccion;
            puerto_destino = puerto;
            num_ejecuciones = repeticiones;
            tam_seleccionado = tamano;
        }

        @Override
        public void run() {
            String ruta = "";
            switch (tam_seleccionado) { //El nombre del archivo.
                case "1 KB":
                    ruta = "1k.txt";
                    break;
                case "10 KB":
                    ruta = "10k.txt";
                    break;
                case "100 KB":
                    ruta = "100k.txt";
                    break;
                case "1 MB":
                    ruta = "1m.txt";
                    break;
                case "10 MB":
                    ruta = "10m.txt";
                    break;
                case "100 MB":
                    ruta = "100m.txt";
                    break;
            }
            File archivo = new File(Environment.getExternalStorageDirectory(), "/archivos/" + ruta);


            for (int i = 0; i < num_ejecuciones; ++i) {
                try {
                    Socket socket = new Socket(direccion_destino, puerto_destino);
                    TareaCliente proc_cliente = new TareaCliente(socket, archivo);
                    proc_cliente.start();
                    proc_cliente.join();    //Espero a que termine de correr el hilo "proc_cliente"
                    socket.close();
                    Thread.sleep(TIEMPO_ESPERA);
                } catch (IOException e) {
                    e.printStackTrace();
                    m_respuesta += "IOException: al crear el socket " + e.toString() + '\n';
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    m_respuesta += "InterruptedException: al hacer join" + e.toString() + '\n';
                }
            }


        }
    }

    /**
     * Clase que se encarga de realizar la conexión por medio de los sockets. Lo realiza en un hilo separado ya que todas las
     * operaciones de red se tienen que realizar en un hilo en el "background".
     *
     * @author Fabián Rodríguez
     * @author Stefano Del Vecchio
     */
    public class TareaCliente extends Thread {

        private Socket socket_cliente;
        private File archivo;

        TareaCliente(Socket socket, File archivo_leido) {
            socket_cliente = socket;
            archivo = archivo_leido;
        }

        @Override
        public void run() {
            long t_inicial = 0;
            long t_final = 0;

            BufferedInputStream buffer_entrada;
            boolean bloqueo = false;

            try {

                byte[] buffer_lectura = new byte[(int) archivo.length()];


                t_inicial = System.nanoTime();

                buffer_entrada = new BufferedInputStream(new FileInputStream(archivo));
                buffer_entrada.read(buffer_lectura, 0, buffer_lectura.length);


                OutputStream stream_salida = socket_cliente.getOutputStream();
                stream_salida.write(buffer_lectura, 0, buffer_lectura.length);
                stream_salida.flush();

                t_final = System.nanoTime();
                long tiempo_tomado = t_final - t_inicial;
                double tiempo_segundos = (double) tiempo_tomado / 1000000000.0; //Convierte de nanosegundos a segundos
                tiempo_segundos = new BigDecimal(tiempo_segundos).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue(); //Trunca el numero, le da 4 decimales de presicion
                m_respuesta += "Duración --> " + String.valueOf(tiempo_segundos) + " s\n";
                m_vec_tiempos[m_index_vec_tiempos] = tiempo_segundos;
                ++m_index_vec_tiempos;

            } catch (UnknownHostException e) {
                e.printStackTrace();
                m_respuesta += "UnknownHostException: " + e.toString() + '\n';

            } catch (IOException e) {
                e.printStackTrace();
                m_respuesta += "IOException: al hacer la conexión " + e.toString() + '\n';
                m_exito = false;
            } finally {
                Principal.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        text_resultados.setText(m_respuesta);
                    }
                });
            }
        }

    }
}
