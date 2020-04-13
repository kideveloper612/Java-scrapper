/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.HistoricoPrecio;
import com.scrapper.objetos.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author root
 */
public class HistoricoPrecioModelo extends Modelo {

    public HistoricoPrecioModelo() {
        tabla = "historicoprecios2";
    }

    public boolean Crear(Producto prodOld, Producto prodNew) {
        boolean result = false;
        String query = "insert into " + tabla
                + " (asin,precioold,reviewold,precionew,reviewnew,statusnew,disponibilidad,proveedor,"
                + "mlcode,changeML,statusml,preciomlold, preciomlnew,statusold,statusmlold,cantidadold,cantidadnew) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, prodNew.getAsin());
            st.setFloat(2, prodOld.getPrecio());
            st.setFloat(3, prodOld.getReviews());
            st.setFloat(4, prodNew.getPrecio());
            st.setFloat(5, prodNew.getReviews());
            st.setString(6, prodNew.getStatus());
            st.setString(7, prodNew.getDisponible());
            st.setString(8, prodNew.getProveedor());
            st.setString(9, prodNew.getMlCode());
            st.setBoolean(10, prodNew.getChanged());
            st.setString(11, prodNew.getStatusML());
            st.setFloat(12, prodOld.getPrecioML());
            st.setFloat(13, prodNew.getPrecioML());
            st.setString(14, prodOld.getStatus());
            st.setString(15, prodOld.getStatusML());

            if (prodOld.getCantidad() != null) {
                st.setInt(16, prodOld.getCantidad().intValue());
            } else {
                st.setNull(16, java.sql.Types.INTEGER);
            }

            if (prodNew.getCantidad() != null) {
                st.setInt(17, prodNew.getCantidad().intValue());
            } else {
                st.setNull(17, java.sql.Types.INTEGER);
            }
            st.executeUpdate();

            System.out.println("SE ACTUALIZO " + prodNew.getMlCode());
            st.close();
            conn.close();
            result = true;
        } catch (Exception ex) {
            System.out.println("Error Creando Atributos Historico d Precios del Producto " + prodOld.getAsin());
        }
        return result;
    }

    public List<HistoricoPrecio> BuscarAsin(String asin) {
        List<HistoricoPrecio> lista = null;

        String query = "SELECT * FROM " + tabla + " WHERE asin = ?";
        try {

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            lista = new ArrayList<HistoricoPrecio>();

            while (rs.next()) {

                Producto producto_viejo = new Producto();
                producto_viejo.setAsin(rs.getString("asin"));
                producto_viejo.setPrecio(rs.getFloat("precioold"));
                producto_viejo.setReviews(rs.getFloat("reviewold"));
                producto_viejo.setDisponible(rs.getString("disponibilidad"));
                producto_viejo.setProveedor(rs.getString("proveedor"));
                producto_viejo.setMlCode(rs.getString("mlcode"));
                producto_viejo.setChanged(rs.getBoolean("changeML"));
                producto_viejo.setStatusML(rs.getString("statusml"));
                producto_viejo.setPrecioML(rs.getFloat("preciomlold"));
                producto_viejo.setStatus(rs.getString("statusold"));
                producto_viejo.setStatusML(rs.getString("statusmlold"));
                producto_viejo.setCantidad(rs.getBigDecimal("cantidadold"));
                producto_viejo.setFechacrea(rs.getString("fechacrea"));

                Producto producto_nuevo = new Producto();
                producto_nuevo.setAsin(rs.getString("asin"));
                producto_nuevo.setPrecio(rs.getFloat("precionew"));
                producto_nuevo.setReviews(rs.getFloat("reviewnew"));
                producto_nuevo.setDisponible(rs.getString("disponibilidad"));
                producto_nuevo.setProveedor(rs.getString("proveedor"));
                producto_nuevo.setMlCode(rs.getString("mlcode"));
                producto_nuevo.setChanged(rs.getBoolean("changeML"));
                producto_nuevo.setStatusML(rs.getString("statusml"));
                producto_nuevo.setPrecioML(rs.getFloat("preciomlnew"));
                producto_nuevo.setStatus(rs.getString("statusnew"));
                producto_nuevo.setStatusML(rs.getString("statusmlold"));
                producto_nuevo.setCantidad(rs.getBigDecimal("cantidadnew"));
                producto_nuevo.setFechacrea(rs.getString("fechacrea"));

                lista.add(
                        new HistoricoPrecio(
                                rs.getInt("id"),
                                producto_viejo,
                                producto_nuevo
                        )
                );
                System.out.println("paso");

            }
            rs.close();
            st.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("BuscarAsin Ex: " + ex.getMessage());
        }

        return lista;
    }
}
