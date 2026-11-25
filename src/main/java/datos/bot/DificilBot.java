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

public class DificilBot extends Bot implements Comodinero, Apostador {

    public DificilBot(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
        super(nombreUsuario, mazoPowerUps, carton);
    }

    @Override
    public Apuesta decidirApuesta() {
        int saldoDisponible = Math.max(1, getSaldoPuntos());
        int monto = Math.min(2, saldoDisponible);
        String prediccion = random.nextBoolean() ? "ROJO" : "NEGRO";
        return new Apuesta(TipoApuesta.COLOR, monto, prediccion);
    }

    @Override
    public PowerUp decidirUsoPowerUps() {
        return tomarComodinSiDisponible();
    }

    @Override
    protected List<FilaColor> obtenerOrdenFilas(List<FilaColor> filas) {
        List<FilaColor> copia = new ArrayList<>(filas);
        copia.sort((f1, f2) -> {
            int m1 = contarMarcadas(f1);
            int m2 = contarMarcadas(f2);
            boolean cercaCandado1 = m1 >= 4;
            boolean cercaCandado2 = m2 >= 4;
            if (cercaCandado1 != cercaCandado2) {
                return Boolean.compare(cercaCandado2, cercaCandado1);
            }
            return Integer.compare(m2, m1);
        });
        return copia;
    }

    @Override
    protected CasillaCarton elegirCasilla(List<CasillaCarton> candidatos, FilaColor fila) {
        return candidatos.get(candidatos.size() - 1);
    }
}
