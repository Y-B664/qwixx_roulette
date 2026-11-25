package datos.bot;

import datos.Apuesta;
import datos.PowerUp;
import datos.TipoApuesta;
import datos.carton.Carton;
import datos.carton.CasillaCarton;
import datos.carton.FilaColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class MedioBot extends Bot implements Comodinero, Apostador {

    public MedioBot(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
        super(nombreUsuario, mazoPowerUps, carton);
    }

    @Override
    public Apuesta decidirApuesta() {
        int saldoDisponible = Math.max(1, getSaldoPuntos());
        int monto = Math.min(2, saldoDisponible);
        String prediccion = random.nextBoolean() ? "PAR" : "IMPAR";
        return new Apuesta(TipoApuesta.PAROIMPAR, monto, prediccion);
    }

    @Override
    public PowerUp decidirUsoPowerUps() {
        if (getCarton() == null || getCarton().getFilas() == null) {
            return null;
        }
        boolean tieneFilaAvanzada = getCarton().getFilas().stream()
                .anyMatch(f -> f != null && !f.isEstaBloqueadaGlobal() && contarMarcadas(f) >= 3);
        return tieneFilaAvanzada ? tomarComodinSiDisponible() : null;
    }

    @Override
    protected List<FilaColor> obtenerOrdenFilas(List<FilaColor> filas) {
        List<FilaColor> copia = new ArrayList<>(filas);
        copia.sort(Comparator.comparingInt(this::contarMarcadas).reversed());
        return copia;
    }

    @Override
    protected CasillaCarton elegirCasilla(List<CasillaCarton> candidatos, FilaColor fila) {
        return candidatos.get(candidatos.size() - 1);
    }
}
