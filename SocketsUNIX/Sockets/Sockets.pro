#-------------------------------------------------
#
# Project created by QtCreator 2015-11-17T14:46:55
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = Sockets
TEMPLATE = app

CONFIG += c++11

SOURCES +=\
        cliente.cpp \
    main.cpp \
    chrono.cpp \
    socket.cpp

HEADERS  += cliente.h \
    chrono.h \
    socket.h

FORMS    += cliente.ui
