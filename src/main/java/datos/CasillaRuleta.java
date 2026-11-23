package datos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasillaRuleta {
    private int valor;
    private Color color;

    public CasillaRuleta(int valor, Color color) {
        this.valor = valor;
        this.color = color;
    }
}
