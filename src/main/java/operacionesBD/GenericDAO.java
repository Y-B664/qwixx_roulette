package operacionesBD;

import java.util.List;

public interface GenericDAO<T> {
    int crear(T objeto);
    List<T> obtenerTodos();
    int actualizar(T objeto);
    boolean eliminar(T objeto);
    boolean existePorNombre(String nombre);
}
