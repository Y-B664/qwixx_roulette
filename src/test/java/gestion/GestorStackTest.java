package gestion;

import datos.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GestorStackTest {
    private GestorStack gestorStack;
    @BeforeEach
    public void setUp() {
        gestorStack = new GestorStack();
    }
    @Test
    public void reorganizarDebeProcesarLista() {
        List<PowerUp> listaOriginal = new ArrayList<>();
        listaOriginal.add(new PowerUp("Congelar", "ACTIVO", 10));

        List<PowerUp> listaReorganizada = gestorStack.reorganizar(listaOriginal);
        assertNotNull(listaReorganizada, "La lista reorganizada no debe ser nula");
        assertEquals(1, listaReorganizada.size(), "Debería mantener el mismo tamaño de la lista original de prueba");
    }
    @Test
    public void reorganizarListaVaciaDebeRetornarListaVacia() {
        List<PowerUp> listaVacia = new ArrayList<>();
        List<PowerUp> resultado = gestorStack.reorganizar(listaVacia); //[cite: 9]

        assertNotNull(resultado, "El método no debe devolver null al recibir una lista vacía");
        assertTrue(resultado.isEmpty(), "Reorganizar una lista vacía debe retornar una lista vacía");
    }

    @Test
    public void reorganizarListaConVariosElementos() {
        List<PowerUp> lista = new ArrayList<>();
        lista.add(new PowerUp("Doble Puntaje", "ACTIVO", 15));
        lista.add(new PowerUp("Anular Fallo", "USADO", 20));

        List<PowerUp> resultado = gestorStack.reorganizar(lista); //[cite: 9]

        assertEquals(2, resultado.size(), "El stack debe mantener sus elementos al ser reorganizado");
    }
}