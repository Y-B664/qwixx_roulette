package controllers;

import datos.Registro;
import gui.Launcher;
import impOperacionesBD.ImpOperacionesRegistro;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VentanaResultadosController {

    @FXML private AnchorPane resultadosRoot;
    @FXML private Button volverButton;
    @FXML private VBox filasRankingVBox;
    @FXML private AnchorPane segundoLugarPane;
    @FXML private AnchorPane primerLugarPane;
    @FXML private AnchorPane tercerLugarPane;
    @FXML private Label segundoNombreLabel;
    @FXML private Label segundoPuntosLabel;
    @FXML private Label primeroNombreLabel;
    @FXML private Label primeroPuntosLabel;
    @FXML private Label terceroNombreLabel;
    @FXML private Label terceroPuntosLabel;
    @FXML private ImageView medallaPlataView;
    @FXML private ImageView medallaOroView;
    @FXML private ImageView medallaBronceView;
    @FXML private Label miNombreLabel;
    @FXML private Label miPosicionLabel;
    @FXML private Label misPuntosLabel;

    private VentanaPrincipalController ventanaPrincipalController;
    private ImpOperacionesRegistro impOperacionesRegistro;
    private String nombreJugadorActual;
    private Integer puntosJugadorActual;

    @FXML
    public void initialize() {
        if (volverButton != null) {
            volverButton.setOnAction(e -> volverAlMenu());
        }
        cargarRanking();
    }

    public void configurarContexto(VentanaPrincipalController principalController,
                                   String nombreJugador,
                                   Integer puntosJugador) {
        this.ventanaPrincipalController = principalController;
        if (nombreJugador != null && !nombreJugador.isBlank()) {
            this.nombreJugadorActual = nombreJugador.trim();
        }
        this.puntosJugadorActual = puntosJugador;
        cargarRanking();
    }

    public void cargarRanking() {
        if (filasRankingVBox != null) {
            filasRankingVBox.getChildren().clear();
        }

        List<Registro> registros = null;
        try {
            if (impOperacionesRegistro == null) {
                impOperacionesRegistro = new ImpOperacionesRegistro();
            }
            @SuppressWarnings("unchecked")
            List<Registro> obtenidos = impOperacionesRegistro.obtenerTodos();
            if (obtenidos != null) {
                registros = new ArrayList<>(obtenidos);
                registros.sort(Comparator.comparingInt(Registro::getPuntuacion).reversed());
            }
        } catch (Exception ex) {
            System.out.println("Aviso al consultar ranking: " + ex.getMessage());
        }

        if (registros == null || registros.isEmpty()) {
            limpiarPodio();
            miPuntuacion(null);
            return;
        }

        organizarPodios(registros);
        mostrarPuestosRestantes(registros);
        miPuntuacion(registros);
    }

    private void limpiarPodio() {
        if (primeroNombreLabel != null) primeroNombreLabel.setText("-");
        if (primeroPuntosLabel != null) primeroPuntosLabel.setText("0 PTS");
        if (segundoNombreLabel != null) segundoNombreLabel.setText("-");
        if (segundoPuntosLabel != null) segundoPuntosLabel.setText("0 PTS");
        if (terceroNombreLabel != null) terceroNombreLabel.setText("-");
        if (terceroPuntosLabel != null) terceroPuntosLabel.setText("0 PTS");
    }

    public void organizarPodios(List<Registro> registros) {
        limpiarPodio();
        if (registros.size() >= 1) {
            Registro registro0 = registros.get(0);
            primeroNombreLabel.setText(registro0.getNombre());
            primeroPuntosLabel.setText(registro0.getPuntuacion() + " PTS");
        }
        if (registros.size() >= 2) {
            Registro registro1 = registros.get(1);
            segundoNombreLabel.setText(registro1.getNombre());
            segundoPuntosLabel.setText(registro1.getPuntuacion() + " PTS");
        }
        if (registros.size() >= 3) {
            Registro registro2 = registros.get(2);
            terceroNombreLabel.setText(registro2.getNombre());
            terceroPuntosLabel.setText(registro2.getPuntuacion() + " PTS");
        }
    }

    public void mostrarPuestosRestantes(List<Registro> registros) {
        if (filasRankingVBox == null) {
            return;
        }
        filasRankingVBox.getChildren().clear();
        for (int indice = 3; indice < registros.size(); indice++) {
            filasRankingVBox.getChildren().add(crearFilaRanking(indice + 1, registros.get(indice)));
        }
    }

    private AnchorPane crearFilaRanking(int posicion, Registro registro) {
        AnchorPane fila = new AnchorPane();
        fila.setMinHeight(56.0);
        fila.setPrefHeight(87.0);
        fila.setMaxHeight(87.0);
        fila.setPrefWidth(875.0);
        fila.getStyleClass().add("ranking-row");

        Label posicionLabel = crearEtiqueta(String.valueOf(posicion), Pos.CENTER);
        posicionLabel.setPrefHeight(37.0);
        posicionLabel.setPrefWidth(67.0);
        posicionLabel.getStyleClass().add("position-cell");
        AnchorPane.setLeftAnchor(posicionLabel, 51.0);
        AnchorPane.setTopAnchor(posicionLabel, 3.0);

        Label nombreLabel = crearEtiqueta(registro.getNombre(), Pos.CENTER);
        nombreLabel.setPrefHeight(50.0);
        nombreLabel.setPrefWidth(253.0);
        AnchorPane.setLeftAnchor(nombreLabel, 151.0);
        AnchorPane.setTopAnchor(nombreLabel, 5.0);

        Label puntosLabel = crearEtiqueta(registro.getPuntuacion() + " PTS", Pos.CENTER_RIGHT);
        puntosLabel.setPrefHeight(50.0);
        puntosLabel.setPrefWidth(451.0);
        AnchorPane.setRightAnchor(puntosLabel, 49.0);
        AnchorPane.setTopAnchor(puntosLabel, 6.0);

        fila.getChildren().addAll(posicionLabel, nombreLabel, puntosLabel);
        return fila;
    }

    private Label crearEtiqueta(String texto, Pos alineacion) {
        Label label = new Label(texto);
        label.setAlignment(alineacion);
        label.getStyleClass().add("ranking-text");
        return label;
    }

    private void miPuntuacion(List<Registro> registros) {
        String nombre = nombreJugadorActual;
        if ((nombre == null || nombre.isBlank()) && ventanaPrincipalController != null) {
            nombre = ventanaPrincipalController.getJugadorEnSesion();
        }

        if (nombre == null || nombre.isBlank()) {
            if (miNombreLabel != null) miNombreLabel.setText("N/D");
            if (miPosicionLabel != null) miPosicionLabel.setText("N/D");
            if (misPuntosLabel != null) misPuntosLabel.setText("N/D");
            return;
        }

        String posicionTexto = "-";
        Integer puntosEncontrados = puntosJugadorActual;

        if (registros != null) {
            for (int i = 0; i < registros.size(); i++) {
                Registro r = registros.get(i);
                if (r.getNombre() != null && r.getNombre().equalsIgnoreCase(nombre.trim())) {
                    posicionTexto = String.valueOf(i + 1);
                    if (puntosEncontrados == null) {
                        puntosEncontrados = r.getPuntuacion();
                    }
                    break;
                }
            }
        }

        if (miNombreLabel != null) miNombreLabel.setText("Tú - " + nombre.trim());
        if (miPosicionLabel != null) miPosicionLabel.setText(posicionTexto);
        if (misPuntosLabel != null) {
            misPuntosLabel.setText((puntosEncontrados != null ? puntosEncontrados : 0) + " PTS");
        }
    }

    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ventanaPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
