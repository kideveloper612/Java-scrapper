/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;
import com.scrapper.util.Agents;
import com.scrapper.util.Proxy;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author casa
 */
public class ProductoScrapper extends Timer  implements Agents{
    private static int problemas=0;
    private Document doc=null;
        MySQLConnectionFactory connectDB=new MySQLConnectionFactory();

    Logger logger = Logger.getLogger(ProductoScrapper.class.getName());
    String ipProxy="";
    String portProxy="";
    String agenteProxy="";
    String asinProd="";
    String urlProd="";

    public void getJson(){
        try {
            String sURL = "http://falcon.proxyrotator.com:51337/?apiKey=y8PN7cDmLp95WKZMtrYge4GnFf3JUkAQ"; //just a string
            
            // Connect to the URL using java's native library
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();
            
            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            ipProxy= rootobj.get("ip").getAsString();
            portProxy=rootobj.get("port").getAsString();
            agenteProxy=rootobj.get("randomUserAgent").getAsString();
            }catch(SocketException ex){
             Logger.getLogger(ProductoScrapper.class.getName()).log(Level.SEVERE, null, ex);
            logger.info("ERROR CON EL PROXI " + ipProxy +" Puerto="+portProxy);
        
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProductoScrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProductoScrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(ProductoScrapper.class.getName()).log(Level.SEVERE, null, ex);
            logger.info("ERROR CON EL PROXI " + ipProxy +" Puerto="+portProxy);
        }catch(Exception ex){
            Logger.getLogger(ProductoScrapper.class.getName()).log(Level.SEVERE, null, ex);
            logger.info("ERROR INESPERADO CON EL PROXI " + ipProxy +" Puerto="+portProxy);
        }
    }
    public ProductoScrapper(String asin)throws HttpStatusException {
        try {
            asinProd=asin;
            // uso del proxy con la libreria http client de apache 
            Proxy proxy=new Proxy();
            doc= proxy.Consulta(
                "https://www.amazon.com.mx/gp/offer-listing/"+asin
            );
            logger.info("Procesando " + asin);
            problemas=0;
        }catch(HttpStatusException ex){
            logger.info("Problemas Leyendo la Pagina");
            problemas++;
            
            /*if(problemas==5){
                System.exit(0);
            }*/
        } catch (IOException ex) {
            Logger.getLogger(ProductoScrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
  
    public void procesa(){
          Producto producto=new Producto();
                        producto.setTitulo(getTitulo());
                        producto.setBullets(getBullets());
                        producto.setImagenes(getImagenes());
                        producto.setPrecio(Float.parseFloat(getPrecio()));
                        if(producto.getPrecio()>0)
                            producto.setStatus("ACTIVO");
                        else
                            producto.setStatus("INACTIVO");
                        producto.setCategoria(getCategoria());
                        producto.setSubcategoria(getSubcategoria());
                        producto.setDescripcion(getDescripcion());
                        producto.setASIN(asinProd);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ProcesaScrapper.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        producto.setGetDetallesBullet(getDetallesBullet());
                        producto.setGetInformacionTecnica(getInformacionTecnica());
                        long finTime = System.currentTimeMillis();
                        logger.info("Producto obtenido por completo " + asinProd);

                        //HelperDB helper=new HelperDB(producto);
                        //helper.start();
                       
                        if(connectDB.updateProducto(urlProd)){
                             logger.info("Producto procesado y actualizado " + asinProd);
                        }
    }
    
    public boolean existeProducto(){
        if(doc.html().indexOf("Sorry! We couldn't find that page. Try searching or go to Amazon's home")!=-1){
            return false;
        }else{
            return true;
        }
    }
    
    public String getTitulo(){
        try{
            String title=doc.getElementById("productTitle").text();
            return title;
        }catch(Exception e){
            return "";
        }
    }
    public String getCategoria(){
         try{
            String categoria=doc.getElementById("searchDropdownBox").html();
            categoria=categoria.substring(categoria.indexOf("selected"),categoria.indexOf("selected")+150);
            String cat=categoria.substring(categoria.indexOf(">")+1,categoria.indexOf("</option>"));
            return cat;
         }catch(Exception e){
            return "";
        }
    }
    
    public String getSubcategoria() {
        try{
            String subcategoria=doc.getElementById("wayfinding-breadcrumbs_feature_div").text();
            return subcategoria;    
        }catch(Exception e){
            return "";
        }
    }
    
    public float getReviews() { 
              Element reviews2 = doc.getElementById("averageCustomerReviews");
             String reviews3=reviews2.text();
             reviews3=reviews3.substring(0,reviews3.indexOf(" "));
             System.out.println("REVIEW="+reviews3);
             return Float.parseFloat(reviews3);
    }
    
    public String getDescripcion(){
        try{
            String html=doc.html();
            int inicio2=html.indexOf("iframeContent");
            int finalText=html.lastIndexOf("onloadCallback"); 
            if(inicio2>0 && finalText>0){
                html=html.substring(inicio2,finalText);
                html=html.substring(html.lastIndexOf("productDescriptionWrapper"),html.indexOf("\n"));
                html=html.substring(html.indexOf("%0A"),html.lastIndexOf("%3Cdiv"));
                html=html.substring(html.indexOf("%0A")+4,html.lastIndexOf("%0A")-4);
                html=html.substring(0,html.lastIndexOf("%0A"));
                html=html.replaceAll("%20", " ");
                html=html.replaceAll("%C3%A1", "á");
                html=html.replaceAll("%C3%B1", "ñ");
                html=html.replaceAll("%C3%B3", "ó");
                html=html.replaceAll("%2C", "");
                html=html.replaceAll("%C3%BA", "ú");
                html=html.replaceAll("%C3%AD", "í");
                html=html.replaceAll("%23", "#");
                html=html.replaceAll("%28", "(");
                html=html.replaceAll("%29", ")");
                html=html.replaceAll("%2F", "/"); 
                html=html.replaceAll("%C3%A9", "é");
                html=html.replaceAll("%C2", "-");
                html=html.replaceAll("%BF", "¿");
                html=html.replaceAll("%3F", "?");
                html=html.replaceAll("%C2%A0", " ");
                html=html.replaceAll("%3Cbr%3E", " ");
                html=html.replaceAll("%BF %3F?", " ");
                html=html.replaceAll("%22", "\"");
                html=html.replaceAll("%3Cb", " ");
                html=html.replaceAll("%3C/b ", " ");
                html=html.replaceAll("%C2¿", " ");
                html=html.replaceAll("%27", "'");
                html=html.replaceAll("%A0", " ");
                html=html.replaceAll("%3Cbr", " ");
                html=html.replaceAll("%3E", " ");
                html=html.replaceAll("%3B", " ");
                html=html.replaceAll("%A1", " ");
                html=html.replaceAll("%A0", " ");
                html=html.replaceAll("r/", "");    
                html=html.substring(8,html.length());
                html=html.replaceAll("%3A", ":");
                html=html.replaceAll("%3Cp%3E%3Cp%3E", "\n");
                html=html.trim();
            }
            if(html.indexOf("<!doctype html>")!=-1)
                html="";
            System.out.println("DESCRIPCION="+html);
            return html;
        }catch(Exception ex){
            return "";
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
    
    
    
    public String getPrecio(){
            Elements precios=doc.getElementsByClass("a-color-price");
            String precio="0.0";
            try{
                        List<String> variantes=new ArrayList<String>();
                precio=precios.get(0).text();
                if(precio.indexOf("$")==0){
                    precio=precio.substring(1,precio.length());
                     if(precio.indexOf("-")>0){
                        System.out.println("Producto tiene variantes");
                        variantes=getVariantesASIN();
                        if(variantes.size()>0){
                          /*  for(String asinValue:variantes){
                                connectDB.agregaVariante(asinProd,asinValue);
                            }
                            throw new Exception("Producto tiene Variantes, se procesará luego con todas sus variantes");
                       */ }else{
                            Element variantesProducto=doc.getElementById("twister_feature_div");
                            System.out.println(variantesProducto.html());
                            Elements opciones=variantesProducto.getElementsByTag("li");
                            if(opciones.size()>0){
                                for(Element element:opciones){
                                    System.out.println(element.getElementsByAttribute("data-defaultasin"));
                                }
                            }else{
                               Element opciones2=doc.getElementById("variation_size_name");
                                for (DataNode node : opciones2.dataNodes()) {
                                String value=node.getWholeData();
                                if(value.indexOf("option")>0){
                                    System.out.println(value);
                                }
                   }
                               System.out.println(opciones2.html());
                               opciones=opciones2.getElementsByTag("option");
                               if(opciones.size()>0){
                                 for(Element element:opciones){
                                    System.out.println(element.getElementsByAttribute("value"));
                                 }
                               }
                            }
                        }
                     }
                     precio=precio.replace(",", "");
                }else if(precio.equals("No disponible por el momento.") || precio.isEmpty()){
                    precio="-1";
                }
            }catch(Exception ex){
                     if(ex.getMessage().indexOf("No disponible por el momento.")>=0){
                    precio="-1";
                }else if(ex.getMessage().equals("Producto tiene Variantes, se procesará luego con todas sus variantes")){
                    precio="VAR";
               }else{
                    precio="0";
                }
                connectDB.updateProducto(urlProd);

            }                       
            
            return precio;
    }
    
    public List<String> getImagenes(){
        List<String> imagenes= new ArrayList<String>();
        try{
            Element imagenId = doc.getElementById("altImages");
            if(imagenId!=null){
                 Elements elements=imagenId.getElementsByTag("IMG");
                 int cantidad=elements.size();
                 for(int i=0;i<cantidad-1;i++)
                 {
                    String urlImag=elements.get(i).attr("src");
                    String urlImag2=urlImag.substring(0,urlImag.indexOf("._"));
                    String urlImag3=urlImag.substring(urlImag.lastIndexOf("."),urlImag.length());  
                    urlImag=urlImag2+urlImag3;
                    imagenes.add(urlImag);
                 }
           }
        }catch(Exception e){
            
        }
       return imagenes;
    }
    
    public List<String> getBullets(){
        List<String> bullets= new ArrayList<String>();
        try{
            String bullets2=doc.getElementById("feature-bullets").outerHtml();
            String [] bulletsValue=bullets2.split("<li>");
            int fila=0;
            for(String valor:bulletsValue){
                if(fila>0 && fila<bulletsValue.length){
                    String bulletx=limpiaBullet(valor);
                    bullets.add(bulletx);
                }
                fila++;
            }
        }catch(Exception e){
        
        }
        return bullets;
    }
    
    private String limpiaBullet(String valor) {
        String bullet=valor.substring(valor.indexOf(">")+1,valor.indexOf("</span>")-1);
        return bullet;
    }
    
    public String getDisponibilidad(){
        String disponible="";
        Element information =doc.getElementById("availability"); //doc.getElementsByClass("a-size-medium a-color-success");//
        if(information!=null){
          Element info=information;//.get(0);
          disponible=info.text();
          System.out.println("HTML="+info.html());
          System.out.println("OUTERHTML="+info.outerHtml());
        }
        System.out.println("DISPONIBILIDAD="+disponible);
        return disponible;

    }
    public Map<String,String> getDetallesBullet(){
        Map<String, String> map = new HashMap<String, String>();
        Element information = doc.getElementById("productDetails_detailBullets_sections1");
        if(information==null)
            information = doc.getElementById("detail-bullets");
        else{
            Elements lineas=information.getElementsByTag("th");
            Elements lineas2=information.getElementsByTag("td");
            if(lineas!=null){
                for(int i=0;i<lineas.size();i++)
                    {
                        String atributo=lineas.get(i).text();
                        atributo.replaceAll("\"", "''");
                        atributo.replaceAll("'", "");
                        String valor=lineas2.get(i).text();
                        valor.replaceAll("\"", "''");
                        valor.replaceAll("'", "");
                        if (atributo.equals("Fabricante")||atributo.equals("Marca")){
                                     //prod.setMarca(valor);
                        }
                        if(atributo.equals("Número de modelo del producto")){
                                     //prod.setModelo(valor);
                        }
                        map.put(atributo, valor);
                    }
            }
            }
            return map;
    }
    
    public Map<String,String> getInformacionTecnica(){
        Map<String, String> map = new HashMap<String, String>();
        Element informationTech = doc.getElementById("prodDetails");
             if(informationTech!=null){
                    Elements lineas=informationTech.getElementsByClass("label");
                    Elements lineas2=informationTech.getElementsByClass("value");
                    if(lineas!=null){
                      for(int i=0;i<lineas.size();i++){
                                 String atributo=lineas.get(i).text();
                                 atributo.replaceAll("\"", "''");
                                 atributo.replaceAll("'", "");
                                 String valor=lineas2.get(i).text();
                                 valor.replaceAll("\"", "''");
                                 valor.replaceAll("'", "");
                                 if (atributo.equals("Fabricante")||atributo.equals("Marca")){
                                    // prod.setMarca(valor);
                                 }
                                 if(atributo.equals("Número de modelo del producto") || atributo.equals("Modelo") ){
                                    // prod.setModelo(valor);
                                 }
                                map.put(atributo, valor);                             
                      }

                    
                    }
              }
                                 return map;

    }
    
    /**
     * 
     * Este metodo retorna la cantidad de productos disponibles del
     * proveerdor, para ello busca la informacion en el documento
     * retornado por la consulta a la API de AMAZON.
     */
    public BigDecimal obtenerCantidadPantalla1() {
    	
        BigDecimal cantidad =null;	
        
        if(doc!=null){
            Elements selects = doc.getElementsByAttributeValue("name","quantity");
            for(Element element : selects) {
                
                Elements opciones = element.getElementsByTag("option");
                Element valor = opciones.get(opciones.size()-1);
                cantidad = new BigDecimal(valor.text().trim());
                break;
            }
        }    
   	 
   	return cantidad;
   }
    
 /*   public static void main(String a[]) throws HttpStatusException{
        ProductoScrapper p=new ProductoScrapper("B000A5CLAW");
        p.getDisponibilidad();
        p.getDescripcion();
        p.getReviews();
    }*/
}
