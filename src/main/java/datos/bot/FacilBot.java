package datos.bot;

import datos.carton.Carton;
import datos.carton.CasillaCarton;
import datos.carton.FilaColor;

import java.util.List;
import java.util.Stack;

public class FacilBot extends Bot {

    public FacilBot(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
        super(nombreUsuario, mazoPowerUps, carton);
    }

    @Override
    protected CasillaCarton elegirCasilla(List<CasillaCarton> candidatos, FilaColor fila) {
        // En dificultad fácil toma la primera casilla válida sin optimizar cierres
        return candidatos.get(0);
    }
}