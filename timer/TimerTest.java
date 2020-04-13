/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.timer;

import com.scrapper.controller.ProcesaScrapper;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author dlunago
 */
public class TimerTest {
        

    public static void main(String a[]){
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TimerTest.class);
        String urls[]={"4332442","3242343","4353454","64645456","14332442","13242343","14353454","64645456","24332442","23242343","24353454","264645456","34332442","33242343","34353454","364645456"};
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        int procesados=0;
        for(String url : urls){
                try{
                    logger.info("Procesando " + url);
                    TimerExample tarea=new TimerExample(url);
                    int tiempo=60/4;
                    executor.scheduleWithFixedDelay(tarea, 1, tiempo, TimeUnit.SECONDS);
                    procesados++;
                    TimeUnit.SECONDS.sleep(tiempo);
                }catch(Exception ex){
                    logger.info("PRODUCTO NO EXISTE ");
                }
            }
    }
     
}
