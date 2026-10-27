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
 *
 * Lanza un proceso hijo ShopSummary por cada tienda, recoge sus resultados, comprueba sus
 * códigos de salida y escribe el informe con el total. Lo hace dos veces, para comparar el
 * tiempo: primero uno a uno (cada hijo cuando ha terminado el anterior) y después todos a la
 * vez. La salida de error de los hijos se ve en la consola del padre.
 *
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
        // TODO fase 1: lanzar un ShopSummary para la tienda 1 con ProcessBuilder y javaCommand(),
        //ProcessBuilder pb  = new ProcessBuilder(javaCommand("ShopSummary", "1"));
       // pb.inheritIO();
        //Process child =  pb.start();
       // int exitCode = child.waitFor();
        //System.out.printf("PID process son : %d  | exit code: %d%n",child.pid(), exitCode);
        // TODO fase 2: sustituir lo anterior por el informe uno a uno: report(false).
        long sequencialTime = report(false);
        // TODO fase 5: hacer el informe uno a uno y después todos a la vez, y escribir cuántas
        //      veces más rápido va el segundo.
        long parallelTime = report(true);
        double moreFast = (double) sequencialTime / parallelTime;
        System.out.printf("El tiempo paralelo es %f más rápido que el secuencial",moreFast);
        double timeDifference = (double) sequencialTime - parallelTime;
        System.out.printf("hay uan diferencia de tiempo de  %f ms del paralelo al secuencial", timeDifference);
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
        totalUnits = 0;
        totalCents = 0;
        failedShops = 0;
        long startTime = System.currentTimeMillis();
        if(!allAtOnce){
            for (int i = 1; i <= SHOPS; i++) {
                Process hijo = launch(i);
                collect(hijo, i);
            }
        }else {
            List<Process> processes = new ArrayList<>();
            for (int i = 1; i <= SHOPS; i++) {
                processes.add(launch(i));
            }
            for (int i = 1; i <= SHOPS; i++) {
                collect(processes.get(i - 1), i); //-1 porque las listas empiezan en 0, así desde la primera posición
            }
        }
            long finalTime = System.currentTimeMillis();
            long fullTime = finalTime-startTime;
            System.out.printf(ES, "Total: %d units, %.2f €%n", totalUnits,totalCents/100.0);

        if(failedShops>0){
            System.out.printf("Tiendas con problemas %d%n",failedShops);
        }

        return fullTime;
    }

    /**
     * Lanza un ShopSummary para la tienda indicada, con javaCommand(): le pasa la tienda por
     * argumento y hereda su salida de error, para que sus mensajes se vean en la consola.
     *
     * @param shop número de la tienda
     * @return el proceso hijo, ya lanzado
     */
    static Process launch(int shop) throws IOException {
        // TODO fase 2
        ProcessBuilder pb = new ProcessBuilder(javaCommand("ShopSummary", String.valueOf(shop)));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        return pb.start();
    }

    /**
     * Recoge el resultado de un hijo y lo suma a los totales.
     *
     * Fase 2: lee su resultado de la salida estándar ("unidades;céntimos"), escribe la línea de la
     * tienda con su PID, las unidades y el importe en euros, y lo suma a totalUnits y totalCents.
     * Fase 3: antes, lo espera como mucho TIMEOUT segundos y, si no ha terminado, lo termina. Si
     * su código de salida no es 0, no lee su resultado: escribe qué tienda ha fallado y con qué
     * código. En los dos casos, suma 1 a failedShops.
     *
     * @param child el proceso hijo
     * @param shop  número de la tienda de ese hijo
     */
    static void collect(Process child, int shop) throws IOException, InterruptedException {
        // TODO fases 2 y 3
        boolean finish = child.waitFor(TIMEOUT, TimeUnit.SECONDS);
        if(!finish){
            child.destroy();
            System.out.printf("La tienda %d no ha respondido a tiempo%n", shop);
            failedShops++;
            return;
        }
        int exitCode = child.exitValue();
        if(exitCode!=0){
            System.out.printf("LA tienda %d ha fallado con el codigo: %d.%n", shop, child.exitValue());
            failedShops++;
            return;
        }
        BufferedReader reader = child.inputReader();
        String line = reader.readLine();
        if(line!=null){
            String[] partes = line.split(";");
            long units = Long.parseLong(partes[0]);
            long cents = Long.parseLong(partes[1]);
            Double euros = cents / 100.0;
            System.out.printf(ES, "El PID es: %d  | units: %d  | cents %.2f €%n",child.pid(),units,euros );
            totalUnits += units;
            totalCents += cents;
        }

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
