package gestion;

import datos.CasillaRuleta;
import datos.Color;
import datos.Ruleta;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GestorRuleta {
    private Ruleta ruleta;
    private Random random;
    private boolean comodinCeroActivo;

    public GestorRuleta() {
        this.ruleta = new Ruleta();
        this.random = new Random();
        this.comodinCeroActivo = false;
        construirRuleta();
    }

    public Ruleta construirRuleta() {
        List<CasillaRuleta> casillas = new ArrayList<>();
        // Casilla 0 Verde
        casillas.add(new CasillaRuleta(0, Color.VERDE));

        // Números del 1 al 36 (impares ROJO, pares NEGRO)
        for (int i = 1; i <= 36; i++) {
            Color color = (i % 2 != 0) ? Color.ROJO : Color.NEGRO;
            casillas.add(new CasillaRuleta(i, color));
        }

        this.ruleta.setCasillas(casillas);
        return this.ruleta;
    }

    public CasillaRuleta girarRuleta() {
        if (this.ruleta.getCasillas() == null || this.ruleta.getCasillas().isEmpty()) {
            construirRuleta();
        }
        int indice = random.nextInt(this.ruleta.getCasillas().size());
        CasillaRuleta ganadora = this.ruleta.getCasillas().get(indice);
        this.ruleta.setResultadoActual(ganadora);

        if (ganadora.getValor() == 0) {
            aplicarReglaCeroVerde();
        } else {
            this.comodinCeroActivo = false;
        }
        return ganadora;
    }

    public void aplicarReglaCeroVerde() {
        this.comodinCeroActivo = true;
    }

    public boolean isComodinCeroActivo() {
        return comodinCeroActivo;
    }
}