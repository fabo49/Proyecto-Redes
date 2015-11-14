# Sockets Android vs Sockets UNIX

El fin de este proyecto es presentar ejemplos que muestren las diferencias en la eficiencia (en terminos de velocidad de transferencia) de los sockets que utilizan las aplicaciones Android en comparación con su similar en UNIX.

El proyecto es desarrollado en el ambiente del curso CI-1320 (Redes de computadoras) de la Universidad de Costa Rica, bajo la supervisión del profesor Francisco Arroyo.

### Manual de uso

##### Android

1. Una vez instalado el ```.apk``` del cliente, se debe agrega la carpeta *archivos* al directorio de almacenamiento interno del teléfono.
2. Después de instalar el ```.apk```del servidor, simplemente se da click en el botón que dice "Levantar sevidor".
3. En el Cliente, se ingresa la dirección IP que se muestra en pantalla del servidor junto con el puerto para el envío, se elije el tamaño del archivo que se quiere enviar y se preciona el botón que aparece en la esquina inferior derecha de la pantalla.
4. Para terminar la recepción de paquetes, en el servidor simplemente se preciona el botón que dice "Cerrar conexión" y listo.
5. Los archivos transferidos quedarán en el directorio raíz del teléfono donde se corrió el servidor con el nombre "recibido_*#secuencia*.txt".

###### Requisitos
* Android 5.0 Lollypop o superior

##### UNIX

Pendiente

### Desarrolladores

* Fabián Rodríguez Obando [GitHub](https://github.com/fabo49)
* Stefano Del Vecchio [GitHub](https://github.com/SDelVecchioC)
