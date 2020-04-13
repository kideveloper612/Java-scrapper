/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.modelo.ProductoInicialModelo;
import com.scrapper.util.Configura;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * @author casa
 */
public class MainScrapperProcess {

    static Logger logger = Logger.getLogger(MainScrapperProcess.class.getName());

    public static void main(String... a) {
        String version = "2.0.7";
        Configura configura = Configura.getInstance();
        logger.info("===================     Process v-" + version + " ===========================");
        logger.info("========================================================================");
        logger.info("Comienza el SCRAPPER PROCESS con : " + configura.getMaximoConsulta() + " PRODUCTOS");
        logger.info("========================================================================");
        ProductoInicialModelo.startProcess();

        //ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        //executor.scheduleWithFixedDelay(new ScrapperProcess2(), 1, configura.getTiempoEspera(), TimeUnit.MINUTES);
        for (int i = 1; i <= configura.getThreads_process(); i++) {
            Thread t = new Thread(new ScrapperProcess2());
            t.start();
        }

        logger.info("========================================================================");
    }
}
