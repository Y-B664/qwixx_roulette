package gestion;

import datos.Jugador;
import datos.PowerUp;
import datos.Tienda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorTienda {
    private final Tienda tienda;

    public GestorTienda() {
        this.tienda = new Tienda();
        inicializarCatalogo();
    }

    private void inicializarCatalogo() {
        List<PowerUp> catalogo = new ArrayList<>();
        catalogo.add(new PowerUp("Doble Puntaje",
                "Otorga +6 puntos de bonificación inmediatos y duplica tu próxima ganancia.",
                "DISPONIBLE", 4));
        catalogo.add(new PowerUp("Anular Fallo",
                "Elimina el último fallo acumulado en tu tablero.",
                "DISPONIBLE", 3));
        catalogo.add(new PowerUp("Comodín Color",
                "Marca cualquier casilla disponible sin importar el color de la ruleta.",
                "DISPONIBLE", 5));
        catalogo.add(new PowerUp("Salto Izquierda",
                "Permite marcar un número situado a la izquierda de una casilla ya tachada.",
                "DISPONIBLE", 4));
        catalogo.add(new PowerUp("Seguro de Apuesta",
                "Recupera +3 puntos de casino y blinda tu siguiente apuesta.",
                "DISPONIBLE", 2));
        this.tienda.getCatalogoGlobal().setPowerUps(catalogo);
    }

    public List<PowerUp> generarOfertaAleatoria() {
        List<PowerUp> copiaCatalogo = new ArrayList<>(this.tienda.getCatalogoGlobal().getPowerUps());
        Collections.shuffle(copiaCatalogo);
        int cantidad = Math.min(3, copiaCatalogo.size());
        List<PowerUp> oferta = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            oferta.add(copiaCatalogo.get(i).clonarDisponible());
        }
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

    public boolean comprarPowerUp(PowerUp seleccionado, Jugador comprador) {
        if (seleccionado == null || comprador == null) {
            return false;
        }
        List<PowerUp> disponibles = this.tienda.getCartasDisponibles().getPowerUps();
        if (disponibles == null || !disponibles.contains(seleccionado)) {
            return false;
        }
        if (comprador.getSaldoPuntos() < seleccionado.getCosto()) {
            return false;
        }

        comprador.setSaldoPuntos(comprador.getSaldoPuntos() - seleccionado.getCosto());
        disponibles.remove(seleccionado);
        seleccionado.setEstadoUso("ACTIVO");
        return true;
    }

    public Tienda abrirTienda() {
        if (this.tienda.getCartasDisponibles().getPowerUps() == null
                || this.tienda.getCartasDisponibles().getPowerUps().isEmpty()) {
            generarOfertaAleatoria();
        }
        return this.tienda;
    }
}
