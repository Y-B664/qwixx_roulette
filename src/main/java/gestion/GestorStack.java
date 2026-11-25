package gestion;

import datos.PowerUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GestorStack {

    public GestorStack() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void agregarPowerUp(Stack mazo, PowerUp powerUp) {
        if (mazo == null || powerUp == null) {
            return;
        }
        if (powerUp.getEstadoUso() == null || "DISPONIBLE".equalsIgnoreCase(powerUp.getEstadoUso())) {
            powerUp.setEstadoUso("ACTIVO");
        }
        mazo.push(powerUp);
    }

    @SuppressWarnings("rawtypes")
    public List<PowerUp> obtenerDisponibles(Stack mazo) {
        List<PowerUp> activos = new ArrayList<>();
        if (mazo == null) {
            return activos;
        }
        for (Object item : mazo) {
            if (item instanceof PowerUp p && !"USADO".equalsIgnoreCase(p.getEstadoUso())) {
                activos.add(p);
            }
        }
        return activos;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean usarPowerUp(Stack mazo, PowerUp seleccionado) {
        if (mazo == null || seleccionado == null) {
            return false;
        }
        boolean removido = mazo.remove(seleccionado);
        if (removido) {
            seleccionado.setEstadoUso("USADO");
            List<PowerUp> vigentes = reorganizar(new ArrayList<>(mazo));
            mazo.clear();
            mazo.addAll(vigentes);
        }
        return removido;
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