/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.ml.MercadoLibreAPI;
import com.scrapper.mlold.MLCreaProductoOld;
import com.scrapper.modelo.Producto;
import com.scrapper.timer.ExecuteScrapper;
import com.scrapper.util.Agents;
import static com.scrapper.util.Agents.agentes;
import static com.scrapper.util.Agents.proxys;
import com.scrapper.util.Configura;
import com.scrapper.util.ProductoNoExisteException;
import com.scrapper.util.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author dlunago
 */
public class AgentePrice extends TimerTask implements Agents{
    Logger logger = Logger.getLogger(AgentePrice.class.getName());
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
    private String url;
   private static int problemas=0;
    private Document doc=null;
    int procesados=0;
    String asin;
    List<String> asins=new ArrayList<String>();
    Configura configura= Configura.getInstance();

    public AgentePrice(){
            //this.asin=url;
            System.out.println("==============================================================");

            //logger.info("PROCESANDO EL AGENTE DE PRECIOS CON  EL ASIN=" + asins.toString());
            System.out.println("==============================================================");
       
    }
    @Override
    public void run() {
            procesados=0;
            //Se usó para solo procesar los INACTIVOS EN ML List<String> asins=connectDB.getProductosUpdateMLInactivos(nro);//new ArrayList<String>();//////
            List<String> asins=connectDB.getProductosUpdate(configura.getMaximoUpdate());//new ArrayList<String>();//////
                        
            MLCreaProductoOld mlAgent=new MLCreaProductoOld();
            String token=mlAgent.getToken();
            
            if(token!=null){
                int totalPro=0;
                System.out.println("==============================================================");

                        logger.info("PROCESANDO EL AGENTE DE PRECIOS CON  EL ASIN=" + asins.toString());
                        System.out.println("==============================================================");
                for(String asin:asins){
                    totalPro++;
                    Producto producto=new Producto(); //procesa(asin);
                    System.out.println("==============================================================");

                        logger.info("PROCESANDO EL AGENTE DE PRECIOS CON  EL ASIN=" + asin);
                        System.out.println("==============================================================");
                    try{

                        long startTime = System.currentTimeMillis();
                        logger.info("Revisando el producto " + asin + " inicio="+startTime);
                        Proxy proxy=new Proxy();
                        doc = proxy.Consulta(
                            "https://www.amazon.com.mx/gp/offer-listing/" +asin+ "/ref=olp_f_freeShipping?ie=UTF8&f_new=true&f_primeEligible=true");
                        logger.info("Procesando " + asin);
                        problemas=0;
                        
                        producto=new Producto(); //procesa(asin);
                        producto=connectDB.getProducto(asin);
                        if(doc!=null && existeData() && producto!=null){
                            Producto prodNew=new Producto();
                            prodNew.setASIN(producto.getASIN());
                            prodNew.setMlCode(producto.getMlCode());
                            prodNew=getPreciosNew(prodNew);
                            
                            if(prodNew.getPrecio()>1){
                                prodNew.setPrecioML(mlAgent.calculaPrecioMX(prodNew.getPrecio()+prodNew.getShipping()));
                                producto.setStatus("ACTIVO");
                                prodNew.setStatus("ACTIVO");
                                System.out.println("================= PRODUCTO ACTIVO "+ producto.getMlCode() + " ========== ");
                                if(producto.getMlCode()!=null && producto.getMlCode().length()>0 && prodNew.getPrecio()>prodNew.getShipping()){
                                    int res=mlAgent.updateProducto(token,producto.getMlCode(),15,prodNew.getPrecio()+prodNew.getShipping(),"active");
                                    prodNew.setStatusml("ACTIVE");
                                    if(res==200){
                                        prodNew.setChanged(true);
                                        System.out.println("================= PRODUCTO MODIFICADO ML ========== " + producto.getMlCode());
                                    }else{
                                        prodNew.setChanged(false);
                                        System.out.println("================= PRODUCTO NO MODIFICADO ML ========== " + producto.getMlCode());
                                    }
                                }else{
                                   logger.info("PRODUCTO NO EXISTE EN MERCADO LIBRE " + asin);

                                    if(prodNew.getPrecio()<=prodNew.getShipping()){
                                            int res=mlAgent.updateProducto(token,producto.getMlCode(),0,0,"paused");
                                            prodNew.setStatusml("PAUSED");
                                        if(res==200){
                                            prodNew.setChanged(true);
                                            System.out.println("================= PRODUCTO MODIFICADO ML ========== " + producto.getMlCode());
                                        }else{
                                            prodNew.setChanged(false);
                                            System.out.println("================= PRODUCTO NO MODIFICADO ML ========== " + producto.getMlCode());

                                        }
                                    }
                                }
                            }else{
                                producto.setStatus("INACTIVO");
                                prodNew.setStatus("INACTIVO");

                                producto.setASIN(asin);
                                int res=mlAgent.updateProducto(token,producto.getMlCode(),0,0,"paused");
                                    prodNew.setStatusml("PAUSED");
                                if(res==200){
                                    prodNew.setChanged(true);
                                    System.out.println("================= PRODUCTO MODIFICADO ML ========== " + producto.getMlCode());
                                }else{
                                    prodNew.setChanged(false);
                                    System.out.println("================= PRODUCTO NO MODIFICADO ML ========== " + producto.getMlCode());

                                }
                            }
                            //----NUEVA CUENTA
                            /*if(prodNew2.getPrecio()>1){
                                prodNew2.setPrecioML(mlAgent.calculaPrecioMX(prodNew2.getPrecio()+prodNew2.getShipping()));
                                producto.setStatus("ACTIVO");
                                prodNew2.setStatus("ACTIVO");
                                System.out.println("================= PRODUCTO ACTIVO "+ producto.getMlCode() + " ========== ");
                                if(producto.getMlCode()!=null && producto.getMlCode().length()>0 && prodNew2.getPrecio()>prodNew2.getShipping()){
                                    int res=mlAgent.updateProducto(token,producto.getMlCode(),15,prodNew2.getPrecio()+prodNew2.getShipping(),"active");
                                    prodNew2.setStatusml("ACTIVE");
                                    if(res==200){
                                        prodNew2.setChanged(true);
                                        System.out.println("================= PRODUCTO MODIFICADO ML ========== " + producto.getMlCode());
                                    }else{
                                        prodNew2.setChanged(false);
                                        System.out.println("================= PRODUCTO NO MODIFICADO ML ========== " + producto.getMlCode());

                                    }
                                }else{
                                   logger.info("PRODUCTO NO EXISTE EN MERCADO LIBRE " + asin);

                                    if(prodNew2.getPrecio()<=prodNew.getShipping()){
                                            int res=mlAgent.updateProducto(token2,producto.getMlCode(),0,0,"paused");
                                            prodNew2.setStatusml("PAUSED");
                                        if(res==200){
                                            prodNew2.setChanged(true);
                                            System.out.println("================= PRODUCTO MODIFICADO ML ========== " + producto.getMlCode());
                                        }else{
                                            prodNew2.setChanged(false);
                                            System.out.println("================= PRODUCTO NO MODIFICADO ML ========== " + producto.getMlCode());

                                        }
                                    }
                                }
                            }else{
                                producto.setStatus("INACTIVO");
                                prodNew2.setStatus("INACTIVO");

                                producto.setASIN(asin);
                                int res=mlAgent.updateProducto(token2,producto.getMlCode(),0,0,"paused");
                                    prodNew2.setStatusml("PAUSED");
                                if(res==200){
                                    prodNew2.setChanged(true);
                                    System.out.println("================= PRODUCTO MODIFICADO ML ========== " + producto.getMlCode());
                                }else{
                                    prodNew2.setChanged(false);
                                    System.out.println("================= PRODUCTO NO MODIFICADO ML ========== " + producto.getMlCode());

                                }
                            }
                            */
                            long finTime = System.currentTimeMillis();
                            long totalTiempo=(finTime-startTime)/1000;
                            logger.info("Datos de Precio tomados en " + totalTiempo + " segundos");

                            connectDB.updatePrecioProducto(prodNew);
                            connectDB.guardaHistoricoPrecio(producto, prodNew);
                            
                        }
                }catch(SocketTimeoutException ex){
                    connectDB.noProcesadoASIN(asin,"Error leyendo la página. No se extrajo información");

                }catch(Exception ex){
                    ex.printStackTrace();
                    logger.info("Ocurrió un error procesando " + asin);
                }
                procesados++;
            }
            if(procesados==0){
                logger.info("NO SE ENCONTRARON PRODUCTOS PARA PROCESAR");
            }else{
                logger.info("SE ENCONTRARON "+ procesados + " PRODUCTOS");
            }
            }else{
                         logger.info("Problemas con el TOKEN DE ML");
                         }
                }

    private void productoNoExiste(String asin) throws ProductoNoExisteException {
        connectDB.noExisteASIN(asin);
        logger.info("============= Producto No Existe " + asin +" =================="); //To change body of generated methods, choose Tools | Templates.
    }
    private void productoNoProcesado(String asin, String motivo){
        connectDB.noProcesadoASIN(asin,motivo);
        logger.info("============= Producto No Existe " + asin +" =================="); //To change body of generated methods, choose Tools | Templates.
    }    
    
    public boolean existeData(){
        if((doc.html().indexOf("Sorry! We couldn't find that page. Try searching or go to Amazon's home")!=-1) || (doc.html().indexOf("No disponible por el momento")!=-1)){
            return false;
        }else{
            return true;
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
    
    public String getPrecio() throws Exception{
            Element price=doc.getElementById("priceblock_ourprice");
            String precio="0.0";
            if(price!=null){
                precio=price.text();
                String disponibilidad=getDisponibilidad();
                if(disponibilidad!="" && disponibilidad.indexOf("Disponible")==0){
                    if(precio!=null && precio.indexOf("$")==0){
                        precio=precio.substring(1,precio.length());
                    }
                        precio=precio.replace(",", "");
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
    
    
    public String getPrice(){
        try{
            Elements precios=doc.getElementsByClass("a-column a-span2 olpPriceColumn");
            String precio=precios.get(0).text();
            System.out.println("PRECIO="+precio);
            System.out.println("OUTER="+precios.get(0).outerHtml());
            System.out.println("HTML="+precios.get(0).html());
            String html=precios.get(0).html();
            precio=precio.substring(precio.indexOf("$")+1, precio.indexOf(" "));
            precio=precio.replace(",", "");
            if(html.indexOf("Amazon Prime")>0){
                System.out.println("ES PRIME");
                
            }else{
                System.out.println("NO ES PRIME");
                precio="0.0";

            }
            return precio;
        }catch(Exception e){
            return "-1";
        }
    }
    public String getProveedor(){
        try{
           Element proveedor=doc.getElementById("merchant-info");
           System.out.println("PROVEEDOR="+proveedor.text());

           return proveedor.text();
        }catch(Exception e){
            return "";
        }
    }
    
    
    
    public String getMensaje(){
         try{
            Elements mensaje=doc.getElementsByClass("a-unordered-list a-vertical olpFastTrack");
                    System.out.println("MENSAJE="+mensaje.get(0).text());
                    return mensaje.text();
         }catch(Exception e){
            return "";
        }
    }
    
    public float getReviews() { 
            try{
              Element reviews2 = doc.getElementById("averageCustomerReviews");
             String reviews3=reviews2.text();
             reviews3=reviews3.substring(0,reviews3.indexOf(" "));
             System.out.println("REVIEW="+reviews3);
             return Float.parseFloat(reviews3);
            }catch(Exception e){
                return 0;
            }
    }
    
    public float getReviewsOld(String asin) { 
        
             Elements reviews=doc.getElementsByClass("offerListingPage" + asin);
        try{
        if(reviews!=null){
                String review=reviews.text();
                review=review.substring(0,review.indexOf(" "));
                        System.out.println("RVIEW="+review);

                return Float.parseFloat(review);
            }else{
                return 0.0f;
            }
        }catch(Exception e){
            return 0.0f;
        }
    }
   
    public Producto getPreciosNew(Producto prod){
        String ASIN=prod.getASIN();
            float menor=99999999999f;
                try {
                    
                    Elements reviews = doc.getElementsByClass("a-icon-alt");
                    float reviewPoint=0f;
                    if(reviews!=null){
                        try{
                        String valorReview=reviews.text();
                        valorReview=valorReview.substring(0,valorReview.indexOf(" "));
                        reviewPoint=Float.parseFloat(valorReview.trim());
                        System.out.println("Review="+reviewPoint);
                        }catch(Exception e){
                            reviewPoint=0.0f;
                        }
                    }
                    Elements boxPrices = doc.getElementsByClass("a-row a-spacing-mini olpOffer");
                    if(boxPrices!=null){
                         Elements prices = doc.getElementsByAttributeValueContaining("class", "a-size-large a-color-price olpOfferPrice a-text-bold");
                        if(prices.size()>0){
                            float precios[]=new float[prices.size()];
                            for(int i=0;i<prices.size();i++){
                                String valor=prices.get(i).text();
                                System.out.println(prices.get(i).text());
                                float price1=0.0f;
                                if(valor.substring(1,valor.length()).indexOf(",")>0){
                                    valor=valor.replace(",", "");    
                                    price1=Float.parseFloat(valor.substring(1,valor.length()));
                                    precios[i]=price1;
                                }else
                                    precios[i]=Float.parseFloat(valor.substring(1,valor.length()));
                            }
                            float shipping[]=new float[prices.size()];
                            String vendors[]=new String[prices.size()];//a-spacing-none olpSellerName
                            String disponibilidad[]=new String[prices.size()];
                            Elements shippingInfo = doc.getElementsByAttributeValueContaining("class", "olpShippingPrice");
                            //if(shippingInfo.size()==prices.size()){
                            int posx=0;
                            for(int i=0;i<shippingInfo.size();i++){
                                if(i%2==0){
                                String valor=shippingInfo.get(i).text();
                                shipping[posx++]=Float.parseFloat(valor.substring(valor.indexOf("$")+1,valor.length()));

                                }
                            }
                            //}
                            float impuestos[]=new float[prices.size()];
                            Elements impuestoInfo = doc.getElementsByAttributeValueContaining("class", "olpEstimatedTaxText");
                            for(int i=0;i<impuestoInfo.size();i++){
                                String valor=shippingInfo.get(i).text();
                            }                
                            Elements shippingVendor = doc.getElementsByAttributeValueContaining("class", "a-spacing-none olpSellerName");
                            for(int i=0;i<shippingVendor.size();i++){
                                String valor=shippingVendor.get(i).text();
                                if(valor==null || valor.length()==0)
                                    valor="AMAZON";
                                vendors[i]=valor;
                            }
                            Elements disponibilidadVendor = doc.getElementsByAttributeValueContaining("class", "a-unordered-list a-vertical olpFastTrack");
                            for(int i=0;i<disponibilidadVendor.size();i++){
                                String valor=disponibilidadVendor.get(i).text();
                                if(valor==null || valor.length()==0)
                                    valor="AMAZON";
                                disponibilidad[i]=valor;
                            }
                            int pos=0;

                            for(int i=0;i<prices.size();i++){
                                float precio=precios[i]+shipping[i];
                                if(vendors[i]!=null && precio<menor){
                                        menor=precio;
                                     pos=i;
                                 }/*else{
                                    menor=precio;
                                }*/
                            }
                            prod.setPrecio(precios[pos]);
                            if(vendors[pos].indexOf("Amazon")>=0)
                                shipping[pos]=0.0f;
                            prod.setShipping(shipping[pos]);
                            prod.setProveedor(vendors[pos]);
                            prod.setDisponible(disponibilidad[pos]);
                            if(disponibilidad[pos].indexOf("Temporalmente agotado")>=0)
                                 prod.setPrecio(1.0f);
                            prod.setReviews(reviewPoint);
                            System.out.println("PRECIO="+precios[pos] + " SHIPPING="+shipping[pos] + " Impuesto="+impuestoInfo + " VENDEDOR="+vendors[pos] + " DISPONIBLE="+disponibilidad[pos]);
                                if(menor==99999999999f)
                                            menor=1.0f;                               
                                prod.setPrecio(menor);
                            }else{
                                 prod.setPrecio(1.0f);
                            }
                            System.out.println("EL PRECIO MENOR ES "+prod.getPrecio());
                        }else{
                            System.out.println("NO TIENE PRECIOS PRIME");
                            prod.setPrecio(1.0f);
                            prod.setShipping(0.0f);
                            prod.setProveedor("");
                            prod.setDisponible("");
                        }
                   
                }catch(Exception e){
                                                                               System.out.println("ERROR EN LOS PRECIOS ");
 
                   e.printStackTrace();
                }
                return prod;
            }  
}

