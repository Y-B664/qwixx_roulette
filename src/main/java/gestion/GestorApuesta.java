package gestion;

import datos.Apuesta;
import datos.Jugador;
import datos.TipoApuesta;

public class GestorApuesta {
    private Apuesta apuestaActual;

    public GestorApuesta() {
    }
    public boolean hacerApuesta(Apuesta apuesta) {
        if (validarApuesta(apuesta)) {
            this.apuestaActual = apuesta;
            return true;
        }
        return false;
    }
    public Apuesta solicitarApuestaInicial() {
        // Genera una apuesta base válida de 1 punto al color ROJO
        Apuesta inicial = new Apuesta(TipoApuesta.COLOR, 1, "ROJO");
        this.apuestaActual = inicial;
        return inicial;
    }
    public boolean validarApuesta(Apuesta apuesta) {
        if (apuesta == null || apuesta.getMonto() <= 0 || apuesta.getPrediccion() == null) {
            return false;
        }
        String pred = apuesta.getPrediccion().trim().toUpperCase();

        switch (apuesta.getTipo()) {
            case COLOR:
                return pred.equals("ROJO") || pred.equals("NEGRO") || pred.equals("VERDE");
            case PAROIMPAR:
                return pred.equals("PAR") || pred.equals("IMPAR");
            case NUMERO:
                try {
                    int num = Integer.parseInt(pred);
                    return num >= 0 && num <= 36;
                } catch (NumberFormatException e) {
                    return false;
                }
            default:
                return false;
        }
    }
    public int calcularPagoDinamico() {
        if (this.apuestaActual == null) {
            return 0;
        }
        switch (this.apuestaActual.getTipo()) {
            case NUMERO:
                return this.apuestaActual.getMonto() * 10;
            case COLOR:
            case PAROIMPAR:
                return this.apuestaActual.getMonto() * 2;
            default:
                return 0;
        }
    }
    public int actualizarSaldo(Jugador jugador) {
        if (jugador == null) {
            return 0;
        }
        return jugador.getSaldoPuntos();
    }
}
