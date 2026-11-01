# Informe de ventas en paralelo

Programa que calcula las ventas de una cadena de 8 tiendas. El padre (`SalesReport`) lanza un
proceso hijo (`ShopSummary`) por cada tienda, recoge lo que ha calculado cada uno y saca el
total. Si alguna tienda falla o tarda demasiado, lo avisa y sigue con las demás.

## Grupo

| Nombre | Qué ha hecho |
|---|---|
| Gabriel Valencia Bañares | |
| Iván Martos Vivas | |
| Juan Alberto | |

## Cómo se ejecuta

Lo hemos probado desde IntelliJ IDEA con Java 25 (Temurin 25.0.4.1): se abre `SalesReport` y se
le da a ejecutar. No hace falta lanzar `ShopSummary` a mano, ya lo lanza el padre.

[Falta: cómo se compila y se lanza desde la terminal.]

## Procesos

| Clase | Papel | Entrada | Salida | Códigos de salida |
|---|---|---|---|---|
| `SalesReport` | Padre. Lanza un hijo por tienda, lee lo que calcula cada uno, lo suma y escribe el informe | Nada | Salida estándar: una línea por tienda (PID, unidades y euros), el total, cuántas tiendas han fallado y el tiempo | — |
| `ShopSummary` | Hijo. Calcula las ventas de una tienda | El número de la tienda, por argumento (del 1 al 8) | Salida estándar: una línea `unidades;céntimos`. Salida de error: el mensaje si algo va mal | 0: todo bien. 1: la tienda no existe o el argumento está mal |

Cómo se conectan: el padre lanza cada hijo con `ProcessBuilder`, usando la misma versión de Java
y el mismo classpath que él, y le pasa el número de tienda como argumento. La salida normal del
hijo va por una tubería hasta el padre, que la lee con `inputReader()`, la parte por el `;` y
la suma a los totales. La salida de error del hijo no pasa por el padre: se hereda
(`redirectError(INHERIT)`), así que sus mensajes salen directamente en nuestra consola.

Antes de leer nada, el padre espera a cada hijo como mucho `TIMEOUT` segundos. Si no ha
terminado, lo mata (`destroy()`). Si ha terminado pero con un código distinto de 0, no lee su
resultado. En los dos casos avisa de qué tienda ha fallado y la cuenta en `failedShops`.

El informe se hace de dos formas:
- **Uno a uno**: lanza una tienda, espera a que acabe y la recoge, y luego pasa a la siguiente.
- **Todos a la vez**: primero lanza las 8 tiendas y las guarda en un array, y después las va
  recogiendo una por una. Así las 8 trabajan al mismo tiempo.

## Pruebas realizadas

| Prueba | Resultado esperado | Resultado obtenido |
|---|---|---|
| Informe de las 8 tiendas (uno a uno y todos a la vez) | Total del enunciado: 1.200.014.349 unidades y 3.381.671.938,70 € | Sale exactamente eso en los dos modos, y cada tienda coincide con la tabla del enunciado |
| Una tienda que no existe (`SHOPS = 9`) | Las tiendas 1-8 bien, la 9 falla con código 1 y no salta ninguna excepción | El hijo escribe "No existe la tienda 9: hay de la 1 a la 8", el padre dice que la tienda 9 ha fallado con código 1, el total sigue siendo el correcto y "tiendas que han fallado: 1" |
| Tiempo límite a 0 (`TIMEOUT = 0`) | Ningún hijo termina a tiempo, todos se cortan y no salta ninguna excepción | Las 8 tiendas salen como "no ha respondido a tiempo", el total es 0 unidades y 0,00 €, y "tiendas que han fallado: 8" |

## Tiempos

Lo hemos probado en un ordenador con [X] núcleos:

| Modo | Tiempo |
|---|---|
| Uno a uno | 3.359 ms |
| Todos a la vez | 471 ms |

Todos a la vez va unas **7,13 veces más rápido**.

¿Por qué no va 8 veces más rápido si hay 8 tiendas? Porque no todo se puede hacer en paralelo:
- Arrancar cada hijo cuesta un poco, porque hay que abrir una Java nueva para cada uno, y eso
  no se acelera.
- El padre sigue recogiendo los resultados uno detrás de otro.
- Si el ordenador tiene menos de 8 núcleos, no puede tener las 8 tiendas trabajando de verdad a
  la vez, y algunas tienen que esperar su turno.

## Caja en directo

[Las trazas de los dos escenarios y qué demuestran: quién espera en cada uno y por qué.]

## Depuración

[Una captura de IntelliJ IDEA detenido en un punto de ruptura del proceso padre, con la vista
Variables, y qué habéis comprobado con ella. Si habéis encontrado algún error depurando,
contadlo.]
