#ifndef SERVER_H
#define SERVER_H

#include <QMainWindow>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#include "socket.h"	// Include your socket interface

#define SIZE 512
namespace Ui {
class Server;
}

class Server : public QMainWindow
{
    Q_OBJECT

public:
    explicit Server(QWidget *parent = 0);
    ~Server();
    int corre();
    int ChildProcess(Socket *);
    char* getIP();

private slots:
    void on_btnLevantar_clicked();

private:
    Ui::Server *ui;
};

#endif // SERVER_H
