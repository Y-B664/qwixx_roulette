package gestion;

import datos.CasillaRuleta;
import datos.Color;
import datos.Jugador;
import datos.carton.Carton;
import datos.carton.CasillaCarton;
import datos.carton.FilaColor;

import java.util.ArrayList;
import java.util.List;

public class GestorCarton {

    //agregar casilla bloqueo
    public List<FilaColor> generarFilas() {
        List<FilaColor> filas = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            CasillaCarton[] casillas = new CasillaCarton[13];
            for (int j = 0; j < 12; j++) {
                int numero;
                if(i<2){
                    numero = 12-j;
                }else{
                    numero = j+1;
                }
                CasillaCarton casillaCarton = new CasillaCarton(numero);
                casillas[j] = casillaCarton;
            }
            casillas[12] = new CasillaCarton(-1);
            FilaColor filaColor = new FilaColor(Color.values()[i],casillas);
            filas.add(filaColor);
        }
        return filas;
    }
    public Carton crearCarton(int casillasFallos) {
        Carton carton = new Carton(generarFilas(),casillasFallos);
        return carton;
    }
    public boolean validarMarcacion(FilaColor fila, CasillaCarton casilla, CasillaRuleta resultadoRuleta) {
        if (fila== null || casilla == null || resultadoRuleta == null) {
            return false;
        }
        if (fila.isEstaBloqueadaGlobal()) {
            return false;
        }
        if (casilla.isMarcada() || casilla.isBloqueada()) {
            return false;
        }
        if(casilla.getValor() != resultadoRuleta.getValor()){
            System.out.println("dentro" + casilla.getValor());
            System.out.println("dentro" + resultadoRuleta.getValor());
            return false;
        }

        if (resultadoRuleta.getColor() == Color.NEGRO) {
            if (fila.getColor() != Color.AMARILLO && fila.getColor() != Color.VERDE){
                System.out.println("negro" + fila.getColor());
                System.out.println(resultadoRuleta.getColor());
                return false;
            };
        }else if (resultadoRuleta.getColor() == Color.ROJO) {
            if (fila.getColor() != Color.AZUL
                    && fila.getColor() != Color.ROJO){
                System.out.println("rojo" +fila.getColor());
                System.out.println(resultadoRuleta.getColor());
                return false;
            };
        }
        else {
            return false;
        }
        casilla.setMarcada(true);
        return true;
    }
    public boolean marcarFallo(Jugador jugador) {
        if (jugador==null || jugador.getCarton().getCasillasFallos() == jugador.getCarton().getFallos()){
            return  false;
        }
       int fallosActuales= jugador.getCarton().getFallos();
       jugador.getCarton().setFallos(fallosActuales+1);
        return true;
    }

    public boolean bloquearIzquierda(int valor, FilaColor filaColor) {
        if(filaColor == null){
            return false;
        }
        CasillaCarton[] casillasCarton = filaColor.getCasillas();
        if(casillasCarton == null){
            return false;
        }
        int index = -1;
        for (int i = 0; i < casillasCarton.length-1; i++) {
            if(casillasCarton[i].getValor() == valor){
                index=i;
                break;
            }
        }
        if (index==0){
            casillasCarton[0].setBloqueada(true);
            return true;
        }
        for(int i = index;i>=0;i--){
            casillasCarton[i].setBloqueada(true);
        }
        return true;
    }
    public boolean validarCierreCandado(FilaColor filaColor) {
        if (filaColor ==null || filaColor.getCasillas() == null){
            return false;
        }
        int numeroMarcadas = 0;
        for (CasillaCarton c : filaColor.getCasillas()) {
            if (c.isMarcada()){
                numeroMarcadas++;
            }
        }
        if (numeroMarcadas >= 5){
            filaColor.setCandadoTachado(true);
            filaColor.setEstaBloqueadaGlobal(true);
            int size = filaColor.getCasillas().length;
            filaColor.getCasillas()[size-1].setMarcada(true);
            return true;
        }
        return false;
    }
    public int sumarPuntosTotales(Jugador jugador) {
        if (jugador ==null){
            return 0;
        }
        int numeroFallos = jugador.getCarton().getFallos();
        int puntosTotales = 0;
        for (FilaColor fila : jugador.getCarton().getFilas()) {
            int casillasMarcadas = 0;
            if (fila.isEstaBloqueadaGlobal() && fila.isCandadoTachado()) {
                for (CasillaCarton casilla : fila.getCasillas()) {
                    if (casilla.isMarcada()) {
                        casillasMarcadas++;
                    }
                }
                puntosTotales += (casillasMarcadas * (casillasMarcadas + 1)) / 2;
            }
        }
        puntosTotales -= numeroFallos * 5;
        int puntosAnteriores = jugador.getSaldoPuntos();
        if (jugador.getEnDeuda() != 0) {
            puntosTotales -= jugador.getEnDeuda();
        }
        int puntosFinales = puntosAnteriores + puntosTotales;
        jugador.setSaldoPuntos(puntosFinales);
        return puntosFinales;
    }
}
