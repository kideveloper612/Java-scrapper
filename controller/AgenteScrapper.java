/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author casa
 */
public class AgenteScrapper {
    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AgenteScrapper.class);
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();

    public void getProductosProcesar(){
        logger.info("Obteniendo lista de productos a Procesar");
        List<String> asins=connectDB.getProductosInactivosxProcesar();//connectDB.getProductosxProcesar();
        for(String asin : asins){   
                connectDB.creaProductosAgente(asin);
        }
        int procesados=0;
        for(String asin : asins){
                logger.info("Seleccionando Productos a Actualizar");
                String urlNew = "https://www.amazon.com.mx/dp/"+asin;
                long startTime = System.currentTimeMillis();
                Producto producto=null;
                try{
                    producto=new Producto();
                    producto.setASIN(asin);
                    ProductoScrapper scrapper;
                        scrapper=new ProductoScrapper(urlNew);
                        producto.setPrecio(Float.parseFloat(scrapper.getPrecio()));
                         producto.setReviews(scrapper.getReviews());
                         producto.setDisponible(scrapper.getDisponibilidad());
                        long finTime = System.currentTimeMillis();
                        long totalTiempo=(finTime-startTime)/1000;
                        logger.info("Producto obtenido por completo " + totalTiempo);
                        procesados++;

                    
                   
                } catch (Exception ex) {
                        Logger.getLogger(AgenteScrapper.class.getName()).log(Level.SEVERE, null, ex);
                        producto.setStatus("INACTIVO");
                        if(ex.getMessage().indexOf("No disponible por el momento.")>=0){
                            producto.setPrecio(-1.0f);
                        }else{
                            producto.setPrecio(0.0f);
                        }
                }
                    HelperAgentDB helper=new HelperAgentDB(producto);
                    helper.start();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AgenteScrapper.class.getName()).log(Level.SEVERE, null, ex);
                    }
               
        }
        if(procesados==0){
            logger.info("NO SE ENCONTRARON PRODUCTOS PARA PROCESAR");

        }else{
            logger.info("SE ACTUALIZARON "+ procesados + " PRODUCTOS");

        }
    }
    
    /*public static void main(String a[]){
        AgenteScrapper scrapper=new AgenteScrapper();
        scrapper.getProductosProcesar();
    }*/
}
