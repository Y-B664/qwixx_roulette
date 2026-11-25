package controllers;

import datos.Registro;
import gui.Launcher;
import impOperacionesBD.ImpOperacionesRegistro;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class VentanaPrincipalController {

    @FXML private AnchorPane principal;
    @FXML private TextField nombreUsuarioField;
    @FXML private Button jugarButton;
    @FXML private Button instruccionesButton;
    @FXML private Button rankingButton;
    private String jugadorEnSesion;
    ImpOperacionesRegistro impOperacionesRegistro = new ImpOperacionesRegistro();

    @FXML
    public void initialize() {
        jugarButton.setOnAction(e -> avanzarADificultad());
        instruccionesButton.setOnAction(e -> mostrarInstrucciones());
        rankingButton.setOnAction(e -> abrirRanking());
    }

    private void avanzarADificultad() {
        String nombre = nombreUsuarioField.getText() != null ? nombreUsuarioField.getText().trim() : "";

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nombre requerido",
                    "Por favor ingresa tu nombre de usuario antes de pulsar Jugar.");
            nombreUsuarioField.requestFocus();
            return;
        }

        if (existeUsuarioEnRanking(nombre)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nombre ya registrado",
                    "El nombre \"" + nombre + "\" ya existe en el ranking. Ingresa uno diferente para no sobrescribir puntuaciones.");
            nombreUsuarioField.requestFocus();
            return;
        }

        this.jugadorEnSesion = nombre;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ventanaDificultad.fxml"));
            Parent root = loader.load();

            VentanaDificultadController dificultadController = loader.getController();
            dificultadController.setNombreJugador(nombre);

            Stage stage = (Stage) jugarButton.getScene().getWindow();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();
        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación",
                    "No se pudo cargar la pantalla de selección de dificultad.");
        }
    }

    private boolean existeUsuarioEnRanking(String nombre) {
        try {
            ImpOperacionesRegistro dao = new ImpOperacionesRegistro();
            return dao.existePorNombre(nombre);
        } catch (Exception ex) {
            System.out.println("Aviso al validar usuario en BD: " + ex.getMessage());
        }
        return false;
    }

    private void mostrarInstrucciones() {
        String reglas = """
- Inicias con 5 puntos de casino y debes hacer una apuesta inicial obligatoria antes de girar la ruleta.
- Además recibes 1 comodín aleatorio gratis al comenzar.
- La ruleta (0-36) determina el color y el número a tachar de izquierda a derecha en tu cartón.
- Si sacas 0 Verde, puedes tachar cualquier casilla libre de cualquier fila.
- Si no puedes o no quieres tachar, sumas 1 fallo (-5 pts cada uno; al 4.º fallo termina la partida).
- También termina cuando se cierran 2 filas (requiere mínimo 5 cruces en la fila para cerrar su candado).
                """;
        mostrarAlerta(Alert.AlertType.INFORMATION, "Instrucciones - Qwixx Roulette", reglas);
    }

    private void abrirRanking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/VentanaResultados.fxml"));
            Parent root = loader.load();

            VentanaResultadosController resultadosController = loader.getController();
            if (resultadosController != null) {
                resultadosController.configurarContexto(this, getJugadorEnSesion(), null);
            }

            Stage stage = (Stage) rankingButton.getScene().getWindow();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();
        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación",
                    "No se pudo cargar la pantalla de Ranking.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public String getJugadorEnSesion() {
        if (nombreUsuarioField != null && nombreUsuarioField.getText() != null && !nombreUsuarioField.getText().isBlank()) {
            this.jugadorEnSesion = nombreUsuarioField.getText().trim();
        }
        return this.jugadorEnSesion;
    }
}
