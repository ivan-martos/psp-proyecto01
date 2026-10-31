import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Proceso padre del informe de ventas (proyecto A01.5). Esqueleto: completad los TODO en la fase
 * que indica cada uno.
 * <p>
 * Lanza un proceso hijo ShopSummary por cada tienda, recoge sus resultados, comprueba sus
 * códigos de salida y escribe el informe con el total. Lo hace dos veces, para comparar el
 * tiempo: primero uno a uno (cada hijo cuando ha terminado el anterior) y después todos a la
 * vez. La salida de error de los hijos se ve en la consola del padre.
 * <p>
 * Uso: java SalesReport
 */
public class SalesReport {

    static final int SHOPS = 8;     // número de tiendas; en la fase 3, probad con 9
    static final int TIMEOUT = 30;  // segundos de espera máxima por hijo; en la fase 3, probad con 0
    static final Locale ES = Locale.of("es", "ES");  // para escribir los números con formato español

    static long totalUnits = 0;  // unidades vendidas en todas las tiendas
    static long totalCents = 0;  // importe de todas las tiendas, en céntimos
    static int failedShops = 0;  // tiendas que han fallado o no han respondido a tiempo (fase 3)

    public static void main(String[] args) throws IOException, InterruptedException {
        /* TODO fase 1: lanzar un ShopSummary para la tienda 1 con ProcessBuilder y javaCommand(),
        //      heredando su entrada y su salida (inheritIO()), y escribir su PID y su código de salida.
        ProcessBuilder pb = new ProcessBuilder(javaCommand("ShopSummary", "1")); //Le pasas solo 1 para que pueda trabajar el integer sobre el

        //Le damos salida por la terminal
        pb.inheritIO();

        //Iniciamos el hijo y sacamos su PID:
        Process hijo = pb.start();
        System.out.println("El PID del hijo es: " + hijo.pid());
        //El codigo de salida del hijo es:
        System.out.println("El codigo de salida es: " + hijo.waitFor());

        Comentamos la fase 1 entera para hacer funcionar la 2.
         */
        // TODO fase 2: sustituir lo anterior por el informe uno a uno: report(false).
        report(false);


        // TODO fase 5: hacer el informe uno a uno y después todos a la vez, y escribir cuántas
        //      veces más rápido va el segundo.
    }

    /**
     * Hace el informe de todas las tiendas, de la 1 a SHOPS, y devuelve lo que ha tardado, en
     * milisegundos. Pone los totales a 0, lanza los hijos con launch(), recoge sus resultados con
     * collect() y escribe el total, las tiendas con problemas y el tiempo.
     *
     * @param allAtOnce false: uno a uno, cada hijo cuando ha terminado el anterior (fase 2);
     *                  true: todos a la vez, todos los hijos antes de esperar a ninguno (fase 5)
     * @return lo que ha tardado el informe, en milisegundos
     */
    static long report(boolean allAtOnce) throws IOException, InterruptedException {
        // TODO fases 2 y 5
        totalCents = 0; //Iniciamos a 0 para que no se sumen los totales de la vez anterior
        totalUnits = 0; //Iniciamos a 0 para que no se sumen los totales de la vez anterior
        failedShops = 0; //Se pone a 0 para que sume de 1 en 1 en caso de que alguna tienda de error
        long horaInicio = System.currentTimeMillis(); //Esta variable nos sirve para guardar la hora de inicio
        for (int i = 1; i <= SHOPS ; i++) { //Bucle que recorra todas las tiendas
            Process hijo = launch(i);
            collect(hijo, i);

        }
        //Calculamos el tiempo que tarda:
        long tiempoTotal = System.currentTimeMillis() - horaInicio;
        System.out.println("TOTAL | " + String.format(ES, "%,d", totalUnits) + " unidades | "
                + String.format(ES, "%,.2f", totalCents / 100.0) + " €");
        System.out.println("El numero de tiendas que ha fallado es: " + failedShops);
        System.out.println("El tiempo total es: " + tiempoTotal + " ms");

        return tiempoTotal;
    }

    /**
     * Lanza un ShopSummary para la tienda indicada, con javaCommand(): le pasa la tienda por
     * argumento y hereda su salida de error, para que sus mensajes se vean en la consola.
     *
     * @param shop número de la tienda
     * @return el proceso hijo, ya lanzado
     */
    static Process launch(int shop) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(javaCommand("ShopSummary", String.valueOf(shop)));
        //Se debe pasar un String.valueOF, porque la variable shop es int, javaCommand trabaja con string.
        //Hereda la salida:
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        // El error del hijo sale en mi consola; la salida normal la leo yo en collect
        //Iniciamos el hijo y devolvemos:
        Process hijo = pb.start();
        return hijo;


    }

    /**
     * Recoge el resultado de un hijo y lo suma a los totales.
     * <p>
     * Fase 2: lee su resultado de la salida estándar ("unidades;céntimos"), escribe la línea de la
     * tienda con su PID, las unidades y el importe en euros, y lo suma a totalUnits y totalCents.
     * Fase 3: antes, lo espera como mucho TIMEOUT segundos y, si no ha terminado, lo termina. Si
     * su código de salida no es 0, no lee su resultado: escribe qué tienda ha fallado y con qué
     * código. En los dos casos, suma 1 a failedShops.
     *
     * @param child el proceso hijo
     * @param shop  número de la tienda de ese hijo
     * @return
     */
    static void collect(Process child, int shop) throws IOException, InterruptedException {
        //Una vez realizada la segunda fase, tenemos que comprar si el hijo lee bien
        boolean comprobarHijo = child.waitFor(TIMEOUT, TimeUnit.SECONDS);

        //Comprobar que pasa si el hijo no termina en el tiempo esperado
        if (comprobarHijo != true){ //Si hijo es distinto de true, osea si no acaba a tiempo:
            child.destroy();
            System.out.println("La tienda: " + shop + " no ha respondido a tiempo");
            //Se destruye al hijo y se informa que tienda ha sido.
            failedShops++; //Se suma 1 a error de tienda.
            return;
        }
        //Ahora debemos comprobar si el codigo de salida del hijo no es 0
        if (child.exitValue() != 0) { //Si el codigo de salida no es 0
            System.out.println("La tienda numero " + shop + " ha fallado, con el codigo de salida " + child.exitValue());
            failedShops++; //Se suma 1 a error de tienda.
            return;
        }

        //Dentro de collect tenemos ya una variable proceso creada, en este caso es child
        //Tenemos que leer lo que escribe el hijo:
        BufferedReader leerDatos = child.inputReader();

        //Creamos una variable que lee la linea con el resultado:
        String leerLineas = leerDatos.readLine();

        //Debemos crear un array para poder dividir cuantas veces ocurra
        String[] nuevaForma = leerLineas.split(";");
        long unidades = Long.parseLong(nuevaForma[0]); // unidades vendidas en la tienda
        long centimos = Long.parseLong(nuevaForma[1]); // dinero total de la tienda, en céntimos

        totalUnits += unidades; //Sumamos a las unidades el total
        totalCents += centimos; //Sumamos los céntimos de esta tienda al total

        //Mostrar por pantalla nºtienda, PID, unidades, dinero:
        // String.format pone los números modo español: puntos en los miles y coma en los decimales.
        // %,d es para números enteros (unidades) y %,.2f para números con 2 decimales (euros)
        System.out.println("Tienda " + shop + " | PID " + child.pid() + " | " + String.format(ES, "%,d", unidades) + " unidades | " + String.format(ES, "%,.2f", centimos / 100.0) + " €");




    }


    /**
     * Construye el comando que ejecuta la clase indicada con la misma JVM y el mismo classpath que
     * este programa. Ya está hecho: es el comandoJava del ejemplo 6.3.
     */
    static List<String> javaCommand(String className, String... arguments) {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(className);
        for (String argument : arguments) {
            command.add(argument);
        }
        return command;
    }

}

