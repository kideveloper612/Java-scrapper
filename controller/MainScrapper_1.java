/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.util.Configura;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * @author casa
 */
public class MainScrapper_1 {
     static Logger logger = Logger.getLogger(MainScrapperProcess.class.getName());
    public static void main(String ... a){
            MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
            Configura configura= Configura.getInstance();
            /*
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            logger.info("========================================================================");
            logger.info("Comienza el SCRAPPER de VARIANTES" + configura.getMaximoConsulta() + " PRODUCTOS");
            logger.info("========================================================================");
            executor.scheduleWithFixedDelay(new ScrapperVariantes(), 1 , configura.getTiempoEspera(),TimeUnit.MINUTES);
            
            
            List<String> asins=connectDB.getProductosxProcesar();
            ExecuteVarianteScrapper exe=new ExecuteVarianteScrapper(asins);
            exe.run();
            */
            //List<String> asins=getProductosAProcesar(configura.getMaximoConsulta());
            //ScrapperProcess procesa=new ScrapperProcess(asins);
           
           /* 
           ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
           logger.info("========================================================================");
           logger.info("Comienza el SCRAPPER de NUEVOS" + configura.getMaximoConsulta() + " PRODUCTOS");
           logger.info("========================================================================");
           executor.scheduleWithFixedDelay(new ScrapperProcess(), 1 , configura.getTiempoEspera(),TimeUnit.MINUTES); 
           */

            
            ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(1);
            int nro=configura.getMaximoUpdate();
            executor2.scheduleWithFixedDelay(new AgentePrice(), 1 , configura.getTiempoUpdate(),TimeUnit.MINUTES); 
            logger.info("========================================================================");
            logger.info("Comienza el SCRAPPER de AGENTE DE PRECIOS CON " + configura.getMaximoUpdate() + " PRODUCTOS");
            logger.info("========================================================================");
           /**/ 
            
    }
}
