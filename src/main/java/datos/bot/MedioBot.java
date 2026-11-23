package datos.bot;

import datos.Apuesta;
import datos.PowerUp;
import datos.carton.Carton;

import java.util.Stack;

public class MedioBot extends Bot implements Comodinero,Apostador{
    public MedioBot(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
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
