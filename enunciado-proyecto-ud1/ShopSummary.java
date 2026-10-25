import java.util.Random;

/**
 * Proceso hijo del informe de ventas: resume las ventas de una tienda.
 *
 * Simula las ventas de la tienda indicada y escribe en la salida estándar una sola línea con
 * el total de unidades y el importe en céntimos: "unidades;céntimos". Los mensajes de error van
 * a la salida de error. Las ventas se generan con una semilla fija (el número de la tienda), así
 * que cada tienda da siempre el mismo resultado.
 *
 * Uso: java ShopSummary <tienda>        (tienda: de 1 a 8)
 * Código de salida: 0 si todo va bien y 1 si el argumento es incorrecto.
 */
public class ShopSummary {

    static final int SHOPS = 8;
    static final int SALES = 50_000_000;  // ventas que se simulan en cada tienda
    static final int[] PRICES = {450, 105, 120, 899, 185, 260, 310, 65, 199, 225};  // céntimos

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java ShopSummary <tienda>");
            System.exit(1);
        }
        int shop = 0;
        try {
            shop = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("La tienda tiene que ser un número: " + args[0]);
            System.exit(1);
        }
        if (shop < 1 || shop > SHOPS) {
            System.err.println("No existe la tienda " + shop + ": hay de la 1 a la " + SHOPS);
            System.exit(1);
        }

        Random random = new Random(shop);  // misma tienda, mismas ventas
        long units = 0;
        long cents = 0;
        for (int i = 0; i < SALES; i++) {
            int product = random.nextInt(PRICES.length);
            int amount = 1 + random.nextInt(5);
            units += amount;
            cents += amount * PRICES[product];
        }
        System.out.println(units + ";" + cents);
    }
}
