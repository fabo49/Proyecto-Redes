#ifndef WORKER_H
#define WORKER_H
#include <QThread>
#include <QString>
#include <QMainWindow>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include "socket.h"

#define SIZE 512

class Worker : public QThread
{
public:

    // constructor
    // set name using initializer
    explicit Worker(QString s);

    // overriding the QThread's run() method

    void run();
    int ChildProcess(Socket *);
    bool keepRunning;
    int puerto;
    Socket * s1;
private:
    QString name;
};

#endif // MYTHREAD_H
