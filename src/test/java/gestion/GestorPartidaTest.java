package gestion;


import datos.*;
import datos.bot.Bot;
import datos.bot.DificilBot;
import datos.bot.FacilBot;
import datos.bot.MedioBot;
import datos.carton.Carton;
import datos.carton.CasillaCarton;
import datos.carton.FilaColor;
import impOperacionesBD.ImpOperacionesRegistro;
import operacionesBD.GenericDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class GestorPartidaTest {
    GestorPartida gestorPartida;
    GestorJugador gestorJugador;
    GestorCarton gestorCarton;
    GestorTemporizador gestorTemporizador;
    GenericDAO<Registro> impOperacionesRegistro;
    @BeforeEach
    public void setUp(){
        impOperacionesRegistro= new ImpOperacionesRegistro();
        gestorJugador = new GestorJugador(impOperacionesRegistro);
        gestorCarton = new GestorCarton();
        gestorTemporizador = new GestorTemporizador();
        gestorPartida = new GestorPartida(gestorJugador, gestorCarton,gestorTemporizador);

    }
    @Test
    public void iniciarPartidaOk()
    {
        assertNotNull(gestorPartida.iniciarPartida(2));
    }
    @Test
    public void iniciarPartidaPuntosInicialesOk()
    {  Partida partida =  gestorPartida.iniciarPartida(2);
        Jugador jugador = partida.getJugadores()[0];
        assertEquals(5,jugador.getSaldoPuntos());
    }

    @Test
    public void otorgarComodinInicialOk() {
        Jugador jugador = new Jugador("Pedro",new Stack(),gestorCarton.crearCarton(4));
        gestorPartida.otorgarComodinInicial(jugador);
    }
    @Test
    public void controlarTemporizadorOk() throws InterruptedException {
        AtomicBoolean finalizo= new AtomicBoolean(false);
        Runnable accion =  () -> {finalizo.set(true);};
        gestorPartida.controlarTemporizador(3, true, accion);
        Thread.sleep(3200);
        assertTrue(finalizo.get());
    }
    @Test
    public void controlarTemporizadorDetener() throws InterruptedException {
        AtomicBoolean finalizo= new AtomicBoolean(false);
        gestorTemporizador.iniciar(3, () -> {finalizo.set(true);});
        gestorTemporizador.detener();
        Thread.sleep(3200);
        assertFalse(finalizo.get());
    }

    @Test
    public void finalizarPartidaOk() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for(Jugador j: jugadores){
            for (int i = 0; i < 2; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setCandadoTachado(true);
                fila.setEstaBloqueadaGlobal(true);
            }
        }
      gestorPartida.finalizarPartida();
      assertEquals(EstadoPartida.FINALIZADA,partida.getEstado());
    }
    @Test
    public void finalizarPartidaCancelada() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for(Jugador j: jugadores){
            for (int i = 0; i < 2; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setCandadoTachado(true);
                fila.setEstaBloqueadaGlobal(true);
            }
        }
        partida.setEstado(EstadoPartida.CANCELADA);
        gestorPartida.finalizarPartida();
        assertEquals(EstadoPartida.CANCELADA,partida.getEstado());
    }
    @Test
    public void cancelarPartidaOk() {
        Partida partida = gestorPartida.iniciarPartida(2);
        gestorPartida.cancelarPartida();
        assertEquals(EstadoPartida.CANCELADA,partida.getEstado());
    }
    @Test
    public void cancelarPartidaFinalizada() {
        Partida partida = gestorPartida.iniciarPartida(2);
        partida.setEstado(EstadoPartida.FINALIZADA);
        gestorPartida.cancelarPartida();
        assertEquals(EstadoPartida.FINALIZADA,partida.getEstado());
    }
    @Test
    public void configurarBotFacilOk() {
        assertNotNull(gestorPartida.configurarBot(Dificultad.FACIL));
    }
    @Test
    public void configurarBotMedioOk() {
        assertNotNull(gestorPartida.configurarBot(Dificultad.MEDIO));
    }
    @Test
    public void configurarBotDificilOk() {
        assertNotNull(gestorPartida.configurarBot(Dificultad.DIFICIL));
    }

    @Test
    public void configurarBotNull() {
        assertNull(gestorPartida.configurarBot(null));
    }
    @Test
    public void configurarBotFacilInstanceOk() {
        assertInstanceOf(FacilBot.class,gestorPartida.configurarBot(Dificultad.FACIL));
    }
    @Test
    public void configurarBotMedioInstanceOk() {
        assertInstanceOf(MedioBot.class,gestorPartida.configurarBot(Dificultad.MEDIO));
    }
    @Test
    public void configurarBotDificilInstanceOk() {
        assertInstanceOf(DificilBot.class,gestorPartida.configurarBot(Dificultad.DIFICIL));
    }
    @Test
    public void agregarBotOk() {
        gestorPartida.iniciarPartida(2);
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        gestorPartida.agregarBot(bot);
        int size = gestorPartida.partida.getJugadores().length;
        assertInstanceOf(DificilBot.class,gestorPartida.partida.getJugadores()[size-1]);
    }
    @Test
    public void calcularPuntajeFinalOk() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for(Jugador j: jugadores){
            FilaColor primeraFila =  j.getCarton().getFilas().getFirst();
            primeraFila.setCandadoTachado(true);
            primeraFila.setEstaBloqueadaGlobal(true);
            CasillaCarton[] casillas = primeraFila.getCasillas();
            for (int i = 0; i < 6; i++) {
                casillas[i].setMarcada(true);
            }
            gestorCarton.sumarPuntosTotales(j);
        }
        for(Jugador j: jugadores){
            assertEquals(26,j.getSaldoPuntos());
        }
    }
    @Test
    public void calcularPuntajeFinalSinFilasCompletadas() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for (Jugador j : partida.getJugadores()) {
            gestorCarton.sumarPuntosTotales(j);
            assertEquals(5, j.getSaldoPuntos());
        }
    }
    @Test
    public void calcularPuntajeFinalSinCandadoTachado() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for (Jugador j : partida.getJugadores()) {
            FilaColor primeraFila = j.getCarton().getFilas().getFirst();
            primeraFila.setEstaBloqueadaGlobal(true);
            gestorCarton.sumarPuntosTotales(j);
            assertEquals(5, j.getSaldoPuntos());
        }
    }
    @Test
    public void calcularPuntajeFinalSinBloqueoGlobal() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for (Jugador j : partida.getJugadores()) {
            FilaColor primeraFila = j.getCarton().getFilas().getFirst();
            primeraFila.setCandadoTachado(true);
            gestorCarton.sumarPuntosTotales(j);
            assertEquals(5, j.getSaldoPuntos());
        }
    }
    @Test
    public void evaluarCondicionFinOk() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for(Jugador j: jugadores){
            for (int i = 0; i < 2; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setCandadoTachado(true);
                fila.setEstaBloqueadaGlobal(true);
            }
        }
        assertTrue(gestorPartida.evaluarCondicionFin());
    }
    @Test
    public void evaluarCondicionFinCon2FilasCompletas() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for(Jugador j: jugadores){
            for (int i = 0; i < 1; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setCandadoTachado(true);
                fila.setEstaBloqueadaGlobal(true);
            }
        }
        assertFalse(gestorPartida.evaluarCondicionFin());
    }
    @Test
    public void evaluarCondicionFinCandadosSinTachar() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for(Jugador j: jugadores){
            for (int i = 0; i < 2; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setEstaBloqueadaGlobal(true);
            }
        }
        assertFalse(gestorPartida.evaluarCondicionFin());
    }
    @Test
    public void evaluarCondicionFinSinEstadoGlobal() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(5);
        gestorPartida.agregarBot(bot);
        for(Jugador j: jugadores){
            for (int i = 0; i < 2; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setCandadoTachado(true);
        }
        assertFalse(gestorPartida.evaluarCondicionFin());
    }}
    @Test
    public void mostrarGanadorOk() {
        Partida partida = gestorPartida.iniciarPartida(2);
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(10);
        gestorPartida.agregarBot(bot);
        for (Jugador j : partida.getJugadores()) {
            for (int i = 0; i < 2; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setCandadoTachado(true);
                fila.setEstaBloqueadaGlobal(true);
                CasillaCarton[] casillasCarton = fila.getCasillas();
                for (int h = 0; h < 4; h++) {
                    casillasCarton[h].setMarcada(true);
                }
                }
            gestorCarton.sumarPuntosTotales(j);
            System.out.println(j.getSaldoPuntos());
        }

        assertInstanceOf(DificilBot.class,gestorPartida.mostrarGanador());
    }
    @Test
    public void mostrarGanadorJugadoresNulos() {
        Partida partida = new Partida(0,false,null,60,2);
        gestorPartida.setPartida(partida);
        assertNull(gestorPartida.mostrarGanador());
    }
    @Test
    public void mostrarGanadorPrimerJugadorNulo() {
        Partida partida = new Partida(0,false,null,60,2);
        gestorPartida.setPartida(partida);
        assertNull(gestorPartida.mostrarGanador());
    }
    @Test
    public void mostrarGanadorDespuesDelPrimerJugadorNulo() {
        String timestamp = String.valueOf(System.nanoTime());
        Partida partida = new Partida(0,false,null,60,3);
        Jugador[] jugadores = partida.getJugadores();
        jugadores[1] = new Jugador("jugadorNulo"+timestamp,null,null);
        jugadores[2] = null;
        gestorPartida.setPartida(partida);
        assertNull(gestorPartida.mostrarGanador());
    }
    @Test
    public void mostrarGanador3() {
        String timestamp = String.valueOf(System.nanoTime());
        Partida partida = gestorPartida.iniciarPartida(3);
        Jugador[] jugadores = partida.getJugadores();
        Carton cartonJugador3 = gestorCarton.crearCarton(4);
        jugadores[1] = gestorJugador.crearJugador("jugador"+timestamp,new Stack(),cartonJugador3);
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(10);
        gestorPartida.agregarBot(bot);
        System.out.println(jugadores.length);
        for (Jugador j : partida.getJugadores()) {
            System.out.println(j);
            System.out.println(j.getSaldoPuntos());
            for (int i = 0; i < 4; i++) {
                FilaColor fila = j.getCarton().getFilas().get(i);
                fila.setCandadoTachado(true);
                fila.setEstaBloqueadaGlobal(true);
                CasillaCarton[] casillasCarton = fila.getCasillas();
                for (int h = 0; h < 4; h++) {
                    casillasCarton[h].setMarcada(true);
                }
            }
            gestorCarton.sumarPuntosTotales(j);
            System.out.println(j.getSaldoPuntos());
        }
        assertInstanceOf(DificilBot.class,gestorPartida.mostrarGanador());
    }

    @Test
    public void verificarConfirmacionGiroOK() {
        String timestamp = String.valueOf(System.nanoTime());
        Partida partida = gestorPartida.iniciarPartida(3);
        Jugador[] jugadores = partida.getJugadores();
        Carton cartonJugador3 = gestorCarton.crearCarton(4);
        jugadores[1] = gestorJugador.crearJugador("jugador"+timestamp,new Stack(),cartonJugador3);
        gestorJugador.confirmarGiro(jugadores[0]);
        gestorJugador.confirmarGiro(jugadores[1]);
        Bot bot = gestorPartida.configurarBot(Dificultad.DIFICIL);
        bot.setSaldoPuntos(10);
        gestorJugador.confirmarGiro(bot);
        gestorPartida.agregarBot(bot);
        gestorPartida.verificarConfirmacionGiro();
        assertTrue(gestorPartida.getPartida().isGiroConfirmado());
    }
    @Test
    public void verificarConfirmacionGiro1JugadorSinConfirmar() {
        String timestamp = String.valueOf(System.nanoTime());
        Partida partida = gestorPartida.iniciarPartida(2);
        Jugador[] jugadores = partida.getJugadores();
        Carton cartonJugador3 = gestorCarton.crearCarton(4);
        jugadores[1] = gestorJugador.crearJugador("jugador"+timestamp,new Stack(),cartonJugador3);
        gestorJugador.confirmarGiro(jugadores[0]);
        gestorPartida.verificarConfirmacionGiro();
        assertFalse(gestorPartida.getPartida().isGiroConfirmado());
    }
}
