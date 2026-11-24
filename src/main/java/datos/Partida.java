package datos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Partida {
    private EstadoPartida estado;
    private int filasCompletadasTotales;
    private boolean apuestaInicialRealizada;
    private Jugador[] jugadores;
    private Jugador turnoActual;
    private Jugador ganador;
    private int tiempoPorTurnoSegundos;
    private boolean giroConfirmado;

    public Partida(int filasCompletadasTotales, boolean apuestaInicialRealizada, Jugador jugador,int tiempoPorTurnoSegundos,int numeroJugadores) {
        this.estado = EstadoPartida.EN_CURSO;
        this.filasCompletadasTotales = filasCompletadasTotales;
        this.apuestaInicialRealizada = apuestaInicialRealizada;
        this.jugadores = new Jugador[numeroJugadores];
        this.jugadores[0] = jugador;
        this.turnoActual = jugador;
        this.tiempoPorTurnoSegundos = tiempoPorTurnoSegundos;
        this.giroConfirmado= false;
    }
}
