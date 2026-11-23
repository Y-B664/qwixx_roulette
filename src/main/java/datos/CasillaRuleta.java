package datos;

<<<<<<< HEAD
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasillaRuleta {
    private int valor;
    private Color color;

=======
public class CasillaRuleta {
    private int valor;
    private Color color;
>>>>>>> 5d2964a34f2f8a3c12c119953ba2293b3e6f6ff4
    public CasillaRuleta(int valor, Color color) {
        this.valor = valor;
        this.color = color;
    }
<<<<<<< HEAD
=======
    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
>>>>>>> 5d2964a34f2f8a3c12c119953ba2293b3e6f6ff4
}
