/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.timer;

import com.scrapper.controller.HelperDB;
import com.scrapper.controller.ProcesaScrapper;
import com.scrapper.controller.ProductoScrapper;
import com.scrapper.controller.ScrapperProcess;
import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.ml.MercadoLibreAPI;
import com.scrapper.modelo.Producto;
import com.scrapper.util.Agents;
import static com.scrapper.util.Agents.agentes;
import static com.scrapper.util.Agents.proxys;
import com.scrapper.util.ProductoNoExisteException;
// en donde esta esta libreria?
//import com.sun.webkit.Timer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author dlunago
 */
public class ExecuteVarianteScrapper extends TimerTask implements Agents{
    Logger logger = Logger.getLogger(ExecuteVarianteScrapper.class.getName());
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    private String url;
   private static int problemas=0;
    private Document doc=null;
    int procesados=0;
    String asin;
    List<String> asins=new ArrayList<String>();
    String token="";
    public ExecuteVarianteScrapper(List<String> asins){
            //this.asin=url;
            this.asins=asins;
            System.out.println("==============================================================");
            logger.info("PROCESANDO EN EL EXECUTESCRAPPER EL ASIN=" + asins.toString());
            System.out.println("==============================================================");
       
    }
    @Override
    public void run() {
                    procesados=0;
                   for(String asin:asins){
                    try{
                        long startTime = System.currentTimeMillis();
                        Random randomGeneratorIP = new Random();
                        int randomIntIP = randomGeneratorIP.nextInt(proxys.length);
                        Random randomGenerator = new Random();
                        int randomInt = randomGenerator.nextInt(agentes.length);
                        String agenteProxy=agentes[randomInt];
                        //String asin=url.substring(url.indexOf("/dp/")+4,url.indexOf("/dp/")+14);
                                logger.info("Revisando el producto " + asin + " inicio="+startTime);
                                String urlNew="https://api.scraperapi.com?key=47c09d9f388bb8f99a2d2c950b4b157d&url=https://www.amazon.com.mx/dp/" +asin;
                                 doc = Jsoup.connect(urlNew)
                                .userAgent(agenteProxy)//agentes[randomInt])
                                .referrer("http://www.google.com")
                                .get();
                        logger.info("Procesando " + asin);
                        problemas=0;
                        Producto producto=new Producto();
                        if(doc!=null && existeData()){
                            getPrecioVar(asin);
                                
                        }
                        Thread.sleep(2000);
                    }catch(Exception ex){
                       ex.printStackTrace();
                       logger.info("Ocurrió un error procesando " + asin);
                        /*if(connectDB.updateProducto(asin)){
                                 logger.info("Producto NO PROCESADO y actualizado " + asin);
                            }*/
                        productoNoProcesado(asin, "Ocurrió un error");
                    }
                    procesados++;
                  }
                               if(procesados==0){
                logger.info("NO SE ENCONTRARON PRODUCTOS PARA PROCESAR");
            }else{
                logger.info("SE ENCONTRARON "+ procesados + " PRODUCTOS");
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
    
    public boolean existeData(){
        if((doc.html().indexOf("Sorry! We couldn't find that page. Try searching or go to Amazon's home")!=-1) || (doc.html().indexOf("No disponible por el momento")!=-1)){
            return false;
        }else{
            return true;
        }
    }
    
 
    public List<String> getVariantesASIN(){
       
        List<String> variantes=new ArrayList<String>();
        Elements scriptElements = doc.getElementsByTag("script");
            for (Element element :scriptElements ){                
                   for (DataNode node : element.dataNodes()) {
                       String value=node.getWholeData();
                       if(value.indexOf("asinToDimensionIndexMap")>0){
                           String map=value.substring(value.indexOf("asinToDimensionIndexMap"),value.indexOf("prioritizeReqPrefetch"));
                           map=map.substring(map.indexOf("{"),map.indexOf("}"));
                           String[] asins=map.split("\"");
                           int i=0;
                           for(String asin : asins){
                                i++;
                               if(i%2==0)
                                variantes.add(asin);
                           }
                       }
                   }
             
            }
            return variantes;
    }
    
    
     public static  List<String> getProductosAProcesar(){
        try {
            MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
          
            List<String> urls=connectDB.getProductosxProcesar();
           
            return urls;
        } catch (Exception ex) {
            Logger.getLogger(ScrapperProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
     public String getDisponibilidad(){
        String disponible="";
        Element information =doc.getElementById("availability"); //doc.getElementsByClass("a-size-medium a-color-success");//
        if(information!=null){
          Element info=information;//.get(0);
          disponible=info.text();
        }
        return disponible;

    }
     
     
     public String getPrecioVar(String asin) throws Exception{
            Element price=doc.getElementById("priceblock_ourprice");
            String precio="0.0";
            if(price!=null){
                precio=price.text();
                String disponibilidad=getDisponibilidad();
                if(disponibilidad!="" && disponibilidad.indexOf("Disponible")==0){
                    if(precio!=null && precio.indexOf("$")==0){
                        List<String> variantes=getVariantesASIN();
                         if(variantes.size()>0){
                                    for(String asinValue:variantes){
                                        if(!asin.equals(asinValue)){
                                            //connectDB.agregaVariante(asin,asinValue);
                                            System.out.println("PRIN=" +  asin + " == VARIANTE "+asinValue);
                                        }
                                    }
                                    throw new Exception("Producto tiene Variantes, se procesará luego con todas sus variantes");
                                }else{
                                    precio=precio.substring(1,precio.length());
                                      precio=precio.replace(",", "");
                                }
                        
                      
                      
                    }
                }else{
                    precio="0.0";
                }
            }else{
                Elements precios=doc.getElementsByClass("a-color-price");
                if(precios!=null){
                    precio=precios.get(0).text();
                    precio=precio.substring(1,precio.length());
                    precio=precio.replace(",", "");

                    
                }else
                    precio="0.0";
            }
           
            return precio;
    }
     
 /*  public static void main(String ... a){
        List<String> asins=getProductosAProcesar();
        ExecuteVarianteScrapper exe=new ExecuteVarianteScrapper(asins);
        exe.run();
    }*/
    
}

