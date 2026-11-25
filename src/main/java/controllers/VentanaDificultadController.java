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
    private Dificultad dificultadSeleccionada;
    private boolean actualizandoSeleccion;

    private Image drakoPng;
    private Image azraelPng;
    private Image valtharionPng;

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
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

        seleccionarFacilButton.setOnAction(e -> seleccionarDificultad(Dificultad.FACIL));
        seleccionarMediaButton.setOnAction(e -> seleccionarDificultad(Dificultad.MEDIO));
        seleccionarDificilButton.setOnAction(e -> seleccionarDificultad(Dificultad.DIFICIL));
    }

    private void seleccionarDificultad(Dificultad nivel) {
        if (actualizandoSeleccion || nivel == dificultadSeleccionada) {
            return;
        }

        actualizandoSeleccion = true;
        try {
            dificultadSeleccionada = nivel;

            seleccionarFacilButton.setText(nivel == Dificultad.FACIL ? "Seleccionado ✓" : "Seleccionar");
            seleccionarMediaButton.setText(nivel == Dificultad.MEDIO ? "Seleccionado ✓" : "Seleccionar");
            seleccionarDificilButton.setText(nivel == Dificultad.DIFICIL ? "Seleccionado ✓" : "Seleccionar");

            setImagenSegura(imagenFacilView, drakoPng);
            setImagenSegura(imagenMediaView, azraelPng);
            setImagenSegura(imagenDificilView, valtharionPng);
        } finally {
            actualizandoSeleccion = false;
        }
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
            juegoController.configurarPartida(nombreJugador, dificultadSeleccionada);

            Stage stage = (Stage) jugarButton.getScene().getWindow();
            javafx.scene.Scene escenaDificultad = stage.getScene();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();
            if (!juegoController.iniciarPartida()) {
                stage.setScene(escenaDificultad);
                stage.centerOnScreen();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación",
                    "No se pudo cargar la pantalla de juego.");
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
