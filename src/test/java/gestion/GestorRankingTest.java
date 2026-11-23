package gestion;

import datos.Registro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestorRankingTest {
    GestorRanking gestorRanking;
    String timestamp;
    @BeforeEach
    public void setUp(){

        gestorRanking = new GestorRanking();
        timestamp = String.valueOf(System.nanoTime());
    }

    @Test
    public void ordenarDescendenteOk(){
        List<Registro> registros = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Registro registro = new Registro("Rank "+ i,5*i);
            registros.add(registro);
            System.out.println(registro.getNombre());
        }
        assertNotNull(gestorRanking.ordenarDescendente(registros));
        assertEquals("Rank 9",registros.getFirst().getNombre());
    }


}
