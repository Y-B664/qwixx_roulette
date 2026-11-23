package datos;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class Ruleta {
    private List<CasillaRuleta> casillas;
    private CasillaRuleta resultadoActual;
    public Ruleta() {
        this.casillas = new ArrayList<>();
    }
}
