package impOperacionesBD;

import datos.Registro;
import operacionesBD.GenericDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ImpOperacionesRegistroTest {

    GenericDAO<Registro> impOperacionesRegistro;
    GestionConexiones gestionConexiones;
    Connection connection;
    String timestamp;
    int puntuacion;
    @BeforeEach
    public void SetUp(){
        impOperacionesRegistro= new ImpOperacionesRegistro();
        gestionConexiones = new GestionConexiones();
        connection = gestionConexiones.obtenerConexion();
        timestamp = String.valueOf(System.nanoTime());
        Random random =  new Random();
        puntuacion = random.nextInt(10);
    }
    @Test
    public void crearOk(){
        List<Registro> registros = impOperacionesRegistro.obtenerTodos();
        int registroCreado = impOperacionesRegistro.crear(new Registro("Test_crear"+timestamp,10*puntuacion));
        assertEquals(1,registroCreado);
    }
    @Test
    public void crearRevisadoConFilasOk(){
        List<Registro> registros = impOperacionesRegistro.obtenerTodos();
        impOperacionesRegistro.crear(new Registro("Test_filas"+timestamp,10*puntuacion));
        List<Registro> registrosPosterior = impOperacionesRegistro.obtenerTodos();
        assertEquals(registros.size()+1,registrosPosterior.size());
    }
    @Test
    public void crearConPuestoRepetidoNotOk() {
        impOperacionesRegistro.crear(new Registro("Test_Puesto2"+timestamp, 10*puntuacion));
        int resultado = impOperacionesRegistro.crear(new Registro("Test_Puesto2"+timestamp, 10*puntuacion));
        assertEquals(0, resultado);
    }
    @Test
    public void crearConNombreRepetidoNotOk(){
        impOperacionesRegistro.crear(new Registro("Test_n2_repetido"+timestamp,10*puntuacion));
        int registroCreado = impOperacionesRegistro.crear(new Registro("Test_n2_repetido"+timestamp,10*puntuacion));
        assertEquals(0,registroCreado);
    }
    @Test
    public void crearConNombreRepetidoConMinusculasNotOk() {
        impOperacionesRegistro.crear(new Registro( "Mario"+timestamp, 10*puntuacion));
        int resultado = impOperacionesRegistro.crear(new Registro( "mario"+timestamp, 10*puntuacion));
        assertEquals(0, resultado);
    }
    @Test
    public void crearConObjetoNulo() {
        assertEquals(0, impOperacionesRegistro.crear(null));
    }
    @Test
    public void crearConNombreNuloNotOk() {
        assertEquals(0, impOperacionesRegistro.crear(new Registro( null, 10*puntuacion)));
    }
    @Test
    public void crearConNombreVacioNotOk() {
        assertEquals(0, impOperacionesRegistro.crear(new Registro( "", 10*puntuacion)));
    }
    @Test
    public void actualizarOk() {
        impOperacionesRegistro.crear(new Registro("Test_Actualizar"+timestamp, 10*puntuacion));
        int resultado = impOperacionesRegistro.actualizar(new Registro( "Test_Actualizar"+timestamp, 10*puntuacion));
        assertEquals(1, resultado);
    }
    @Test
    public void actualizarObjetoNulo() {
        assertEquals(0, impOperacionesRegistro.actualizar(null));
    }
    @Test
    public void actualizarNombreVacioNotOk() {
        assertEquals(0, impOperacionesRegistro.actualizar(new Registro( "", 10*puntuacion)));
    }
    @Test
    public void actualizarRegistroInexistente() {
        assertEquals(0, impOperacionesRegistro.actualizar(new Registro( "Nombre"+timestamp, 10*puntuacion)));
    }
    @Test
    public void leerTodosOk(){
        List<Registro> registros = impOperacionesRegistro.obtenerTodos();
        impOperacionesRegistro.crear(new Registro( "null1"+timestamp, 10*puntuacion));
        List<Registro> registrosPosterior = impOperacionesRegistro.obtenerTodos();
        assertEquals(registros.size()+1,registrosPosterior.size());
    }
    @Test
    public void existePorNombreOk(){
        impOperacionesRegistro.crear(new Registro( "Test_n_Existe"+timestamp, 10*puntuacion));
        assertTrue(impOperacionesRegistro.existePorNombre("Test_n_Existe"+timestamp));
    }
    @Test
    public void existePorNombreMinusculasOk(){
         impOperacionesRegistro.crear(new Registro("Test_n_minuscula"+timestamp, 10*puntuacion));
        assertTrue(impOperacionesRegistro.existePorNombre("test_n_minuscula"+timestamp));
    }
    @Test
    public void eliminarOk(){
    }
}
