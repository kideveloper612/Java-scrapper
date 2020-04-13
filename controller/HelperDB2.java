/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.BulletsModelo;
import com.scrapper.modelo.ImagenesModelo;
import com.scrapper.modelo.ProductosModelo;
import com.scrapper.objetos.Producto;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author casa
 */
public class HelperDB2 
{      
    private Producto producto=null;
    private MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    private Logger logger = Logger.getLogger(HelperDB2.class.getName());
    
    private ProductosModelo producto_modelo= new ProductosModelo();
    private ImagenesModelo  imagenModelo= new ImagenesModelo();
    private BulletsModelo   bulletsModelo= new BulletsModelo();
    
    public HelperDB2(Producto prod){
        this.producto=prod;
        
        try
        { 
            System.out.println("=======================================================================");
            logger.info("Iniciando guardado en Base de Datos Producto " + producto.getAsin());
            producto_modelo.Crear(producto);
            List<String> imagenes= producto.getImagenes();
            if(imagenes != null)
                for(String imagen: imagenes){
                    imagenModelo.Crear(producto.getAsin(), imagen);
                }
            List<String> bullets=producto.getBullets();
            if(bullets != null)
            for(String bullet: bullets){
                bulletsModelo.Crear(producto.getAsin(), bullet);
            }
            producto.getGetInformacionTecnica().forEach((k,v)->connectDB.addAtributos2(producto.getAsin(),k,v));
            producto.getGetDetallesBullet().forEach((k,v)->connectDB.addAtributos2(producto.getAsin(),k,v));
            logger.info("Finaliza el gaurdado del Producto" + producto.getAsin());
            System.out.println("=======================================================================");
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Error Guardando en BD ASIN= " + prod.getAsin() ); 
           e.printStackTrace();
        } 
    } 
    
}
