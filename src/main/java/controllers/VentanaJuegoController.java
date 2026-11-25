package controllers;

import datos.*;
import datos.bot.Bot;
import datos.bot.DificilBot;
import datos.bot.MedioBot;
import datos.carton.Carton;
import datos.carton.CasillaCarton;
import datos.carton.FilaColor;
import gestion.*;
import gui.IndicadorTurnoUI;
import gui.Launcher;
import impOperacionesBD.ImpOperacionesRegistro;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import operacionesBD.GenericDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

public class VentanaJuegoController {

    private static final int UMBRAL_DEUDA = 5;
    @FXML private AnchorPane juegoRoot;
    @FXML private Group ruletaGroup;
    @FXML private Button confirmarGiroButton;
    @FXML private Label temporizadorLabel;
    @FXML private Label saldoJugadorLabel;
    @FXML private HBox filaRoja;
    @FXML private HBox filaAmarilla;
    @FXML private HBox filaVerde;
    @FXML private HBox filaAzul;
    private final ImpOperacionesRegistro impOperacionesRegistro = new ImpOperacionesRegistro();
    @FXML private Button instruccionesButton;
    @FXML private Button finalizarButton;
    @FXML private Button powerUpsButton;
    @FXML private Button tiendaButton;
    @FXML private Label puntosRojosLabel;
    @FXML private Label puntosAmarillosLabel;
    @FXML private Label puntosVerdesLabel;
    @FXML private Label puntosAzulesLabel;
    @FXML private Label penalizacionLabel;
    @FXML private Label puntuacionTotalLabel;
    @FXML private ToggleButton fallo1;
    @FXML private ToggleButton fallo2;
    @FXML private ToggleButton fallo3;
    @FXML private ToggleButton fallo4;

    private final GenericDAO<Registro> daoRegistro;
    private final GestorRuleta gestorRuleta = new GestorRuleta();
    private final GestorApuesta gestorApuesta = new GestorApuesta();
    private final GestorTienda gestorTienda = new GestorTienda();
    private final GestorStack gestorStack = new GestorStack();
    private final GestorTemporizador gestorTemporizador = new GestorTemporizador();
    private final GestorCarton gestorCarton = new GestorCarton();
    private final GestorJugador gestorJugador;
    private final GestorPartida gestorPartida;

    private boolean primeraVez;
    private boolean giroConfirmado;
    private Timeline temporizador;
    private int totalSegundos;
    private int segundosRestantes;
    private int numeroTurno = 0;

    private String nombreJugador;
    private String nombreBotRival;
    private Dificultad dificultadBot;
    private Ruleta ruleta;
    private Carton cartonJugador;
    private boolean temporizadorActivo = false;
    private Jugador jugador;
    private CasillaRuleta resultadoActual;
    private Bot botRival;
    private IndicadorTurnoUI indicadorTurno;
    private boolean casillaMarcadaEnTurno;
    private boolean ruletaGirando;

    // Estados de comodines activos en el turno del jugador
    private boolean comodinColorActivo = false;
    private boolean saltoIzquierdaActivo = false;
    private boolean doblePuntajeActivo = false;
    private boolean seguroApuestaActivo = false;

    public VentanaJuegoController() {
        this.daoRegistro = crearDaoSeguro();
        this.gestorJugador = new GestorJugador(this.daoRegistro);
        this.gestorPartida = new GestorPartida(this.gestorJugador, this.gestorCarton, this.gestorTemporizador);
    }

    private GenericDAO<Registro> crearDaoSeguro() {
        try {
            return new ImpOperacionesRegistro();
        } catch (Exception ex) {
            System.out.println("Aviso BD no disponible, usando respaldo en memoria: " + ex.getMessage());
            return new GenericDAO<>() {
                private final List<Registro> memoria = new ArrayList<>();

                @Override
                public int crear(Registro objeto) {
                    if (objeto == null || objeto.getNombre() == null || objeto.getNombre().isBlank()) {
                        return 0;
                    }
                    if (existePorNombre(objeto.getNombre())) {
                        return 0;
                    }
                    memoria.add(objeto);
                    return 1;
                }

                @Override
                public List<Registro> obtenerTodos() {
                    return new ArrayList<>(memoria);
                }

                @Override
                public int actualizar(Registro objeto) {
                    if (objeto == null || objeto.getNombre() == null || objeto.getNombre().isBlank()) {
                        return 0;
                    }
                    for (int i = 0; i < memoria.size(); i++) {
                        Registro actual = memoria.get(i);
                        if (actual.getNombre() != null && actual.getNombre().equalsIgnoreCase(objeto.getNombre().trim())) {
                            memoria.set(i, objeto);
                            return 1;
                        }
                    }
                    return 0;
                }

                @Override
                public boolean eliminar(Registro objeto) {
                    if (objeto == null || objeto.getNombre() == null) {
                        return false;
                    }
                    return memoria.removeIf(r -> r.getNombre() != null && r.getNombre().equalsIgnoreCase(objeto.getNombre().trim()));
                }

                @Override
                public boolean existePorNombre(String nombre) {
                    if (nombre == null) {
                        return false;
                    }
                    return memoria.stream().anyMatch(r -> r.getNombre() != null && r.getNombre().equalsIgnoreCase(nombre.trim()));
                }
            };
        }
    }

    @FXML
    public void initialize() {
        agregarBotonVolver();

        if (instruccionesButton != null) {
            instruccionesButton.setOnAction(e -> mostrarModalInstruccion(
                    "Instrucciones",
                    "Marca los números de izquierda a derecha según el color y número de la ruleta.\n" +
                            "Si sale 0 Verde, puedes marcar cualquier casilla libre.\n" +
                            "Necesitas mínimo 5 marcas para cerrar el candado de una fila."
            ));
        }
        if (finalizarButton != null) {
            finalizarButton.setOnAction(e -> finalizarYAbrirResultados());
        }
        if (tiendaButton != null) {
            tiendaButton.setOnAction(e -> abrirModalTienda());
        }
        if (powerUpsButton != null) {
            powerUpsButton.setOnAction(e -> abrirModalPowerUps());
        }
    }

    private void agregarBotonVolver() {
        if (juegoRoot == null) {
            return;
        }
        Button volverButton = new Button("← Volver");
        volverButton.setPrefSize(180, 64);
        volverButton.setStyle("-fx-font-size: 22px; -fx-text-fill: #191919; -fx-background-color: white; " +
                "-fx-background-radius: 18; -fx-border-color: #191919; -fx-border-width: 2; " +
                "-fx-border-radius: 18; -fx-cursor: hand;");
        AnchorPane.setTopAnchor(volverButton, 155.0);
        AnchorPane.setLeftAnchor(volverButton, 53.0);
        volverButton.setOnAction(e -> volverADificultad());
        juegoRoot.getChildren().add(volverButton);
    }

    private void volverADificultad() {
        pausarTemporizador();
        gestorPartida.cancelarPartida();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ventanaDificultad.fxml"));
            Parent root = loader.load();

            VentanaDificultadController dificultadController = loader.getController();
            if (dificultadController != null && nombreJugador != null) {
                dificultadController.setNombreJugador(nombreJugador);
            }

            Stage stage = (Stage) juegoRoot.getScene().getWindow();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();
        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación",
                    "No se pudo regresar a la selección de dificultad.");
        }
    }

    public void configurarPartida(String nombreJugador, Dificultad dificultadBot) {
        if (dificultadBot == null) {
            throw new IllegalArgumentException("La dificultad del bot es obligatoria.");
        }
        this.nombreJugador = nombreJugador;
        this.dificultadBot = dificultadBot;
        this.nombreBotRival = obtenerNombreBot(dificultadBot);
        mostrarPrimeraVez();
    }

    public boolean iniciarPartida() {
        if (dificultadBot == null) {
            throw new IllegalStateException("La partida debe configurarse antes de iniciarse.");
        }
        if (primeraVez) {
            String instruccion = "¡Bienvenido al juego!\n" +
                    "Al comenzar, recibirás 5 puntos para participar. Antes de iniciar la partida, deberás realizar una apuesta inicial utilizando los puntos disponibles.\n" +
                    "Una vez realizada tu apuesta, estarás listo para continuar con el juego.";
            mostrarModalInstruccion("Puntos iniciales", instruccion);
        }
        jugador = gestorJugador.crearJugador(nombreJugador,new Stack<>(),gestorCarton.crearCarton(4));
        cartonJugador = jugador.getCarton();
        ponerFilasCarton();
        Partida partida = gestorPartida.iniciarPartida(2,jugador);
        inicializarTemporizador(partida.getTiempoPorTurnoSegundos());

        Partida partidaActual = gestorPartida.iniciarPartida(2,jugador);
        if (partidaActual != null && partidaActual.getJugadores() != null && partidaActual.getJugadores().length > 0) {
            Jugador jugadorPrincipal = partidaActual.getJugadores()[0];
            if (jugadorPrincipal != null) {
                jugadorPrincipal.setNombreUsuario(
                        (nombreJugador != null && !nombreJugador.isBlank()) ? nombreJugador.trim() : "Jugador"
                );
            }
        }

        this.botRival = gestorPartida.configurarBot(dificultadBot);
        if (this.botRival != null) {
            this.botRival.setSaldoPuntos(5);
            gestorPartida.agregarBot(this.botRival);
        }

        ruleta = gestorRuleta.construirRuleta();
        dibujarNumerosRuleta();

        List<PowerUp> ofertaTienda = gestorTienda.generarOfertaAleatoria();
        PowerUp comodinInicial = ofertaTienda.isEmpty() ? null : ofertaTienda.get(0);
        if (comodinInicial != null && this.botRival != null) {
            @SuppressWarnings("unchecked")
            Stack<Object> mazoBot = this.botRival.getMazoPowerUps();
            if (mazoBot != null) {
                mazoBot.push(comodinInicial);
            }
        }

        Apuesta apuestaInicial = solicitarApuestaInicialJavaFX();
        if (apuestaInicial == null) {
            return false;
        }

        if (apuestaInicial.getMonto() > jugador.getSaldoPuntos()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Saldo insuficiente",
                    "No tienes suficientes puntos para realizar esta apuesta.");
            return false;
        }

        if (!gestorApuesta.hacerApuesta(apuestaInicial)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Apuesta inválida",
                    "No se pudo registrar la apuesta inicial.");
            return false;
        }
        jugador.setSaldoPuntos(jugador.getSaldoPuntos() - apuestaInicial.getMonto());
        actualizarSaldoJugador();

        if (botRival instanceof MedioBot medioBot) {
            Apuesta apuestaBot = medioBot.decidirApuesta();
            if (gestorApuesta.validarApuesta(apuestaBot)) {
                gestorApuesta.hacerApuesta(apuestaBot);
            }
        } else if (botRival instanceof DificilBot dificilBot) {
            Apuesta apuestaBot = dificilBot.decidirApuesta();
            if (gestorApuesta.validarApuesta(apuestaBot)) {
                gestorApuesta.hacerApuesta(apuestaBot);
            }
        }

        indicadorTurno = new IndicadorTurnoUI(
                nombreJugador, nombreBotRival, obtenerTextoDificultad(dificultadBot)
        );
        indicadorTurno.setTurno(true, nombreJugador, nombreBotRival);

        String nombreComodin = (comodinInicial != null) ? comodinInicial.getNombre() : "Comodín aleatorio listo";
        girarRuleta(resultado -> {
            this.resultadoActual = resultado;
            this.casillaMarcadaEnTurno = false;
            actualizarCasillasDisponibles(resultado);
            if (esApuestaGanadora(apuestaInicial, resultado)) {
                int premio = gestorApuesta.calcularPagoDinamico();
                jugador.setSaldoPuntos(jugador.getSaldoPuntos() + premio);
            }
            actualizarSaldoJugador();
            mostrarResumenConIndicador(indicadorTurno, apuestaInicial, nombreComodin, () -> {
                if (primeraVez) {
                    String instruccion = "¡Es tu momento de jugar! Después de realizar tu apuesta y el primer giro, comienza tu turno. Tendrás 1 minuto para realizar tus acciones.\n" +
                            "\n" +
                            "¿Qué debes saber?\n" +
                            "Tiempo límite: Dispones de 1 minuto para completar tu turno. El temporizador comenzará una vez aceptes estas instrucciones.\n" +
                            "Fallos: Si se agota el tiempo antes de completar tu turno, recibirás un fallo. ¡Ten cuidado! Al acumular 4 fallos, perderás la partida.\n" +
                            "Acciones disponibles: Durante tu turno podrás realizar las acciones permitidas por el juego para avanzar y alcanzar tu objetivo.\n" +
                            "Comodines: Al inicio recibirás un comodin de manera aleatoria.\n" +
                            "Pulsa «Aceptar» para cerrar esta ventana e iniciar tu temporizador de 1 minuto.\n" +
                            "\n" +
                            "¡Mucha suerte!";
                    mostrarModalInstruccion("¡Comienza tu turno!", instruccion, this::iniciarTemporizador);
                } else {
                    iniciarTemporizador();
                }
            });
        });
        return true;
    }

    // ==================== TIENDA Y POWER-UPS ====================

    private void actualizarBotonPowerUps() {
        if (powerUpsButton == null || jugador == null) {
            return;
        }
        int cantidad = gestorStack.obtenerDisponibles(jugador.getMazoPowerUps()).size();
        powerUpsButton.setStyle("-fx-font-size: 20px;");
        powerUpsButton.setText("PowerUps (" + cantidad + ")");
    }

    private void abrirModalTienda() {
        if (jugador == null) {
            return;
        }

        Stage modal = new Stage();
        if (juegoRoot.getScene() != null && juegoRoot.getScene().getWindow() != null) {
            modal.initOwner(juegoRoot.getScene().getWindow());
        }
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox contenedor = new VBox(16);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(26, 30, 26, 30));
        contenedor.setPrefWidth(680);
        contenedor.setStyle("""
                -fx-background-color: #FFFFFF;
                -fx-background-radius: 24;
                -fx-border-color: #191919;
                -fx-border-width: 3;
                -fx-border-radius: 24;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 22, 0, 0, 8);
                """);

        Label lblTitulo = new Label("TIENDA DE COMODINES");
        lblTitulo.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #191919;");

        Label lblSaldoModal = new Label();
        lblSaldoModal.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #191919; " +
                "-fx-background-color: #FBC02D; -fx-padding: 6 16 6 16; -fx-background-radius: 14;");

        Label lblMensaje = new Label("Elige un comodín para añadirlo a tu mazo.");
        lblMensaje.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        HBox cardsBox = new HBox(14);
        cardsBox.setAlignment(Pos.CENTER);

        Runnable[] renderizarOferta = new Runnable[1];
        renderizarOferta[0] = () -> {
            lblSaldoModal.setText("Tu saldo disponible: " + jugador.getSaldoPuntos() + " puntos");
            cardsBox.getChildren().clear();

            Tienda tienda = gestorTienda.abrirTienda();
            List<PowerUp> oferta = tienda.getCartasDisponibles().getPowerUps();

            for (PowerUp item : oferta) {
                VBox card = new VBox(10);
                card.setAlignment(Pos.TOP_CENTER);
                card.setPadding(new Insets(16));
                card.setPrefWidth(195);
                card.setPrefHeight(225);
                card.setStyle("""
                        -fx-background-color: #F9F9F6;
                        -fx-background-radius: 18;
                        -fx-border-color: #191919;
                        -fx-border-width: 2;
                        -fx-border-radius: 18;
                        """);

                Label nombre = new Label(item.getNombre());
                nombre.setWrapText(true);
                nombre.setTextAlignment(TextAlignment.CENTER);
                nombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #191919;");

                Label desc = new Label(item.getDescripcion());
                desc.setWrapText(true);
                desc.setTextAlignment(TextAlignment.CENTER);
                desc.setPrefHeight(88);
                desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #444444;");

                Label costo = new Label(item.getCosto() + " pts");
                costo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

                Button btnComprar = new Button("Comprar");
                btnComprar.setPrefSize(140, 36);
                boolean puedePagar = jugador.getSaldoPuntos() >= item.getCosto();
                btnComprar.setDisable(!puedePagar);
                btnComprar.setStyle(puedePagar
                        ? "-fx-background-color: #191919; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand;"
                        : "-fx-background-color: #CCCCCC; -fx-text-fill: #666666; -fx-font-weight: bold; -fx-background-radius: 12;");

                btnComprar.setOnAction(ev -> {
                    if (gestorTienda.comprarPowerUp(item, jugador)) {
                        gestorStack.agregarPowerUp(jugador.getMazoPowerUps(), item);
                        actualizarSaldoJugador();
                        actualizarBotonPowerUps();
                        lblMensaje.setText("¡Compraste \"" + item.getNombre() + "\"! Ya está en tu mazo.");
                        lblMensaje.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                        renderizarOferta[0].run();
                    }
                });

                card.getChildren().addAll(nombre, desc, costo, btnComprar);
                cardsBox.getChildren().add(card);
            }
        };

        renderizarOferta[0].run();

        Button btnRefrescar = new Button("Cambiar oferta (1 pt)");
        btnRefrescar.setPrefSize(185, 40);
        btnRefrescar.setStyle("-fx-background-color: #F5F5F0; -fx-text-fill: #191919; -fx-font-weight: bold; " +
                "-fx-border-color: #191919; -fx-border-width: 2; -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand;");
        btnRefrescar.setOnAction(e -> {
            if (jugador.getSaldoPuntos() < 1) {
                lblMensaje.setText("Necesitas al menos 1 punto para cambiar la oferta.");
                lblMensaje.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #C62828;");
                return;
            }
            jugador.setSaldoPuntos(jugador.getSaldoPuntos() - 1);
            actualizarSaldoJugador();
            gestorTienda.generarOfertaAleatoria();
            lblMensaje.setText("Oferta renovada.");
            lblMensaje.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");
            renderizarOferta[0].run();
        });

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setPrefSize(140, 40);
        btnCerrar.setStyle("-fx-background-color: #191919; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> modal.close());

        HBox footer = new HBox(14, btnRefrescar, btnCerrar);
        footer.setAlignment(Pos.CENTER);

        contenedor.getChildren().addAll(lblTitulo, lblSaldoModal, cardsBox, lblMensaje, footer);

        Scene scene = new Scene(contenedor);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void abrirModalPowerUps() {
        if (jugador == null) {
            return;
        }

        final PowerUp[] powerUpSeleccionado = {null};

        Stage modal = new Stage();
        if (juegoRoot.getScene() != null && juegoRoot.getScene().getWindow() != null) {
            modal.initOwner(juegoRoot.getScene().getWindow());
        }
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox contenedor = new VBox(16);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(26, 30, 26, 30));
        contenedor.setPrefWidth(620);
        contenedor.setStyle("""
                -fx-background-color: #FFFFFF;
                -fx-background-radius: 24;
                -fx-border-color: #191919;
                -fx-border-width: 3;
                -fx-border-radius: 24;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 22, 0, 0, 8);
                """);

        Label lblTitulo = new Label("MIS COMODINES (POWER-UPS)");
        lblTitulo.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #191919;");

        VBox listaBox = new VBox(10);
        listaBox.setAlignment(Pos.CENTER);

        List<PowerUp> disponibles = gestorStack.obtenerDisponibles(jugador.getMazoPowerUps());
        if (disponibles.isEmpty()) {
            Label vacio = new Label("No tienes comodines disponibles en tu mazo.\nVisita la Tienda para adquirir uno con tus puntos.");
            vacio.setTextAlignment(TextAlignment.CENTER);
            vacio.setStyle("-fx-font-size: 15px; -fx-text-fill: #555555; -fx-padding: 18;");
            listaBox.getChildren().add(vacio);
        } else {
            for (PowerUp powerUp : disponibles) {
                HBox fila = new HBox(14);
                fila.setAlignment(Pos.CENTER_LEFT);
                fila.setPadding(new Insets(12, 16, 12, 16));
                fila.setStyle("""
                        -fx-background-color: #F9F9F6;
                        -fx-background-radius: 16;
                        -fx-border-color: #191919;
                        -fx-border-width: 2;
                        -fx-border-radius: 16;
                        """);

                VBox textos = new VBox(4);
                HBox.setHgrow(textos, Priority.ALWAYS);

                Label nombre = new Label(powerUp.getNombre());
                nombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #191919;");

                Label desc = new Label(powerUp.getDescripcion());
                desc.setWrapText(true);
                desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #444444;");

                textos.getChildren().addAll(nombre, desc);

                Button btnUsar = new Button("Activar");
                btnUsar.setPrefSize(110, 38);
                btnUsar.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand;");
                btnUsar.setOnAction(ev -> {
                    powerUpSeleccionado[0] = powerUp;
                    modal.close();
                });

                fila.getChildren().addAll(textos, btnUsar);
                listaBox.getChildren().add(fila);
            }
        }

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setPrefSize(140, 40);
        btnCerrar.setStyle("-fx-background-color: #191919; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> modal.close());

        contenedor.getChildren().addAll(lblTitulo, listaBox, btnCerrar);

        Scene scene = new Scene(contenedor);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();

        // Se activa después de que el modal anterior cerró por completo para evitar congelamiento
        if (powerUpSeleccionado[0] != null) {
            activarPowerUpJugador(powerUpSeleccionado[0]);
        }
    }

    private void activarPowerUpJugador(PowerUp powerUp) {
        if (powerUp == null || jugador == null) {
            return;
        }
        if (!gestorStack.usarPowerUp(jugador.getMazoPowerUps(), powerUp)) {
            return;
        }

        powerUp.aplicarEfecto(jugador);
        actualizarBotonPowerUps();
        actualizarSaldoJugador();

        String nombre = powerUp.getNombre() != null ? powerUp.getNombre() : "";
        String detalleActivacion = switch (nombre) {
            case "Doble Puntaje" -> {
                this.doblePuntajeActivo = true;
                yield "Recibiste +6 puntos de bonificación y tu próxima ganancia será doble.";
            }
            case "Anular Fallo" -> {
                boolean quitado = quitarUltimoFalloVisual();
                yield quitado
                        ? "Se anuló tu último fallo marcado en el tablero."
                        : "Comodín activado (no tenías fallos marcados, recibes +2 puntos compensatorios).";
            }
            case "Comodín Color" -> {
                this.comodinColorActivo = true;
                yield "Comodín Color activo: ahora puedes marcar cualquier casilla libre de cualquier fila.";
            }
            case "Salto Izquierda" -> {
                this.saltoIzquierdaActivo = true;
                yield "Salto Izquierda activo: puedes marcar una casilla a la izquierda de tus marcas actuales.";
            }
            case "Seguro de Apuesta" -> {
                this.seguroApuestaActivo = true;
                yield "Recibiste +3 puntos de respaldo y tu próxima apuesta quedó protegida.";
            }
            default -> "Comodín \"" + nombre + "\" activado en tu turno.";
        };

        if ("Anular Fallo".equals(nombre) && !detalleActivacion.contains("Se anuló")) {
            jugador.setSaldoPuntos(jugador.getSaldoPuntos() + 2);
            actualizarSaldoJugador();
        }

        mostrarModalInstruccion("Comodín Activado", detalleActivacion);
    }

    private boolean quitarUltimoFalloVisual() {
        ToggleButton[] fallos = {fallo4, fallo3, fallo2, fallo1};
        for (ToggleButton fallo : fallos) {
            if (fallo != null && fallo.isSelected()) {
                fallo.setSelected(false);
                return true;
            }
        }
        return false;
    }

    // ==================== LÓGICA DE PARTIDA DE TU COMPAÑERA ====================

    private boolean esApuestaGanadora(Apuesta apuesta, CasillaRuleta resultado) {
        if (apuesta == null || resultado == null || apuesta.getTipo() == null
                || !gestorApuesta.validarApuesta(apuesta)) {
            return false;
        }

        String prediccion = apuesta.getPrediccion().trim().toUpperCase();
        return switch (apuesta.getTipo()) {
            case COLOR -> resultado.getColor() != null
                    && prediccion.equals(resultado.getColor().name());
            case NUMERO -> Integer.parseInt(prediccion) == resultado.getValor();
            case PAROIMPAR -> resultado.getValor() != 0
                    && ((resultado.getValor() % 2 == 0 && prediccion.equals("PAR"))
                    || (resultado.getValor() % 2 != 0 && prediccion.equals("IMPAR")));
        };
    }

    private void ejecutarTurnoBotRival() {
        if (botRival == null) {
            botRival = gestorPartida.configurarBot(dificultadBot);
            if (botRival != null) {
                gestorPartida.agregarBot(botRival);
            }
        }
        if (botRival == null) {
            return;
        }
        pausarTemporizador();
        if (confirmarGiroButton != null) {
            confirmarGiroButton.setDisable(true);
        }
        gestorJugador.confirmarGiro(jugador);
        gestorPartida.getPartida().setTurnoActual(botRival);
        if (indicadorTurno != null) {
            indicadorTurno.setTurno(false, nombreJugador, nombreBotRival);
        }

        if (botRival instanceof MedioBot medioBot) {
            medioBot.decidirUsoPowerUps();
        } else if (botRival instanceof DificilBot dificilBot) {
            dificilBot.decidirUsoPowerUps();
        }

        CasillaRuleta resultadoActual = (ruleta != null) ? ruleta.getResultadoActual() : null;
        botRival.ejecutarTurno(resultadoActual, gestorCarton, gestorRuleta.isComodinCeroActivo());
        gestorJugador.confirmarGiro(botRival);

        if (finalizarPartidaSiCorresponde()) {
            return;
        }

        gestorPartida.verificarConfirmacionGiro();
        gestorPartida.getPartida().setTurnoActual(jugador);
        jugador.setGiroConfirmado(false);
        botRival.setGiroConfirmado(false);
        gestorPartida.getPartida().setGiroConfirmado(false);
        this.numeroTurno++;
        if (indicadorTurno != null) {
            indicadorTurno.setTurno(true, nombreJugador, nombreBotRival);
        }
        girarRuleta(resultado -> {
            this.resultadoActual = resultado;
            this.casillaMarcadaEnTurno = false;
            actualizarCasillasDisponibles(resultado);
            reiniciarTemporizador();
            iniciarTemporizador();
        });
    }

    private void finalizarYAbrirResultados() {
        pausarTemporizador();
        if (gestorPartida.getPartida() != null) {
            gestorPartida.calcularPuntajeFinal();
        }
        Jugador jugadorPrincipal = null;
        if (gestorPartida.getPartida() != null
                && gestorPartida.getPartida().getJugadores() != null
                && gestorPartida.getPartida().getJugadores().length > 0) {
            jugadorPrincipal = gestorPartida.getPartida().getJugadores()[0];
        }

        String nombreFinal = (jugadorPrincipal != null && jugadorPrincipal.getNombreUsuario() != null)
                ? jugadorPrincipal.getNombreUsuario()
                : nombreJugador;
        int puntosFinales = (jugadorPrincipal != null) ? jugadorPrincipal.getSaldoPuntos() : 0;
        if (doblePuntajeActivo) {
            puntosFinales *= 2;
        }

        try {
            if (daoRegistro != null && nombreFinal != null && !nombreFinal.isBlank()) {
                Registro registroFinal = new Registro(nombreFinal.trim(), puntosFinales);
                if (daoRegistro.existePorNombre(registroFinal.getNombre())) {
                    daoRegistro.actualizar(registroFinal);
                } else {
                    daoRegistro.crear(registroFinal);
                }
            }
        } catch (Exception ex) {
            System.out.println("Aviso al guardar resultado: " + ex.getMessage());
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/VentanaResultados.fxml"));
            Parent root = loader.load();

            VentanaResultadosController resultadosController = loader.getController();
            if (resultadosController != null) {
                resultadosController.configurarContexto(null, nombreFinal, puntosFinales);
            }

            Stage stage = (Stage) juegoRoot.getScene().getWindow();
            stage.setScene(Launcher.crearEscenaAjustada(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Partida finalizada",
                    "Puntaje final de " + nombreFinal + ": " + puntosFinales + " PTS");
        }
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
        } else {
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

    private void mostrarResumenConIndicador(
            IndicadorTurnoUI indicadorTurno,
            Apuesta apuestaInicial,
            String nombreComodin,
            Runnable alCerrar) {
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
        dialog.setOnHidden(event -> alCerrar.run());
        dialog.show();
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
        mostrarModalInstruccion(titulo, instruccion, null);
    }

    private void mostrarModalInstruccion(String titulo, String instruccion, Runnable alCerrar) {
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

        if (alCerrar != null) {
            dialog.setOnHidden(event -> alCerrar.run());
        }
        dialog.show();
        dialog.toFront();
        dialog.requestFocus();
    }

    private void girarRuleta(Consumer<CasillaRuleta> alFinalizar){
        giroConfirmado = false;
        ruletaGirando = true;
        resultadoActual = null;
        actualizarCasillasDisponibles(null);
        if (confirmarGiroButton != null) {
            confirmarGiroButton.setText("Confirmar giro");
            confirmarGiroButton.setDisable(true);
        }

        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(3000));
        rotateTransition.setNode(ruletaGroup);
        rotateTransition.setFromAngle(ruletaGroup.getRotate());
        rotateTransition.setToAngle(ruletaGroup.getRotate() + 720);
        rotateTransition.setCycleCount(2);
        CasillaRuleta resultado = gestorRuleta.girarRuleta();
        rotateTransition.setOnFinished(event -> {
            Platform.runLater(() -> {
                confirmarGiroButton.setDisable(false);
                mostrarModalResultadoRuleta(resultado, () -> {
                    ruletaGirando = false;
                    alFinalizar.accept(resultado);
                });
            });
        });
        rotateTransition.play();
    }

    private void mostrarModalResultadoRuleta(CasillaRuleta resultado, Runnable alCerrar) {
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

        String colorFondo = obtenerColorResultado(resultado.getColor());
        String colorTexto = resultado.getColor() == datos.Color.AMARILLO ? "#191919" : "#FFFFFF";

        Label titulo = new Label("Resultado de la ruleta");
        titulo.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 20px; " +
                "-fx-font-weight: bold; -fx-text-fill: #191919;");

        Label numero = new Label(Integer.toString(resultado.getValor()));
        numero.setAlignment(Pos.CENTER);
        numero.setPrefSize(82, 82);
        numero.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 38px; " +
                "-fx-font-weight: bold; -fx-text-fill: " + colorTexto + "; " +
                "-fx-background-color: " + colorFondo + "; -fx-background-radius: 41;");

        Label color = new Label(resultado.getColor() == null
                ? "Sin color" : resultado.getColor().name());
        color.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 15px; " +
                "-fx-text-fill: #555555;");

        Button aceptar = new Button("Continuar");
        aceptar.setPrefSize(130, 40);
        aceptar.setStyle("-fx-font-family: 'Pixelify Sans'; -fx-font-size: 15px; " +
                "-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #191919; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        aceptar.setOnAction(event -> dialog.close());
        dialog.setOnHidden(event -> alCerrar.run());

        VBox contenido = new VBox(12, titulo, numero, color, aceptar);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(24, 30, 24, 30));
        contenido.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20; " +
                "-fx-border-color: #191919; -fx-border-width: 2; -fx-border-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 16, 0, 0, 5);");

        Scene scene = new Scene(contenido);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.show();
    }

    private String obtenerColorResultado(datos.Color color) {
        if (color == null) {
            return "#191919";
        }
        return switch (color) {
            case ROJO -> "#9F4242";
            case NEGRO -> "#191919";
            case VERDE -> "#1D5822";
            case AMARILLO -> "#FBC02D";
            case AZUL -> "#1E88E5";
        };
    }

    @FXML
    private void confirmarGiro() {
        if (giroConfirmado) {
            return;
        }
        if(!casillaMarcadaEnTurno){
            gestorCarton.marcarFallo(jugador);
        }
        giroConfirmado = true;
        gestorJugador.confirmarGiro(jugador);
        if (confirmarGiroButton != null) {
            confirmarGiroButton.setText("Giro confirmado");
            confirmarGiroButton.setDisable(true);
        }
        ejecutarTurnoBotRival();
    }

    private void dibujarNumerosRuleta() {
        if (ruleta == null || ruletaGroup == null) {
            return;
        }
        List<CasillaRuleta> casillas = ruleta.getCasillas();
        if (casillas == null || casillas.isEmpty()) {
            return;
        }

        double radio = 242;
        double espacioAngular = 2 * Math.PI / casillas.size();

        for (int indice = 0; indice < casillas.size(); indice++) {
            CasillaRuleta casilla = casillas.get(indice);
            double angulo = -Math.PI / 2 - Math.toRadians(25) + indice * espacioAngular;
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

    private void iniciarTemporizador() {
        if (temporizador == null) {
            throw new IllegalStateException("El temporizador no ha sido inicializado.");
        }
        temporizadorActivo = true;
        temporizador.play();
    }

    private void inicializarTemporizador(int segundos) {
        if (segundos <= 0) {
            throw new IllegalArgumentException("La duración del turno debe ser mayor que cero.");
        }
        if (temporizador != null) {
            temporizador.stop();
        }
        totalSegundos = segundos;
        temporizador = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> actualizarTemporizador()));
        temporizador.setCycleCount(Timeline.INDEFINITE);
        reiniciarTemporizador();
    }

    private void reiniciarTemporizador() {
        segundosRestantes = totalSegundos;
        temporizadorLabel.setText(darFormatoTiempo(segundosRestantes));
    }

    private void actualizarTemporizador(){
        if(segundosRestantes > 0){
            segundosRestantes--;
            actualizarUI();
        }
        if (segundosRestantes == 0) {
            pausarTemporizador();
            if(!jugador.isGiroConfirmado()){
                gestorCarton.marcarFallo(jugador);
                actualizarFallos();
            }
            if (finalizarPartidaSiCorresponde()) {
                return;
            }
            giroConfirmado = false;
            if (confirmarGiroButton != null) {
                confirmarGiroButton.setDisable(true);
            }
            ejecutarTurnoBotRival();
        }
    }

    private void pausarTemporizador() {
        temporizadorActivo = false;
        if (temporizador != null) {
            temporizador.pause();
        }
    }

    private void actualizarUI() {
        temporizadorLabel.setText(darFormatoTiempo(segundosRestantes));
    }

    private String darFormatoTiempo(int segundos) {
        int minutos = segundos / 60;
        int segundosT = segundos % 60;
        return String.format("%02d:%02d", minutos, segundosT);
    }

    public void actualizarSaldoJugador() {
        if (saldoJugadorLabel != null && jugador != null) {
            saldoJugadorLabel.setText("Saldo: " + jugador.getSaldoPuntos() + " puntos");
        }
    }
    private Map<datos.Color, HBox> obtenerVistasFilasCarton() {
        Map<datos.Color, HBox> vistas = new EnumMap<>(datos.Color.class);
        vistas.put(datos.Color.ROJO, filaRoja);
        vistas.put(datos.Color.AMARILLO, filaAmarilla);
        vistas.put(datos.Color.VERDE, filaVerde);
        vistas.put(datos.Color.AZUL, filaAzul);
        return vistas;
    }
    private void ponerFilasCarton() {
        Map<datos.Color, HBox> vistas = obtenerVistasFilasCarton();

        for (FilaColor fila : cartonJugador.getFilas()) {
            HBox vista = vistas.get(fila.getColor());
            CasillaCarton[] casillas = fila.getCasillas();

            if (vista == null || vista.getChildren().size() != casillas.length) {
                throw new IllegalStateException("La fila del FXML no coincide con el cartón.");
            }
            vista.setDisable(true);
            Button botonCandado = (Button) vista.getChildren().get(casillas.length - 1);
            botonCandado.setOnAction(event -> intentarCerrarFila(fila));

            for (int i = 0; i < casillas.length - 1; i++) {
                Button boton = (Button) vista.getChildren().get(i);
                CasillaCarton casilla = casillas[i];
                boton.setText(Integer.toString(casilla.getValor()));
                boton.setDisable(false);

                boton.setOnAction(event -> {
                    if (resultadoActual == null || !validarYMarcarCasilla(fila, casilla, resultadoActual)) {
                        return;
                    }
                    casillaMarcadaEnTurno = true;
                    gestorCarton.bloquearIzquierda(casilla.getValor(), fila);
                    actualizarCasillasDisponibles(resultadoActual);
                });
            }
        }
        actualizarCasillasDisponibles(resultadoActual);
    }

    private void actualizarFallos() {
        int fallos = jugador.getCarton().getFallos();
        ToggleButton[] indicadores = {fallo1, fallo2, fallo3, fallo4};
        for (int i = 0; i < indicadores.length; i++) {
            if (indicadores[i] != null) {
                indicadores[i].setSelected(i < fallos);
            }
        }
    }
    private boolean validarYMarcarCasilla(
            FilaColor fila,
            CasillaCarton casilla,
            CasillaRuleta resultado) {
        if (casillaMarcadaEnTurno) {
            return false;
        }
        boolean esCeroVerde = resultado.getValor() == 0 && resultado.getColor() == datos.Color.VERDE;
        if (!esCeroVerde) {
            return gestorCarton.validarMarcacion(fila, casilla, resultado);
        }
        if (fila == null || casilla == null || fila.isEstaBloqueadaGlobal()
                || casilla.isMarcada() || casilla.isBloqueada()) {
            return false;
        }
        casilla.setMarcada(true);
        return true;
    }private void intentarCerrarFila(FilaColor fila) {
        if (fila == null || fila.isEstaBloqueadaGlobal()) {
            return;
        }
        if (!cumpleCondicionesCierre(fila)) {
            int valorFinal = fila.getColor() == datos.Color.ROJO
                    || fila.getColor() == datos.Color.AMARILLO ? 12 : 2;
            mostrarAlerta(Alert.AlertType.INFORMATION, "No se puede bloquear la fila",
                    "Debes marcar el " + valorFinal + " y tener al menos 5 números marcados en esa fila.");
            return;
        }
        if (gestorCarton.validarCierreCandado(fila)) {
            actualizarCasillasDisponibles(resultadoActual);
            finalizarPartidaSiCorresponde();
        }
    }
    private void actualizarEstilosFila(FilaColor fila, HBox vista, CasillaCarton[] casillas) {
        if (fila == null || vista == null || casillas == null) {
            return;
        }
        for (int i = 0; i < casillas.length - 1; i++) {
            Button boton = (Button) vista.getChildren().get(i);
            actualizarEstiloCasilla(boton, casillas[i]);
        }
    }

    private void actualizarEstiloCasilla(Button boton, CasillaCarton casilla) {
        boton.getStyleClass().removeAll("casilla-marcada", "casilla-bloqueada");
        if (casilla.isMarcada()) {
            boton.getStyleClass().add("casilla-marcada");
        } else if (casilla.isBloqueada()) {
            boton.getStyleClass().add("casilla-bloqueada");
        }
    }

    private boolean cumpleCondicionesCierre(FilaColor fila) {
        CasillaCarton[] casillas = fila.getCasillas();
        if (casillas == null || fila.getColor() == null) {
            return false;
        }
        int valorFinal;
        if (fila.getColor() == datos.Color.ROJO || fila.getColor() == datos.Color.AMARILLO) {
            valorFinal = 12;
        } else if (fila.getColor() == datos.Color.VERDE || fila.getColor() == datos.Color.AZUL) {
            valorFinal = 2;
        } else {
            return false;
        }

        int numerosMarcados = 0;
        boolean casillaFinalMarcada = false;
        for (int i = 0; i < casillas.length - 1; i++) {
            CasillaCarton casilla = casillas[i];
            if (casilla != null && casilla.isMarcada()) {
                numerosMarcados++;
                casillaFinalMarcada |= casilla.getValor() == valorFinal;
            }
        }
        return numerosMarcados >= 5 && casillaFinalMarcada;
    }
    private void actualizarCasillasDisponibles(CasillaRuleta resultado) {
        if (cartonJugador == null || cartonJugador.getFilas() == null) {
            return;
        }

        Map<datos.Color, HBox> vistas = obtenerVistasFilasCarton();
        if (resultado == null) {
            for (FilaColor fila : cartonJugador.getFilas()) {
                HBox vista = vistas.get(fila.getColor());
                if (vista != null) {
                    actualizarEstilosFila(fila, vista, fila.getCasillas());
                    vista.setDisable(true);
                }
            }
            return;
        }

        boolean esCeroVerde = resultado.getValor() == 0 && resultado.getColor() == datos.Color.VERDE;
        for (FilaColor fila : cartonJugador.getFilas()) {
            HBox vista = vistas.get(fila.getColor());
            CasillaCarton[] casillas = fila.getCasillas();
            if (vista == null || casillas == null || vista.getChildren().size() != casillas.length) {
                throw new IllegalStateException("La fila del FXML no coincide con el cartón.");
            }

            actualizarEstilosFila(fila, vista, casillas);
            boolean colorPermitido = esCeroVerde
                    || (resultado.getColor() == datos.Color.NEGRO
                    && (fila.getColor() == datos.Color.AMARILLO || fila.getColor() == datos.Color.VERDE))
                    || (resultado.getColor() == datos.Color.ROJO
                    && (fila.getColor() == datos.Color.ROJO || fila.getColor() == datos.Color.AZUL));
            vista.setDisable(ruletaGirando || !colorPermitido || fila.isEstaBloqueadaGlobal());
        }
    }
    private boolean finalizarPartidaSiCorresponde() {
        Partida partida = gestorPartida.getPartida();
        if (partida == null || partida.getEstado() != EstadoPartida.EN_CURSO) {
            return false;
        }

        boolean filasCompletadas = gestorPartida.evaluarCondicionFin();
        boolean deudaSuperada = false;
        boolean fallosCompletados = false;
        Jugador[] jugadores = partida.getJugadores();
        if (jugadores != null) {
            for (Jugador participante : jugadores) {
                if (participante == null || participante.getCarton() == null) {
                    continue;
                }
                deudaSuperada |= participante.getEnDeuda() >= UMBRAL_DEUDA;
                fallosCompletados |= participante.getCarton().getFallos()
                        >= participante.getCarton().getCasillasFallos();
            }
        }

        if (!filasCompletadas && !deudaSuperada && !fallosCompletados) {
            return false;
        }

        if (filasCompletadas) {
            gestorPartida.finalizarPartida();
        }
        partida.setEstado(EstadoPartida.FINALIZADA);
        pausarTemporizador();

        if (deudaSuperada || fallosCompletados) {
            String motivo = deudaSuperada
                    ? "Se alcanzó el umbral de deuda de " + UMBRAL_DEUDA + " puntos."
                    : "Se completaron los fallos permitidos.";
            mostrarAlerta(Alert.AlertType.INFORMATION, "Partida finalizada", motivo);
        }
        finalizarYAbrirResultados();
        return true;
    }

}
