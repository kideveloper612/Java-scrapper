package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;
import com.scrapper.timer.ExecuteScrapper2;
import com.scrapper.util.Configura;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dlunago
 */
public class ScrapperProcess2 extends TimerTask {

    private Logger logger;
    private MySQLConnectionFactory connectDB;
    private List<String> asins;
    private Configura configura;

    public ScrapperProcess2() {

        logger = Logger.getLogger(ScrapperProcess2.class.getName());
        connectDB = new MySQLConnectionFactory();
        asins = new ArrayList<String>();
        configura = Configura.getInstance();

    }

    public List<String> getProductosAProcesar(int max) {
        try {
            MySQLConnectionFactory connectDB = new MySQLConnectionFactory();
            logger.info("========================================================================");
            logger.info("Obteniendo lista de PRODUCTOS NUEVOS a Procesar");
            logger.info("========================================================================");

            //Handler handler = new FileHandler("scrapper.log");
            //logger.addHandler(handler);
            List<String> urls = connectDB.getNuevosProductos2(max);

            logger.info("========================================================================");
            logger.info(urls.toString());
            logger.info("========================================================================");
            return urls;
        } catch (Exception ex) {
            Logger.getLogger(ScrapperProcess2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void run() {
        try {
            List<Producto> productos = new ArrayList<Producto>();
            logger.info("RUNNING THREAD ID:" + Thread.currentThread().getId());

            System.out.println("==============================================================");
            System.out.println("LISTA DE ASINS A PROCESAR");
            logger.info("========================================================================");
            logger.info("Obteniendo lista de PRODUCTOS NUEVOS a Procesar");
            logger.info("========================================================================");
            //System.out.println("configura: " +configura.toString());

            List<String> urls = connectDB.getNuevosProductos2(configura.getMaximoConsulta());
            while(urls.size()>0){
                urls.forEach(new Consumer<String>() {
                    public void accept(final String name) {
                        System.out.println(name);
                    }
                });
                System.out.println("==============================================================");
                /* List<String> variantes=connectDB.getProductosVariantes();
                 urls.addAll(variantes);*/
                ExecuteScrapper2 tarea = new ExecuteScrapper2(urls);
                tarea.run();
                urls = connectDB.getNuevosProductos2(configura.getMaximoConsulta());
            }

        } catch (Exception ex) {
            Logger.getLogger(ScrapperProcess2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
