package impOperacionesBD;



import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class GestionConexiones {


    private Properties propiedades =  new Properties();
    private String user ;
    private String password;
    private String url ;

    public GestionConexiones() {
        try {
            propiedades.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            user = propiedades.getProperty("db.usuario");
            password = propiedades.getProperty("db.password");
            url = propiedades.getProperty("db.url");
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar properties", e);
        }
    }


    public Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void inicializarBD() {
        String sql = "CREATE TABLE IF NOT EXISTS registros (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    nombre VARCHAR(100) NOT NULL UNIQUE,\n" +
                "    puntuacion INTEGER NOT NULL\n" +
                ");";
        try(Connection connection = obtenerConexion()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
