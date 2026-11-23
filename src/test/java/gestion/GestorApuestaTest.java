package gestion;

import datos.Apuesta;
import datos.TipoApuesta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorApuestaTest {
    private GestorApuesta gestorApuesta;

    @BeforeEach
    public void setUp() {
        gestorApuesta = new GestorApuesta();
    }

    @Test
    public void hacerApuestaDebeRetornarTrue() {
        Apuesta apuesta = new Apuesta(TipoApuesta.COLOR, 10, "ROJO");
        assertTrue(gestorApuesta.hacerApuesta(apuesta), "La apuesta debería realizarse con éxito");
    }

    @Test
    public void validarApuestaDebeSerCorrecta() {
        Apuesta apuesta = new Apuesta(TipoApuesta.NUMERO, 25, "25");
        assertTrue(gestorApuesta.validarApuesta(apuesta), "La apuesta por número debe ser válida");
    }
    @Test
    public void solicitarApuestaInicialNoDebeSerNula() {
        // Evalúa el método solicitarApuestaInicial()
        Apuesta apuesta = gestorApuesta.solicitarApuestaInicial();
        // Este test fallará (rojo) hasta que dejes de retornar "null" en el gestor
        assertNotNull(apuesta, "Se debe generar una apuesta inicial válida, no null");
    }

    @Test
    public void calcularPagoDinamicoDebeSerCeroOMayor() {
        // Evalúa el método calcularPagoDinamico()[cite: 9]
        int pago = gestorApuesta.calcularPagoDinamico();
        assertTrue(pago >= 0, "El pago dinámico calculado nunca debe ser un valor negativo");
    }
}