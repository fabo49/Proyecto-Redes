package com.redes.fabian_stefano.sockets;

/**
 * Método que se encarga de controlar los eventos de la interfaz.
 * @author Fabian Rodriguez
 * @author Stefano Del Vecchio
 */

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class Principal extends AppCompatActivity {

    private EditText edit_resultados;
    private EditText input_direccion;
    private EditText input_mensaje;
    private EditText input_cant_veces;
    private FloatingActionButton fab;
    private ControladoraSocket m_controladora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_direccion.getText().toString() != "") {
                    m_controladora = new ControladoraSocket(input_direccion.getText().toString());
                }
            }
        });

        /* //Para generar la alerta abajo.
        * new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        * */
        edit_resultados = (EditText) findViewById(R.id.edit_resultado);
        input_direccion = (EditText) findViewById(R.id.input_direccion);
        input_mensaje = (EditText) findViewById(R.id.input_mensaje);
        input_cant_veces = (EditText) findViewById(R.id.input_cant_veces);

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
            input_direccion.setText("");
            edit_resultados.setText("");
            input_mensaje.setText("");
            input_cant_veces.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
