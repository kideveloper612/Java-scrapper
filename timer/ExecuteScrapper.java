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
import com.scrapper.mlold.MLCreaProductoOld;
import com.scrapper.modelo.Producto;
import com.scrapper.util.Agents;
import static com.scrapper.util.Agents.agentes;
import static com.scrapper.util.Agents.proxys;
import com.scrapper.util.ProductoNoExisteException;
import com.scrapper.util.Proxy;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
public class ExecuteScrapper extends TimerTask implements Agents{
    Logger logger = Logger.getLogger(ExecuteScrapper.class.getName());
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    private String url;
    private static int problemas=0;
    private Document doc=null;
    int procesados=0;
    String asin;
    List<String> asins=new ArrayList<String>();
    MercadoLibreAPI mlAgent=new MercadoLibreAPI();
    MLCreaProductoOld mlAgentOld=new MLCreaProductoOld();
    String token="";
    String tokenOld="";
    public ExecuteScrapper(List<String> asins){
            //this.asin=url;
            this.asins=asins;
            /*this.asins.clear();
            this.asins.add("B00UY1T5HO");*/
            
            System.out.println("==============================================================");
            logger.info("PROCESANDO EN EL EXECUTESCRAPPER EL ASIN=" + asins.toString());
            System.out.println("==============================================================");
            token=mlAgent.getToken();
            tokenOld=mlAgentOld.getToken();
    }
    @Override
    public void run() {
                    procesados=0;
                   for(String asin:asins){
                    try{
                        long startTime = System.currentTimeMillis();
                        
                        logger.info("Revisando el producto " + asin + " inicio="+startTime);
                        // implementacion del proxy
                        Proxy proxy=new Proxy();
                        doc = proxy.Consulta(
                            "https://www.amazon.com.mx/dp/" +asin
                        );
                                                         
                        logger.info("Procesando " + asin);
                        problemas=0;
                        Producto producto=connectDB.getProductoNew(asin); //procesa(asin);
                        boolean existeProducto=connectDB.existeProducto(asin);
                        if(doc!=null){//&& existeData() && !existeProducto){

                            //producto.setTitulo(getTitulo());
                            producto.setBullets(getBullets());
                            producto.setImagenes(getImagenes());
                            producto.setPrecio(Float.parseFloat(getPrecioVar(asin)));
                            if(producto.getPrecio()>0)
                                producto.setStatus("ACTIVO");
                            else
                                producto.setStatus("INACTIVO");
                            producto.setCategoria(getCategoria());
                            producto.setSubcategoria(getSubcategoria());
                            producto.setProveedor(getProveedor());
                            producto.setDisponible(getDisponibilidad());
                            //producto.setDescripcion(getDescripcion());
                            producto.setASIN(asin);
                            producto.setGetDetallesBullet(getDetallesBullet());
                            producto.setGetInformacionTecnica(getInformacionTecnica());
                            long finTime = System.currentTimeMillis();
                            long totalTiempo=(finTime-startTime)/1000;
                             String descripcionText="Por favor no olvide preguntar por la disponibilidad del producto antes de realizar la compra, un operador lo atenderá lo más pronto posible.";
                            if(producto.getDescripcion()!=null && producto.getDescripcion().indexOf("Por favor no olvide preguntar")<0){
                                            descripcionText=descripcionText + "\n" + producto.getDescripcion();
                            }else{
                                 descripcionText=producto.getDescripcion();
                            }
                            if(producto.getProveedor()!=null  && (producto.getProveedor().equals("Amazon México") || producto.getProveedor().contains("Amazon México"))){
                                 descripcionText=descripcionText + "Envío gratis a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas.(SkyDrop, SpinBox, Redpack, etc.)\n" +
                "El envío no tendrá ningún costo adicional y tomará de 1 a 3 días en llegar a su domicilio. Podrá tomar de 3 a 5 días para algunas zonas rurales.\n" +
                "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.";
                            }else if(producto.getProveedor()!=null && (producto.getProveedor().equals("Amazon EE.UU") || producto.getProveedor().contains("Amazon Estados Unidos") || producto.getProveedor().contains("Amazon EE.UU") || (producto.getProveedor()!=null && producto.getProveedor().equals("Amazon Estados Unidos")))){
                                 descripcionText=descripcionText + "Envío gratuito a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas. (SkyDrop, SpinBox, Redpack, etc.)\n" +
                "Por ser un envío de importación el tiempo estimado de entrega es de 3 a 6 días hábiles dependiendo del despacho aduanal. Podrá tomar de 6 a 9 días para algunas zonas rurales.\n" +
                "Adicionalmente, si requiere del producto antes del tiempo de entrega estimado, puede solicitar un envío express por un cargo adicional de $199 Pesos tras realizar su compra.\n" +
                "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.";
                            } else if (producto.getProveedor()!=null && (producto.getProveedor().contains("Vendido por") || producto.getProveedor().contains("y enviado por Amazon"))){
                             descripcionText=descripcionText + "Envío gratis a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas.(SkyDrop, SpinBox, Redpack, etc.)\n" +
                "El envío no tendrá ningún costo adicional y tomará de 1 a 3 días en llegar a su domicilio. Podrá tomar de 3 a 5 días para algunas zonas rurales.\n" +
                "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.";
                            }else {
                            descripcionText=descripcionText + "Envío gratuito a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas. (SkyDrop, SpinBox, Redpack, etc.)\n" +
                "Por ser un envío de importación el tiempo estimado de entrega es de 6 a 9 días hábiles dependiendo del despacho aduanal. Podrá tomar de 9 a 15 días para algunas zonas rurales.\n" +
                "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.";
                            }
                            producto.setDescripcion(descripcionText);
                            logger.info("Producto obtenido por comple"
                                    + "to " + totalTiempo + " segundos");

                            
                            
                            HelperDB helper=new HelperDB(producto);

                            if(connectDB.updateProducto(asin)){
                                logger.info("Producto procesado y actualizado " + asin);
                            }
                            
                            //Producto mlProd=mlAgent.addProducto(token, producto);
                            Producto mlProd2=mlAgentOld.addProducto(tokenOld, producto);
                            if(mlProd2!=null){
                                connectDB.updateMLProducto(asin, mlProd2.getMlCode(), mlProd2.getPrecioML());
                                //connectDB.updateMLProductoNew(asin, mlProd.getMlCode(), mlProd.getPrecioML(), mlProd2.getPrecioML());
                            }
                        }else{
                            if(connectDB.updateProducto(asin)){
                                 logger.info("Producto procesado NO DISPONIBLE y actualizado " + asin);
                            }
                            if(!existeProducto)
                                productoNoExiste(asin);
                            else
                                logger.info("PRODUCTO YA EXISTE");
                                
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
    
    public String getTitulo(){
        try{
            String title=doc.getElementById("productTitle").text();
            return title;
        }catch(Exception e){
            return "";
        }
    }
    public String getMarca(){
        try{
            String title=doc.getElementById("bylineInfo_feature_div").text();
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
            Element reviews = doc.getElementById("averageCustomerReviews");
            float reviewPoint=0f;
            if(reviews!=null){
                try{
                    String valorReview=reviews.text();
                    valorReview=valorReview.substring(valorReview.indexOf("estrellas")-9,valorReview.indexOf("estrellas")-5);
                    reviewPoint=Float.parseFloat(valorReview.trim());
                }catch(Exception e){
                    reviewPoint=0.0f;
                }
            }
            return reviewPoint;
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
            return html;
        }catch(Exception ex){
            return "";
        }
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
     public String getProveedor(){
        String disponible="";
        Element information =doc.getElementById("merchant-info"); //doc.getElementsByClass("a-size-medium a-color-success");//
        if(information!=null){
          Element info=information;//.get(0);
          disponible=info.text();
        }
        return disponible;

    }
     
    public List<String> getVariantesASIN(String asinParent) throws ParseException{
       
        List<String> variantes=new ArrayList<String>();
        Elements scriptElements = doc.getElementsByTag("script");
            for (Element element :scriptElements ){                
                   for (DataNode node : element.dataNodes()) {
                       String value=node.getWholeData();
                       if(value.indexOf("asinToDimensionIndexMap")>0){
                           String labels=value.substring(value.lastIndexOf("dimensionsDisplay"),value.lastIndexOf("variationDisplayLabels"));
                           labels=labels.substring(labels.indexOf("[")+1,labels.indexOf("]"));
                           labels.replaceAll("\"", "");
                           String labelsValues[]=labels.split(",");
                           String etiquetas="";
                           for(String lab:labelsValues){
                               etiquetas+=lab+",";
                           }
                           System.out.println("LABELS="+etiquetas);
                           String map=value.substring(value.indexOf("dimensionValuesDisplayData"),value.indexOf("prioritizeReqPrefetch"));
                           map=map.substring(map.indexOf("{"),map.indexOf("}")+1);
                           String productos[]=map.split("]");
                           for(int i=0;i<productos.length-1;i++){
                               String parts[]=productos[i].split(":");
                               String asin=parts[0].substring(2,parts[0].length()-1);
                               System.out.println("ASIN="+asin);
                               String valores=parts[1].substring(2,parts[1].length()-1);
                               valores=valores.replaceAll("\"", "");
                               String values[]=valores.split(",");
                               valores="";
                               for(String lab:values){
                                   valores+=lab+",";
                               }
                               valores=valores.substring(0, valores.length()-1);
                               System.out.println("VALORES="+valores);
                               connectDB.agregaVariante(asinParent, asin, labels, valores);
                               connectDB.updateVariante(asinParent);
                               variantes.add(asin);
                           }
                        
                       }
                   }
             
            }
            return variantes;
    }
    
    
    
    public String getPrecio() throws Exception{
            Element price=doc.getElementById("priceblock_ourprice");
            String precio="0.0";
            if(price!=null){
                precio=price.text();
                String disponibilidad=getDisponibilidad();
                if(disponibilidad!="" && disponibilidad.indexOf("Disponible")==0){
                    if(precio!=null && precio.indexOf("$")==0){
                        precio=precio.substring(1,precio.length());
                        if(precio.indexOf("-")>0){
                                System.out.println("Producto tiene variantes");
                                List<String> variantes=getVariantesASIN(asin);
                                /*if(variantes.size()>0){
                                    for(String asinValue:variantes){
                                        connectDB.agregaVariante(asin,asinValue);
                                    }
                                    throw new Exception("Producto tiene Variantes, se procesará luego con todas sus variantes");
                                }else{

                                }*/
                        }
                        precio=precio.replace(",", "");
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
            /*Elements precios=doc.getElementsByClass("a-color-price");
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
                            for(String asinValue:variantes){
                                connectDB.agregaVariante(asinProd,asinValue);
                            }
                            throw new Exception("Producto tiene Variantes, se procesará luego con todas sus variantes");
                        }else{
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

            }    */                 
            //String precio="0.0";
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
     
     
     
     public String getPrecioVar(String asinParent) throws Exception{
            Element price=doc.getElementById("priceblock_ourprice");
            String precio="0.0";
            if(price!=null){
                precio=price.text();
                String disponibilidad=getDisponibilidad();
                if(disponibilidad!="" && disponibilidad.indexOf("Disponible")==0){
                    if(precio!=null && precio.indexOf("$")==0){
                        List<String> variantes=getVariantesASIN(asinParent);
                         if(variantes.size()>0){
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
     
     

     
    public static void main(String ... a){
        List<String> asins=getProductosAProcesarVar();
        ExecuteScrapper exe=new ExecuteScrapper(asins);
        exe.run();
    }
    
}

class Configura{
    
    final static int nroMinutosEspera=1;
    final static int nroProductosxMinuto=5;
    final static int nroProductosxConsulta=2;

}
