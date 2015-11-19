#ifndef CLIENTE_H
#define CLIENTE_H

#include <QMainWindow>

namespace Ui {
class Cliente;
}

class Cliente : public QMainWindow
{
    Q_OBJECT

public:
    explicit Cliente(QWidget *parent = 0);
    virtual ~Cliente();
    int corre();


private slots:
    void on_btnAceptar_clicked();

    void on_btnRestablecer_clicked();

private:
    Ui::Cliente *ui;
};

#endif // CLIENTE_H
