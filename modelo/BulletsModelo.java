/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.Bullet;
import com.scrapper.objetos.Imagen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class BulletsModelo extends Modelo {

    public BulletsModelo() {
        tabla = "bullets2";
    }

    public boolean Crear(String asin, String url) {
        boolean result = false;
        String query = "insert into bullets2 (asin,bullet) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            st.setString(2, url);
            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                System.out.println("Error Creando Bullet " + url + " Producto " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (Exception ex) {
            System.out.println("Error Creando Bullet " + url + " Producto " + asin);
        }
        return result;
    }

    public List<Bullet> BuscarAsin(String asin) {
        List<Bullet> result = null;

        String query = "SELECT * FROM " + tabla + " WHERE asin = ?";
        try {

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            result = new ArrayList<Bullet>();
            while (rs.next()) {
                result.add(
                        new Bullet(
                                rs.getInt("id"),
                                rs.getString("asin"),
                                rs.getString("bullet"),
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
