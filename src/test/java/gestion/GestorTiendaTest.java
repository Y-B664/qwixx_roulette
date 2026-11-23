package gestion;

import datos.Tienda;
import datos.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GestorTiendaTest {
    private GestorTienda gestorTienda;
    @BeforeEach
    public void setUp() {
        gestorTienda = new GestorTienda();
    }
    @Test
    public void abrirTiendaDebeCrearInstancia() {
        Tienda tienda = gestorTienda.abrirTienda();
        assertNotNull(tienda, "La tienda debe abrirse correctamente");
    }
    @Test
    public void generarOfertaAleatoriaDebeRetornarLista() {
        List<PowerUp> oferta = gestorTienda.generarOfertaAleatoria();
        assertNotNull(oferta, "La oferta generada no debe ser nula");
    }
    @Test
    public void generarOfertaAleatoriaDebeEstarVaciaPorDefecto() {
        // Evalúa el comportamiento inicial de generarOfertaAleatoria() que retorna un ArrayList vacío[cite: 9]
        List<PowerUp> oferta = gestorTienda.generarOfertaAleatoria();
        assertTrue(oferta.isEmpty(), "La oferta inicial debe estar vacía hasta que se configure la lógica");
    }

    @Test
    public void comprarPowerUpNoDebeRetornarNulo() {
        // Evalúa el método comprarPowerUp()[cite: 9]
        PowerUp compra = gestorTienda.comprarPowerUp();
        // Este test fallará hasta que programes la lógica de retorno del PowerUp comprado
        assertNotNull(compra, "La compra debe generar un objeto PowerUp válido");
    }
}
