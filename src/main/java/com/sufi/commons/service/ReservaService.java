package com.sufi.commons.service;

import com.sufi.module.service.cancel.CancelRequest;
import com.sufi.module.service.confirm.ConfirmRequest;
import com.sufi.module.service.confirm.ConfirmResponse;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;

import static com.sufi.HotelDatabase.getDatabaseUrl;

@Service
public class ReservaService {
    public String insertReserva(ConfirmRequest request, ConfirmResponse reserva) {
        String url = getDatabaseUrl();
        if (url == null) {
            System.err.println("No se pudo obtener la URL de la base de datos");
            return "Error al obtener la URL de la base de datos";
        }

        if (request.getFecha_entrada() == null || request.getFecha_salida() == null) {
            throw new IllegalArgumentException("Las fechas de entrada y salida no pueden ser nulas");
        }

        String insertQuery = "INSERT INTO reservas (listing_id, fecha_entrada, fecha_salida, occupants, precio_total, nombre_cliente, email_cliente, fecha_reserva, localizador, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, request.getListing_id());
            stmt.setDate(2, Date.valueOf(request.getFecha_entrada().toLocalDate().toString()));
            stmt.setDate(3, Date.valueOf(request.getFecha_salida().toLocalDate().toString()));
            stmt.setInt(4, request.getNum_personas());
            stmt.setDouble(5, reserva.getReserva().getPrecio_total());
            stmt.setString(6, request.getNombre_cliente());
            stmt.setString(7, request.getEmail_cliente());
            stmt.setDate(8, Date.valueOf(LocalDate.now().toString()));
            stmt.setInt(9, reserva.getReserva().getLocalizador());
            stmt.setInt(10, 1); // Estado 1 para "Reservado"

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? "Reserva realizada con éxito" : "Error al realizar la reserva";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al realizar la reserva: " + e.getMessage();
        }
    }

    public String deleteReserva(CancelRequest request) {
        String url = getDatabaseUrl();
        if (url == null) {
            System.err.println("No se pudo obtener la URL de la base de datos");
            return "Error al obtener la URL de la base de datos";
        }

        String deleteQuery = "update reservas set estado = 0 where localizador = ? and email_cliente = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, request.getLocalizador());
            stmt.setString(2, request.getEmail_cliente());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? "Reserva cancelada con éxito" : "No se encontró la reserva con el localizador proporcionado";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al cancelar la reserva: " + e.getMessage();
        }
    }
}
