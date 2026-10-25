import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Consumidor de la caja en directo (proyecto A01.5). Esqueleto: completad el TODO en la fase 4.
 * Se adapta del Consumidor del ejemplo 8.4.
 *
 * Un registro que lee tickets de su entrada estándar ("número;céntimos"), suma sus importes y
 * espera una pausa después de cada uno. Cuando la entrada se acaba, escribe en la salida
 * estándar el número de tickets y el total. Anota en la salida de error la hora a la que lee
 * cada ticket.
 *
 * Uso: java Ledger <pausa en ms>
 */
public class Ledger {

    static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");  // hora con ms

    public static void main(String[] args) throws IOException, InterruptedException {
        int pause = Integer.parseInt(args[0]);

        // TODO fase 4: leer los tickets de la entrada estándar, línea a línea, hasta que se acabe;
        //      sumar sus importes, anotar en la salida de error la hora (TIME) y el número de cada
        //      ticket y esperar la pausa después de cada uno. Al terminar, escribir en la salida
        //      estándar el número de tickets y el total en euros.
    }
}
