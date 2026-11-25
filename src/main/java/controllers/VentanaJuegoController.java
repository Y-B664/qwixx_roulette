
package controllers;

import datos.*;
import gestion.*;
import gui.IndicadorTurnoUI;
import impOperacionesBD.ImpOperacionesRegistro;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.List;
import java.util.Stack;

public class VentanaJuegoController {

    @FXML private AnchorPane juegoRoot;
    @FXML private Group ruletaGroup;
    @FXML private Button confirmarGiroButton;
    private final ImpOperacionesRegistro impOperacionesRegistro = new ImpOperacionesRegistro();
    private final GestorRuleta gestorRuleta = new GestorRuleta();
    private final GestorApuesta gestorApuesta = new GestorApuesta();
    private final GestorTienda gestorTienda = new GestorTienda();
    private final GestorTemporizador gestorTemporizador = new GestorTemporizador();
    private final GestorCarton gestorCarton = new GestorCarton();
    private final GestorJugador gestorJugador = new GestorJugador(impOperacionesRegistro);;
    private final GestorPartida gestorPartida = new GestorPartida(gestorJugador,gestorCarton,gestorTemporizador);
    private boolean primeraVez;
    private boolean giroConfirmado;
    private Timeline temporizador;
    private int segundosRestantes = 60;
    private int numeroTurno = 0;
    private String nombreJugador;
    private String nombreBotRival;
    private Dificultad dificultadBot;
    private Ruleta ruleta;


    public void configurarPartida(String nombreJugador, Dificultad dificultadBot) {
        if (dificultadBot == null) {
            throw new IllegalArgumentException("La dificultad del bot es obligatoria.");
        }
        mostrarPrimeraVez();
        this.nombreJugador = nombreJugador;
        this.dificultadBot = dificultadBot;
        this.nombreBotRival = obtenerNombreBot(dificultadBot);
    }

    public boolean iniciarPartida() {
        if (dificultadBot == null) {
            throw new IllegalStateException("La partida debe configurarse antes de iniciarse.");
        }
        if (primeraVez){
            String instruccion ="¡Bienvenido al juego!\n" +
                    "Al comenzar, recibirás 5 puntos para participar. Antes de iniciar la partida, deberás realizar una apuesta inicial utilizando los puntos disponibles.\n" +
                    "Una vez realizada tu apuesta, estarás listo para continuar con el juego.";
            mostrarModalInstruccion("Puntos iniciales",instruccion);
        }
        girarRuleta();


        Jugador jugador = gestorJugador.crearJugador(nombreJugador,new Stack(),gestorCarton.crearCarton(4));
        gestorPartida.iniciarPartida(2,jugador);
        ruleta = gestorRuleta.construirRuleta();
        dibujarNumerosRuleta();
        List<PowerUp> ofertaTienda = gestorTienda.generarOfertaAleatoria();
        PowerUp comodinInicial = ofertaTienda.isEmpty() ? null : ofertaTienda.get(0);

        Apuesta apuestaInicial = solicitarApuestaInicialJavaFX();
        if (apuestaInicial == null) {
            return false;
        }

        if (!gestorApuesta.hacerApuesta(apuestaInicial)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Apuesta inválida",
                    "No se pudo registrar la apuesta inicial.");
            return false;
        }

        IndicadorTurnoUI indicadorTurno = new IndicadorTurnoUI(
                nombreJugador, nombreBotRival, obtenerTextoDificultad(dificultadBot)
        );
        indicadorTurno.setTurno(true, nombreJugador, nombreBotRival);

        String nombreComodin = (comodinInicial != null) ? comodinInicial.getNombre() : "Comodín aleatorio listo";
        mostrarResumenConIndicador(indicadorTurno, apuestaInicial, nombreComodin);
        return true;
    }
    public void mostrarPrimeraVez() {
        ButtonType si = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
        Alert modal = new Alert(Alert.AlertType.CONFIRMATION);
        modal.setTitle("Primera partida");
        modal.setHeaderText(null);
        modal.setContentText("¿Es la primera vez que juegas?");
        modal.getButtonTypes().setAll(si, no);
        if (juegoRoot.getScene() != null && juegoRoot.getScene().getWindow() != null) {
            modal.initOwner(juegoRoot.getScene().getWindow());
        }else{
            modal.initOwner(Window.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElse(null));
        }
        primeraVez = modal.showAndWait().filter(si::equals).isPresent();
    }

    private String obtenerNombreBot(Dificultad dificultad) {
        return switch (dificultad) {
            case FACIL -> "Drako";
            case MEDIO -> "Azrael";
            case DIFICIL -> "Valtharion";
        };
    }

    private String obtenerTextoDificultad(Dificultad dificultad) {
        return switch (dificultad) {
            case FACIL -> "Fácil";
            case MEDIO -> "Medio";
            case DIFICIL -> "Difícil";
        };
    }

    private void mostrarResumenConIndicador(IndicadorTurnoUI indicadorTurno, Apuesta apuestaInicial, String nombreComodin) {
        Stage dialog = new Stage();
        dialog.initOwner(juegoRoot.getScene().getWindow());
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
        modal.initOwner(juegoRoot.getScene().getWindow());
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

        Label lblSub = new Label("Saldo inicial: 5 puntos de casino • Rival: " + nombreBotRival +
                " (" + obtenerTextoDificultad(dificultadBot) + ")");
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
            if (i == 2) {
                ficha.setSelected(true);
            }
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

            int montoActual = grupoMonto.getSelectedToggle() != null
                    ? (int) grupoMonto.getSelectedToggle().getUserData() : 2;
            for (Node nodo : boxFichas.getChildren()) {
                ToggleButton ficha = (ToggleButton) nodo;
                boolean activa = (int) ficha.getUserData() == montoActual;
                ficha.setStyle(activa
                        ? "-fx-background-color: #FBC02D; -fx-text-fill: #191919; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #191919; -fx-border-width: 3; -fx-cursor: hand;"
                        : "-fx-background-color: #FFF9C4; -fx-text-fill: #555555; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #E0C040; -fx-border-width: 1; -fx-cursor: hand;");
            }

            String predTexto = esColor ? (rojoSel ? "ROJO" : "NEGRO")
                    : esParidad ? (parSel ? "PAR" : "IMPAR")
                    : (txtNumero.getText().trim().isEmpty()
                    ? "NÚMERO (0-36)" : "#" + txtNumero.getText().trim());
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
            if (grupoMonto.getSelectedToggle() == null) {
                return;
            }
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
                mostrarAlerta(Alert.AlertType.ERROR, "Apuesta inválida",
                        "Ingresa un número válido entre 0 y 36.");
                return;
            }
            resultado[0] = candidata;
            modal.close();
        });

        HBox boxAcciones = new HBox(14, btnCancelar, btnConfirmar);
        boxAcciones.setAlignment(Pos.CENTER);
        tarjeta.getChildren().addAll(lblTitulo, lblSub, boxTipos, panelPrediccion,
                lblMonto, boxFichas, lblResumen, boxAcciones);

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
    public void mostrarModalInstruccion(String titulo, String instruccion) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        if (juegoRoot.getScene() != null && juegoRoot.getScene().getWindow() != null) {
            dialog.initOwner(juegoRoot.getScene().getWindow());
        } else {
            Window.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .ifPresent(dialog::initOwner);
        }

        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 28px; " +
                "-fx-font-weight: bold; -fx-text-fill: #191919;");
        tituloLabel.setWrapText(true);
        tituloLabel.setTextAlignment(TextAlignment.CENTER);

        Region acento = new Region();
        acento.setPrefSize(54, 4);
        acento.setMaxSize(54, 4);
        acento.setStyle("-fx-background-color: #9F4242; -fx-background-radius: 2;");

        Label instruccionLabel = new Label(instruccion);
        instruccionLabel.setMaxWidth(500);
        instruccionLabel.setWrapText(true);
        instruccionLabel.setTextAlignment(TextAlignment.LEFT);
        instruccionLabel.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 17px; " +
                "-fx-text-fill: #444444; -fx-line-spacing: 5px;");

        Button cerrarButton = new Button("Entendido");
        cerrarButton.setPrefSize(150, 44);
        cerrarButton.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 16px; " +
                "-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #191919; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        cerrarButton.setOnAction(event -> dialog.close());

        VBox contenido = new VBox(18, tituloLabel, acento, instruccionLabel, cerrarButton);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30, 34, 30, 34));
        contenido.setPrefWidth(570);
        contenido.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 22; " +
                "-fx-border-color: #191919; -fx-border-width: 2; -fx-border-radius: 22; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 18, 0, 0, 6);");

        Scene scene = new Scene(contenido);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    private CasillaRuleta girarRuleta(){
        giroConfirmado = false;
        confirmarGiroButton.setText("Confirmar giro");
        confirmarGiroButton.setDisable(true);

        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(3000));
        rotateTransition.setNode(ruletaGroup);
        rotateTransition.setFromAngle(ruletaGroup.getRotate());
        rotateTransition.setToAngle(ruletaGroup.getRotate() + 720);
        rotateTransition.setCycleCount(2);
        rotateTransition.setOnFinished(event -> confirmarGiroButton.setDisable(false));
        rotateTransition.play();
        return gestorRuleta.girarRuleta();
    }

    @FXML
    private void confirmarGiro() {
        giroConfirmado = true;
        confirmarGiroButton.setText("Giro confirmado");
        confirmarGiroButton.setDisable(true);
    }
    private void dibujarNumerosRuleta(){
        List<CasillaRuleta> casillas = ruleta.getCasillas();
        if (casillas == null || casillas.isEmpty()) {
            return;
        }

        double radio = 242;
        double espacioAngular = 2 * Math.PI / casillas.size();

        for (int indice = 0; indice < casillas.size(); indice++) {
            CasillaRuleta casilla = casillas.get(indice);
            double angulo = -Math.PI / 2 - Math.toRadians(25)+ indice * espacioAngular;
            double x = radio * Math.cos(angulo);
            double y = radio * Math.sin(angulo);

            Label etiqueta = new Label(Integer.toString(casilla.getValor()));
            etiqueta.getStyleClass().add("roulette-number");
            etiqueta.setAlignment(Pos.CENTER);
            etiqueta.setMouseTransparent(true);
            etiqueta.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 22px; " +
                    "-fx-font-weight: bold; -fx-text-fill: white; " +
                    "-fx-background-radius: 19; -fx-border-color: white; -fx-border-width: 1; " +
                    "-fx-border-radius: 19; -fx-min-width: 38px; -fx-pref-width: 38px; " +
                    "-fx-max-width: 38px; -fx-min-height: 38px; -fx-pref-height: 38px; " +
                    "-fx-max-height: 38px; -fx-alignment: center;");
            etiqueta.setPrefSize(38, 38);
            etiqueta.setLayoutX(x - 19);
            etiqueta.setLayoutY(y - 19);
            ruletaGroup.getChildren().add(etiqueta);
        }
    }

}

