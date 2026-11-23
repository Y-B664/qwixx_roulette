package datos;

public class Tienda {
    private Stack cartasDisponibles;
    private Stack catalogoGlobal;
    public Tienda() {
        this.cartasDisponibles = new Stack();
        this.catalogoGlobal = new Stack();
    }
    public Stack getCartasDisponibles() { return cartasDisponibles; }
    public void setCartasDisponibles(Stack cartasDisponibles) { this.cartasDisponibles = cartasDisponibles; }
    public Stack getCatalogoGlobal() { return catalogoGlobal; }
    public void setCatalogoGlobal(Stack catalogoGlobal) { this.catalogoGlobal = catalogoGlobal; }
}
