package impOperacionesBD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GestionConexionesTest {

    GestionConexiones gestionConexiones;
    @BeforeEach
    public void setUp(){
        gestionConexiones = new GestionConexiones();
    }

    @Test
    public void conexionOk() throws SQLException, IOException {
        Connection connection =  gestionConexiones.obtenerConexion();
        assertNotNull(connection);
    }
    @Test
    public void inicializarBdOk() throws SQLException, IOException {
        gestionConexiones.inicializarBD();
        try(Connection connection = gestionConexiones.obtenerConexion()){
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try(ResultSet resultSet = databaseMetaData.getTables(null,"public","registros",new String[]{"TABLE"})) {
                assertTrue(resultSet.next());
            }
        }catch(Exception e){
            throw new RuntimeException("Error al verificar tabla",e);
        }
    }


}
