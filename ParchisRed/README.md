# Parchís en red (P2P) — Hito 2A

CU-01 en P2P sobre mocks + prototipo del estilo (dos computadoras se mandan un mensaje).

## Módulos (uno por componente de la vista de componentes, mismo nombre)
| Módulo | Contenido | Depende de |
|---|---|---|
| DTOs | JugadorDTO, SalaDTO | — |
| Interfaces | IFachadaSala, IObservadorSala, SalaException | DTOs |
| SalaMock | FachadaSalaMock, RedSimulada (en memoria; falla en A2 y A3) | DTOs, Interfaces |
| Fabrica | FabricaFachadaSala (**único** que conoce al mock) | Interfaces, SalaMock |
| Presentacion | FrmMenuSala, FrmSala, ControlSala, Main | DTOs, Interfaces, Fabrica |
| Red | NodoP2P, ConexionPar, Mensaje, PrototipoP2P (prototipo del estilo) | — |

Regla: ninguna vista ni el controlador nombran al mock; solo la fábrica.
En el Hito 2B se cambia **una línea** en `FabricaFachadaSala` para usar la fachada real.

## Compilar y correr el CU-01 (mock)
```
mvn clean install
mvn -pl Presentacion exec:java      # o en NetBeans: ejecutar parchis.presentacion.Main
```
Para simular varios pares en una misma PC, usa la ventana inicial varias veces (el puerto local sube de 1 en 1).

## Prototipo P2P entre dos computadoras (módulo Red)
```
# Computadora A
java -cp Red/target/classes parchis.red.PrototipoP2P Ana 5000
# Computadora B (IP_DE_A = la IP de la computadora A en la red local)
java -cp Red/target/classes parchis.red.PrototipoP2P Luis 5001 IP_DE_A 5000
```
Al conectarse cada nodo manda su saludo con su estilo; después, lo que escribas + Enter llega a todos los pares. `salir` termina.
Prueba rápida en una sola máquina: `java -cp Red/target/classes parchis.red.DemoLocal`.
(Abre los puertos en el firewall si las máquinas no se ven.)

## Supuestos a revisar contra su especificación del CU-01
- A2 = sala llena (máx. 4); A3 = anfitrión no disponible; además hay validación de datos (nombre vacío, puerto fuera de 1024–65535, puerto en uso).
- `unirseASala` recibe también el puerto local porque en P2P cada par escucha en el suyo.
- `IObservadorSala` existe porque el paso 3 (cada par actualiza su copia) debe reflejarse en la pantalla.

## Recordatorios del manual
- Cada integrante hace sus propios commits; no subir contraseñas de MySQL (el `.gitignore` ya excluye `*.local.properties` y `credenciales*`).
- Si cambian la interfaz de la fachada, avisen al equipo y actualicen el modelo de StarUML.
