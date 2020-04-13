/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.mysql.jdbc.Statement;
import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.ProductoInicial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public final class ProductoInicialModelo extends Modelo {

    public ProductoInicialModelo() {
        tabla = "producto_inicial";
    }

    public int Crear(
            ProductoInicial obj
    ) {
        int id = -1;

        String query = "INSERT INTO " + tabla + " ("
                + "asin,"
                + "sku,"
                + "url,"
                + "titulo,"
                + "descripcion,"
                + "procesado,"
                + "idusuario,"
                + "fechaupdate,"
                + "fechacrea,"
                + "procesado2,"
                + "flagerror"
                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getAsin());
            st.setString(2, obj.getSku());
            st.setString(3, obj.getUrl());
            st.setString(4, obj.getTitulo());
            st.setString(5, obj.getDescripcion());

            st.setInt(6, obj.getProcesado());
            st.setInt(7, obj.getIdusuario());

            st.setString(8, obj.getFechaupdate());
            st.setString(9, obj.getFechacrea());

            st.setInt(10, obj.getProcesado2());
            st.setInt(11, obj.getFlagerror());

            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                st.close();
                conn.close();
                return id;

            }

            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println("ex" + ex.getMessage());
        }

        return id;
    }

    public boolean Eliminar(int id) {
        boolean result = false;
        String query = "DELETE FROM " + tabla + " WHERE id = ?";

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

    public ProductoInicial Buscar(int id) {
        ProductoInicial inicial = null;

        String query = "SELECT * FROM " + tabla + " WHERE id = ?";

        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                inicial = new ProductoInicial(
                        rs.getInt("id"),
                        rs.getString("asin"),
                        rs.getString("sku"),
                        rs.getString("url"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getInt("idusuario"),
                        rs.getInt("procesado"),
                        rs.getInt("procesado2"),
                        rs.getInt("flagerror"),
                        rs.getString("fechacrea"),
                        rs.getString("fechaupdate")
                );
            }
            rs.close();
            st.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        }

        return inicial;
    }

    public ProductoInicial BuscarAsin(String asin) {
        ProductoInicial result = null;
        try {
            String query = "SELECT * FROM " + tabla + " WHERE asin = ?";

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                result = new ProductoInicial(
                        rs.getInt("id"),
                        rs.getString("asin"),
                        rs.getString("sku"),
                        rs.getString("url"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getInt("idusuario"),
                        rs.getInt("procesado"),
                        rs.getInt("procesado2"),
                        rs.getInt("flagerror"),
                        rs.getString("fechacrea"),
                        rs.getString("fechaupdate")
                );
            }
            rs.close();
            st.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ProductoInicialModelo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public boolean EstadoProcesado(String asin) {
        boolean result = false;
        String query = "update producto_inicial set  procesado2=0  where asin=?";
        //System.out.println(query+" "+asin);
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            st.executeUpdate();
            st.close();
            conn.close();
            result = true;
        } catch (SQLException e) {
            Logger.getLogger(ProductoInicialModelo.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    public boolean EstadoPorProcesado(String asin) {
        boolean result = false;
        String query = "update producto_inicial set  procesado2=0  where asin=?";
        System.out.println(query + " " + asin);
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            st.executeUpdate();
            st.close();
            conn.close();
            result = true;
        } catch (SQLException e) {
            Logger.getLogger(ProductoInicialModelo.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    /*
     Actualiza productos2, procesado=2 para todos los registros
     */
    public static boolean startProcess() {
        String query = "UPDATE producto_inicial SET procesado2=2";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     Actualiza productos2, procesado=0
     */
    public static boolean processProcesado(int status, String asin) {
        String query = "UPDATE producto_inicial SET procesado2=? WHERE asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setInt(1, status);
            st2.setString(2, asin);
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
