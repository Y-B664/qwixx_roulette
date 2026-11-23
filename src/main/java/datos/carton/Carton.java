package datos.carton;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Carton {
    private List<FilaColor> filas;
    private int casillasFallos;
    private int fallos;

    public Carton(List<FilaColor> filas, int casillasFallos) {
        this.filas = filas;
        this.casillasFallos = casillasFallos;
        this.fallos = 0;
    }
}
