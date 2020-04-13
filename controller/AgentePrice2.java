/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.ml.MercadoLibreAPI;
import com.scrapper.modelo.HistoricoPrecioModelo;
import com.scrapper.modelo.NoProcesadoModelo;
import com.scrapper.modelo.ProductosModelo;
import com.scrapper.objetos.Producto;
import com.scrapper.util.Agents;
import com.scrapper.util.Configura;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 *
 * @author dlunago
 */
public class AgentePrice2 extends TimerTask implements Agents {

    private Logger logger = Logger.getLogger(AgentePrice2.class.getName());

    private int procesados;
    private List<String> asins;

    private Configura configura = Configura.getInstance();
    private Producto producto_bd;      // producto de la base de datos
    private Producto producto_amazon; // producto amazon
    //modelos
    private ProductosModelo producto_modelo;

    private ScraperAmazon scraper_amazon;
    private MercadoLibreAPI ML_API;

    public AgentePrice2() {
        procesados = 0;
        ML_API = new MercadoLibreAPI();
        asins = new ArrayList<String>();
        System.out.println("==============================================================");
        producto_modelo = new ProductosModelo();
        System.out.println("==============================================================");
    }

    @Override
    public void run() {
        logger.info("RUNNING THREAD ID:" + Thread.currentThread().getId());
        boolean actualizar = true;
        procesados = 0;
        //Se usó para solo procesar los INACTIVOS EN ML List<String> asins=connectDB.getProductosUpdateMLInactivos(nro);//new ArrayList<String>();//////
        asins = ProductosModelo.Buscar((int) configura.getMaximoUpdate());//new ArrayList<String>();//////
        while(asins.size()>0){
            if (!ML_API.getToken().isEmpty()) {

                System.out.println("==============================================================");
                logger.info("PROCESANDO EL AGENTE DE PRECIOS CON  EL ASIN=" + asins.toString() + " THREAD ID:" + Thread.currentThread().getId());
                System.out.println("==============================================================");
                for (String asin : asins) {
                    //
                    actualizar = true;

                    System.out.println("==============================================================");
                    logger.info("PROCESANDO EL AGENTE DE PRECIOS CON  EL ASIN=" + asin + " THREAD ID:" + Thread.currentThread().getId());
                    System.out.println("==============================================================");
                    try {

                        long startTime = System.currentTimeMillis();
                        logger.info("Revisando el producto " + asin + " inicio=" + startTime);

                        logger.info("Procesando " + asin + " THREAD ID:" + Thread.currentThread().getId());
                        producto_bd = producto_modelo.BuscarAsin(asin);
                        if (producto_bd != null) {
                            System.out.println("base de datos: " + producto_bd);

                            scraper_amazon = new ScraperAmazon(asin);
                            /* descarga el html de la patalla del producto*/
                            scraper_amazon.PantallaUno();

                            if (scraper_amazon.existeData(1)) {
                                // crear nuevo producto
                                System.out.println("amazon pantalla uno " + scraper_amazon.getProducto());

                                // la pantalla dos es solo para los productos cuya disponibilidad es 
                                //Disponible a través de estos vendedores. 
                                if (scraper_amazon.getProducto().getDisponible()
                                        .equals("Disponible a través de estos vendedores.")) {
                                    scraper_amazon.PantallaDos();
                                    if (scraper_amazon.existeData(2)) {
                                        System.out.println("Amazon pantalla dos: " + scraper_amazon.getProducto());
                                    } else {
                                        actualizar = false;
                                        logger.info("API NO RESPONDE PANTALLA DOS" + " THREAD ID:" + Thread.currentThread().getId());
                                    }
                                } else {
                                    System.out.println("no requiere pantalla dos");
                                }

                                // actualizar producto
                                if (actualizar) {
                                    ActualizarProducto();
                                    long finTime = System.currentTimeMillis();
                                    long totalTiempo = (finTime - startTime) / 1000;
                                    logger.info("Datos de Precio tomados en " + totalTiempo + " segundos" + " THREAD ID:" + Thread.currentThread().getId());
                                } else {
                                    ProductosModelo.agentesProcesado(2, asin);
                                    logger.info("Error al consultar pantalla Dos" + " THREAD ID:" + Thread.currentThread().getId());
                                }
                            } else {
                                actualizar = false;
                                ProductosModelo.agentesProcesado(2, asin);
                                logger.info("API NO RESPONDE PANTALLA UNO" + " THREAD ID:" + Thread.currentThread().getId());
                            }
                        } else {
                            ProductosModelo.agentesProcesado(0, asin);
                            logger.info("NO SE ENCONTRARON PRODUCTOS EN PRODUCTO2" + " THREAD ID:" + Thread.currentThread().getId());
                        }
                    } catch (Exception ex) {
                        ProductosModelo.agentesProcesado(2, asin);
                        new NoProcesadoModelo().Crear(asin, "Error leyendo la página. No se extrajo información");
                        logger.info("Ocurrió un error procesando " + asin + " THREAD ID:" + Thread.currentThread().getId());
                    }
                    procesados++;
                }
                if (procesados == 0) {
                    logger.info("NO SE ENCONTRARON PRODUCTOS PARA PROCESAR"  + " THREAD ID:" + Thread.currentThread().getId());
                } else {
                    logger.info("SE ENCONTRARON " + procesados + " PRODUCTOS" + " THREAD ID:" + Thread.currentThread().getId());
                }
            } else {
                logger.info("Problemas con el TOKEN DE ML" + " THREAD ID:" + Thread.currentThread().getId());
            }
            asins = ProductosModelo.Buscar((int) configura.getMaximoUpdate());
        }
    }

    private void ActualizarProducto() {

        producto_amazon = scraper_amazon.getProducto();
        producto_amazon.setMlCode(producto_bd.getMlCode());

        // validar producto 
        if (producto_amazon != null) {
            // validar si el producto existe en Ml

            if (producto_amazon.getMlCode() != null
                    && producto_amazon.getMlCode().length() > 0) {
                System.out.println("Actualizando producto " + producto_amazon.getAsin());
                producto_amazon.setStatusML();
                //Bruno
                producto_amazon=ML_API.updateProducto(producto_amazon);
                producto_modelo.updateProducto(producto_amazon);
                ProductosModelo.agentesProcesado(0, producto_amazon.getAsin());
                new HistoricoPrecioModelo().Crear(producto_bd, producto_amazon);

            } else {
                System.out.println("Error ML: No se pudo recuperar codigo ML");
            }
        } else {
            System.out.println("Error Amazon: No se pudo recuperar producto");
        }

    }

}
