# Taller gradual de Java I/O y concurrencia

Guía de preparación para el **Proyecto Final 2027-1** de Programación Avanzada (PCIC, UNAM). Los 12 problemas son prácticas **distintas** del proyecto; no implementan el proyecto completo.

## Contrato de APIs permitidas

Los problemas utilizan los mecanismos presentados en las cinco fuentes del curso:

- **I/O** (pp. 13–20, 27–35): `File`, `listFiles`, `mkdirs`, `delete`, `FileReader` / `FileWriter`, `BufferedReader` / `BufferedWriter`, `readLine`, `write`, `close`.
- **Hilos, introducción** (pp. 33–42): `Runtime.getRuntime().availableProcessors()`, `Thread`, `Runnable`, `run`, `start`, `join`, `currentThread`.
- **Hilos, sincronización** (pp. 13–17, 35–38): `synchronized`, `Lock`, `ReentrantLock`, `Executors.newFixedThreadPool`, `ExecutorService.execute`, `shutdown`.
- **Patrones de concurrencia** (pp. 7–16, 18–26): `LinkedList`, `Condition.await`, `signal`, productor–consumidor y Manager–Worker.
- **Proyecto** (pp. 2 y 5): archivos temporales, un archivo de salida, manejo de filas problemáticas, pool de hilos y comparación con versión secuencial.

Para procesar parámetros o valores de datos se usan también operaciones elementales del lenguaje como `String.split`, `equals` e `Integer.parseInt`. No se usan `Callable`, `Future`, `CompletableFuture`, `parallelStream`, NIO, `BlockingQueue`, `AtomicInteger` ni bibliotecas externas.

**Simplificación deliberada:** los CSV de los ejercicios no incluyen comas protegidas por comillas ni campos multilínea. El `split(",")` de las diapositivas no basta para un CSV general.

## Orden recomendado

Primero resuelve los ejercicios 1–4 (archivos); después 5–7 (compartición y sincronización); luego 8–9 (Productor–Consumidor), 10–11 (Manager–Worker) y finalmente 12 (limpieza). Intenta cada enunciado **antes** de consultar la solución.

# PARTE I — ENUNCIADOS

## Ejercicio 1. Inventario recursivo de archivos

**Tema:** I/O y `File`.

Escribe un programa que reciba la ruta de una carpeta y recorra su contenido. Debe imprimir las rutas encontradas y calcular cuántos archivos, cuántos directorios y cuántos bytes hay en total. Si la ruta no existe o no es directorio, debe rechazarla. **No se deben abrir los archivos**: solo consultar sus metadatos.

**Prueba sugerida:** `java E01Inventario ../datos_prueba/logs`

**Resultado esperado:** 2 archivos, 1 directorio (en la carpeta de muestra).

## Ejercicio 2. Filtro de texto y bitácora de registros inválidos

**Tema:** I/O de caracteres y excepciones.

Recibe un archivo con columnas `id,ciudad,monto`, una ruta de salida, una ruta de errores y una ciudad. Copia al resultado únicamente los registros de la ciudad solicitada, dejando solo `id,monto`. Una fila con número incorrecto de campos o monto no numérico debe ir al archivo de errores. Procesa las filas **una a una** y nunca guardes el CSV completo en RAM.

**Prueba sugerida:** `java E02FiltrarVentas ../datos_prueba/ventas.csv ../datos_prueba/filtrado.csv ../datos_prueba/errores_ventas.txt CDMX`

**Resultado esperado:** 2 filas aceptadas; 2 filas inválidas.

## Ejercicio 3. Partición de un CSV por número de filas

**Tema:** I/O secuencial y subarchivos.

Recibe `origen.csv`, un entero `filasPorParte` y una carpeta temporal. Crea `parte_1.csv`, `parte_2.csv`, etc. y distribuye todas las filas de datos **sin duplicar ni perder ninguna**. Cada fragmento debe empezar con el mismo encabezado del archivo original. Cada subarchivo debe contener a lo sumo `filasPorParte` filas de datos, sin contar el encabezado.

**Prueba sugerida:** `java E03PartirArchivo ../datos_prueba/ventas.csv 2 ../datos_prueba/fragmentos_practica`

**Resultado esperado:** 3 subarchivos con 2, 2 y 2 filas de datos; cada uno contiene el encabezado.

## Ejercicio 4. Dos lectores independientes con Runnable y join

**Tema:** Creación y coordinación de hilos.

Dado un par de archivos de texto, crea **dos hilos**. Cada hilo debe contar las líneas del archivo que le corresponde y guardar su resultado parcial en una posición distinta de un arreglo. Después de que **ambos** terminen, el hilo principal imprime la suma total. ¿Qué ocurriría si omitieras los `join()`?

**Prueba sugerida:** `java E04ContarDosArchivos ../datos_prueba/texto1.txt ../datos_prueba/texto2.txt`

**Resultado esperado:** Total = 5 líneas.

## Ejercicio 5. Contador compartido: carrera y monitor

**Tema:** Sección crítica y synchronized.

Crea dos hilos que incrementen 100 000 veces una misma variable. Realiza primero la prueba **sin** exclusión mutua, y después **con** un método `synchronized`. Ejecuta varias veces y explica por qué el resultado sin protección **puede** diferir de 200 000. Explica también por qué `join()` no arregla la carrera.

**Prueba sugerida:** `java E05CarreraYMonitor`

**Resultado esperado:** Con monitor: 200000; sin monitor: puede variar, incluso coincidir por casualidad.

## Ejercicio 6. Muchos hilos, un solo archivo de salida

**Tema:** Monitores e I/O.

Dos trabajadores generan cada uno 1 000 registros con formato `nombre,numero`; ambos deben escribirlos en **un único** archivo compartido. Protege cada registro para que cada línea sea íntegra. El hilo principal debe esperar la terminación y **cerrar** el escritor solo después. ¿Por qué no es correcto que cada trabajador cierre el escritor al finalizar?

**Prueba sugerida:** `java E06EscrituraSincronizada ../datos_prueba/registros.txt`

**Resultado esperado:** 2 000 líneas; el orden entre A y B no está garantizado.

## Ejercicio 7. Estadísticas parciales protegidas con ReentrantLock

**Tema:** Candados y reducción de resultados.

Divide los enteros del 1 al 100 en dos rangos. Cada hilo calcula su suma **local**, y una vez terminada su sección de trabajo añade `sumaLocal` y `cantidadLocal` a un acumulador compartido utilizando `Lock` y `ReentrantLock`. Obtén suma, número de datos y media. ¿Por qué es mejor bloquear una sola vez por parcial que por cada entero?

**Prueba sugerida:** `java E07CandadoEstadisticas`

**Resultado esperado:** Cantidad = 100, suma = 5050 y media = 50.5.

## Ejercicio 8. Búfer acotado de números: 1 productor, 1 consumidor

**Tema:** Patrón Productor–Consumidor (I).

Crea un buzón compartido de capacidad **5**. Un productor coloca los enteros del 1 al 100; un consumidor los extrae y calcula la suma. El productor debe **esperar** cuando el buzón esté lleno y el consumidor cuando esté vacío, sin usar `Thread.sleep()` como mecanismo de sincronización. Debe existir una forma inequívoca de señalar el fin de los datos.

**Prueba sugerida:** `java E08ProductorConsumidor`

**Resultado esperado:** Suma recibida = 5050.

## Ejercicio 9. Dos lectores productores y un escritor consumidor

**Tema:** Patrón Productor–Consumidor (II).

Ahora dos hilos productores leen **archivos distintos** y depositan líneas en un buzón de capacidad **3**. Un solo consumidor retira las líneas y escribe `archivo:linea` en un archivo final. El consumidor debe terminar solo cuando **los dos** productores hayan anunciado que no enviarán más datos. ¿Por qué un único indicador de fin sería insuficiente?

**Prueba sugerida:** `java E09ProductoresArchivos ../datos_prueba/texto1.txt ../datos_prueba/texto2.txt ../datos_prueba/combinado.txt`

**Resultado esperado:** 5 líneas combinadas; el orden relativo entre los dos archivos no es fijo.

## Ejercicio 10. Manager–Worker sobre arreglo numérico

**Tema:** Patrón Manager–Worker (I).

Un manager obtiene el número de procesadores visibles con `Runtime.getRuntime().availableProcessors()`, crea un arreglo con los números 1 a 100 y distribuye **rangos disjuntos** entre varios trabajadores `Runnable`. Cada worker obtiene una suma parcial y la coloca en su casilla. El manager espera a todos, suma los parciales y calcula la media. Debe funcionar aunque el número de procesadores cambie.

**Prueba sugerida:** `java E10ManagerWorkerArreglo`

**Resultado esperado:** Suma 5050 y media 50.5, con hilos limitados a 100 y al número de procesadores visibles.

## Ejercicio 11. Pool fijo para procesar archivos de registros de eventos

**Tema:** Patrón Manager–Worker (II).

Crea una carpeta que contenga varios archivos de líneas `categoria,mensaje`. Un manager debe enumerar los archivos, crear un `newFixedThreadPool` ajustado al número de procesadores, y enviar **un Runnable por archivo**. Cada worker cuenta los registros con categoría `ALERTA`; las filas mal formadas van a un log. Todos los resultados parciales se consolidan en **un solo** archivo, sin intercalar escrituras. El programa termina cuando todas las tareas finalizan.

**Prueba sugerida:** `java E11ManagerWorkerArchivos ../datos_prueba/logs ../datos_prueba/resumen.csv ../datos_prueba/errores_logs.txt`

**Resultado esperado:** Cada archivo produce un registro con el número de alertas; en la muestra: 2 y 1 (en orden indeterminado).

## Ejercicio 12. Limpieza segura de una carpeta temporal plana

**Tema:** I/O final y ciclo de vida.

Crea un programa separado que, después de la partición de E03, borre los archivos de una carpeta temporal y luego la carpeta. Antes de borrar comprueba que exista, sea directorio y que no contenga subdirectorios. Si una operación de `delete()` falla, informa del error. **Trabaja exclusivamente con una carpeta de práctica** porque los borrados son reales.

**Prueba sugerida:** `java E12LimpiarFragmentos ../datos_prueba/fragmentos_practica`

**Resultado esperado:** Se borran 3 fragmentos y después la carpeta (si no hay otros elementos).

# PARTE II — SOLUCIONES EXPLICADAS Y CÓDIGO

Cada solución es un archivo Java autocontenido, con una clase pública del mismo nombre que su archivo.

## Solución 1. Inventario recursivo de archivos

**Idea de la solución.** Usa `File.isFile()`, `isDirectory()`, `listFiles()`, `length()` y recursión. El método `recorrer` solo se llama sobre archivos y directorios existentes. Esta habilidad luego permite comprobar la carpeta temporal y recorrer fragmentos.

**Archivo:** `E01Inventario.java`

```java
import java.io.File;

public class E01Inventario {
    private static int archivos = 0;
    private static int directorios = 0;
    private static long bytes = 0;

    private static void recorrer(File actual) {
        if (actual.isFile()) {
            archivos++;
            bytes += actual.length();
            System.out.println("ARCHIVO: " + actual.getPath());
        } else if (actual.isDirectory()) {
            directorios++;
            System.out.println("DIRECTORIO: " + actual.getPath());
            File[] hijos = actual.listFiles();
            if (hijos != null) {
                for (int i = 0; i < hijos.length; i++) {
                    recorrer(hijos[i]);
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java E01Inventario rutaDirectorio");
            return;
        }
        File raiz = new File(args[0]);
        if (!raiz.exists() || !raiz.isDirectory()) {
            System.out.println("Ruta de directorio invalida");
            return;
        }
        recorrer(raiz);
        System.out.println("Directorios: " + directorios);
        System.out.println("Archivos: " + archivos);
        System.out.println("Bytes: " + bytes);
    }
}
```

## Solución 2. Filtro de texto y bitácora de registros inválidos

**Idea de la solución.** Combina `BufferedReader`, `FileReader`, `readLine()`, `String.split(",")`, `BufferedWriter`, `FileWriter`, `write()` y manejo de `IOException`. El método `parseInt` es la conversión elemental de String a entero, necesaria para los parámetros y campos numéricos. **Supuesto didáctico**: CSV simples sin comas dentro de campos entrecomillados ni saltos de línea internos.

**Archivo:** `E02FiltrarVentas.java`

```java
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class E02FiltrarVentas {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Uso: java E02FiltrarVentas origen.csv salida.csv errores.txt ciudad");
            return;
        }
        File entrada = new File(args[0]);
        if (!entrada.isFile()) {
            System.out.println("El archivo de entrada no existe");
            return;
        }
        BufferedReader lector = null;
        BufferedWriter salida = null;
        BufferedWriter errores = null;
        int aceptadas = 0;
        int rechazadas = 0;
        try {
            lector = new BufferedReader(new FileReader(entrada));
            salida = new BufferedWriter(new FileWriter(new File(args[1])));
            errores = new BufferedWriter(new FileWriter(new File(args[2])));
            String linea = lector.readLine(); // Encabezado: id,ciudad,monto
            salida.write("id,monto\n");
            while ((linea = lector.readLine()) != null) {
                String[] c = linea.split(",");
                if (c.length != 3) {
                    errores.write(linea + "\n");
                    rechazadas++;
                    continue;
                }
                try {
                    int monto = Integer.parseInt(c[2]);
                    if (c[1].equals(args[3]) && monto >= 0) {
                        salida.write(c[0] + "," + monto + "\n");
                        aceptadas++;
                    }
                } catch (NumberFormatException e) {
                    errores.write(linea + "\n");
                    rechazadas++;
                }
            }
            System.out.println("Filas seleccionadas: " + aceptadas);
            System.out.println("Filas con errores: " + rechazadas);
        } catch (IOException e) {
            System.out.println("Error de I/O: " + e.getMessage());
        } finally {
            try { if (lector != null) lector.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
            try { if (salida != null) salida.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
            try { if (errores != null) errores.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
        }
    }
}
```

## Solución 3. Partición de un CSV por número de filas

**Idea de la solución.** Mantén un solo lector abierto; cierra cada escritor al llenar su fragmento. Usa `File.mkdir(s)` para la carpeta. Esta práctica divide en un número de filas fijo; en el proyecto se requiere adaptar la cantidad de partes al número de CPU, lo que estudiarás en E10 y E11.

**Archivo:** `E03PartirArchivo.java`

```java
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class E03PartirArchivo {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso: java E03PartirArchivo origen.csv filasPorParte directorioDestino");
            return;
        }
        int maximo = Integer.parseInt(args[1]);
        if (maximo <= 0) {
            System.out.println("filasPorParte debe ser positivo");
            return;
        }
        File origen = new File(args[0]);
        File destino = new File(args[2]);
        if (!origen.isFile() || (!destino.exists() && !destino.mkdirs()) || !destino.isDirectory()) {
            System.out.println("Ruta de entrada o destino invalida");
            return;
        }
        BufferedReader lector = null;
        BufferedWriter escritor = null;
        try {
            lector = new BufferedReader(new FileReader(origen));
            String encabezado = lector.readLine();
            if (encabezado == null) return;
            String linea;
            int parte = 0;
            int filas = 0;
            while ((linea = lector.readLine()) != null) {
                if (escritor == null || filas == maximo) {
                    if (escritor != null) escritor.close();
                    parte++;
                    escritor = new BufferedWriter(new FileWriter(new File(destino, "parte_" + parte + ".csv")));
                    escritor.write(encabezado + "\n");
                    filas = 0;
                }
                escritor.write(linea + "\n");
                filas++;
            }
            System.out.println("Subarchivos creados: " + parte);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (lector != null) lector.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
            try { if (escritor != null) escritor.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
        }
    }
}
```

## Solución 4. Dos lectores independientes con Runnable y join

**Idea de la solución.** `Runnable.run()` describe la tarea; `new Thread(runnable)` crea el hilo; `start()` lo pone en ejecución; `join()` establece el punto a partir del cual el manager puede leer resultados completos. No se necesita monitor para posiciones diferentes del arreglo, si el manager espera a los dos `join()`.

**Archivo:** `E04ContarDosArchivos.java`

```java
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class E04ContarDosArchivos {
    static class Contador implements Runnable {
        private final File archivo;
        private final int[] resultados;
        private final int indice;

        Contador(File archivo, int[] resultados, int indice) {
            this.archivo = archivo;
            this.resultados = resultados;
            this.indice = indice;
        }
        @Override
        public void run() {
            int filas = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(archivo));
                try {
                    while (in.readLine() != null) filas++;
                } finally {
                    in.close();
                }
                resultados[indice] = filas;
                System.out.println(Thread.currentThread().getName() + " conto " + filas);
            } catch (IOException e) {
                System.out.println("No se pudo leer " + archivo.getPath());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Uso: java E04ContarDosArchivos archivo1 archivo2");
            return;
        }
        int[] resultados = new int[2];
        Thread a = new Thread(new Contador(new File(args[0]), resultados, 0), "Lector-A");
        Thread b = new Thread(new Contador(new File(args[1]), resultados, 1), "Lector-B");
        a.start();
        b.start();
        a.join();
        b.join();
        System.out.println("Total: " + (resultados[0] + resultados[1]));
    }
}
```

## Solución 5. Contador compartido: carrera y monitor

**Idea de la solución.** El incremento `valor++` es una operación de lectura-modificación-escritura, no una acción atómica. En la versión protegida, los dos hilos comparten **la misma instancia** de `Cuenta`, y por tanto el mismo monitor. `join()` solo coordina la terminación; no serializa los incrementos.

**Archivo:** `E05CarreraYMonitor.java`

```java
public class E05CarreraYMonitor {
    static class Cuenta {
        private int valor = 0;
        void incrementarSinProteccion() { valor++; }
        synchronized void incrementarProtegido() { valor++; }
        int obtener() { return valor; }
    }
    static class Trabajo implements Runnable {
        private final Cuenta cuenta;
        private final boolean protegido;
        Trabajo(Cuenta cuenta, boolean protegido) {
            this.cuenta = cuenta;
            this.protegido = protegido;
        }
        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                if (protegido) cuenta.incrementarProtegido();
                else cuenta.incrementarSinProteccion();
            }
        }
    }
    private static void probar(boolean protegido) throws InterruptedException {
        Cuenta c = new Cuenta();
        Thread a = new Thread(new Trabajo(c, protegido));
        Thread b = new Thread(new Trabajo(c, protegido));
        a.start(); b.start();
        a.join(); b.join();
        System.out.println((protegido ? "Monitor" : "Sin monitor") + ": " + c.obtener() + " / 200000");
    }
    public static void main(String[] args) throws InterruptedException {
        probar(false);
        probar(true);
    }
}
```

## Solución 6. Muchos hilos, un solo archivo de salida

**Idea de la solución.** La clase `Registro` es dueña del `BufferedWriter`. Cada trabajador llama al método `synchronized escribir`. La sección crítica es la escritura de **un registro completo**; `join()` garantiza que nadie seguirá escribiendo cuando el manager cierre el recurso.

**Archivo:** `E06EscrituraSincronizada.java`

```java
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class E06EscrituraSincronizada {
    static class Registro {
        private final BufferedWriter salida;
        Registro(File archivo) throws IOException {
            salida = new BufferedWriter(new FileWriter(archivo));
        }
        synchronized void escribir(String texto) throws IOException {
            salida.write(texto + "\n");
        }
        void cerrar() throws IOException { salida.close(); }
    }
    static class Trabajador implements Runnable {
        private final String nombre;
        private final Registro registro;
        Trabajador(String nombre, Registro registro) {
            this.nombre = nombre;
            this.registro = registro;
        }
        @Override
        public void run() {
            for (int i = 1; i <= 1000; i++) {
                try {
                    registro.escribir(nombre + "," + i);
                } catch (IOException e) {
                    System.out.println("Error de escritura: " + e.getMessage());
                    return;
                }
            }
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) { System.out.println("Uso: java E06EscrituraSincronizada salida.txt"); return; }
        Registro registro = new Registro(new File(args[0]));
        try {
            Thread uno = new Thread(new Trabajador("A", registro));
            Thread dos = new Thread(new Trabajador("B", registro));
            uno.start(); dos.start();
            uno.join(); dos.join();
        } finally {
            registro.cerrar();
        }
        System.out.println("Deberia haber 2000 lineas completas; el orden no tiene que ser A,B,A,B...");
    }
}
```

## Solución 7. Estadísticas parciales protegidas con ReentrantLock

**Idea de la solución.** `lock()` y `unlock()` protegen únicamente la consolidación de cada parcial; `unlock()` va en `finally`. Los cálculos largos quedan fuera de la sección crítica, lo cual reduce contención.

**Archivo:** `E07CandadoEstadisticas.java`

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class E07CandadoEstadisticas {
    static class Acumulador {
        private long suma;
        private int cantidad;
        private final Lock candado = new ReentrantLock();
        void incorporar(long sumaLocal, int cantidadLocal) {
            candado.lock();
            try {
                suma += sumaLocal;
                cantidad += cantidadLocal;
            } finally {
                candado.unlock();
            }
        }
        long suma() { return suma; }  // Consultar solo despues de join().
        int cantidad() { return cantidad; }
    }
    static class Trabajador implements Runnable {
        private final int inicio, fin;
        private final Acumulador global;
        Trabajador(int inicio, int fin, Acumulador global) {
            this.inicio = inicio;
            this.fin = fin;
            this.global = global;
        }
        @Override
        public void run() {
            long parcial = 0;
            for (int i = inicio; i <= fin; i++) parcial += i;
            global.incorporar(parcial, fin - inicio + 1);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Acumulador global = new Acumulador();
        Thread a = new Thread(new Trabajador(1, 50, global));
        Thread b = new Thread(new Trabajador(51, 100, global));
        a.start(); b.start();
        a.join(); b.join();
        System.out.println("Cantidad: " + global.cantidad());
        System.out.println("Suma: " + global.suma());
        System.out.println("Media: " + ((double)global.suma() / global.cantidad()));
    }
}
```

## Solución 8. Búfer acotado de números: 1 productor, 1 consumidor

**Idea de la solución.** Se emplea `LinkedList`, un `ReentrantLock` y dos `Condition`: hay espacio y hay datos. Usa `while` alrededor de `await()` porque al reanudar hay que volver a comprobar la condición. Se reserva `-1` como centinela de finalización.

**Archivo:** `E08ProductorConsumidor.java`

```java
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class E08ProductorConsumidor {
    static class Buzon {
        private final LinkedList<Integer> cola = new LinkedList<Integer>();
        private final int capacidad;
        private final Lock candado = new ReentrantLock();
        private final Condition hayEspacio = candado.newCondition();
        private final Condition hayDatos = candado.newCondition();
        Buzon(int capacidad) { this.capacidad = capacidad; }
        void poner(int numero) throws InterruptedException {
            candado.lock();
            try {
                while (cola.size() == capacidad) hayEspacio.await();
                cola.offer(numero);
                hayDatos.signal();
            } finally { candado.unlock(); }
        }
        int tomar() throws InterruptedException {
            candado.lock();
            try {
                while (cola.isEmpty()) hayDatos.await();
                int numero = cola.remove();
                hayEspacio.signal();
                return numero;
            } finally { candado.unlock(); }
        }
    }
    static class Productor implements Runnable {
        private final Buzon buzon;
        Productor(Buzon buzon) { this.buzon = buzon; }
        @Override
        public void run() {
            try {
                for (int i = 1; i <= 100; i++) buzon.poner(i);
                buzon.poner(-1);  // Marca de fin.
            } catch (InterruptedException e) {
                System.out.println("Productor interrumpido");
            }
        }
    }
    static class Consumidor implements Runnable {
        private final Buzon buzon;
        Consumidor(Buzon buzon) { this.buzon = buzon; }
        @Override
        public void run() {
            long suma = 0;
            try {
                while (true) {
                    int numero = buzon.tomar();
                    if (numero == -1) break;
                    suma += numero;
                }
                System.out.println("Suma recibida = " + suma);
            } catch (InterruptedException e) {
                System.out.println("Consumidor interrumpido");
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Buzon b = new Buzon(5);
        Thread productor = new Thread(new Productor(b));
        Thread consumidor = new Thread(new Consumidor(b));
        consumidor.start(); productor.start();
        productor.join(); consumidor.join();
    }
}
```

## Solución 9. Dos lectores productores y un escritor consumidor

**Idea de la solución.** Reutiliza la lógica de `Lock`, `Condition` y `LinkedList` de E08 con cadenas. Cada productor envía exactamente un centinela `#FIN#`. El consumidor cuenta dos centinelas. Como **solo el consumidor escribe**, no hace falta monitor en el archivo de salida. Asume que las entradas no contienen la cadena reservada `#FIN#` como registro generado.

**Archivo:** `E09ProductoresArchivos.java`

```java
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class E09ProductoresArchivos {
    static class Buzon {
        private final LinkedList<String> cola = new LinkedList<String>();
        private final Lock lock = new ReentrantLock();
        private final Condition espacio = lock.newCondition();
        private final Condition datos = lock.newCondition();
        private final int capacidad;
        Buzon(int capacidad) { this.capacidad = capacidad; }
        void poner(String s) throws InterruptedException {
            lock.lock();
            try {
                while (cola.size() == capacidad) espacio.await();
                cola.offer(s);
                datos.signal();
            } finally { lock.unlock(); }
        }
        String tomar() throws InterruptedException {
            lock.lock();
            try {
                while (cola.isEmpty()) datos.await();
                String s = cola.remove();
                espacio.signal();
                return s;
            } finally { lock.unlock(); }
        }
    }
    static class Productor implements Runnable {
        private final File archivo;
        private final Buzon buzon;
        Productor(File archivo, Buzon buzon) { this.archivo = archivo; this.buzon = buzon; }
        @Override
        public void run() {
            try {
                BufferedReader lector = new BufferedReader(new FileReader(archivo));
                try {
                    String linea;
                    while ((linea = lector.readLine()) != null) {
                        buzon.poner(archivo.getPath() + ":" + linea);
                    }
                } finally { lector.close(); }
            } catch (IOException e) {
                System.out.println("No se pudo leer " + archivo.getPath());
            } catch (InterruptedException e) {
                System.out.println("Lector interrumpido: " + archivo.getPath());
            } finally {
                try { buzon.poner("#FIN#"); }
                catch (InterruptedException e) { System.out.println("No se pudo anunciar fin"); }
            }
        }
    }
    static class Consumidor implements Runnable {
        private final File salida;
        private final Buzon buzon;
        Consumidor(File salida, Buzon buzon) { this.salida = salida; this.buzon = buzon; }
        @Override
        public void run() {
            int terminados = 0;
            BufferedWriter escritor = null;
            try {
                escritor = new BufferedWriter(new FileWriter(salida));
                while (terminados != 2) {
                    String linea = buzon.tomar();
                    if (linea.equals("#FIN#")) terminados++;
                    else escritor.write(linea + "\n");
                }
            } catch (IOException e) {
                System.out.println("Fallo el archivo de salida");
            } catch (InterruptedException e) {
                System.out.println("Consumidor interrumpido");
            } finally {
                if (escritor != null) {
                    try { escritor.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
                }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.out.println("Uso: java E09ProductoresArchivos origen1.txt origen2.txt salida.txt");
            return;
        }
        Buzon buzon = new Buzon(3);
        Thread c = new Thread(new Consumidor(new File(args[2]), buzon));
        Thread a = new Thread(new Productor(new File(args[0]), buzon));
        Thread b = new Thread(new Productor(new File(args[1]), buzon));
        c.start(); a.start(); b.start();
        a.join(); b.join(); c.join();
        System.out.println("Terminado. El orden entre archivos puede variar.");
    }
}
```

## Solución 10. Manager–Worker sobre arreglo numérico

**Idea de la solución.** Para el worker `i` de `h`, usa el intervalo semiabierto `[n*i/h, n*(i+1)/h)`. Esta fórmula cubre exactamente los índices `0..n-1` incluso cuando `n` no es divisible entre `h`. El manager es responsable de repartir tareas, esperar y consolidar.

**Archivo:** `E10ManagerWorkerArreglo.java`

```java
public class E10ManagerWorkerArreglo {
    static class Trabajador implements Runnable {
        private final int[] datos;
        private final int inicio, fin, posicion;
        private final long[] resultados;
        Trabajador(int[] datos, int inicio, int fin, int posicion, long[] resultados) {
            this.datos = datos;
            this.inicio = inicio;
            this.fin = fin;
            this.posicion = posicion;
            this.resultados = resultados;
        }
        @Override
        public void run() {
            long suma = 0;
            for (int i = inicio; i < fin; i++) suma += datos[i];
            resultados[posicion] = suma;
        }
    }
    public static void main(String[] args) throws InterruptedException {
        int[] datos = new int[100];
        for (int i = 0; i < datos.length; i++) datos[i] = i + 1;
        int hilos = Runtime.getRuntime().availableProcessors();
        if (hilos > datos.length) hilos = datos.length;
        Thread[] trabajadores = new Thread[hilos];
        long[] parciales = new long[hilos];
        for (int i = 0; i < hilos; i++) {
            int inicio = datos.length * i / hilos;
            int fin = datos.length * (i + 1) / hilos;
            trabajadores[i] = new Thread(new Trabajador(datos, inicio, fin, i, parciales));
            trabajadores[i].start();
        }
        long total = 0;
        for (int i = 0; i < hilos; i++) {
            trabajadores[i].join();
            total += parciales[i];
        }
        System.out.println("Hilos: " + hilos + ", suma: " + total + ", media: " + ((double) total / datos.length));
    }
}
```

## Solución 11. Pool fijo para procesar archivos de registros de eventos

**Idea de la solución.** El pool limita el número de trabajadores simultáneos aunque existan más tareas. El `Consolidador` posee los dos `BufferedWriter` compartidos y sus métodos `synchronized` protegen las escrituras. Solo después de `shutdown()` e `isTerminated()` se cierran los archivos. **Importante**: escribe los archivos de salida fuera de la carpeta de entrada.

**Archivo:** `E11ManagerWorkerArchivos.java`

```java
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class E11ManagerWorkerArchivos {
    static class Consolidador {
        private final BufferedWriter resumen;
        private final BufferedWriter errores;
        Consolidador(File salida, File fallos) throws IOException {
            resumen = new BufferedWriter(new FileWriter(salida));
            errores = new BufferedWriter(new FileWriter(fallos));
        }
        synchronized void resultado(String texto) throws IOException {
            resumen.write(texto + "\n");
        }
        synchronized void error(String texto) throws IOException {
            errores.write(texto + "\n");
        }
        void cerrar() throws IOException {
            resumen.close();
            errores.close();
        }
    }
    static class Trabajador implements Runnable {
        private final File archivo;
        private final Consolidador consolidador;
        Trabajador(File archivo, Consolidador consolidador) {
            this.archivo = archivo;
            this.consolidador = consolidador;
        }
        @Override
        public void run() {
            int alertas = 0;
            BufferedReader lector = null;
            try {
                lector = new BufferedReader(new FileReader(archivo));
                String linea;
                while ((linea = lector.readLine()) != null) {
                    String[] partes = linea.split(","); // categoria,mensaje
                    if (partes.length < 2) {
                        consolidador.error(archivo.getPath() + ":" + linea);
                    } else if (partes[0].equals("ALERTA")) {
                        alertas++;
                    }
                }
                consolidador.resultado(archivo.getPath() + "," + alertas);
            } catch (IOException e) {
                System.out.println("Error leyendo/escribiendo: " + archivo.getPath() + " " + e.getMessage());
            } finally {
                if (lector != null) {
                    try { lector.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
                }
            }
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 3) {
            System.out.println("Uso: java E11ManagerWorkerArchivos directorioEntrada resumen.csv errores.txt");
            return;
        }
        File directorio = new File(args[0]);
        if (!directorio.isDirectory()) { System.out.println("No es directorio"); return; }
        File[] entradas = directorio.listFiles();
        if (entradas == null) { System.out.println("No se puede listar"); return; }
        int archivos = 0;
        for (int i = 0; i < entradas.length; i++) if (entradas[i].isFile()) archivos++;
        if (archivos == 0) { System.out.println("No hay archivos"); return; }
        int hilos = Runtime.getRuntime().availableProcessors();
        if (hilos > archivos) hilos = archivos;
        Consolidador salida = new Consolidador(new File(args[1]), new File(args[2]));
        ExecutorService pool = Executors.newFixedThreadPool(hilos);
        try {
            for (int i = 0; i < entradas.length; i++) {
                if (entradas[i].isFile()) pool.execute(new Trabajador(entradas[i], salida));
            }
        } finally {
            pool.shutdown();
            while (!pool.isTerminated()) Thread.sleep(10);
            salida.cerrar();
        }
        System.out.println("Archivos atendidos: " + archivos + " con " + hilos + " hilos");
    }
}
```

## Solución 12. Limpieza segura de una carpeta temporal plana

**Idea de la solución.** `File.delete()` devuelve booleano; no presupongas éxito. En esta solución se rechazan subdirectorios y solo se acepta una carpeta **plana**, como la que genera E03. En el proyecto, el manager debe limpiar solo su directorio temporal, y hacerlo cuando todos los trabajadores ya terminaron.

**Archivo:** `E12LimpiarFragmentos.java`

```java
import java.io.File;

public class E12LimpiarFragmentos {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java E12LimpiarFragmentos directorioTemporal");
            return;
        }
        File carpeta = new File(args[0]);
        if (!carpeta.isDirectory()) {
            System.out.println("La ruta no es un directorio");
            return;
        }
        File[] contenido = carpeta.listFiles();
        if (contenido == null) {
            System.out.println("No se pudo listar el directorio");
            return;
        }
        for (int i = 0; i < contenido.length; i++) {
            if (!contenido[i].isFile()) {
                System.out.println("No se borrara: hay subdirectorios u otros elementos");
                return;
            }
        }
        for (int i = 0; i < contenido.length; i++) {
            if (!contenido[i].delete()) System.out.println("No se pudo eliminar " + contenido[i].getPath());
        }
        if (carpeta.delete()) System.out.println("Directorio temporal eliminado");
        else System.out.println("No se pudo eliminar la carpeta: puede no estar vacia");
    }
}
```

# Cómo ejecutar

Desde la carpeta `soluciones`:

```bash
javac *.java
java E01Inventario ../datos_prueba/logs
java E02FiltrarVentas ../datos_prueba/ventas.csv ../datos_prueba/filtrado.csv ../datos_prueba/errores_ventas.txt CDMX
java E03PartirArchivo ../datos_prueba/ventas.csv 2 ../datos_prueba/fragmentos_practica
java E04ContarDosArchivos ../datos_prueba/texto1.txt ../datos_prueba/texto2.txt
java E05CarreraYMonitor
java E06EscrituraSincronizada ../datos_prueba/registros.txt
java E07CandadoEstadisticas
java E08ProductorConsumidor
java E09ProductoresArchivos ../datos_prueba/texto1.txt ../datos_prueba/texto2.txt ../datos_prueba/combinado.txt
java E10ManagerWorkerArreglo
java E11ManagerWorkerArchivos ../datos_prueba/logs ../datos_prueba/resumen.csv ../datos_prueba/errores_logs.txt
java E12LimpiarFragmentos ../datos_prueba/fragmentos_practica
```

Ejecuta E03 **antes** de E12. Nunca pases un directorio real de trabajo a E12: eliminará sus archivos.

## Autoevaluación

1. ¿Por qué `Thread.run()` invocado directamente no equivale a `start()`?
2. ¿Por qué `join()` es necesario para la consolidación, pero no sustituye a `synchronized`?
3. ¿Qué recursos son compartidos y cuáles son privados de cada trabajador en E11?
4. ¿Qué pasa si el productor de E08 intenta insertar al estar llena la cola?
5. ¿Por qué se usa `while`, y no `if`, al reanudar desde `await()`?
6. ¿Por qué conviene hacer la suma local y proteger solo la combinación en E07?
7. ¿Cómo convertirías la división de E03 de «filas por parte» a «número de partes basado en CPU»?
8. ¿Por qué el archivo de salida de E11 no debe encontrarse dentro del directorio analizado?
9. ¿Cómo cambiarían memoria y número de archivos abiertos si tuvieras 30 millones de líneas?
10. ¿Por qué los tiempos de ejecución no tienen que mejorar monotonamente con más hilos?

**Extensiones opcionales dentro del mismo temario:** compara un pool fijo con `Executors.newCachedThreadPool()`, incluido en las diapositivas; repite E05 20 veces; procesa archivos vacíos o con entradas inválidas; y compara el tiempo de un recorrido secuencial frente al mismo trabajo con 1, 2, 4 y 8 hilos.
