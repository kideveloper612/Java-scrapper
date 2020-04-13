/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.timer;

import com.scrapper.controller.ScraperAmazon;
import com.scrapper.controller.HelperDB2;
import com.scrapper.controller.ScrapperProcess;
import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.ml.MercadoLibreAPI;
import com.scrapper.modelo.NoProcesadoModelo;
import com.scrapper.modelo.ProductoInicialModelo;
import com.scrapper.modelo.ProductosModelo;
import com.scrapper.objetos.NoProcesado;
import com.scrapper.objetos.Producto;
import com.scrapper.objetos.ProductoInicial;
import com.scrapper.util.Agents;
import com.scrapper.util.ProductoNoExisteException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dlunago
 */
public class ExecuteScrapper2 extends TimerTask implements Agents{
    Logger logger = Logger.getLogger(ExecuteScrapper2.class.getName());
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    private String url;
    private static int problemas=0;
    
    private ScraperAmazon scraper_amazon;
    
    int procesados=0;
    String asin;
    List<String> asins=new ArrayList<String>();
    MercadoLibreAPI ML_API;
    
    private ProductosModelo producto_modelo;
    private ProductoInicialModelo producto_inicial_modelo;
    private NoProcesadoModelo no_procesado_modelo;
    private ProductoInicial   producto_inicial;
    
    public ExecuteScrapper2(List<String> asins){
        producto_modelo         = new ProductosModelo();
        no_procesado_modelo     = new NoProcesadoModelo();
        producto_inicial_modelo = new ProductoInicialModelo();
        this.asins=asins;
            
        System.out.println("==============================================================");
        logger.info("PROCESANDO EN EL EXECUTESCRAPPER EL ASIN=" + asins.toString());
        System.out.println("==============================================================");
        ML_API =new MercadoLibreAPI();
    }
    /**
     * este metodo busca cada asin en amazon y procede a extraer los datos 
     * del .html retornado por el proxy
     **/
    @Override
    public void run() {
        procesados=0;
        HelperDB2 helper;
        Producto  producto;
        
        for(String asin:asins){
            // verificar si el producto ya esta resgistrado
            boolean existeProducto=producto_modelo.ExisteProducto(asin);
            if(!existeProducto){
                // El producto no esta resgistrado
                try{
                    long startTime = System.currentTimeMillis();
                    logger.info("Revisando el producto " + asin + " inicio="+startTime + " THREAD ID:" + Thread.currentThread().getId());
                    
                    scraper_amazon= new ScraperAmazon(asin);
                    scraper_amazon.PantallaUno();
                    logger.info("Procesando " + asin + " THREAD ID:" + Thread.currentThread().getId());
                    problemas=0;
                    // la respuesta es correcta
                    if (scraper_amazon.existeData( 1)){
                        // recuperar producto inicial
                        producto_inicial=producto_inicial_modelo.BuscarAsin(asin);
                        // crear nuevo producto
                        producto= scraper_amazon.getProducto();
                        // actualizar producto con producto inicial
                        producto.setTitulo(producto_inicial.getTitulo());
                        producto.setDescripcion(producto_inicial.getDescripcion());
                        
                        System.out.println(producto);
                        // tiempos
                        long finTime = System.currentTimeMillis();
                        long totalTiempo=(finTime-startTime)/1000;
                        //            
                        logger.info(
                            "Producto obtenido por comple"
                            + "to " + totalTiempo + " segundos"
                        );
                        // Verificar si el producto es variante o no
                        if(producto.getTipo().equals("SIMPLE")){
                            
                            helper=new HelperDB2(producto);
                            // actualizar estado en base de datos
                            if(producto_inicial_modelo.EstadoProcesado(asin)){
                                logger.info("Producto procesado y actualizado " + asin + " THREAD ID:" + Thread.currentThread().getId());
                            }
                            // agregar producto a mercado libre
                            Producto mlProd=ML_API.addProducto(producto);
                            if(mlProd != null){
                                System.out.println("El producto fue enviado a ML");
                                producto_modelo.updateMLProducto(asin, mlProd.getMlCode(), mlProd.getPrecioML());
                            }else{
                                System.out.println("El producto no fue enviado a ML");
                                producto_modelo.updateMLProducto(asin, null, 0);
                            }                            
                        }else{
                            // es variante
                            // ya que no se cargo a mercado libre
                            System.out.println(
                                "El producto no fue enviado a ML"+
                                "\n El producto es Variate y aun no hay soporte"
                            );                            
                            producto_inicial_modelo.EstadoProcesado(asin);
                            new NoProcesadoModelo()
                                .Crear(
                                    new NoProcesado(
                                        -1,
                                        asin,
                                        "El producto es Variate y aun no hay soporte",
                                        ""
                                    )
                                );
                        }
                    }else{
                        // verificar el tipo de respuesta
                        if(scraper_amazon.getCodigoRespuesta()==404){
                            //no encontro el producto
                            productoNoExiste(asin);
                            if(producto_inicial_modelo.EstadoProcesado(asin)){
                                logger.info("Producto No Existe " + asin + " THREAD ID:" + Thread.currentThread().getId());
                            }                        
                        }else{
                            ProductoInicialModelo.processProcesado(2, asin);
                            no_procesado_modelo.Crear(new NoProcesado(
                                -1,
                                asin,
                                "Scraper Amazon no logro retornar el producto",
                                ""
                                )     
                            );
                            logger.info("Producto No procesado " + asin + " THREAD ID:" + Thread.currentThread().getId());
                        }

                    }    
                }catch(Exception ex){
                    ProductoInicialModelo.processProcesado(2, asin);
                    logger.info("Ocurrió un error procesando " + asin + " THREAD ID:" + Thread.currentThread().getId());
                    productoNoProcesado(asin, "Ocurrió un error");
                }
                procesados++;                           
            }else{
                // El producto ya esta registrado
                if(producto_inicial_modelo.EstadoProcesado(asin))
                    logger.info("PRODUCTO YA EXISTE [" + asin +"]" + " THREAD ID:" + Thread.currentThread().getId());
                else
                    logger.info("Imposible cambiar el estado en productos_inicial" + " THREAD ID:" + Thread.currentThread().getId());
            }
        }
        
        if(procesados==0){
            logger.info("NO SE PROCESO NINGUN PRODUCTOS" + " THREAD ID:" + Thread.currentThread().getId());
        }else{
                logger.info("SE PROCESARON "+ procesados + " PRODUCTOS" + " THREAD ID:" + Thread.currentThread().getId());
        }
    }

    private void productoNoExiste(String asin) throws ProductoNoExisteException {
        connectDB.noExisteASIN(asin);
        logger.info("============= Producto No Existe " + asin +" =================="); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void productoNoProcesado(String asin, String motivo){
        connectDB.noProcesadoASIN(asin, motivo);
        logger.info("============= Producto No Existe " + asin +" =================="); //To change body of generated methods, choose Tools | Templates.
    }  
    
         
    public static  List<String> getProductosAProcesar(int max){
        try {
            MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
          
            List<String> urls=connectDB.getNuevosProductos(max);
           
            return urls;
        } catch (Exception ex) {
            Logger.getLogger(ScrapperProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static  List<String> getProductosAProcesarVar(){
        try {
            MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
          
            List<String> urls=connectDB.getNuevosProductosVar();
           
            return urls;
        } catch (Exception ex) {
            Logger.getLogger(ScrapperProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
     
    public static void main(String ... a){
        List<String> asins=getProductosAProcesarVar();
        ExecuteScrapper2 exe=new ExecuteScrapper2(asins);
        exe.run();
    }
    
}
