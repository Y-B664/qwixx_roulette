package gestion;

import datos.Registro;

import java.util.Comparator;
import java.util.List;

public class GestorRanking {
    //tentativo  --
    // calcular puesot a nivel de interfaz
    public boolean validarUsuarioUnico(String nombre) {
        return false;
    }
    public List<Registro> ordenarDescendente(List<Registro> registros) {
        registros.sort(Comparator.comparingInt(Registro::getPuntuacion).reversed());
        return registros;
    }
}
