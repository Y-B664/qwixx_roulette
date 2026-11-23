package datos;

public class CasillaRuleta {
    private int valor;
    private Color color;
    public CasillaRuleta(int valor, Color color) {
        this.valor = valor;
        this.color = color;
    }
    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
}
