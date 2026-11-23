package datos;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter 
public class Stack {
    private List<PowerUp> powerUps;
    public Stack() {
        this.powerUps = new ArrayList<>();
    }
}
