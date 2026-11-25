package datos;

import datos.carton.Carton;

public class PowerUp {
    private String nombre;
    private String descripcion;
    private String estadoUso;
    private int costo;

    public PowerUp(String nombre, String estadoUso, int costo) {
        this(nombre, obtenerDescripcionPorDefecto(nombre), estadoUso, costo);
    }

    public PowerUp(String nombre, String descripcion, String estadoUso, int costo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estadoUso = estadoUso;
        this.costo = costo;
    }

    public static String obtenerDescripcionPorDefecto(String nombre) {
        if (nombre == null) {
            return "Comodín especial de partida.";
        }
        return switch (nombre) {
            case "Doble Puntaje" -> "Otorga +6 puntos de bonificación inmediatos y duplica tu próxima ganancia.";
            case "Anular Fallo" -> "Elimina el último fallo marcado en tu cartón.";
            case "Comodín Color" -> "Permite marcar cualquier casilla libre de cualquier color en este turno.";
            case "Salto Izquierda" -> "Permite marcar una casilla a la izquierda de tu última marca en una fila.";
            case "Seguro de Apuesta" -> "Devuelve +3 puntos de respaldo y protege tu próxima apuesta.";
            default -> "Comodín especial aplicable durante tu turno.";
        };
    }

    public void aplicarEfecto(Object objetivo) {
        this.estadoUso = "USADO";
        if (objetivo instanceof Jugador jugador) {
            if ("Doble Puntaje".equalsIgnoreCase(nombre)) {
                jugador.setSaldoPuntos(jugador.getSaldoPuntos() + 6);
            } else if ("Seguro de Apuesta".equalsIgnoreCase(nombre)) {
                jugador.setSaldoPuntos(jugador.getSaldoPuntos() + 3);
            }
        } else if (objetivo instanceof Carton) {
            // El efecto visual y de validación sobre el cartón se aplica en el turno activo
        }
    }

    public PowerUp clonarDisponible() {
        return new PowerUp(this.nombre, this.descripcion, "DISPONIBLE", this.costo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstadoUso() {
        return estadoUso;
    }

    public void setEstadoUso(String estadoUso) {
        this.estadoUso = estadoUso;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }
}
