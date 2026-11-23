package gestion;

import datos.PowerUp;
import java.util.ArrayList;
import java.util.List;

public class GestorStack {

    public GestorStack() {
    }

    public List<PowerUp> reorganizar(List<PowerUp> stack) {
        if (stack == null) {
            return new ArrayList<>();
        }
        List<PowerUp> activos = new ArrayList<>();
        for (PowerUp p : stack) {
            if (p != null && !"USADO".equalsIgnoreCase(p.getEstadoUso())) {
                activos.add(p);
            }
        }
        return activos;
    }
}