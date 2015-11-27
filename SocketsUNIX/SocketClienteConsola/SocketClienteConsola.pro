QT += core
QT -= gui

TARGET = SocketClienteConsola
CONFIG += console
CONFIG -= app_bundle

TEMPLATE = app

SOURCES += main.cpp \
    ../Sockets/chrono.cpp \
    ../Sockets/socket.cpp

HEADERS += \
    ../Sockets/chrono.h \
    ../Sockets/socket.h

