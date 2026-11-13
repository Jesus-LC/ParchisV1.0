import parchis.fabrica.FabricaFachadaSala;
import parchis.interfaces.*;
import parchis.dtos.*;
import java.util.function.BooleanSupplier;
public class Casos {
  static int fallos=0;
  static void ok(String caso, boolean cond){ System.out.println((cond?"  OK    ":"  FALLO ")+caso); if(!cond) fallos++; }
  static boolean espera(BooleanSupplier c){ for(int i=0;i<40;i++){ if(c.getAsBoolean()) return true; try{Thread.sleep(50);}catch(Exception e){} } return c.getAsBoolean(); }
  static SalaException.Codigo cod(Runnable2 r){ try{ r.run(); return null;}catch(SalaException e){ return e.getCodigo(); } }
  interface Runnable2{ void run() throws SalaException; }
  public static void main(String[] a) throws Exception {
    int b=Integer.parseInt(a[0]);
    System.out.println("Fachada="+System.getProperty("parchis.fachada","real")+"  puertos desde "+b);
    IFachadaSala pedro=FabricaFachadaSala.crear(), ana=FabricaFachadaSala.crear();
    int[] avisosPedro={0}; pedro.registrarObservador(s->avisosPedro[0]++);
    SalaDTO s1=pedro.crearSala("Pedro",b);
    ok("CP-01 crear sala: 1 jugador, pedro es anfitrión", s1.jugadores().size()==1 && s1.anfitrion().nombre().equals("Pedro") && s1.capacidad()==4);
    SalaDTO s2=ana.unirseASala("Ana",b+1,"127.0.0.1",b);
    ok("CP-02 Ana ve 2 jugadores", s2.jugadores().size()==2);
    ok("CP-02 Pedro actualiza su copia (2) y fue avisado", espera(()->pedro.obtenerSala().jugadores().size()==2) && avisosPedro[0]>=1);
    ok("CP-04/A3 anfitrión no disponible", cod(()->FabricaFachadaSala.crear().unirseASala("Luis",b+9,"127.0.0.1",b+90))==SalaException.Codigo.ANFITRION_NO_DISPONIBLE);
    IFachadaSala luis=FabricaFachadaSala.crear(), eva=FabricaFachadaSala.crear();
    luis.unirseASala("Luis",b+2,"127.0.0.1",b);
    eva.unirseASala("Eva",b+3,"127.0.0.1",b);
    ok("4 jugadores: todos los pares tienen la misma copia", espera(()->
        pedro.obtenerSala().jugadores().size()==4 && ana.obtenerSala().jugadores().size()==4 &&
        luis.obtenerSala().jugadores().size()==4 && eva.obtenerSala().jugadores().size()==4));
    ok("misma lista en todos", pedro.obtenerSala().jugadores().containsAll(ana.obtenerSala().jugadores())
        && ana.obtenerSala().jugadores().containsAll(eva.obtenerSala().jugadores()));
    ok("CP-03/A2 sala llena", cod(()->FabricaFachadaSala.crear().unirseASala("Tomas",b+4,"127.0.0.1",b))==SalaException.Codigo.SALA_LLENA);
    ok("CP-05 nombre vacío", cod(()->FabricaFachadaSala.crear().crearSala("  ",b+5))==SalaException.Codigo.DATOS_INVALIDOS);
    ok("CP-05 puerto 80", cod(()->FabricaFachadaSala.crear().crearSala("X",80))==SalaException.Codigo.DATOS_INVALIDOS);
    ok("CP-05 puerto en uso", cod(()->FabricaFachadaSala.crear().crearSala("X",b))==SalaException.Codigo.DATOS_INVALIDOS);
    ok("no cuenta al rechazado: sigue en 4", pedro.obtenerSala().jugadores().size()==4);
    System.out.println(fallos==0?"TODO OK":"HAY "+fallos+" FALLOS"); System.exit(fallos==0?0:1);
  }
}
