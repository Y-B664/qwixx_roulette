package datos;

import java.util.ArrayList;
import java.util.List;
public class Ruleta {
    private List<CasillaRuleta> casillas;
    private CasillaRuleta resultadoActual;
    public Ruleta() {
        this.casillas = new ArrayList<>();
    }
    public List<CasillaRuleta> getCasillas() { return casillas; }
    public void setCasillas(List<CasillaRuleta> casillas) { this.casillas = casillas; }
    public CasillaRuleta getResultadoActual() { return resultadoActual; }
    public void setResultadoActual(CasillaRuleta resultadoActual) { this.resultadoActual = resultadoActual; }
}
