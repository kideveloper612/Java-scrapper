/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;
import com.scrapper.util.Configura;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author casa
 */
public class ProcesaScrapper  extends TimerTask {
    Logger logger = Logger.getLogger(ProcesaScrapper.class.getName());
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    
    
    public List<String> getProductosAProcesar(int max){
        try {
            logger.info("Obteniendo lista de productos a Procesar");
            Handler handler = new FileHandler("procesascrapper.log");
            logger.addHandler(handler);
            List<String> urls=connectDB.getNuevosProductos(max);
            return urls;
        } catch (IOException ex) {
            Logger.getLogger(ProcesaScrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ProcesaScrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void run() {
        try {

            ProcesaScrapper scrapper=new ProcesaScrapper();
            Configura configura= Configura.getInstance();
            List<String> urls =scrapper.getProductosAProcesar(configura.getMaximoConsulta().intValue());
            logger.info("VOY A PROCESAR " + urls.size() + " PRODUCTOS");
            List<Producto> productos=new ArrayList<Producto>();
            Timer t=new Timer();
            int procesados=0;
            int tiempo=configura.getProductosxMinuto()/4;

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(tiempo);
            for(String url : urls){
                try{
                    logger.info("Procesando " + url);
                    procesados++;
                    TimeUnit.SECONDS.sleep(tiempo);
                }catch(Exception ex){
                    logger.info("PRODUCTO NO EXISTE ");
                }
            }
            if(procesados==0){
                logger.info("NO SE ENCONTRARON PRODUCTOS PARA PROCESAR");
            }else{
                logger.info("SE ENCONTRARON "+ procesados + " PRODUCTOS");
            }
        } catch (SecurityException ex) {
            Logger.getLogger(ProcesaScrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
