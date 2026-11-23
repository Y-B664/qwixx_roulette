package datos;

import java.util.ArrayList;
import java.util.List;
public class Stack {
    private List<PowerUp> powerUps;
    public Stack() {
        this.powerUps = new ArrayList<>();
    }
    public List<PowerUp> getPowerUps() { return powerUps; }
    public void setPowerUps(List<PowerUp> powerUps) { this.powerUps = powerUps; }
}
