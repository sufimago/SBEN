package com.sufi;

import com.sufi.module.dto.DataBaseDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDatabase {
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
            System.out.println("Error al consultar los datos: " + e.getMessage());
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
            System.out.println("Error al consultar los datos: " + e.getMessage());
        }

        return alojamientos;
    }

}
