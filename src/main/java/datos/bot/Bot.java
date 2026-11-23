package datos.bot;

import datos.Jugador;
import datos.carton.Carton;

import java.util.Stack;

public class Bot extends Jugador {
    public Bot(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
        super(nombreUsuario, mazoPowerUps, carton);
    }
}
