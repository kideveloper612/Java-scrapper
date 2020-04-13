/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.db;

//import com.scrapper.modelo.Categoria;
import com.scrapper.modelo.Producto;
import java.util.List;

public interface ConnectionDBFactory {
    public List<String> getNuevosProductosVar();
    public List<String> getProductosUpdateMLInactivos(int cantidad);
    public List<String> getProductosInactivosxProcesar();
    public boolean noProcesadoVAR(String asin, String motivo);
    public boolean updateVariante(String asin);
    public boolean addVariante(Producto prod);
    public List<String> getProductosVariantes();
    public boolean updateProductoMLSKU(String sku, String ml);
    public boolean updateMLProducto(String asin, String mlCode,float precio);
    public boolean updateMLData(String asin, String json,String res);

    public boolean updateProductoMLInicial(String asin, String sku, String ml);
    public boolean agregaVariante(String parentAsin, String asin,String labels, String valores);
    public Producto getProducto(String asin);
    public boolean guardaHistoricoPrecio(Producto prodOld, Producto prodNew);
    public List<String> getProductosUpdate(int cantidad);

    public boolean noProcesadoASIN(String asin, String motivo);
    public boolean noExisteASIN(String asin);
    public List<Integer> getConfiguracion();
    //Para el agente
    public List<String> getProductosxProcesar();
    public boolean updatePrecioProducto(Producto prod);
    public boolean creaProductosAgente(String asin);
     public boolean updatePorProcesar(Producto prod);
    //Queries para el manejo de la tabla de Productos Iniciales
    public boolean creaProductoInit(String asin, String url);

    public List<String> getNuevosProductos(int cantidad);
     public boolean updateProducto(String url);
    public boolean existeProducto(String asin);
    
    //Queries para guardar toda la información de los productos
    
    public boolean addProducto(Producto prod);
    
    public boolean addImagenes(String asin, String url);
    
    public boolean addBullets(String asin, String bullet);
    
    public boolean addAtributos(String asin, String atributo, String valor);
    public Producto getProductoNew(String asin);

}
