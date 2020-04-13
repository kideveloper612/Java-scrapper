/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.NoExiste;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author root
 */
public class NoExisteModelo extends Modelo {

    public NoExisteModelo() {
        tabla = "noexiste";
    }

    public NoExiste BuscarAsin(String asin) {
        NoExiste result = null;

        String query = "SELECT * FROM " + tabla + " WHERE asin = ?";
        try {

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                result = new NoExiste(
                        rs.getInt("id"),
                        rs.getString("asin"),
                        rs.getString("fechacrea")
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
