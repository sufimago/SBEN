package com.sufi;

import com.sufi.module.dto.DataBaseDto;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class HotelDatabase {
    private static final Logger logger = Logger.getLogger(HotelDatabase.class.getName());

    private HotelDatabase() {
        // Constructor privado para evitar instanciación
    }
    public static String getDatabaseUrl() {
        Properties props = new Properties();
        try (InputStream input = HotelDatabase.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.info("No se encontró application.properties");
                return null;
            }
            props.load(input);
            return props.getProperty("database.sqlite.url");
        } catch (IOException e) {
            logger.severe("Error al cargar application.properties: " + e.getMessage());
            return null;
        }
    }

    private static List<DataBaseDto> getAlojamientos(Connection conn) {
        List<DataBaseDto> alojamientos = new ArrayList<>();
        String query = "SELECT * FROM alojamientos";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Mapear los resultados a un objeto Alojamiento
                DataBaseDto alojamiento = new DataBaseDto(
                        rs.getInt("hotCodigo"),
                        rs.getString("ciudad"),
                        rs.getInt("imagen_id"),  // Puede ser null
                        rs.getBoolean("disponible"),
                        rs.getInt("occupants"),
                        rs.getString("direccion"),
                        rs.getInt("listing"),
                        rs.getString("nombre"),
                        rs.getString("pais")
                );

                alojamientos.add(alojamiento);
            }
        } catch (SQLException e) {
            logger.severe("Error al consultar los datos: " + e.getMessage());
        }

        return alojamientos;
    }

    private static List<DataBaseDto> getAlojamientosPorCiudad(Connection conn, String ciudad) {
        List<DataBaseDto> alojamientos = new ArrayList<>();
        String query = "SELECT * FROM alojamientos WHERE ciudad = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ciudad);  // Establecer el valor de la ciudad en la consulta

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DataBaseDto alojamiento = new DataBaseDto(
                            rs.getInt("hotCodigo"),
                            rs.getString("ciudad"),
                            rs.getInt("imagen_id"),  // Puede ser null
                            rs.getBoolean("disponible"),
                            rs.getInt("occupants"),
                            rs.getString("direccion"),
                            rs.getInt("listing"),
                            rs.getString("nombre"),
                            rs.getString("pais")
                    );
                    alojamientos.add(alojamiento);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al consultar los datos por ciudad: " + e.getMessage());
        }

        return alojamientos;
    }

}
