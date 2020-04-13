/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.Atributo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class AtributoModelo extends Modelo {

    public AtributoModelo() {
        tabla = "atributos2";

    }

    public List<Atributo> BuscarAsin(String asin) {
        List<Atributo> result = null;

        String query = "SELECT * FROM " + tabla + " WHERE asin = ?";

        try {

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            result = new ArrayList<Atributo>();
            while (rs.next()) {
                result.add(
                        new Atributo(
                                rs.getInt("id"),
                                rs.getString("asin"),
                                rs.getString("atributo"),
                                rs.getString("valor"),
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
