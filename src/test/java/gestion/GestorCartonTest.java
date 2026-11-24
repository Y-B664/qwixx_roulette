package gestion;

import datos.CasillaRuleta;
import datos.Color;
import datos.Jugador;
import datos.Registro;
import datos.carton.Carton;
import datos.carton.CasillaCarton;
import datos.carton.FilaColor;
import impOperacionesBD.ImpOperacionesRegistro;
import operacionesBD.GenericDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class GestorCartonTest {
    GestorCarton gestorCarton;
    GestorPartida gestorPartida;
    GestorJugador gestorJugador;
    GestorTemporizador gestorTemporizador;
    GenericDAO<Registro> impOperacionesRegistro;
    @BeforeEach
    public void setUp(){
        gestorCarton = new GestorCarton();
        impOperacionesRegistro= new ImpOperacionesRegistro();
        gestorJugador = new GestorJugador(impOperacionesRegistro);
        gestorTemporizador = new GestorTemporizador();
        gestorPartida = new GestorPartida(gestorJugador, gestorCarton,gestorTemporizador);
    }
    @Test
    public void generarFilasOk(){
        List<FilaColor> filas = gestorCarton.generarFilas();
        assertEquals(4,filas.size());
    };
    @Test
    public void crearCartonOk(){
        Carton carton = gestorCarton.crearCarton(4);
        assertNotNull(carton);
        assertNotNull(carton.getFilas());
    };
    @Test
    public void validarMarcacionOk(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[3];
        CasillaRuleta casillaRuleta = new CasillaRuleta(9,Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor,casillaCarton,casillaRuleta);
        assertTrue(valido);
        assertTrue(casillaCarton.isMarcada());
    };

    @Test
    public void validarMarcacionCasillaMarcada(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[6];
        casillaCarton.setMarcada(true);
        CasillaRuleta casillaRuleta = new CasillaRuleta(8,Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor,casillaCarton,casillaRuleta);
        assertFalse(valido);
    };

    @Test
    public void validarMarcacionCasillaBloqueada(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[10];
        casillaCarton.setBloqueada(true);
        CasillaRuleta casillaRuleta = new CasillaRuleta(10,Color.ROJO);
        boolean valido =gestorCarton.validarMarcacion(filaColor,casillaCarton,casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    };
    @Test
    public void validarMarcacionCasillaDiferenteDeValorResultado(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(2,Color.ROJO);
        boolean valido =gestorCarton.validarMarcacion(filaColor,casillaCarton,casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    };
    @Test
    public void validarMarcacionColorNegroFilaAmarilla() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(1);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.NEGRO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertTrue(valido);
        assertTrue(casillaCarton.isMarcada());
    }

    @Test
    public void validarMarcacionColorNegroFilaVerde() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(2);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.NEGRO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertTrue(valido);
        assertTrue(casillaCarton.isMarcada());
    }

    @Test
    public void validarMarcacionColorNegroFilaAzul() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(3);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.NEGRO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    }

    @Test
    public void validarMarcacionColorNegroFilaRoja() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.NEGRO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    }
    @Test
    public void validarMarcacionColorRojoFilaAzul() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(3);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertTrue(valido);
        assertTrue(casillaCarton.isMarcada());
    }

    @Test
    public void validarMarcacionColorRojoFilaRoja() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertTrue(valido);
        assertTrue(casillaCarton.isMarcada());
    }

    @Test
    public void validarMarcacionColorRojoFilaAmarilla() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(1);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    }

    @Test
    public void validarMarcacionColorRojoFilaVerde() {
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(2);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(casillaCarton.getValor(), Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor, casillaCarton, casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    }
    @Test
    public void validarMarcacionCasillaFilaColorNulo(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        CasillaRuleta casillaRuleta = new CasillaRuleta(7,Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(null,casillaCarton,casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    };
    @Test
    public void validarMarcacionCasillaCasillaCartonNulo(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaRuleta casillaRuleta = new CasillaRuleta(7,Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor,null,casillaRuleta);
        assertFalse(valido);
    };
    @Test
    public void validarMarcacionCasillaCasillaRuletaNulo(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[5];
        boolean valido = gestorCarton.validarMarcacion(filaColor,casillaCarton,null);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    };

    @Test
    public void validarMarcacionFilaBloqueada(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(3);
        filaColor.setEstaBloqueadaGlobal(true);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        CasillaCarton casillaCarton = casillasCarton[2];
        CasillaRuleta casillaRuleta = new CasillaRuleta(2,Color.ROJO);
        boolean valido = gestorCarton.validarMarcacion(filaColor,casillaCarton,casillaRuleta);
        assertFalse(valido);
        assertFalse(casillaCarton.isMarcada());
    };


    @Test
    public void marcarFalloOk(){
        Jugador jugador = new Jugador("Pedro",new Stack(),gestorCarton.crearCarton(4));
        jugador.getCarton().setFallos(1);
        int fallosActuales = jugador.getCarton().getFallos();
        gestorCarton.marcarFallo(jugador);
        assertEquals(fallosActuales+1,jugador.getCarton().getFallos());
    };

    @Test
    public void marcarFalloJugadorNulo(){
        assertFalse(gestorCarton.marcarFallo(null));
    };
    @Test
    public void marcarFalloJugadorConTodosLosFallos(){
        Jugador jugador = new Jugador("Pedro",new Stack(),gestorCarton.crearCarton(4));
        jugador.getCarton().setFallos(4);
        assertFalse(gestorCarton.marcarFallo(jugador));
    };
    @Test
    public void bloquearIzquierdaOk(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(1);
        assertTrue(gestorCarton.bloquearIzquierda(6,filaColor));
        for (int i = 0; i <=6 ; i++) {
            assertTrue(filaColor.getCasillas()[i].isBloqueada());
        }
    };
    @Test
    public void bloquearIzquierdaPrimerCasillaOk(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(2);
        assertTrue(gestorCarton.bloquearIzquierda(1,filaColor));
        assertTrue(filaColor.getCasillas()[0].isBloqueada());
    };

    @Test
    public void validarCierreCandadoOk(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(3);
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        for (int i = 0; i < 6; i++) {
            casillasCarton[i].setMarcada(true);
        }
        assertTrue(gestorCarton.validarCierreCandado(filaColor));
        assertTrue(filaColor.isEstaBloqueadaGlobal());
        assertTrue(filaColor.isCandadoTachado());
    };
    @Test
    public void validarCierreCandado5CasillasNoMarcadas(){
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.get(3);
        assertFalse(gestorCarton.validarCierreCandado(filaColor));
        assertFalse(filaColor.isEstaBloqueadaGlobal());
        assertFalse(filaColor.isCandadoTachado());
    };
    @Test
    public void validarCierreCandadoFilaNula(){
        assertFalse(gestorCarton.validarCierreCandado(null));
    };
    @Test
    public void validarCierreCandadoCasillasNulas(){
        FilaColor filaColor = new FilaColor(Color.ROJO,null);
        assertFalse(gestorCarton.validarCierreCandado(filaColor));
    };
    @Test
    public void sumarPuntosTotalesConCasillasMarcadasSoloPuntos() {
        String timestamp = String.valueOf(System.nanoTime());
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        for (int i = 0; i < 7; i++) {
            casillasCarton[i].setMarcada(true);
        }
        filaColor.setEstaBloqueadaGlobal(true);
        filaColor.setCandadoTachado(true);
        Carton carton = new Carton(filasColor, 4);
        Jugador jugador = new Jugador("Jugador" + timestamp, null, carton);
        int puntos = gestorCarton.sumarPuntosTotales(jugador);
        assertEquals(28, puntos);
        assertEquals(28, jugador.getSaldoPuntos());
    }
    @Test
    public void sumarPuntosTotalesConDeuda() {
        String timestamp = String.valueOf(System.nanoTime());
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillas = filaColor.getCasillas();
        for (int i = 0; i < 5; i++) {
            casillas[i].setMarcada(true);
        }
        filaColor.setEstaBloqueadaGlobal(true);
        filaColor.setCandadoTachado(true);
        Carton carton = new Carton(filasColor, 4);
        Jugador jugador = new Jugador("Jugador" + timestamp, null, carton);
        jugador.setEnDeuda(5);
        int puntos = gestorCarton.sumarPuntosTotales(jugador);
        assertEquals(10, puntos);
        assertEquals(10, jugador.getSaldoPuntos());
    }

    @Test
    public void sumarPuntosTotalesConUnFalloYDeuda() {
        String timestamp = String.valueOf(System.nanoTime());
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillas = filaColor.getCasillas();
        for (int i = 0; i < 5; i++) {
            casillas[i].setMarcada(true);
        }
        filaColor.setEstaBloqueadaGlobal(true);
        filaColor.setCandadoTachado(true);
        Carton carton = new Carton(filasColor, 4);
        carton.setFallos(1);
        Jugador jugador = new Jugador("Jugador1" + timestamp, null, carton);
        jugador.setEnDeuda(5);
        int puntos = gestorCarton.sumarPuntosTotales(jugador);
        assertEquals(5, puntos);
        assertEquals(5, jugador.getSaldoPuntos());
    }
    @Test
    public void sumarPuntosTotalesSinBloqueo() {
        String timestamp = String.valueOf(System.nanoTime());
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillas = filaColor.getCasillas();
        for (int i = 0; i < 5; i++) {
            casillas[i].setMarcada(true);
        }
        Carton carton = new Carton(filasColor, 4);
        Jugador jugador = new Jugador("Jugador1" + timestamp, null, carton);
        int puntos = gestorCarton.sumarPuntosTotales(jugador);
        assertEquals(0, puntos);
        assertEquals(0, jugador.getSaldoPuntos());
    }
    @Test
    public void sumarPuntosTotalesJugadorNulo() {
        int puntos = gestorCarton.sumarPuntosTotales(null);
        assertEquals(0, puntos);
    }
    @Test
    public void sumarPuntosTotalesConSaldo() {
        String timestamp = String.valueOf(System.nanoTime());
        List<FilaColor> filasColor = gestorCarton.generarFilas();
        FilaColor filaColor = filasColor.getFirst();
        CasillaCarton[] casillas = filaColor.getCasillas();
        for (int i = 0; i < 5; i++) {
            casillas[i].setMarcada(true);
        }
        filaColor.setEstaBloqueadaGlobal(true);
        filaColor.setCandadoTachado(true);
        Carton carton = new Carton(filasColor, 4);
        Jugador jugador = new Jugador("Jugador1" + timestamp, null, carton);
        jugador.setSaldoPuntos(5);
        int puntos = gestorCarton.sumarPuntosTotales(jugador);
        assertEquals(20, puntos);
        assertEquals(20, jugador.getSaldoPuntos());
    }

}