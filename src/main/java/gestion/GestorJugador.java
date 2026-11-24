package gestion;

import datos.Jugador;
import datos.Registro;
import datos.carton.Carton;
import lombok.AllArgsConstructor;
import operacionesBD.GenericDAO;


import java.util.Stack;

@AllArgsConstructor
public class GestorJugador {
    GenericDAO<Registro> impOperacionesRegistro;

    public Jugador crearJugador(String nombreUsuario, Stack<?> mazoPowerUps, Carton carton) {
        if( nombreUsuario ==null || nombreUsuario.isBlank() || mazoPowerUps == null || carton == null ){
            return null;
        }
        boolean existe = impOperacionesRegistro.existePorNombre(nombreUsuario);
        if(existe){
            return null;
        }
        Jugador jugador = new Jugador(nombreUsuario,mazoPowerUps,carton);
        return jugador;
    }
    //revisar logica de giro en gestor partida - verificar confirmacion
    public boolean confirmarGiro(Jugador jugador) {
        if(jugador == null){
            return false;
        }
        jugador.setGiroConfirmado(true);
        return true;
    }
}
