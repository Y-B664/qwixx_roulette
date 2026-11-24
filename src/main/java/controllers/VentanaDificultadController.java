package controllers;

import datos.Apuesta;
import datos.PowerUp;
import datos.TipoApuesta;
import gestion.GestorApuesta;
import gestion.GestorRuleta;
import gestion.GestorStack;
import gestion.GestorTienda;
import gui.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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

    private final GestorRuleta gestorRuleta = new GestorRuleta();
    private final GestorApuesta gestorApuesta = new GestorApuesta();
    private final GestorTienda gestorTienda = new GestorTienda();
    private final GestorStack gestorStack = new GestorStack();

    private Image drakoPng, drakoGif;
    private Image azraelPng, azraelGif;
    private Image valtharionPng, valtharionGif;

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    @FXML
    public void initialize() {
        cargarImagenesBots();
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
        drakoGif = cargarImagen("/gui/img/bots/Drako.gif");

        azraelPng = cargarImagen("/gui/img/bots/Azrael.png");
        azraelGif = cargarImagen("/gui/img/bots/Azrael.gif");

        valtharionPng = cargarImagen("/gui/img/bots/Valtharion.png");
        valtharionGif = cargarImagen("/gui/img/bots/Valtharion.gif");
    }

    private Image cargarImagen(String ruta) {
        URL url = getClass().getResource(ruta);
        return (url != null) ? new Image(url.toExternalForm()) : null;
    }

    private void configurarEventosTarjetas() {
        facilCard.setOnMouseEntered(e -> { if (drakoGif != null) imagenFacilView.setImage(drakoGif); });
        facilCard.setOnMouseExited(e -> {
            if (!"Fácil".equals(dificultadSeleccionada) && drakoPng != null) imagenFacilView.setImage(drakoPng);
        });

        mediaCard.setOnMouseEntered(e -> { if (azraelGif != null) imagenMediaView.setImage(azraelGif); });
        mediaCard.setOnMouseExited(e -> {
            if (!"Medio".equals(dificultadSeleccionada) && azraelPng != null) imagenMediaView.setImage(azraelPng);
        });

        dificilCard.setOnMouseEntered(e -> { if (valtharionGif != null) imagenDificilView.setImage(valtharionGif); });
        dificilCard.setOnMouseExited(e -> {
            if (!"Difícil".equals(dificultadSeleccionada) && valtharionPng != null) imagenDificilView.setImage(valtharionPng);
        });

        seleccionarFacilButton.setOnAction(e -> seleccionarDificultad("Fácil"));
        seleccionarMediaButton.setOnAction(e -> seleccionarDificultad("Medio"));
        seleccionarDificilButton.setOnAction(e -> seleccionarDificultad("Difícil"));
    }

    private void seleccionarDificultad(String nivel) {
        this.dificultadSeleccionada = nivel;

        seleccionarFacilButton.setText(nivel.equals("Fácil") ? "Seleccionado ✓" : "Seleccionar");
        seleccionarMediaButton.setText(nivel.equals("Medio") ? "Seleccionado ✓" : "Seleccionar");
        seleccionarDificilButton.setText(nivel.equals("Difícil") ? "Seleccionado ✓" : "Seleccionar");

        if (drakoGif != null && drakoPng != null) {
            imagenFacilView.setImage(nivel.equals("Fácil") ? drakoGif : drakoPng);
        }
        if (azraelGif != null && azraelPng != null) {
            imagenMediaView.setImage(nivel.equals("Medio") ? azraelGif : azraelPng);
        }
        if (valtharionGif != null && valtharionPng != null) {
            imagenDificilView.setImage(nivel.equals("Difícil") ? valtharionGif : valtharionPng);
        }
    }

    private void confirmarYPrepararPartida() {
        if (dificultadSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un Bot",
                    "Debes seleccionar una dificultad (Fácil, Medio o Difícil) antes de jugar.");
            return;
        }

        gestorRuleta.construirRuleta();
        List<PowerUp> ofertaTienda = gestorTienda.generarOfertaAleatoria();
        PowerUp comodinInicial = ofertaTienda.isEmpty() ? null : ofertaTienda.get(0);

        Apuesta apuestaInicial = solicitarApuestaInicialJavaFX();
        if (apuestaInicial == null) {
            return;
        }

        gestorApuesta.hacerApuesta(apuestaInicial);

        String nombreComodin = (comodinInicial != null) ? comodinInicial.getNombre() : "Comodín aleatorio listo";
        mostrarAlerta(Alert.AlertType.INFORMATION, "¡Partida Lista!",
                "Jugador: " + nombreJugador +
                        "\nBot rival: " + dificultadSeleccionada +
                        "\nApuesta inicial: " + apuestaInicial.getMonto() + " pts a " + apuestaInicial.getPrediccion() +
                        "\nComodín inicial otorgado: " + nombreComodin);
    }

    private Apuesta solicitarApuestaInicialJavaFX() {
        final Apuesta[] resultado = {null};

        Stage modal = new Stage();
        modal.initOwner(jugarButton.getScene().getWindow());
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox tarjeta = new VBox(16);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(28, 34, 28, 34));
        tarjeta.setStyle("""
                -fx-background-color: #FFFFFF;
                -fx-background-radius: 26;
                -fx-border-color: #191919;
                -fx-border-width: 3;
                -fx-border-radius: 26;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 24, 0, 0, 8);
                """);

        Label lblTitulo = new Label("APUESTA INICIAL OBLIGATORIA");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #191919;");

        Label lblSub = new Label("Saldo inicial: 5 puntos de casino • Rival: " + dificultadSeleccionada);
        lblSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        // 1. Tipo de Apuesta
        ToggleGroup grupoTipo = new ToggleGroup();
        ToggleButton btnTipoColor = new ToggleButton("COLOR (x2)");
        ToggleButton btnTipoParidad = new ToggleButton("PAR / IMPAR (x2)");
        ToggleButton btnTipoNumero = new ToggleButton("NÚMERO 0-36 (x10)");
        btnTipoColor.setToggleGroup(grupoTipo);
        btnTipoParidad.setToggleGroup(grupoTipo);
        btnTipoNumero.setToggleGroup(grupoTipo);
        btnTipoColor.setSelected(true);
        btnTipoColor.setPrefHeight(40);
        btnTipoParidad.setPrefHeight(40);
        btnTipoNumero.setPrefHeight(40);

        HBox boxTipos = new HBox(10, btnTipoColor, btnTipoParidad, btnTipoNumero);
        boxTipos.setAlignment(Pos.CENTER);

        // 2. Predicción dinámica
        StackPane panelPrediccion = new StackPane();
        panelPrediccion.setPrefHeight(56);

        ToggleGroup grupoColor = new ToggleGroup();
        ToggleButton btnRojo = new ToggleButton("ROJO");
        btnRojo.setToggleGroup(grupoColor);
        btnRojo.setSelected(true);
        btnRojo.setPrefSize(150, 44);

        ToggleButton btnNegro = new ToggleButton("NEGRO");
        btnNegro.setToggleGroup(grupoColor);
        btnNegro.setPrefSize(150, 44);

        HBox boxColores = new HBox(14, btnRojo, btnNegro);
        boxColores.setAlignment(Pos.CENTER);

        ToggleGroup grupoParidad = new ToggleGroup();
        ToggleButton btnPar = new ToggleButton("PAR");
        btnPar.setToggleGroup(grupoParidad);
        btnPar.setSelected(true);
        btnPar.setPrefSize(150, 44);

        ToggleButton btnImpar = new ToggleButton("IMPAR");
        btnImpar.setToggleGroup(grupoParidad);
        btnImpar.setPrefSize(150, 44);

        HBox boxParidad = new HBox(14, btnPar, btnImpar);
        boxParidad.setAlignment(Pos.CENTER);
        boxParidad.setVisible(false);

        TextField txtNumero = new TextField();
        txtNumero.setPromptText("Número del 0 al 36");
        txtNumero.setMaxWidth(220);
        txtNumero.setAlignment(Pos.CENTER);
        txtNumero.setStyle("-fx-font-size: 16px; -fx-background-radius: 14; -fx-border-color: #191919; -fx-border-width: 2; -fx-border-radius: 14; -fx-padding: 8;");
        txtNumero.setVisible(false);

        panelPrediccion.getChildren().addAll(boxColores, boxParidad, txtNumero);

        // 3. Fichas de monto (1 a 5 puntos)
        Label lblMonto = new Label("¿Cuántos puntos apuestas?");
        lblMonto.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #191919;");

        ToggleGroup grupoMonto = new ToggleGroup();
        HBox boxFichas = new HBox(10);
        boxFichas.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 5; i++) {
            ToggleButton ficha = new ToggleButton(i + " pt");
            ficha.setUserData(i);
            ficha.setToggleGroup(grupoMonto);
            ficha.setPrefSize(68, 42);
            if (i == 2) ficha.setSelected(true);
            boxFichas.getChildren().add(ficha);
        }

        // Indicador en vivo de lo seleccionado
        Label lblResumen = new Label();
        lblResumen.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Runnable refrescarVisual = () -> {
            boolean esColor = btnTipoColor.isSelected();
            boolean esParidad = btnTipoParidad.isSelected();
            boolean esNumero = btnTipoNumero.isSelected();

            boxColores.setVisible(esColor);
            boxParidad.setVisible(esParidad);
            txtNumero.setVisible(esNumero);

            String baseTipo = "-fx-font-weight: bold; -fx-background-radius: 14; -fx-border-radius: 14; -fx-padding: 0 14 0 14; -fx-cursor: hand; -fx-border-width: 2; ";
            btnTipoColor.setStyle(baseTipo + (esColor
                    ? "-fx-background-color: #191919; -fx-text-fill: white; -fx-border-color: #191919;"
                    : "-fx-background-color: #F5F5F0; -fx-text-fill: #191919; -fx-border-color: #D6D9DB;"));
            btnTipoParidad.setStyle(baseTipo + (esParidad
                    ? "-fx-background-color: #191919; -fx-text-fill: white; -fx-border-color: #191919;"
                    : "-fx-background-color: #F5F5F0; -fx-text-fill: #191919; -fx-border-color: #D6D9DB;"));
            btnTipoNumero.setStyle(baseTipo + (esNumero
                    ? "-fx-background-color: #191919; -fx-text-fill: white; -fx-border-color: #191919;"
                    : "-fx-background-color: #F5F5F0; -fx-text-fill: #191919; -fx-border-color: #D6D9DB;"));

            boolean rojoSel = btnRojo.isSelected();
            btnRojo.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand; -fx-opacity: " + (rojoSel ? "1.0;" : "0.55;") + (rojoSel ? " -fx-border-color: #191919; -fx-border-width: 3;" : " -fx-border-color: transparent; -fx-border-width: 3;"));
            btnNegro.setStyle("-fx-background-color: #191919; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand; -fx-opacity: " + (!rojoSel ? "1.0;" : "0.55;") + (!rojoSel ? " -fx-border-color: #FBC02D; -fx-border-width: 3;" : " -fx-border-color: transparent; -fx-border-width: 3;"));

            boolean parSel = btnPar.isSelected();
            btnPar.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand; -fx-border-width: 2; " + (parSel
                    ? "-fx-background-color: #191919; -fx-text-fill: white; -fx-border-color: #191919;"
                    : "-fx-background-color: #F5F5F0; -fx-text-fill: #191919; -fx-border-color: #D6D9DB;"));
            btnImpar.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand; -fx-border-width: 2; " + (!parSel
                    ? "-fx-background-color: #191919; -fx-text-fill: white; -fx-border-color: #191919;"
                    : "-fx-background-color: #F5F5F0; -fx-text-fill: #191919; -fx-border-color: #D6D9DB;"));

            int montoActual = grupoMonto.getSelectedToggle() != null ? (int) grupoMonto.getSelectedToggle().getUserData() : 2;
            for (Node nodo : boxFichas.getChildren()) {
                ToggleButton f = (ToggleButton) nodo;
                boolean activa = (int) f.getUserData() == montoActual;
                f.setStyle(activa
                        ? "-fx-background-color: #FBC02D; -fx-text-fill: #191919; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #191919; -fx-border-width: 3; -fx-cursor: hand;"
                        : "-fx-background-color: #FFF9C4; -fx-text-fill: #555555; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #E0C040; -fx-border-width: 1; -fx-cursor: hand;");
            }

            String predTexto = esColor ? (rojoSel ? "ROJO" : "NEGRO")
                    : esParidad ? (parSel ? "PAR" : "IMPAR")
                    : (txtNumero.getText().trim().isEmpty() ? "NÚMERO (0-36)" : "#" + txtNumero.getText().trim());
            String multTexto = esNumero ? "x10" : "x2";
            lblResumen.setText("Seleccionado: " + predTexto + " • " + montoActual + " pt(s) (" + multTexto + ")");
        };

        btnTipoColor.setOnAction(e -> refrescarVisual.run());
        btnTipoParidad.setOnAction(e -> refrescarVisual.run());
        btnTipoNumero.setOnAction(e -> {
            refrescarVisual.run();
            txtNumero.requestFocus();
        });
        btnRojo.setOnAction(e -> refrescarVisual.run());
        btnNegro.setOnAction(e -> refrescarVisual.run());
        btnPar.setOnAction(e -> refrescarVisual.run());
        btnImpar.setOnAction(e -> refrescarVisual.run());
        txtNumero.textProperty().addListener((obs, oldV, newV) -> refrescarVisual.run());
        grupoMonto.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
            }
            refrescarVisual.run();
        });

        refrescarVisual.run();

        // 4. Botones de acción
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setPrefSize(140, 46);
        btnCancelar.setStyle("-fx-background-color: #EFEFEF; -fx-text-fill: #191919; -fx-font-size: 15px; -fx-background-radius: 16; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        Button btnConfirmar = new Button("Apostar y Jugar");
        btnConfirmar.setPrefSize(180, 46);
        btnConfirmar.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 16; -fx-cursor: hand;");

        btnConfirmar.setOnAction(e -> {
            if (grupoMonto.getSelectedToggle() == null) return;
            int monto = (int) grupoMonto.getSelectedToggle().getUserData();

            TipoApuesta tipo;
            String prediccion;

            if (btnTipoColor.isSelected()) {
                tipo = TipoApuesta.COLOR;
                prediccion = btnRojo.isSelected() ? "ROJO" : "NEGRO";
            } else if (btnTipoParidad.isSelected()) {
                tipo = TipoApuesta.PAROIMPAR;
                prediccion = btnPar.isSelected() ? "PAR" : "IMPAR";
            } else {
                tipo = TipoApuesta.NUMERO;
                prediccion = txtNumero.getText().trim();
            }

            Apuesta candidata = new Apuesta(tipo, monto, prediccion);
            if (!gestorApuesta.validarApuesta(candidata)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Apuesta inválida", "Ingresa un número válido entre 0 y 36.");
                return;
            }
            resultado[0] = candidata;
            modal.close();
        });

        HBox boxAcciones = new HBox(14, btnCancelar, btnConfirmar);
        boxAcciones.setAlignment(Pos.CENTER);

        tarjeta.getChildren().addAll(lblTitulo, lblSub, boxTipos, panelPrediccion, lblMonto, boxFichas, lblResumen, boxAcciones);

        Scene scene = new Scene(tarjeta);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();

        return resultado[0];
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}