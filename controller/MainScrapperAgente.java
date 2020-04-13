/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.modelo.ProductosModelo;
import com.scrapper.util.Configura;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * @author casa
 */
public class MainScrapperAgente {

    static Logger logger = Logger.getLogger(MainScrapperAgente.class.getName());

    public static void main(String... a) {
        String version= "2.0.7";
        Configura configura = Configura.getInstance();
        logger.info("=================== Agente v-"+version+" ==============================");
        //Agente de precio ------ 500 ---------- Cada 30 minutos
        //Productos con Problema ---- Sacarlos todos y que se procese 1 vez al día
        // ScrapperProcess scrapper=new ScrapperProcess();
        // scrapper.run();
        
        ProductosModelo.startAgentes();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        logger.info("========================================================================");
        logger.info("Comienza el SCRAPPER de AGENTE DE PRECIOS CON " + configura.getMaximoUpdate() + " PRODUCTOS, Threads: " + configura.getThreads_agentes());
        logger.info("========================================================================");
        //executor.scheduleWithFixedDelay(new AgentePrice2(), 1 , configura.getTiempoUpdate(),TimeUnit.MINUTES); 
        for(int i=1;i<=configura.getThreads_agentes();i++){
            AgentePrice2 agente=new AgentePrice2();
            Thread t = new Thread(agente);
            t.start();
        }
        //agente.run();
        //agente=new AgentePrice2();
        //t = new Thread(agente);
        //t.start();
        /*executor.scheduleWithFixedDelay(
            new Runnable() {
                @Override
                public void run() {
                    String nombre= Thread.currentThread().getName();
                    System.out.println("Inside : " + nombre);
                    int i=0;
                    while(true){
                        System.out.println("nombre: " + nombre + "{"+ i+"}");
                        i++;
                        if(i==10)
                            break;
                        
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainScrapperAgente.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            },
            2,
            2,//configura.getTiempoUpdate(),
            TimeUnit.SECONDS
        ); */
    }
}
