/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.Imagen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class ImagenesModelo extends Modelo {

    public ImagenesModelo() {
        tabla = "imagenes2";

    }

    public boolean Crear(String asin, String url) {
        boolean result = false;
        String query = "insert into " + tabla + " (asin,url) VALUES (?,?)";
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            st.setString(2, url);
            st.executeUpdate();
            result = true;
            st.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println("Error Creando Imagen " + url + " Producto " + asin);
        }

        return result;
    }

    public List<Imagen> BuscarAsin(String asin) {
        List<Imagen> result = null;

        String query = "SELECT * FROM " + tabla + " WHERE asin = ?";
        try {

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            result = new ArrayList<Imagen>();
            while (rs.next()) {
                result.add(
                        new Imagen(
                                rs.getInt("id"),
                                rs.getString("asin"),
                                rs.getString("url"),
                                rs.getString("fechacrea")
                        )
                );
            }
            rs.close();
            st.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("BuscarAsin Ex: " + ex.getMessage());
        }

        return result;
    }

}
