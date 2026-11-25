package controllers;

import datos.Registro;
import impOperacionesBD.ImpOperacionesRegistro;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class VentanaResultadosController {
    @FXML
    private VBox filasRankingVBox;
    @FXML
    private AnchorPane segundoLugarPane;
    @FXML
    private AnchorPane primerLugarPane;
    @FXML
    private AnchorPane tercerLugarPane;
    @FXML
    private Label segundoNombreLabel;
    @FXML
    private Label segundoPuntosLabel;
    @FXML
    private Label primeroNombreLabel;
    @FXML
    private Label primeroPuntosLabel;
    @FXML
    private Label terceroNombreLabel;
    @FXML
    private Label terceroPuntosLabel;
    @FXML
    private ImageView medallaPlataView;
    @FXML
    private ImageView medallaOroView;
    @FXML
    private ImageView medallaBronceView;
    @FXML
    private Label miNombreLabel;
    @FXML
    private Label miPosicionLabel;
    @FXML
    private Label misPuntosLabel;
    private  VentanaPrincipalController ventanaPrincipalController;
    private ImpOperacionesRegistro impOperacionesRegistro;

    @FXML
    public void initialize() {
        this.ventanaPrincipalController = new VentanaPrincipalController();
        filasRankingVBox.getChildren().clear();
        List<Registro> registros =  new ImpOperacionesRegistro().obtenerTodos();
        if(registros == null){
            return;
        }
        organizarPodios(registros);
        mostrarPuestosRestantes(registros);
        miPuntuacion();
    }

    public void organizarPodios(List<Registro> registros){
        Registro registro0 = registros.getFirst();
        primeroNombreLabel.setText(registro0.getNombre());
        primeroPuntosLabel.setText(registro0.getPuntuacion()+ " PTS");
        Registro registro1 = registros.get(1);
        segundoNombreLabel.setText(registro1.getNombre());
        segundoPuntosLabel.setText(registro1.getPuntuacion()+ " PTS");
        Registro registro2 = registros.get(2);
        terceroNombreLabel.setText(registro2.getNombre());
        terceroPuntosLabel.setText(registro2.getPuntuacion()+ " PTS");
    }
    public void mostrarPuestosRestantes(List<Registro> registros){
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
    private void miPuntuacion(){
        String nombre =  ventanaPrincipalController.getJugadorEnSesion();
        if (nombre == null || nombre.isBlank()){
            miNombreLabel.setText("N/D");
            miPosicionLabel.setText("N/D");
            misPuntosLabel.setText("N/D");
        }else{
            miNombreLabel.setText(nombre);
            miPosicionLabel.setText("12");
            miPosicionLabel.setText("12");
        }
    }
}
