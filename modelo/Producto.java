/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import lombok.Data;
@Data 
public class Producto {
    private int id;
    private float shipping;
    private String ASIN;
    private String titulo="";
    private String descripcion="";
    private float precio;
    private float precioML;
    private String categoria;
    private String subcategoria;
    private String marca="";
    private String modelo="";
    private float reviews=0.0f;
    private String status="ACTIVO";
    private String statusml="INACTIVO";
    private String url;
    private String disponible="";
    private String proveedor;
    private Timestamp fechaUpdate;
    private List<String> imagenes;
    private List<String> bullets;
    private Map<String,String> getDetallesBullet;
    private Map<String,String> getInformacionTecnica; 
    private String sku;
    private String mlCode;
    private boolean changed;
    private float precioMLNew;
    private BigDecimal cantidad;
    
    @Override
    public String toString(){
        return "[PRODUCTO="+ASIN+"]="+titulo+"-"+precio+"-"+status+"-"+cantidad;
    }
}
