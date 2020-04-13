/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;
import com.scrapper.timer.ExecuteScrapper;
import com.scrapper.util.Configura;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dlunago
 */
public class ScrapperProcess  extends TimerTask {
    Logger logger = Logger.getLogger(ScrapperProcess.class.getName());
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    List<String> asins=new ArrayList<String>();
    Configura configura= Configura.getInstance();

    
         public  List<String> getProductosAProcesar(int max){
        try {
            MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
            logger.info("========================================================================");
            logger.info("Obteniendo lista de PRODUCTOS NUEVOS a Procesar");
            logger.info("========================================================================");

            //Handler handler = new FileHandler("scrapper.log");
            //logger.addHandler(handler);
            List<String> urls=connectDB.getNuevosProductos(max);
            
            logger.info("========================================================================");
            logger.info(urls.toString());
            logger.info("========================================================================");
            return urls;
        } catch (Exception ex) {
            Logger.getLogger(ScrapperProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    

    @Override
    public void run() {
        try{
            List<Producto> productos=new ArrayList<Producto>();
            Timer t=new Timer();
            int procesados=0;
            int tiempo=(int)(60/configura.getProductosxMinuto());
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(asins.size());
            List<TimerTask> tareas=new ArrayList<TimerTask>();
           
                                 System.out.println("==============================================================");
        System.out.println("LISTA DE ASINS A PROCESAR");
            logger.info("========================================================================");
            logger.info("Obteniendo lista de PRODUCTOS NUEVOS a Procesar");
            logger.info("========================================================================");

            //Handler handler = new FileHandler("scrapper.log");
            //logger.addHandler(handler);
            List<String> urls=connectDB.getNuevosProductos(configura.getMaximoConsulta());
            urls.forEach(new Consumer<String>() {
                public void accept(final String name) {
                  System.out.println(name);
                }
              });
            System.out.println("==============================================================");
           /* List<String> variantes=connectDB.getProductosVariantes();
            urls.addAll(variantes);*/
            ExecuteScrapper tarea=new ExecuteScrapper(urls);
            tarea.run();

        } catch (Exception ex) {
            Logger.getLogger(ScrapperProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
