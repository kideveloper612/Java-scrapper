/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.db;

import com.scrapper.modelo.ProductoInicialModelo;
import com.scrapper.objetos.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class MySQLConnectionFactory implements ConnectionDBFactory {

    Logger logger = Logger.getLogger(MySQLConnectionFactory.class.getName());

    @Override
    public boolean updateProductoMLSKU(String sku, String ml) {
        String query = "update productos set mlcode=? where sku=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setString(2, sku);
            st2.setString(1, ml);
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + sku);
            return false;
        }
    }

    @Override
    public boolean updateProductoMLInicial(String asin, String sku, String ml) {
        String query = "update productos set sku=?, mlcode=? where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setString(1, sku);
            st2.setString(2, ml);
            st2.setString(3, asin);
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + asin);
            return false;
        }
    }

    @Override
    public boolean existeProducto(String asin) {
        String query = "select asin from productos where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, asin);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asinProd = rs.getString("asin");
                rs.close();
                statement.close();
                conn.close();
                return true;
            }
            rs.close();
            statement.close();
            conn.close();
            return false;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Buscando Producto ");

            return false;
        }
    }

    public boolean existeProducto2(String asin) {
        String query = "select asin from productos2 where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, asin);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asinProd = rs.getString("asin");
                rs.close();
                statement.close();
                conn.close();
                return true;
            }
            rs.close();
            statement.close();
            conn.close();
            return false;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Buscando Producto ");

            return false;
        }
    }

    @Override
    public List<Integer> getConfiguracion() {
        List<Integer> configura = new ArrayList<Integer>();
        String query = "select * from configura";
        //System.out.println("query: "+query);
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int valor1 = rs.getInt("maximoconsulta");
                int valor2 = rs.getInt("productosxminuto");
                int valor3 = rs.getInt("minutosespera");
                int valor4 = rs.getInt("tiempoespera");
                int valor5 = rs.getInt("tiempoupdate");
                int valor6 = rs.getInt("maximoupdate");
                int valor7 = rs.getInt("threads_process");
                int valor8 = rs.getInt("threads_agentes");

//                                                System.out.println(
//                                                    "\n valor1 :"+valor1+
//                                                    "\n valor2 :"+valor2+
//                                                    "\n valor3 :"+valor3+
//                                                    "\n valor4 :"+valor4+        
//                                                    "\n valor5 :"+valor5+
//                                                    "\n valor6 :"+valor6+    
//                                                    "\n valor7 :"+valor7+    
//                                                    "\n valor8 :"+valor8    
//                                                );
                configura.add(valor1);
                configura.add(valor2);
                configura.add(valor3);
                configura.add(valor4);
                configura.add(valor6);
                configura.add(valor5);
                configura.add(valor7);
                configura.add(valor8);
                //break;            
            }
            rs.close();
            statement.close();
            conn.close();
            return configura;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Error Buscando Valores de Configuracion");

            return null;
        }
    }

    @Override
    public boolean agregaVariante(String parentAsin, String asin, String labels, String valores) {
        String query = "INSERT INTO `variantes`(`asinparent`,`asin`, labels,valores,`procesado`) VALUES (?,?,?,?,0)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, parentAsin);
            statement.setString(2, asin);
            statement.setString(3, labels);
            statement.setString(4, valores);

            try {
                statement.execute();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Producto repetido  en VARIANTE" + asin);
                statement.close();
                conn.close();
                return false;

            }
            statement.close();
            conn.close();
            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Producto repetido  en VARIANTE" + asin);
        }
        return false;
    }

    @Override
    public boolean noProcesadoASIN(String asin, String motivo) {
        String query = "INSERT INTO noprocesado(asin,motivo) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, asin);
            statement.setString(2, motivo);
            try {
                statement.execute();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Producto repetido " + asin);
                statement.close();
                conn.close();
                return false;

            }
            statement.close();
            conn.close();
            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Producto repetido " + asin);
        }
        return false;

    }

    @Override
    public boolean noProcesadoVAR(String asin, String motivo) {
        String query = "INSERT INTO noprocesadovar(asin,motivo) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, asin);
            statement.setString(2, motivo);
            try {
                statement.execute();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Producto repetido " + asin);
                statement.close();
                conn.close();
                return false;

            }
            statement.close();
            conn.close();
            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Producto repetido " + asin);
        }
        return false;

    }

    @Override
    public boolean noExisteASIN(String asin) {
        boolean result = false;
        String query = "INSERT INTO noexiste(asin) VALUES (?)";
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            try {
                st.execute();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Producto repetido " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            result = true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Producto repetido " + asin);
        }
        return result;
    }

    public boolean noExisteASIN2(String asin) {
        String query = "INSERT INTO `noexiste2`(`asin`) VALUES (?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, asin);
            try {
                statement.execute();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Producto repetido " + asin);
                statement.close();
                conn.close();
                return false;

            }
            statement.close();
            conn.close();
            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Producto repetido " + asin);
        }
        return false;

    }

    public boolean updatePorProcesar(Producto prod) {
        String query = "update porprocesar set procesado=?  where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setBoolean(1, prod.getStatus().equals("ACTIVO") ? true : false);
            st2.setString(2, prod.getAsin());
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Modificando Producto " + prod.getAsin());

            return false;
        }
    }

    @Override
    public boolean updateVariante(String asin) {
        String query = "update variantes set procesado=1  where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setString(1, asin);
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Modificando Producto " + asin);

            return false;
        }
    }

    @Override
    public boolean creaProductosAgente(String asin) {
        String query = "INSERT INTO `porprocesar`(`asin`, `procesado`) VALUES (?,0)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, asin);
            try {
                statement.execute();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Producto repetido " + asin);
                statement.close();
                conn.close();
                return false;

            }
            statement.close();
            conn.close();
            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Producto repetido " + asin);
        }
        return false;

    }

    public boolean updatePrecioProducto(Producto prod) {
        String query = "update productos set precio=?, reviews=?, status=?, disponible=?, proveedor=?, precioml=?, statusml=?, shipping=? where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setFloat(1, prod.getPrecio());
            st2.setFloat(2, prod.getReviews());
            st2.setString(3, prod.getStatus());
            st2.setString(4, prod.getDisponible());
            st2.setString(5, prod.getProveedor());
            st2.setFloat(6, prod.getPrecioML());
            st2.setString(7, prod.getStatusML());
            st2.setFloat(8, prod.getShipping());

            st2.setString(9, prod.getAsin());

            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + prod.getAsin());
            return false;
        }
    }

    public boolean updatePrecioProductoNew(Producto prod, Producto prodNew) {
        String query = "update productos set precio=?, reviews=?, status=?, disponible=?, proveedor=?, precioml=?, preciomlnew=?, statusml=?, shipping=? where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setFloat(1, prod.getPrecio());
            st2.setFloat(2, prod.getReviews());
            st2.setString(3, prod.getStatus());
            st2.setString(4, prod.getDisponible());
            st2.setString(5, prod.getProveedor());
            st2.setFloat(6, prod.getPrecioML());
            st2.setFloat(7, prodNew.getPrecioML());
            st2.setString(8, prod.getStatusML());
            st2.setFloat(9, prod.getShipping());

            st2.setString(10, prod.getAsin());

            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + prod.getAsin());
            return false;
        }
    }

    @Override
    public List<String> getProductosxProcesar() {
        List<String> productos = new ArrayList<String>();
        String query = "select asin from productos  order by fechaupdate asc";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asin = rs.getString("asin");
                productos.add(asin);
            }
            rs.close();
            statement.close();
            conn.close();
            return productos;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Buscando Productos a Procesar ");

            return null;
        }
    }

    @Override
    public List<String> getProductosInactivosxProcesar() {
        List<String> productos = new ArrayList<String>();
        String query = "select asin from productos where status='ACTIVO' and statusml='INACTIVO' order by fechaupdate asc";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asin = rs.getString("asin");
                productos.add(asin);
            }
            rs.close();
            statement.close();
            conn.close();
            return productos;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Buscando Productos a Procesar ");

            return null;
        }
    }

    @Override
    public boolean updateProducto(String asin) {
        String query = "update producto_inicial set  procesado=1  where asin=?";
        System.out.println(query + " " + asin);
////        if(url.indexOf("/dp/")>=0){
        //                   String asin=url.substring(url.indexOf("/dp/")+4,url.indexOf("/dp/")+14);
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setString(1, asin);
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Error Modificando Producto " + asin);

            return false;
        }
        /*}else{
         return false;
         }*/

    }

    public boolean updateProducto2(String asin) {
        String query = "update producto_inicial set  procesado2=1  where asin=?";
        System.out.println(query + " " + asin);
////        if(url.indexOf("/dp/")>=0){
        //                   String asin=url.substring(url.indexOf("/dp/")+4,url.indexOf("/dp/")+14);
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setString(1, asin);
            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Error Modificando Producto " + asin);

            return false;
        }
        /*}else{
         return false;
         }*/

    }

    @Override
    public List<String> getNuevosProductos(int cantidad) {
        List<String> productos = new ArrayList<String>();
        String query = "select url,asin from producto_inicial where procesado=0 order by fechaupdate desc limit " + cantidad;
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String url = rs.getString("url");
                String asin = rs.getString("asin");
                productos.add(asin);
            }
            rs.close();
            statement.close();
            conn.close();
            return productos;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Agregando Nuevos Productos ");

            return null;
        }
    }

    public synchronized List<String> getNuevosProductos2(int cantidad) {

        List<String> productos = null;
        String query = "SELECT url,asin FROM producto_inicial WHERE procesado2=2 ORDER BY fechaupdate DESC LIMIT " + cantidad;
        //System.out.println("sql"+query);        
        try {
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            productos = new ArrayList<String>();
            while (rs.next()) {
                String url = rs.getString("url");
                String asin = rs.getString("asin");
                productos.add(asin);

                ProductoInicialModelo.processProcesado(1, asin);
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Agregando Nuevos Productos ");
        }
        return productos;
    }

    @Override
    public List<String> getProductosVariantes() {
        List<String> productos = new ArrayList<String>();
        String query = "select asin from variantes where procesado=0";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asin = rs.getString("asin");
                productos.add(asin);
            }
            rs.close();
            statement.close();
            conn.close();
            return productos;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Agregando Nuevos Productos ");

            return null;
        }
    }

    @Override
    public boolean creaProductoInit(String asin, String url) {
        String query = "INSERT INTO `producto_inicial`(`asin`, `url`) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, asin);
            statement.setString(2, url);

            try {
                statement.execute();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Producto_Inicial ");
                statement.close();
                conn.close();
                return false;

            }
            statement.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Creando Producto_Inicial ");

        }
        return false;

    }

    public boolean addProducto(Producto prod) {
        System.out.println(prod);
        String query = "insert into productos (asin, titulo,descripcion,marca,modelo,precio,categoria,subcategoria,reviews,status) values(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, prod.getAsin());
            st.setString(2, prod.getTitulo());
            st.setString(3, prod.getDescripcion());
            st.setFloat(6, prod.getPrecio());
            st.setString(4, prod.getMarca());
            st.setString(5, prod.getModelo());
            st.setString(7, prod.getCategoria());
            st.setString(8, prod.getSubCategoria());
            st.setFloat(9, prod.getReviews());
            st.setString(10, prod.getStatus());
            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Producto " + prod.getAsin());
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Error Creando Producto " + prod.getAsin());
            return false;
        }
    }

    public boolean addProducto2(Producto prod) {
        System.out.println(prod);
        String query = "insert into productos2 (asin, titulo,descripcion,marca,modelo,precio,categoria,subcategoria,reviews,status) values(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, prod.getAsin());
            st.setString(2, prod.getTitulo());
            st.setString(3, prod.getDescripcion());
            st.setFloat(6, prod.getPrecio());
            st.setString(4, prod.getMarca());
            st.setString(5, prod.getModelo());
            st.setString(7, prod.getCategoria());
            st.setString(8, prod.getSubCategoria());
            st.setFloat(9, prod.getReviews());
            st.setString(10, prod.getStatus());
            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Producto " + prod.getAsin());
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Error Creando Producto " + prod.getAsin());
            return false;
        }
    }

    public boolean addVariante(Producto prod) {
        System.out.println(prod);
        String query = "insert into variantesdata (asin, titulo,descripcion,marca,modelo,precio,categoria,subcategoria,reviews,status,url) values(?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, prod.getAsin());
            st.setString(2, prod.getTitulo());
            st.setString(3, prod.getDescripcion());
            st.setFloat(6, prod.getPrecio());
            st.setString(4, prod.getMarca());
            st.setString(5, prod.getModelo());
            st.setString(7, prod.getCategoria());
            st.setString(8, prod.getSubCategoria());
            st.setFloat(9, prod.getReviews());
            st.setString(10, prod.getStatus());
            st.setString(11, prod.getUrl());

            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Producto " + prod.getAsin());
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("Error Creando Producto " + prod.getAsin());
            return false;
        }
    }

    @Override
    public boolean addImagenes(String asin, String url) {
        String query = "insert into imagenes (asin,url) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            st.setString(2, url);
            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Imagen " + url + " Producto " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Imagen " + url + " Producto " + asin);
            return false;
        }
    }

    public boolean addImagenes2(String asin, String url) {
        String query = "insert into imagenes2 (asin,url) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            st.setString(2, url);
            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Imagen " + url + " Producto " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Imagen " + url + " Producto " + asin);
            return false;
        }
    }

    @Override
    public boolean addBullets(String asin, String url) {
        String query = "insert into bullets (asin,bullet) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            st.setString(2, url);
            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Bullet " + url + " Producto " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Bullet " + url + " Producto " + asin);
            return false;
        }
    }

    public boolean addBullets2(String asin, String url) {
        String query = "insert into bullets2 (asin,bullet) VALUES (?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            st.setString(2, url);
            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Bullet " + url + " Producto " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Bullet " + url + " Producto " + asin);
            return false;
        }
    }

    @Override
    public boolean addAtributos(String asin, String atributo, String valor) {
        String query = "insert into atributos (asin,atributo,valor) VALUES (?,?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            st.setString(2, atributo);
            st.setString(3, valor);

            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Atributos " + atributo + " Producto " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Atributos " + atributo + " Producto " + asin);

            return false;
        }
    }

    public boolean addAtributos2(String asin, String atributo, String valor) {
        String query = "insert into atributos2 (asin,atributo,valor) VALUES (?,?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            st.setString(2, atributo);
            st.setString(3, valor);

            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Atributos " + atributo + " Producto " + asin);
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Atributos " + atributo + " Producto " + asin);

            return false;
        }
    }

    public Producto getProductoAsin(String asin) {
        String query = "select * from productos where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            Producto prod = new Producto();
            if (rs.next()) {
                String url = rs.getString("asin");
                float precio = rs.getFloat("precio");
                float precioMlNew = rs.getFloat("preciomlnew");
                float review = rs.getFloat("reviews");
                String mlCode = rs.getString("mlcode");
                String status = rs.getString("status");
                float shipping = rs.getFloat("shipping");

                prod.setAsin(asin);
                prod.setPrecio(precio);
                prod.setReviews(review);
                prod.setMlCode(mlCode);
                prod.setStatus(status);
                prod.setShipping(shipping);
                prod.setPrecioMLNew(precioMlNew);
                //break;
            }
            rs.close();
            st.close();
            conn.close();
            return prod;
        } catch (Exception ex) {
            return null;
        }
    }

    public Producto getProducto2(String asin) {
        String query = "select * from productos2 where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            Producto prod = new Producto();
            if (rs.next()) {
                String url = rs.getString("asin");
                float precio = rs.getFloat("precio");
                float precioMlNew = rs.getFloat("preciomlnew");
                float review = rs.getFloat("reviews");
                String mlCode = rs.getString("mlcode");
                String status = rs.getString("status");
                float shipping = rs.getFloat("shipping");

                prod.setAsin(asin);
                prod.setPrecio(precio);
                prod.setReviews(review);
                prod.setMlCode(mlCode);
                prod.setStatus(status);
                prod.setShipping(shipping);
                prod.setPrecioMLNew(precioMlNew);
                //break;
            }
            rs.close();
            st.close();
            conn.close();
            return prod;
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean guardaHistoricoPrecio(Producto prodOld, Producto prodNew) {
        String query = "insert into historicoprecios "
                + "(asin,precioold,reviewold,precionew,reviewnew,statusnew,disponibilidad,proveedor,"
                + "mlcode,changeML,statusml,preciomlold, preciomlnew,statusold,statusmlold) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
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

            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Atributos Historico d Precios del Producto " + prodOld.getAsin());
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            System.out.println("SE ACTUALIZO " + prodNew.getMlCode());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Atributos Historico d Precios del Producto " + prodOld.getAsin());

            return false;
        }
    }

    public boolean guardaHistoricoPrecioNew(Producto prodOld, Producto prodNew, Producto prodNew2) {
        String query = "insert into historicoprecios "
                + "(asin,precioold,reviewold,precionew,reviewnew,statusnew,disponibilidad,proveedor,"
                + "mlcode,changeML,statusml,preciomlold, preciomlnew,statusold,statusmlold, nuevomlpreciomlold, nuevomlpreciomlnew) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
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
            st.setFloat(16, prodOld.getPrecioMLNew());
            st.setFloat(17, prodNew2.getPrecioML());

            try {
                st.executeUpdate();

            } catch (SQLException ee) {
                ee.printStackTrace();
                logger.info("Error Creando Atributos Historico d Precios del Producto " + prodOld.getAsin());
                st.close();
                conn.close();
                return false;

            }
            st.close();
            conn.close();
            System.out.println("SE ACTUALIZO " + prodNew.getMlCode());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Error Creando Atributos Historico d Precios del Producto " + prodOld.getAsin());

            return false;
        }
    }

    @Override
    public List<String> getProductosUpdate(int cantidad) {
        List<String> productos = new ArrayList<String>();
        String query = "select asin from productos order by fechaupdate asc limit " + cantidad;
        System.out.println("SQL=" + query);
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asin = rs.getString("asin");
                productos.add(asin);
            }
            rs.close();
            statement.close();
            conn.close();
            System.out.println("Se consiguieron " + productos.size());
            return productos;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Buscando Productos para el Update ");

            return null;
        }
    }

    public List<String> getProductosUpdate2(int cantidad) {
        List<String> productos = new ArrayList<String>();
        String query = "select asin from productos2 order by fechaupdate asc limit " + cantidad;
        System.out.println("SQL=" + query);
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asin = rs.getString("asin");
                productos.add(asin);
            }
            rs.close();
            statement.close();
            conn.close();
            System.out.println("Se consiguieron " + productos.size());
            return productos;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Buscando Productos para el Update ");

            return null;
        }
    }

    @Override
    public List<String> getProductosUpdateMLInactivos(int cantidad) {
        List<String> productos = new ArrayList<String>();
        String query = "select asin from productos where statusml='INACTIVO' order by fechaupdate asc limit " + cantidad;
        System.out.println("SQL=" + query);
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asin = rs.getString("asin");
                productos.add(asin);
            }
            rs.close();
            statement.close();
            conn.close();
            System.out.println("Se consiguieron " + productos.size());
            return productos;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Buscando Productos para el Update ");

            return null;
        }
    }

    public Producto getProductoNewAsin(String asin) {
        Producto prod = null;
        String query = "select * from producto_inicial where asin=? order by fechacrea desc";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st = conn.prepareStatement(query);) {
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            prod = new Producto();
            if (rs.next()) {
                prod.setAsin(rs.getString("asin"));
                prod.setTitulo(rs.getString("titulo"));
                prod.setDescripcion(rs.getString("descripcion"));
                //break;
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception ex) {

        }
        return prod;
    }

    @Override
    public boolean updateMLProducto(String asin, String mlCode, float precio) {
        String query = "update productos set  precioml=?, mlcode=?,statusml='ACTIVE' where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setFloat(1, precio);
            st2.setString(2, mlCode);
            st2.setString(3, asin);

            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + asin);
            return false;
        }
    }

    public boolean updateMLProducto2(String asin, String mlCode, float precio) {
        String query = "update productos2 set  precioml=?, mlcode=?,statusml='ACTIVE' where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setFloat(1, precio);
            st2.setString(2, mlCode);
            st2.setString(3, asin);

            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + asin);
            return false;
        }
    }

    public boolean updateMLProductoNew(String asin, String mlCode, float precio, float precioNew) {
        String query = "update productos set  precioml=?, preciomlnew=?, mlcode=?,statusml='ACTIVE' where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setFloat(1, precio);
            st2.setFloat(2, precioNew);
            st2.setString(3, mlCode);
            st2.setString(4, asin);

            st2.executeUpdate();
            st2.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + asin);
            return false;
        }
    }

    @Override
    public boolean updateMLData(String asin, String json, String res) {
        String query = "update productos set json=?, respuesta=? where asin=?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement st2 = conn.prepareStatement(query);) {
            st2.setString(2, res);
            st2.setString(1, json);
            st2.setString(3, asin);
            st2.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            logger.info("Error Modificando Precio Producto " + asin);
            return false;
        }
    }

    @Override
    public List<String> getNuevosProductosVar() {
        String query = "select asinparent from variantesxprocesar where procesado=0 and id>308";
        List<String> productos = new ArrayList<String>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement(query);) {

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String asinProd = rs.getString("asinparent");
                productos.add(asinProd);
            }
            rs.close();
            statement.close();
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error Buscando Producto ");

        }
        return productos;
    }

    @Override
    public boolean addVariante(com.scrapper.modelo.Producto prod) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public com.scrapper.modelo.Producto getProducto(String asin) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean guardaHistoricoPrecio(com.scrapper.modelo.Producto prodOld, com.scrapper.modelo.Producto prodNew) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updatePrecioProducto(com.scrapper.modelo.Producto prod) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updatePorProcesar(com.scrapper.modelo.Producto prod) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addProducto(com.scrapper.modelo.Producto prod) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public com.scrapper.modelo.Producto getProductoNew(String asin) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
