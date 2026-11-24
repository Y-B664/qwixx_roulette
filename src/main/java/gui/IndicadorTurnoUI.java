package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class IndicadorTurnoUI {

    private final HBox contenedor;
    private final Circle indicadorEstado;
    private final Label lblQuienJuega;
    private final Label lblParticipantes;

    public IndicadorTurnoUI(String nombreJugador, String nombreBot, String dificultadBot) {
        this.contenedor = new HBox(10);
        this.contenedor.setAlignment(Pos.CENTER_LEFT);
        this.contenedor.setPadding(new Insets(8, 14, 8, 14));

        this.indicadorEstado = new Circle(6);

        this.lblQuienJuega = new Label();
        this.lblQuienJuega.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        this.lblParticipantes = new Label(
                "Jugador: " + nombreJugador + "  vs  Bot: " + nombreBot + " (" + dificultadBot + ")"
        );
        this.lblParticipantes.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        this.contenedor.getChildren().addAll(indicadorEstado, lblQuienJuega, lblParticipantes);
        setTurno(true, nombreJugador, nombreBot);
    }

    public HBox getContenedor() {
        return contenedor;
    }

    public void setTurno(boolean esTurnoJugador, String nombreJugador, String nombreBot) {
        if (esTurnoJugador) {
            indicadorEstado.setFill(Color.web("#2E7D32"));
            lblQuienJuega.setText("JUGANDO AHORA: " + nombreJugador + " (Jugador)");
            lblQuienJuega.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
            contenedor.setStyle(
                    "-fx-background-color: #E8F5E9; -fx-background-radius: 14; " +
                            "-fx-border-color: #2E7D32; -fx-border-width: 1.5; -fx-border-radius: 14;"
            );
        } else {
            indicadorEstado.setFill(Color.web("#E53935"));
            lblQuienJuega.setText("JUGANDO AHORA: " + nombreBot + " (Bot)");
            lblQuienJuega.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #C62828;");
            contenedor.setStyle(
                    "-fx-background-color: #FFEBEE; -fx-background-radius: 14; " +
                            "-fx-border-color: #E53935; -fx-border-width: 1.5; -fx-border-radius: 14;"
            );
        }
    }
}
