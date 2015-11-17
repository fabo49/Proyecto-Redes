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
    ~Cliente();

private:
    Ui::Cliente *ui;
};

#endif // CLIENTE_H
