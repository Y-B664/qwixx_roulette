package gestion;

import datos.Jugador;
import datos.Registro;
import impOperacionesBD.ImpOperacionesRegistro;
import operacionesBD.GenericDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class GestorJugadorTest {
    GestorJugador gestorJugador;
    GestorCarton gestorCarton;
    GenericDAO<Registro> impOperacionesRegistro;
    String timestamp;
    @BeforeEach
    public void setUp(){
        impOperacionesRegistro= new ImpOperacionesRegistro();
        gestorJugador = new GestorJugador(impOperacionesRegistro);
        gestorCarton =  new GestorCarton();
        timestamp = String.valueOf(System.nanoTime());
    }
    @Test
    public void crearJugadorOk() {
        assertNotNull(gestorJugador.crearJugador("Pedro",new Stack(),gestorCarton.crearCarton(4)));
    }
    @Test
    public void crearJugadorNombreNoNulo() {
        Jugador jugador = gestorJugador.crearJugador(null, new Stack(), gestorCarton.crearCarton(4));
        assertNull(jugador);
    }
    @Test
    public void crearJugadorStackNulo() {
        Jugador jugador = gestorJugador.crearJugador("Pedro", null, gestorCarton.crearCarton(4));
        assertNull(jugador);
    }
    @Test
    public void crearJugadorCartonNulo() {
        Jugador jugador = gestorJugador.crearJugador("Pedro", new Stack(), null);
        assertNull(jugador);
    }
    @Test
    public void crearJugadorExistente() {
        impOperacionesRegistro.crear(new Registro("Jugador"+timestamp, 50));
        Jugador jugador = gestorJugador.crearJugador("Jugador"+timestamp,new Stack(),gestorCarton.crearCarton(4));
        assertNull(jugador);
    }
    @Test
    public void confirmarGiroOk() {
        Jugador jugador = gestorJugador.crearJugador("Giro"+timestamp,new Stack(),gestorCarton.crearCarton(4));
        assertTrue(gestorJugador.confirmarGiro(jugador));
    }
    @Test
    public void confirmarGiroJugadorNulo() {
        assertFalse(gestorJugador.confirmarGiro(null));
    }

}
