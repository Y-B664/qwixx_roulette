package datos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
public class CasillaRuleta {
    private int valor;
    private Color color;

    public CasillaRuleta(int valor, Color color) {
        this.valor = valor;
        this.color = color;
    }
}
