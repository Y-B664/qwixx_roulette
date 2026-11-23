package datos;

<<<<<<< HEAD
import datos.carton.Carton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Stack;

@Getter
@Setter
@ToString
public class Jugador {
    private String nombreUsuario;
    private int saldoPuntos;
    private int enDeuda;
    private Stack mazoPowerUps;
    private Carton carton;
    private boolean giroConfirmado;

    public Jugador(String nombreUsuario, Stack mazoPowerUps, Carton carton) {
        this.nombreUsuario = nombreUsuario;
        this.saldoPuntos = 0;
        this.enDeuda = 0;
        this.mazoPowerUps = mazoPowerUps;
        this.carton = carton;
        this.giroConfirmado = false;
=======
public class Jugador {
    public Jugador() {
    }
    public int getSaldoPuntos() {
        return 0; // Valor por defecto
>>>>>>> 5d2964a34f2f8a3c12c119953ba2293b3e6f6ff4
    }
}
