package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {

    private static double offsetX = 0;
    private static double offsetY = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/gui/ventanaPrincipal.fxml")
        );

        primaryStage.setTitle("Qwixx Roulette");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(crearEscenaAjustada(root));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static Scene crearEscenaAjustada(Parent root) {
        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
        double altoBarra = 36.0;
        double anchoOriginal = root.prefWidth(-1);
        double altoOriginal = root.prefHeight(-1);

        double escalaX = (pantalla.getWidth() * 0.90) / anchoOriginal;
        double escalaY = ((pantalla.getHeight() * 0.90) - altoBarra) / altoOriginal;
        double escala = Math.min(1.0, Math.min(escalaX, escalaY));

        root.getTransforms().add(new Scale(escala, escala, 0, 0));
        Group contenidoEscalado = new Group(root);

        // Barra superior personalizada con Minimizar, Maximizar/Pantalla Completa y Cerrar
        HBox barraSuperior = crearBarraVentana(contenidoEscalado, anchoOriginal, altoOriginal, altoBarra);

        BorderPane contenedorPrincipal = new BorderPane();
        contenedorPrincipal.setTop(barraSuperior);
        contenedorPrincipal.setCenter(contenidoEscalado);
        contenedorPrincipal.setStyle("-fx-background-color: white; -fx-border-color: #D6D9DB; -fx-border-width: 1;");

        return new Scene(contenedorPrincipal, anchoOriginal * escala, (altoOriginal * escala) + altoBarra);
    }

    private static HBox crearBarraVentana(Group contenidoEscalado, double anchoOriginal, double altoOriginal, double altoBarra) {
        HBox barra = new HBox(6);
        barra.setPrefHeight(altoBarra);
        barra.setAlignment(Pos.CENTER_RIGHT);
        barra.setPadding(new Insets(0, 10, 0, 16));
        barra.setStyle("-fx-background-color: #191919;");

        Label titulo = new Label("Qwixx Roulette");
        titulo.setStyle("-fx-text-fill: #D6D9DB; -fx-font-size: 13px; -fx-font-weight: bold;");

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        Button btnMinimizar = crearBotonBarra("—", "#333333");
        Button btnMaximizar = crearBotonBarra("☐", "#333333");
        Button btnCerrar = crearBotonBarra("✕", "#E53935");

        // Minimizar ventana
        btnMinimizar.setOnAction(e -> {
            Stage stage = (Stage) btnMinimizar.getScene().getWindow();
            stage.setIconified(true);
        });

        // Alternar Pantalla Completa / Tamaño Normal
        btnMaximizar.setOnAction(e -> {
            Stage stage = (Stage) btnMaximizar.getScene().getWindow();
            boolean irPantallaCompleta = !stage.isFullScreen();
            stage.setFullScreen(irPantallaCompleta);
            btnMaximizar.setText(irPantallaCompleta ? "❐" : "☐");

            Rectangle2D bounds = irPantallaCompleta
                    ? Screen.getPrimary().getBounds()
                    : Screen.getPrimary().getVisualBounds();
            double factor = irPantallaCompleta ? 1.0 : 0.90;

            double nuevaEscalaX = (bounds.getWidth() * factor) / anchoOriginal;
            double nuevaEscalaY = ((bounds.getHeight() * factor) - altoBarra) / altoOriginal;
            double nuevaEscala = Math.min(nuevaEscalaX, nuevaEscalaY);
            if (!irPantallaCompleta) {
                nuevaEscala = Math.min(1.0, nuevaEscala);
            }

            Parent fxmlRoot = (Parent) contenidoEscalado.getChildren().get(0);
            fxmlRoot.getTransforms().setAll(new Scale(nuevaEscala, nuevaEscala, 0, 0));
        });

        // Cerrar aplicación
        btnCerrar.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        // Permitir arrastrar la ventana desde la barra superior
        barra.setOnMousePressed(e -> {
            offsetX = e.getSceneX();
            offsetY = e.getSceneY();
        });
        barra.setOnMouseDragged(e -> {
            Stage stage = (Stage) barra.getScene().getWindow();
            if (!stage.isFullScreen()) {
                stage.setX(e.getScreenX() - offsetX);
                stage.setY(e.getScreenY() - offsetY);
            }
        });

        barra.getChildren().addAll(titulo, espaciador, btnMinimizar, btnMaximizar, btnCerrar);
        return barra;
    }

    private static Button crearBotonBarra(String simbolo, String colorHover) {
        Button btn = new Button(simbolo);
        btn.setPrefSize(32, 26);
        String estiloBase = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;";
        String estiloActivo = "-fx-background-color: " + colorHover + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;";
        btn.setStyle(estiloBase);
        btn.setOnMouseEntered(e -> btn.setStyle(estiloActivo));
        btn.setOnMouseExited(e -> btn.setStyle(estiloBase));
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}