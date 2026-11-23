package gestion;

import datos.PowerUp;
import datos.Tienda;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorTienda {
    private Tienda tienda;

    public GestorTienda() {
        this.tienda = new Tienda();
        inicializarCatalogo();
    }
    private void inicializarCatalogo() {
        List<PowerUp> catalogo = new ArrayList<>();
        catalogo.add(new PowerUp("Doble Puntaje", "DISPONIBLE", 4));
        catalogo.add(new PowerUp("Anular Fallo", "DISPONIBLE", 3));
        catalogo.add(new PowerUp("Comodín Color", "DISPONIBLE", 5));
        catalogo.add(new PowerUp("Salto Izquierda", "DISPONIBLE", 4));
        catalogo.add(new PowerUp("Seguro de Apuesta", "DISPONIBLE", 2));
        this.tienda.getCatalogoGlobal().setPowerUps(catalogo);
    }
    public List<PowerUp> generarOfertaAleatoria() {
        List<PowerUp> copiaCatalogo = new ArrayList<>(this.tienda.getCatalogoGlobal().getPowerUps());
        Collections.shuffle(copiaCatalogo);
        int cantidad = Math.min(3, copiaCatalogo.size());
        List<PowerUp> oferta = new ArrayList<>(copiaCatalogo.subList(0, cantidad));
        this.tienda.getCartasDisponibles().setPowerUps(oferta);
        return oferta;
    }
    public PowerUp comprarPowerUp() {
        List<PowerUp> disponibles = this.tienda.getCartasDisponibles().getPowerUps();
        if (disponibles == null || disponibles.isEmpty()) {
            disponibles = generarOfertaAleatoria();
        }
        PowerUp comprado = disponibles.remove(0);
        comprado.setEstadoUso("ACTIVO");
        return comprado;
    }
    public Tienda abrirTienda() {
        if (this.tienda.getCartasDisponibles().getPowerUps().isEmpty()) {
            generarOfertaAleatoria();
        }
        return this.tienda;
    }
}
