#-------------------------------------------------
#
# Project created by QtCreator 2015-11-18T21:01:44
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = SocketServer
TEMPLATE = app


SOURCES += main.cpp\
        server.cpp \
    socket.cpp

HEADERS  += server.h \
    socket.h

FORMS    += server.ui
