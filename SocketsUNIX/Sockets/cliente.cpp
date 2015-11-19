#include "cliente.h"
#include "ui_cliente.h"

#include <stdio.h>
#include <fcntl.h>
#include <cstdlib>
#include <unistd.h>
#include "socket.h"	// Include your socket interface
#include "chrono.h" 	// Include the chrono interface

#define SIZE 512
#define MAX_PATH 2048


Cliente::Cliente(QWidget *parent):
    QMainWindow(parent),
    ui(new Ui::Cliente)
{
    ui->setupUi(this);
    ui->labelError->hide();
}



Cliente::~Cliente(){
    delete ui;
}

int Cliente::corre() {
    Socket s(SOCK_STREAM);
    Chrono ci, cf;
    char buffer[ SIZE ];
    int id, st;
    long sent, psize;
    double rate;
    std::string rutaArchivo;
    QString output;

    psize = 128 * 1024 * 1024;	// Print info each 128 MB

    if(ui->dropTamano->currentText() == "1K")
    {
        rutaArchivo = "1k.txt";
    }
    else if(ui->dropTamano->currentText() == "10K")
    {
        rutaArchivo = "10k.txt";
    }
    else if(ui->dropTamano->currentText() == "100K")
    {
        rutaArchivo = "100k.txt";
    }

    id = open(rutaArchivo.c_str(), O_RDONLY );
    if ( -1 == id ) {
        ui->textResultados->document()->setPlainText("Error: File not found.");
        return 1;
    }
    // Change with valid server IP address, like 10.1.1.10
    QByteArray ba = ui->inputIP->text().toLatin1();
    char *c_str = ba.data();
    if(s.Connect( c_str, ui->inputPuerto->text().toInt() ) >= 0){// Same port as server
    s.Write( const_cast<char*>(rutaArchivo.c_str()), 16);	// Send first the filename to server
    s.Read( (char *) buffer, SIZE );	// Read file name confirmation from server
    printf( "%s\n", buffer );		// Print the confirmation string, can be removed

    ci.getTime();	// Start the time measurement

    sent = 0;
    while ( st = read( id, buffer, SIZE ) ) {	// Send the file
        st = s.Write( buffer, SIZE );
        sent += st;
        if ( (sent % psize) == 0 ) {	// Print info each 128 Mb, can be removed
            cf.getTime();
            cf -= ci;
            ui->textResultados->moveCursor(QTextCursor::End);
            ui->textResultados->insertPlainText("Time taken to transfer " +QString::number(sent)+" bytes is: "+QString::number(cf.getSecs()) + " sec., "+ QString::number(cf.getnSecs())+ " ns\n");
            rate = cf.getSecs() + (cf.getnSecs()/1000000000);
            rate = (double) (sent / 1024 /1024) / rate;
            ui->textResultados->moveCursor(QTextCursor::End);
            ui->textResultados->insertPlainText( "Transfer rate:"+QString::number(rate)+" MBps\n");
        }
    }

    s.Shutdown( SHUT_WR );		// Mark the socket EOF in server
    s.Read( (char *) buffer, SIZE );	// Read the answer from server

    cf.getTime();	// Get the time now
    cf -= ci;		// Calculate the difference

    ui->textResultados->moveCursor(QTextCursor::End);
    ui->textResultados->insertPlainText("Time taken to transfer " +QString::number(sent)+" bytes is: "+QString::number(cf.getSecs()) + " sec., "+ QString::number(cf.getnSecs())+ " ns\n");

    rate = cf.getSecs() + (cf.getnSecs()/1000000000);
    rate = (double) (sent / 1024 /1024) / rate;
    ui->textResultados->moveCursor(QTextCursor::End);
    ui->textResultados->insertPlainText( "Total transfer rate: "+QString::number(rate)+" MBps\n" );

    }else
        ui->textResultados->document()->setPlainText("Error: No se puedo conectar a la dirección.");
    ::close(id);
    s.Close();
    return 0;
}


void Cliente::on_btnAceptar_clicked()
{
    bool aceptado = true;
    if(ui->inputIP->text() == ""){
        aceptado = false;
        ui->labelError->setText("Por favor ingrese una dirección IP");
    }
    else
    if(ui->inputPuerto->text() == ""){
        aceptado = false;
        ui->labelError->setText("Por favor ingrese el puerto");
    }
    else
    if(ui->inputNumero->text() == ""){
        aceptado = false;
        ui->labelError->setText("Por favor ingrese la cantidad de ejecuciones a realizar");
    }
    if(aceptado){
        corre();
        ui->labelError->hide();
    }
    else
        ui->labelError->show();
}

void Cliente::on_btnRestablecer_clicked()
{
    ui->inputIP->setText("");
    ui->inputNumero->setText("");
    ui->inputPuerto->setText("");
    ui->textResultados->document()->setPlainText("");
}
