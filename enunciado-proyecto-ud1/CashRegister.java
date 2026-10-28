import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Productor de la caja en directo (proyecto A01.5). Esqueleto: completad el TODO en la fase 4.
 * Se adapta del Productor del ejemplo 8.4.
 *
 * Una caja registradora que emite tickets por su salida estándar, uno por línea, con el número
 * del ticket y su importe en céntimos ("número;céntimos"). Espera una pausa antes de cada
 * ticket. Anota en la salida de error la hora a la que escribe cada uno.
 *
 * Uso: java CashRegister <tickets> <pausa en ms>
 */
public class CashRegister {

    static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");  // hora con ms

    public static void main(String[] args) throws InterruptedException {
        int tickets = Integer.parseInt(args[0]);
        int pause = Integer.parseInt(args[1]);
        Random random = new Random();

        // TODO fase 4: para cada ticket, del 1 a tickets, esperar la pausa, escribir en la salida
        //      estándar "número;céntimos" con un importe aleatorio y anotar en la salida de error
        //      la hora (TIME) y el número del ticket. Al terminar, anotar también la hora.
        for (int i = 1; i <= tickets ; i++) {
            Thread.sleep(pause);
            int cents =  random.nextInt(10000);
            System.out.println(i + ";" + cents);
            System.err.println(LocalTime.now().format(TIME)+ " | ticket " + i);
        }
        System.err.println(LocalTime.now().format(TIME));
    }
}
