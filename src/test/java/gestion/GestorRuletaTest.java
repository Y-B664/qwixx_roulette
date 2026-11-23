package gestion;

import datos.Ruleta;
import datos.CasillaRuleta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorRuletaTest {
    private GestorRuleta gestorRuleta;
    @BeforeEach
    public void setUp() {
        gestorRuleta = new GestorRuleta();
    }
    @Test
    public void construirRuletaNoDebeDevolverNulo() {
        Ruleta ruleta = gestorRuleta.construirRuleta();
        assertNotNull(ruleta, "La ruleta no debe ser nula al construirse");
    }
    @Test
    public void girarRuletaNoDebeSerNulo() {
        CasillaRuleta resultado = gestorRuleta.girarRuleta();
        assertNotNull(resultado, "El giro no puede retornar null, debe dar una casilla");
    }
    @Test
    public void aplicarReglaCeroVerdeNoDebeGenerarError() {
        // Como el método aplicarReglaCeroVerde() es void, comprobamos que se ejecute sin que el programa colapse
        assertDoesNotThrow(() -> gestorRuleta.aplicarReglaCeroVerde(), "La regla del cero verde debe ejecutarse sin errores");
    }
}
