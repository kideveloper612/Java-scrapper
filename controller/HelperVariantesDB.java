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
public class HelperVariantesDB 
{      
    Producto producto=null;
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    Logger logger = Logger.getLogger(HelperVariantesDB.class.getName());

     public HelperVariantesDB(Producto prod){
         this.producto=prod;
        

        try
        { 
            // Displaying the thread that is running 
            //System.out.println ("Thread " + 
            //      Thread.currentThread().getId() + 
            //      " is running"); 
            System.out.println("=======================================================================");
            logger.info("Iniciando guardado en Base de Datos Producto " + producto.getASIN());
            connectDB.addVariante(producto);
           
            logger.info("Finaliza el gaurdado del Producto" + producto.getASIN());
            System.out.println("=======================================================================");
            connectDB.updateVariante(producto.getASIN());
            
  
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Error Guardando en BD ASIN= " + prod.getASIN() ); 
           e.printStackTrace();
        } 
    } 
    
}
