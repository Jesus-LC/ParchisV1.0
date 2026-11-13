import parchis.fabrica.FabricaFachadaSala; import parchis.interfaces.*; import parchis.dtos.*;
import java.util.*; import java.util.concurrent.*;
public class Carrera { public static void main(String[] a) throws Exception {
  int ronda=0, malas=0;
  for(int r=0;r<15;r++){
    int b=7400+r*20;
    IFachadaSala h=FabricaFachadaSala.crear(); h.crearSala("H",b);
    int n=5; List<IFachadaSala> pares=new ArrayList<>(); List<Future<String>> res=new ArrayList<>();
    ExecutorService ex=Executors.newFixedThreadPool(n); CountDownLatch go=new CountDownLatch(1);
    for(int i=0;i<n;i++){ IFachadaSala f=FabricaFachadaSala.crear(); final int k=i;
      res.add(ex.submit(()->{ go.await(); try{ f.unirseASala("J"+k,b+1+k,"127.0.0.1",b); synchronized(pares){pares.add(f);} return "ok"; }catch(SalaException e){ return e.getCodigo().name(); } })); }
    go.countDown(); int oks=0, llenas=0; for(var fu:res){ String s=fu.get(); if(s.equals("ok")) oks++; else if(s.equals("SALA_LLENA")) llenas++; }
    Thread.sleep(600);
    Set<Set<JugadorDTO>> copias=new HashSet<>(); copias.add(new HashSet<>(h.obtenerSala().jugadores())); for(var p:pares) copias.add(new HashSet<>(p.obtenerSala().jugadores()));
    boolean bien = oks==3 && llenas==2 && copias.size()==1 && copias.iterator().next().size()==4;
    if(!bien){ malas++; System.out.println("ronda "+r+": oks="+oks+" llenas="+llenas+" copiasDistintas="+copias.size()); }
    ex.shutdownNow();
  }
  System.out.println("rondas con problema: "+malas+" de 15"); System.exit(0);}}
