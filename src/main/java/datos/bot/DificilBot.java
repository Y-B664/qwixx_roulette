package datos.bot;

import datos.Apuesta;
import datos.PowerUp;
import datos.carton.Carton;

import java.util.Stack;

public class DificilBot extends Bot implements Comodinero,Apostador{
    public DificilBot(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
        super(nombreUsuario, mazoPowerUps, carton);
    }

    @Override
    public Apuesta decidirApuesta() {
        return null;
    }
    @Override
    public PowerUp decidirUsoPowerUps() {
        return null;
    }
}
