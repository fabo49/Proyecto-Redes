QT += core
QT -= gui

TARGET = SocketServerConsola
CONFIG += console
CONFIG -= app_bundle

TEMPLATE = app

SOURCES += main.cpp \
    socket.cpp

HEADERS += \
    socket.h

