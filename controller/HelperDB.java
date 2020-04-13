/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;
import com.scrapper.timer.ExecuteScrapper;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author casa
 */
public class HelperDB 
{      
    Producto producto=null;
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    Logger logger = Logger.getLogger(HelperDB.class.getName());

     public HelperDB(Producto prod){
         this.producto=prod;
        

        try
        { 
            System.out.println("=======================================================================");
            logger.info("Iniciando guardado en Base de Datos Producto " + producto.getASIN());
            connectDB.addProducto(producto);
            for(String imagen:producto.getImagenes()){
                connectDB.addImagenes(producto.getASIN(), imagen);
            }
            for(String bullet:producto.getBullets()){
                connectDB.addBullets(producto.getASIN(), bullet);
            }
            producto.getGetInformacionTecnica().forEach((k,v)->connectDB.addAtributos(producto.getASIN(),k,v));
            producto.getGetDetallesBullet().forEach((k,v)->connectDB.addAtributos(producto.getASIN(),k,v));
            logger.info("Finaliza el gaurdado del Producto" + producto.getASIN());
            System.out.println("=======================================================================");
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Error Guardando en BD ASIN= " + prod.getASIN() ); 
           e.printStackTrace();
        } 
    } 
    
}
