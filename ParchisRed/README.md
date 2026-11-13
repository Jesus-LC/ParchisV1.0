# Parchís en red (P2P) — Hito 2B

CU-01 en P2P con el subsistema Sala real, MySQL para el perfil y comunicación entre computadoras.

## Módulos (uno por componente, mismo nombre que en StarUML)
| Módulo | Contenido | Depende de |
|---|---|---|
| DTOs | JugadorDTO, SalaDTO, PerfilDTO | — |
| Interfaces | IFachadaSala (sin cambios desde 2A), IObservadorSala, IPerfilDAO, SalaException, PersistenciaException | DTOs |
| Red | NodoP2P, ConexionPar, Mensaje, PrototipoP2P, DemoLocal | — |
| SalaMock | FachadaSalaMock, RedSimulada (Hito 2A, sirve para pruebas) | DTOs, Interfaces |
| Sala | **FachadaSala**, PerfilBO, ProtocoloSala | DTOs, Interfaces, Red |
| Persistencia | **PerfilDAOMySQL** (todo el SQL), PerfilDAOMemoria (solo pruebas), ConfiguracionBD | DTOs, Interfaces |
| Fabrica | FabricaFachadaSala: **único** que conoce clases concretas | Interfaces, Sala, Persistencia, SalaMock |
| Presentacion | FrmMenuSala, FrmSala, ControlSala, Main | DTOs, Interfaces, Fabrica |

La interfaz `IFachadaSala` no cambió. Lo único que se agregó a `Interfaces` fue el código `PERFIL_NO_DISPONIBLE` en `SalaException.Codigo`.

## 1. Base de datos (en cada computadora)
1. Instala MySQL y ejecuta una vez: `mysql -u root -p < sql/schema.sql`
2. Copia `db.example.properties` como `db.local.properties` y pon tu usuario y contraseña.
   **Ese archivo está en .gitignore. Nunca lo subas a GitHub.**
   Se busca en: `-Dparchis.db.config=ruta`, la carpeta desde donde ejecutas, `~/.parchis/`, o las variables `PARCHIS_DB_URL`, `PARCHIS_DB_USER`, `PARCHIS_DB_PASSWORD`.

## 2. Compilar y correr
```
mvn clean install
mvn -pl Presentacion exec:java      # o en NetBeans: ejecutar parchis.presentacion.Main
```
Para dos computadoras: misma red, misma versión, puerto abierto en el firewall. El anfitrión comparte la dirección que muestra su sala (IP:puerto). Si tiene VPN o varias tarjetas de red y muestra una IP equivocada, ejecuta con `-Dparchis.host=SU_IP`.

## 3. Modos de prueba (sin tocar código)
- `-Dparchis.dao=memoria` — perfiles en memoria, sin MySQL.
- `-Dparchis.fachada=mock` — usa FachadaSalaMock (la del 2A).

## 4. Pruebas automáticas (carpeta pruebas/, no es módulo)
Después de `mvn clean install`, en la raíz (Linux/Mac):
```
CP=$(ls -d */target/classes | tr '\n' ':')
mkdir -p pruebas/out && javac -cp "$CP" -d pruebas/out pruebas/*.java
java -Dparchis.dao=memoria -cp "$CP:pruebas/out" Casos 7100      # casos del CU-01 (A2, A3, datos inválidos)
java -Dparchis.dao=memoria -cp "$CP:pruebas/out" Carrera         # 5 jugadores a la vez contra sala de 4
java -Dparchis.dao=memoria -Dparchis.fachada=mock -cp "$CP:pruebas/out" Casos 7200   # lo mismo con el mock
```
En Windows reemplaza `$CP` por la lista de carpetas `*/target/classes` separadas por `;`.

## Reglas del manual que cumple este proyecto
- Vistas y controlador solo usan interfaces; las clases concretas solo las conoce `Fabrica`.
- Todo el SQL está en `PerfilDAOMySQL`; `PerfilBO` solo ve `IPerfilDAO`.
- No hay funciones extra (chat, partidas guardadas, ranking).
- Cada integrante debe hacer sus propios commits; no subas contraseñas.
