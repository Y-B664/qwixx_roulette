package gestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GestorTemporizadorTest {
    GestorTemporizador gestorTemporizador;
    @BeforeEach
    public void setUp(){
        gestorTemporizador = new GestorTemporizador();
    }
    @Test
    public void iniciarOK() throws InterruptedException {
        AtomicBoolean finalizo= new AtomicBoolean(false);
        gestorTemporizador.iniciar(5, () ->{
            finalizo.set(true);
        });
        Thread.sleep(5500);
        assertTrue(finalizo.get());
    }

    @Test
    public void testDetener() throws InterruptedException {
        AtomicBoolean finalizo= new AtomicBoolean(false);
        gestorTemporizador.iniciar(3, () -> {finalizo.set(true);});
        gestorTemporizador.detener();
        Thread.sleep(3200);
        assertFalse(finalizo.get());
    }
}
