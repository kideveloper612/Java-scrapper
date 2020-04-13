/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;

/**
 *
 * @author casa
 */
public class HelperAgentDB extends Thread 
{      
    Producto producto=null;
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HelperAgentDB.class);

     public HelperAgentDB(Producto prod){
         this.producto=prod;
     }
    public void run() 
    { 
        try
        { 
            // Displaying the thread that is running 
            System.out.println ("Thread " + 
                  Thread.currentThread().getId() + 
                  " is running"); 
             logger.info("Iniciando update en Base de Datos Producto " + producto.getASIN());
            connectDB.updatePrecioProducto(producto);
            connectDB.updatePorProcesar(producto);
            logger.info("Finaliza el gaurdado del Producto" + producto.getASIN());

            
  
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Exception is caught " ); 
           e.printStackTrace();
        } 
    } 
    
}
