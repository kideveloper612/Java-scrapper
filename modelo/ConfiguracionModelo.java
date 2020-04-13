/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.mysql.jdbc.Statement;
import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.Configuracion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author root
 */
public class ConfiguracionModelo extends Modelo {

    public ConfiguracionModelo() {
        tabla = "configura";
    }

    public Configuracion Crear(Configuracion config) {
        int id = -1;

        String query = "INSERT INTO " + tabla + " ("
                + "maximoconsulta"
                + ",productosxminuto"
                + ",minutosespera"
                + ",tiempoespera"
                + ",maximoupdate"
                + ",tiempoupdate"
                + ",fechaupdate"
                + ",fechacrea"
                + ") VALUES (?,?,?,?,?,?,?,?);";
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            st.setInt(1, config.getMaximoConsulta());
            st.setInt(2, config.getProductosxMinuto());
            st.setInt(3, config.getMinutosEspera());
            st.setInt(4, config.getTiempoEspera());
            st.setInt(5, config.getMaximoUpdate());
            st.setInt(6, config.getTiempoUpdate());
            st.setString(7, null);
            st.setString(8, null);
            st.executeUpdate();

            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                config.setId(id);
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println("ex" + ex.getMessage());
        }

        return config;
    }

    public boolean Eliminar(int id) {
        boolean result = false;
        String query = "DELETE FROM configura WHERE id = ?";

        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);
            st.execute();
            st.close();
            conn.close();
            result = true;
        } catch (Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        }

        return result;
    }

    public boolean Buscar(int id) {
        boolean result = false;
        String query = "SELECT id FROM configura WHERE id = ?";

        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                result = true;
            }

            rs.close();
            st.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        }

        return result;
    }

    public Configuracion BuscarID(int id) {
        Configuracion result = null;
        String query = "SELECT * FROM configura WHERE id = ?";

        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                result = new Configuracion(
                        rs.getInt("id"),
                        rs.getInt("maximoconsulta"),
                        rs.getInt("productosxminuto"),
                        rs.getInt("minutosespera"),
                        rs.getInt("tiempoespera"),
                        rs.getInt("maximoupdate"),
                        rs.getInt("tiempoupdate")
                );
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        }

        return result;
    }

    public Configuracion getConfiguracion() {

        String query = "select * from configura";
        Configuracion result = null;

        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result = new Configuracion(
                        rs.getInt("id"),
                        rs.getInt("maximoconsulta"),
                        rs.getInt("productosxminuto"),
                        rs.getInt("minutosespera"),
                        rs.getInt("tiempoespera"),
                        rs.getInt("maximoupdate"),
                        rs.getInt("tiempoupdate")
                );
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Error Buscando Valores de Configuracion" + e.getMessage());
        }
        return result;
    }
}
