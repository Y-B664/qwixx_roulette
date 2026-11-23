package datos.carton;

import datos.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FilaColor {
    private Color color;
    private boolean estaBloqueadaGlobal;
    private boolean candadoTachado;
    private CasillaCarton[] casillas;

    public FilaColor(Color color, CasillaCarton[] casillas) {
        this.estaBloqueadaGlobal = false;
        this.color = color;
        this.candadoTachado = false;
        this.casillas = casillas;
    }
}
