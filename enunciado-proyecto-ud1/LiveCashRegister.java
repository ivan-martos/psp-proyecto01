import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Caja en directo (proyecto A01.5). Esqueleto: completad los TODO en la fase 4.
 * Se adapta del ProductorConsumidor del ejemplo 8.4.
 * <p>
 * Productor-consumidor entre dos procesos unidos por una tubería: la caja (CashRegister) emite
 * tickets y el registro (Ledger) los suma.
 * <p>
 * Uso: java LiveCashRegister
 */
public class LiveCashRegister {

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO fase 4: ejecutar los dos escenarios con runScenario(), escribiendo antes cuál es.
        //      Escenario 1, caja lenta y registro rápido: 5 tickets; pausa de la caja, 500 ms,
        //      y del registro, 0.
        //      Escenario 2, caja rápida y registro lento: 10 tickets; pausa de la caja, 0, y del
        //      registro, 300 ms.
        // Escenario 1: la caja tarda en sacar tickets y el registro los lee al momento
        System.out.println("Escenario 1: caja lenta, registro rápido");
        runScenario("5", "500", "0");

    // Escenario 2: la caja saca los tickets de golpe y el registro tarda en leerlos
        System.out.println(" Escenario 2: caja rápida, registro lento ");
        runScenario("10", "0", "300");
    }

    /**
     * Lanza una CashRegister y un Ledger unidos por una tubería con ProcessBuilder.startPipeline()
     * y espera a los dos. Las trazas de los dos (salida de error) y el total del Ledger (salida
     * estándar) se ven en la consola del padre.
     *
     * @param tickets       número de tickets que emite la caja
     * @param registerPause pausa de la caja antes de cada ticket, en ms
     * @param ledgerPause   pausa del registro después de cada ticket, en ms
     */
    static void runScenario(String tickets, String registerPause, String ledgerPause)
            throws IOException, InterruptedException {
        // TODO fase 4
        //Creamos los procesos para poder trabajar con las clases
        // Creamos un ProcessBuilder para la caja y otro para el registro, con su clase y sus argumentos
        ProcessBuilder pbCaja = new ProcessBuilder(javaCommand("CashRegister", tickets, registerPause));
        ProcessBuilder pbRegistro = new ProcessBuilder(javaCommand("Ledger", ledgerPause));

        //Rederigimos los procesos.
        pbCaja.redirectError(ProcessBuilder.Redirect.INHERIT);
        pbRegistro.redirectError(ProcessBuilder.Redirect.INHERIT);
        // El total del registro (su salida normal) también sale en nuestra consola.
        // La salida normal de la caja NO, porque esa va por la tubería al registro
        pbRegistro.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        // Lanzamos la caja y el registro unidos por una tubería: lo que escribe la caja le llega al registro
        List<Process> procesos = ProcessBuilder.startPipeline(List.of(pbCaja, pbRegistro));

        // Esperamos a que termine la caja
        procesos.get(0).waitFor();
        // Y a que termine el registro
        procesos.get(1).waitFor();
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
