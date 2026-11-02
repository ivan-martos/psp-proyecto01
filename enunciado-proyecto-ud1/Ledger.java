import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Consumidor de la caja en directo (proyecto A01.5). Esqueleto: completad el TODO en la fase 4.
 * Se adapta del Consumidor del ejemplo 8.4.
 * <p>
 * Un registro que lee tickets de su entrada estándar ("número;céntimos"), suma sus importes y
 * espera una pausa después de cada uno. Cuando la entrada se acaba, escribe en la salida
 * estándar el número de tickets y el total. Anota en la salida de error la hora a la que lee
 * cada ticket.
 * <p>
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

        //Tenemos que crear dos contadores:
        int ticketCount = 0;
        long totalCents = 0;
        //Leemos la entrada del programa
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //Bucle que lea lineas y mientras no sea null sigue
        String line;
        while ((line = reader.readLine()) != null) {
            // Partimos el ticket por el ";" -> [0] es el número y [1] los céntimos
            String[] parts = line.split(";");

            // Pasamos los céntimos de texto a número
            long cents = Long.parseLong(parts[1]);

            // Lo sumamos al total
            totalCents += cents;

            // Contamos un ticket más
            ticketCount++;

            //Miramos la hora y qué ticket hemos leído
            System.err.println(LocalTime.now().format(TIME) + " registro: ticket " + parts[0]);
            //Pausamos:
            Thread.sleep(pause);
        }
        // Cuando ya no llegan más tickets, escribimos cuántos ha habido y el total en euros
        // %.2f hace que salgan los euros con 2 decimales
        System.out.println("Registro: " + ticketCount + " tickets, total " + String.format("%.2f", totalCents / 100.0) + " euros");
    }
}
