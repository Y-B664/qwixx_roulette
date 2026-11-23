package datos.carton;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CasillaCarton {
    private int valor;
    private boolean marcada;
    private boolean bloqueada;

    public CasillaCarton(int valor) {
        this.valor = valor;
        this.marcada = false;
        this.bloqueada = false;
    }
}
