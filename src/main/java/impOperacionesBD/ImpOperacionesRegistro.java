package impOperacionesBD;

import datos.Registro;
import datos.TipoPosicion;
import operacionesBD.GenericDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImpOperacionesRegistro implements GenericDAO<Registro> {
    private Connection connection;
    private GestionConexiones gestionConexiones = new GestionConexiones();

    public ImpOperacionesRegistro() {
        connection = gestionConexiones.obtenerConexion();
        gestionConexiones.inicializarBD();
    }

    @Override
    public int crear(Registro objeto) {
        if(objeto == null || objeto.getNombre() == null || objeto.getNombre().isBlank()){
            return 0;
        }
        if(existePorNombre(objeto.getNombre())){
            return 0;
        }
        String sql = "INSERT INTO registros(nombre,puntuacion) VALUES (?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,objeto.getNombre());
            preparedStatement.setInt(2,objeto.getPuntuacion());
            int filasInsertadas = preparedStatement.executeUpdate();
            return filasInsertadas;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Registro> obtenerTodos() {
        List<Registro> registros = new ArrayList<>();
        String sql = "SELECT * FROM  registros ORDER BY puntuacion DESC, id desc";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String nombre = resultSet.getString("nombre");
                int puntuacion = resultSet.getInt("puntuacion");
                Registro registro = new Registro(nombre,puntuacion);
                registros.add(registro);
            }
            return registros;
        }catch(SQLException e){
            throw new RuntimeException("No se pudieron obtener los registros");
        }
    }

    @Override
    public int actualizar(Registro objeto) {
        if(objeto == null || objeto.getNombre() == null || objeto.getNombre().isBlank()){
            return 0;
        }
        String sql = "UPDATE registros SET puntuacion = ? WHERE nombre ILIKE ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,objeto.getPuntuacion());
            preparedStatement.setString(2, objeto.getNombre());
            int filasActualizadas = preparedStatement.executeUpdate();
            return filasActualizadas;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean eliminar(Registro objeto) {
        return false;
    }

    @Override
    public boolean existePorNombre(String nombre) {
        String sql = "SELECT * FROM registros WHERE nombre ILIKE ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,nombre);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
