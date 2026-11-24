package datos;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class Apuesta {
    private TipoApuesta tipo;
    private int monto;
    private String prediccion;
    public Apuesta(TipoApuesta tipo, int monto, String prediccion) {
        this.tipo = tipo;
        this.monto = monto;
        this.prediccion = prediccion;
    }
}
