package controllers;

import datos.Apuesta;
import datos.PowerUp;
import datos.TipoApuesta;
import gestion.GestorApuesta;
import gestion.GestorRuleta;
import gestion.GestorStack;
import gestion.GestorTienda;
import gui.IndicadorTurnoUI;
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
    private String nombreBotRival = "Drako";
    private boolean actualizandoSeleccion = false;

    private final GestorRuleta gestorRuleta = new GestorRuleta();
    private final GestorApuesta gestorApuesta = new GestorApuesta();
    private final GestorTienda gestorTienda = new GestorTienda();
    private final GestorStack gestorStack = new GestorStack();

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

    private String obtenerNombreBotPorDificultad(String nivel) {
        if ("Medio".equals(nivel)) {
            return "Azrael";
        }
        if ("Difícil".equals(nivel)) {
            return "Valtharion";
        }
        return "Drako";
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
            this.nombreBotRival = obtenerNombreBotPorDificultad(nivel);

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

        IndicadorTurnoUI indicadorTurno = new IndicadorTurnoUI(
                nombreJugador, nombreBotRival, dificultadSeleccionada
        );
        indicadorTurno.setTurno(true, nombreJugador, nombreBotRival);

        String nombreComodin = (comodinInicial != null) ? comodinInicial.getNombre() : "Comodín aleatorio listo";
        mostrarResumenConIndicador(indicadorTurno, apuestaInicial, nombreComodin);
    }

    private void mostrarResumenConIndicador(IndicadorTurnoUI indicadorTurno, Apuesta apuestaInicial, String nombreComodin) {
        Stage dialog = new Stage();
        dialog.initOwner(jugarButton.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox box = new VBox(14);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(26, 30, 26, 30));
        box.setStyle("""
                -fx-background-color: #FFFFFF;
                -fx-background-radius: 24;
                -fx-border-color: #191919;
                -fx-border-width: 3;
                -fx-border-radius: 24;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 6);
                """);

        Label titulo = new Label("¡PARTIDA LISTA!");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #191919;");

        Label detalle = new Label(
                "Apuesta inicial: " + apuestaInicial.getMonto() + " pts a " + apuestaInicial.getPrediccion() +
                        "\nComodín inicial otorgado: " + nombreComodin
        );
        detalle.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        Button btnAceptar = new Button("Continuar");
        btnAceptar.setPrefSize(150, 42);
        btnAceptar.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 14; -fx-cursor: hand;");
        btnAceptar.setOnAction(e -> dialog.close());

        box.getChildren().addAll(titulo, indicadorTurno.getContenedor(), detalle, btnAceptar);

        Scene scene = new Scene(box);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
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

        Label lblSub = new Label("Saldo inicial: 5 puntos de casino • Rival: " + nombreBotRival + " (" + dificultadSeleccionada + ")");
        lblSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

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