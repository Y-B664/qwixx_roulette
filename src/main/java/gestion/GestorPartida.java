package gestion;

import datos.Dificultad;
import datos.EstadoPartida;
import datos.Jugador;
import datos.Partida;
import datos.bot.Bot;
import datos.bot.DificilBot;
import datos.bot.FacilBot;
import datos.bot.MedioBot;
import datos.carton.FilaColor;
import lombok.Getter;
import lombok.Setter;

import java.util.Stack;
@Getter
@Setter
public class GestorPartida {
    GestorJugador gestorJugador;
    GestorCarton gestorCarton;
    GestorTemporizador gestorTemporizador;
    Partida partida;

    public GestorPartida(GestorJugador gestorJugador, GestorCarton gestorCarton,GestorTemporizador gestorTemporizador) {
        this.gestorJugador = gestorJugador;
        this.gestorCarton = gestorCarton;
        this.gestorTemporizador = gestorTemporizador;
    }

    public Partida iniciarPartida(int numeroJugadores,Jugador jugador) {
        jugador.setSaldoPuntos(5);
        this.partida = new Partida(0, false, jugador, 60,numeroJugadores);
        return this.partida;
    }
    public void otorgarComodinInicial(Jugador jugador) {
    }
    //verificar logica temporizador, considerar otra clase gestora para ello
    public void controlarTemporizador(int segundos,boolean iniciar, Runnable accionAlFInalizar) {
        if(iniciar) {
            gestorTemporizador.iniciar(segundos, accionAlFInalizar);
        }else{
            gestorTemporizador.detener();
        }
    }
    //verificar logica de finalizacion de partida
    public void finalizarPartida() {
        if(evaluarCondicionFin()) {
            if (this.partida == null || this.partida.getEstado().equals(EstadoPartida.CANCELADA)) {
                return;
            }
            this.partida.setEstado(EstadoPartida.FINALIZADA);
        }
    }
    public void cancelarPartida() {
        if(this.partida==null || this.partida.getEstado().equals(EstadoPartida.FINALIZADA)){
            return;
        }
        this.partida.setEstado(EstadoPartida.CANCELADA);
    }
    public boolean evaluarCondicionFin() {
        Jugador[] jugadores = partida.getJugadores();
        int numeroFilasCompletadas=0;
        for (Jugador j: jugadores){
            for (FilaColor f: j.getCarton().getFilas()){
                if(f.isEstaBloqueadaGlobal() && f.isCandadoTachado()){
                    numeroFilasCompletadas ++;
                }
            }
        }
        partida.setFilasCompletadasTotales(numeroFilasCompletadas);
        if(partida.getFilasCompletadasTotales()>=4){
            return true;
        }else {
            return false;
        }
    }
    public Bot configurarBot(Dificultad nivelDificultad) {
        if(nivelDificultad == null){
            return null;
        };
        Bot bot;
        switch (nivelDificultad) {
            case FACIL:
                bot = new FacilBot("Drako",new Stack<>(),gestorCarton.crearCarton(4));
                break;
            case MEDIO:
                bot = new MedioBot("Azrael",new Stack<>(),gestorCarton.crearCarton(3));
                break;
            case DIFICIL:
                bot = new DificilBot("Valtharion",new Stack<>(),gestorCarton.crearCarton(2));
                break;
            default:
                bot = new FacilBot("Drako",new Stack<>(),gestorCarton.crearCarton(4));
                break;
        }
        return bot;
    }

    public boolean agregarBot(Bot bot){
        Jugador[] jugadores = this.partida.getJugadores();
        jugadores[jugadores.length-1] = bot;
        this.partida.setJugadores(jugadores);
        return true;
    }
    public void calcularPuntajeFinal() {
        for (Jugador j : partida.getJugadores()){
            gestorCarton.sumarPuntosTotales(j);
        }
    }
    // verificar logica
    public Jugador mostrarGanador() {
        Jugador[] jugadores = partida.getJugadores();
        if (jugadores == null || jugadores.length == 0) {
            return null;
        }
        Jugador ganador = jugadores[0];
        for (int i = 1; i < jugadores.length; i++) {
            if (jugadores[i] != null) {
                if (ganador == null || jugadores[i].getSaldoPuntos() > ganador.getSaldoPuntos()) {
                    ganador = jugadores[i];
                }
            }else{
                return null;
            }
        }
        return ganador;
    }
    public boolean verificarConfirmacionGiro(){
        boolean giroConfirmado = true;
        for(Jugador j : partida.getJugadores()){
            if(!j.isGiroConfirmado()){
                giroConfirmado = false;
            }
        }
        partida.setGiroConfirmado(giroConfirmado);
        return giroConfirmado;
    }
    public boolean resetearConfirmacionGiro(){
        boolean giroConfirmado = true;
        for(Jugador j : partida.getJugadores()){
            if(!j.isGiroConfirmado()){
                giroConfirmado = false;
            }
        }
        partida.setGiroConfirmado(giroConfirmado);
        return giroConfirmado;
    }
}
