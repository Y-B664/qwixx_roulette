package datos.bot;

import datos.CasillaRuleta;
import datos.Color;
import datos.Jugador;
import datos.PowerUp;
import datos.carton.Carton;
import datos.carton.CasillaCarton;
import datos.carton.FilaColor;
import gestion.GestorCarton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Bot extends Jugador {

    protected final Random random = new Random();

    public Bot(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
        super(nombreUsuario, mazoPowerUps, carton);
    }

    public boolean ejecutarTurno(CasillaRuleta resultadoRuleta, GestorCarton gestorCarton, boolean comodinCeroActivo) {
        if (gestorCarton == null || getCarton() == null || getCarton().getFilas() == null) {
            return false;
        }

        List<FilaColor> filasOrdenadas = obtenerOrdenFilas(getCarton().getFilas());
        boolean esCeroVerde = (resultadoRuleta != null && resultadoRuleta.getValor() == 0) || comodinCeroActivo;
        int valorObjetivo = (resultadoRuleta != null) ? resultadoRuleta.getValor() : 0;

        for (FilaColor fila : filasOrdenadas) {
            if (fila == null || fila.isEstaBloqueadaGlobal()
                    || !filaPermiteColor(fila, resultadoRuleta != null ? resultadoRuleta.getColor() : null)) {
                continue;
            }
            CasillaCarton[] casillas = fila.getCasillas();
            if (casillas == null) {
                continue;
            }

            List<CasillaCarton> candidatos = new ArrayList<>();
            for (int i = 0; i < casillas.length - 1; i++) {
                CasillaCarton casilla = casillas[i];
                if (casilla == null || casilla.isMarcada() || casilla.isBloqueada()) {
                    continue;
                }
                if (esCeroVerde || casilla.getValor() == valorObjetivo) {
                    candidatos.add(casilla);
                }
            }

            if (!candidatos.isEmpty()) {
                CasillaCarton elegida = elegirCasilla(candidatos, fila);
                elegida.setMarcada(true);
                gestorCarton.bloquearIzquierda(elegida.getValor(), fila);
                if (cumpleCondicionesCierre(fila)) {
                    gestorCarton.validarCierreCandado(fila);
                }
                return true;
            }
        }

        gestorCarton.marcarFallo(this);
        return false;
    }

    private boolean cumpleCondicionesCierre(FilaColor fila) {
        CasillaCarton[] casillas = fila.getCasillas();
        if (casillas == null || fila.getColor() == null) {
            return false;
        }

        int valorFinal;
        if (fila.getColor() == Color.ROJO || fila.getColor() == Color.AMARILLO) {
            valorFinal = 12;
        } else if (fila.getColor() == Color.VERDE || fila.getColor() == Color.AZUL) {
            valorFinal = 2;
        } else {
            return false;
        }

        int numerosMarcados = 0;
        boolean casillaFinalMarcada = false;
        for (int i = 0; i < casillas.length - 1; i++) {
            CasillaCarton casilla = casillas[i];
            if (casilla != null && casilla.isMarcada()) {
                numerosMarcados++;
                casillaFinalMarcada |= casilla.getValor() == valorFinal;
            }
        }
        return numerosMarcados >= 5 && casillaFinalMarcada;
    }

    protected List<FilaColor> obtenerOrdenFilas(List<FilaColor> filas) {
        return new ArrayList<>(filas);
    }

    protected CasillaCarton elegirCasilla(List<CasillaCarton> candidatos, FilaColor fila) {
        return candidatos.get(0);
    }

    protected int contarMarcadas(FilaColor fila) {
        if (fila == null || fila.getCasillas() == null) {
            return 0;
        }
        int total = 0;
        for (CasillaCarton c : fila.getCasillas()) {
            if (c != null && c.isMarcada()) {
                total++;
            }
        }
        return total;
    }

    protected boolean filaPermiteColor(FilaColor fila, Color colorRuleta) {
        if (fila == null || colorRuleta == null) {
            return true;
        }
        if (colorRuleta == Color.NEGRO) {
            return fila.getColor() == Color.AMARILLO || fila.getColor() == Color.VERDE;
        }
        if (colorRuleta == Color.ROJO) {
            return fila.getColor() == Color.ROJO || fila.getColor() == Color.AZUL;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected PowerUp tomarComodinSiDisponible() {
        Stack<Object> mazo = getMazoPowerUps();
        if (mazo != null && !mazo.isEmpty()) {
            Object top = mazo.pop();
            if (top instanceof PowerUp powerUp) {
                return powerUp;
            }
        }
        return null;
    }
}