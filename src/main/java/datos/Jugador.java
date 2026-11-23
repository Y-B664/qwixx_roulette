package datos;

import datos.carton.Carton;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Stack;

@Getter
@Setter
@ToString
@NoArgsConstructor 
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
    }
}
