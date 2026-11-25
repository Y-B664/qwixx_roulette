package controllers;

import datos.Dificultad;
import gui.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class VentanaDificultadController {

    @FXML private AnchorPane dificultadRoot;
    @FXML private AnchorPane cabeceraPane;
    @FXML private GridPane dificultadesGrid;
    @FXML private Button volverButton;

    @FXML private AnchorPane facilCard;
    @FXML private StackPane imagenFacilPane;
    @FXML private ImageView imagenFacilView;
    @FXML private Button seleccionarFacilButton;

    @FXML private AnchorPane mediaCard;
    @FXML private StackPane imagenMediaPane;
    @FXML private ImageView imagenMediaView;
    @FXML private Button seleccionarMediaButton;

    @FXML private AnchorPane dificilCard;
    @FXML private StackPane imagenDificilPane;
    @FXML private ImageView imagenDificilView;
    @FXML private Button seleccionarDificilButton;

    @FXML private Button jugarButton;

    private String nombreJugador = "Jugador";
    private String dificultadSeleccionada = null;
    private boolean actualizandoSeleccion = false;

    private Image drakoPng;
    private Image azraelPng;
    private Image valtharionPng;

    public void setNombreJugador(String nombreJugador) {
        if (nombreJugador != null && !nombreJugador.isBlank()) {
            this.nombreJugador = nombreJugador.trim();
        }
    }

    @FXML
    public void initialize() {
        cargarImagenesBots();
        configurarTamanoImagenes();
        configurarEventosTarjetas();
        configurarBotonVolver();
        jugarButton.setOnAction(e -> confirmarYPrepararPartida());
    }

    private void configurarBotonVolver() {
        String estiloNormal = "-fx-font-size: 24px; -fx-text-fill: #191919; -fx-background-color: white; " +
                "-fx-background-radius: 18; -fx-border-color: #D6D9DB; -fx-border-width: 2; " +
                "-fx-border-radius: 18; -fx-cursor: hand;";
        String estiloHover = "-fx-font-size: 24px; -fx-text-fill: white; -fx-background-color: #191919; " +
                "-fx-background-radius: 18; -fx-border-color: #191919; -fx-border-width: 2; " +
                "-fx-border-radius: 18; -fx-cursor: hand;";

        volverButton.setOnMouseEntered(e -> volverButton.setStyle(estiloHover));
        volverButton.setOnMouseExited(e -> volverButton.setStyle(estiloNormal));
        volverButton.setOnAction(e -> volverAPrincipal());
    }

    private void volverAPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ventanaPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) volverButton.getScene().getWindow();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();
        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo regresar a la pantalla principal.");
        }
    }

    private void cargarImagenesBots() {
        drakoPng = cargarImagen("/gui/img/bots/Drako.png");
        azraelPng = cargarImagen("/gui/img/bots/Azrael.png");
        valtharionPng = cargarImagen("/gui/img/bots/Valtharion.png");

        setImagenSegura(imagenFacilView, drakoPng);
        setImagenSegura(imagenMediaView, azraelPng);
        setImagenSegura(imagenDificilView, valtharionPng);
    }

    private void configurarTamanoImagenes() {
        configurarView(imagenFacilView);
        configurarView(imagenMediaView);
        configurarView(imagenDificilView);
    }

    private void configurarView(ImageView view) {
        if (view != null) {
            view.setFitWidth(140);
            view.setFitHeight(140);
            view.setPreserveRatio(true);
        }
    }

    private Image cargarImagen(String ruta) {
        URL url = getClass().getResource(ruta);
        return (url != null) ? new Image(url.toExternalForm(), true) : null;
    }

    private void setImagenSegura(ImageView view, Image nuevaImagen) {
        if (view != null && nuevaImagen != null && view.getImage() != nuevaImagen) {
            view.setImage(nuevaImagen);
        }
    }

    private void configurarEventosTarjetas() {
        facilCard.setOnMouseEntered(e -> setImagenSegura(imagenFacilView, drakoPng));
        facilCard.setOnMouseExited(e -> setImagenSegura(imagenFacilView, drakoPng));

        mediaCard.setOnMouseEntered(e -> setImagenSegura(imagenMediaView, azraelPng));
        mediaCard.setOnMouseExited(e -> setImagenSegura(imagenMediaView, azraelPng));

        dificilCard.setOnMouseEntered(e -> setImagenSegura(imagenDificilView, valtharionPng));
        dificilCard.setOnMouseExited(e -> setImagenSegura(imagenDificilView, valtharionPng));

        seleccionarFacilButton.setOnAction(e -> seleccionarDificultad("Fácil"));
        seleccionarMediaButton.setOnAction(e -> seleccionarDificultad("Medio"));
        seleccionarDificilButton.setOnAction(e -> seleccionarDificultad("Difícil"));
    }

    private void seleccionarDificultad(String nivel) {
        if (actualizandoSeleccion || nivel.equals(this.dificultadSeleccionada)) {
            return;
        }
        actualizandoSeleccion = true;
        try {
            this.dificultadSeleccionada = nivel;

            String textoFacil = "Fácil".equals(nivel) ? "Seleccionado ✓" : "Seleccionar";
            String textoMedia = "Medio".equals(nivel) ? "Seleccionado ✓" : "Seleccionar";
            String textoDificil = "Difícil".equals(nivel) ? "Seleccionado ✓" : "Seleccionar";

            if (!textoFacil.equals(seleccionarFacilButton.getText())) {
                seleccionarFacilButton.setText(textoFacil);
            }
            if (!textoMedia.equals(seleccionarMediaButton.getText())) {
                seleccionarMediaButton.setText(textoMedia);
            }
            if (!textoDificil.equals(seleccionarDificilButton.getText())) {
                seleccionarDificilButton.setText(textoDificil);
            }

            setImagenSegura(imagenFacilView, drakoPng);
            setImagenSegura(imagenMediaView, azraelPng);
            setImagenSegura(imagenDificilView, valtharionPng);
        } finally {
            actualizandoSeleccion = false;
        }
    }

    private Dificultad convertirDificultad(String nivel) {
        return switch (nivel) {
            case "Medio" -> Dificultad.MEDIO;
            case "Difícil" -> Dificultad.DIFICIL;
            default -> Dificultad.FACIL;
        };
    }

    private void confirmarYPrepararPartida() {
        if (dificultadSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un Bot",
                    "Debes seleccionar una dificultad (Fácil, Medio o Difícil) antes de jugar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ventanaJuego.fxml"));
            Parent root = loader.load();

            VentanaJuegoController juegoController = loader.getController();
            Stage stage = (Stage) jugarButton.getScene().getWindow();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();

            Dificultad dificultadEnum = convertirDificultad(dificultadSeleccionada);
            juegoController.configurarPartida(nombreJugador, dificultadEnum);
            boolean iniciada = juegoController.iniciarPartida();

            if (!iniciada) {
                FXMLLoader volverLoader = new FXMLLoader(getClass().getResource("/gui/ventanaDificultad.fxml"));
                Parent dificultadRootPrevio = volverLoader.load();
                VentanaDificultadController prevController = volverLoader.getController();
                prevController.setNombreJugador(nombreJugador);
                stage.setScene(Launcher.crearEscenaAjustada(dificultadRootPrevio));
                stage.centerOnScreen();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al iniciar partida",
                    "No se pudo abrir la ventana de juego: " + ex.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}