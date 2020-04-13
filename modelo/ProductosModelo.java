/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.Producto;
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
public class ProductosModelo extends Modelo {

    private ImagenesModelo imagen_modelo;

    public ProductosModelo() {
        tabla = "productos2";
        imagen_modelo = new ImagenesModelo();
    }

    public Producto BuscarAsin(String asin) {
        Producto result = null;

        String query = "SELECT * FROM " + tabla + " WHERE asin = ?";

        try {
            //System.out.println("ASIN:"+asin);
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                //System.out.println("rs = " + rs);
                result = new Producto(
                        rs.getInt("id"),
                        rs.getString("asin"),
                        rs.getString("sku"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("categoria"),
                        rs.getString("subcategoria"),
                        rs.getString("proveedor"),
                        rs.getString("disponible"),
                        rs.getString("status"),
                        rs.getString("mlcode"),
                        rs.getString("statusml"),
                        rs.getFloat("precio"),
                        rs.getFloat("precioml"),
                        rs.getFloat("reviews"),
                        rs.getBigDecimal("cantidad"),
                        rs.getFloat("shipping"),
                        rs.getFloat("preciomlnew"),
                        rs.getString("fechacrea"),
                        rs.getString("fechaupdate")
                );
                //System.out.println("Producto = " + result);
            }
            rs.close();
            st.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("BuscarAsin Ex: " + ex.getMessage());
        }

        return result;
    }

    public boolean ExisteProducto(String asin) {
        boolean result = false;
        String query = "SELECT asin FROM " + tabla + " WHERE asin=?";
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, asin);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String asinProd = rs.getString("asin");
                result = true;
                //break;
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("ExisteProducto Ex: " + e.getMessage());
        }
        return result;
    }

    public boolean updateMLProducto(String asin, String mlCode, float precio) {
        boolean result = false;

        //String query = "update "+tabla+" set  precioml=?, mlcode=?,statusml='ACTIVE' where asin=?";
        String query = "update " + tabla + " set  precioml=?, mlcode=? where asin=?";
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setFloat(1, precio);
            st.setString(2, mlCode);
            st.setString(3, asin);

            st.executeUpdate();
            result = true;
            st.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Error Modificando Precio Producto " + asin);
        }
        return result;
    }

    public boolean Crear(Producto prod) {
        boolean result = false;
        String query = "insert into productos2 (asin, titulo,descripcion,proveedor,disponible,cantidad,marca,modelo,precio,categoria,subcategoria,reviews,status,statusml) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {

            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, prod.getAsin());
            st.setString(2, prod.getTitulo());
            st.setString(3, prod.getDescripcion());
            st.setString(4, prod.getProveedor());
            st.setString(5, prod.getDisponible());
            st.setBigDecimal(6, prod.getCantidad());
            st.setString(7, prod.getMarca());
            st.setString(8, prod.getModelo());
            st.setFloat(9, prod.getPrecio());
            st.setString(10, prod.getCategoria());
            st.setString(11, prod.getSubCategoria());
            st.setFloat(12, prod.getReviews());
            st.setString(13, prod.getStatus());
            st.setString(14, prod.getStatusML());
            st.executeUpdate();
            st.close();
            conn.close();
            result = true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Error Creando Producto " + prod.getAsin() + e.getMessage());
        }
        return result;
    }

    public static synchronized List<String> Buscar(int cantidad) {

        List<String> asins = null;
        String query = "SELECT asin FROM productos2 WHERE procesado=2 ORDER BY fechaupdate asc LIMIT " + cantidad;
        //System.out.println("query = " + query);

        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            asins = new ArrayList<String>();
            while (rs.next()) {
                String asin = rs.getString("asin");
                asins.add(asin);

                ProductosModelo.agentesProcesado(1, asin);
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Error Buscando Productos para el Update ");
            asins = null;
        }
        return asins;
    }

    public boolean updateProducto(Producto prod) {
        boolean result = false;
        String query = "update " + tabla + " set precio=?, reviews=?, status=?, disponible=?, proveedor=?, precioml=?, statusml=?, shipping=?, cantidad=? where asin=?";

        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setFloat(1, prod.getPrecio());
            st.setFloat(2, prod.getReviews());
            st.setString(3, prod.getStatus());
            st.setString(4, prod.getDisponible());
            st.setString(5, prod.getProveedor());
            st.setFloat(6, prod.getPrecioML());
            st.setString(7, prod.getStatusML());
            st.setFloat(8, prod.getShipping());

            if (prod.getCantidad() != null) {
                st.setInt(9, prod.getCantidad().intValue());
            } else {
                st.setNull(9, java.sql.Types.INTEGER);
            }
            st.setString(10, prod.getAsin());

            st.executeUpdate();
            st.close();
            conn.close();
            result = true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Error Modificando Precio Producto " + prod.getAsin());
        }
        return result;
    }

    /*
     Actualiza productos2, procesado=2 para todos los registros
     */
    public static boolean startAgentes() {
        String query = "UPDATE productos2 SET procesado=2";
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
    public static boolean agentesProcesado(int status, String asin) {
        String query = "UPDATE productos2 SET procesado=? WHERE asin=?";
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
