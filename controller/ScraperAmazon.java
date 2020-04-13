/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.controller;

import com.scrapper.objetos.Producto;
import com.scrapper.util.Proxy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author root
 */
public class ScraperAmazon {
    
    private Document pantalla_uno;
    private Document pantalla_dos;
    private Producto producto;
    private String   url_pantalla_uno;
    private String   url_pantalla_dos;
    private int      codigo_repuesta;
    private int      cantidad_minima;
    
    public ScraperAmazon(String asin){
        producto= new Producto();
        producto.setAsin(asin);
        pantalla_uno=null;
        pantalla_dos=null;
        url_pantalla_uno="https://www.amazon.com.mx/dp/"+producto.getAsin();
        url_pantalla_dos=
            "https://www.amazon.com.mx/gp/offer-listing/"
            +producto.getAsin()
            +"/ref=dp_olp_new_mbc?ie=UTF8&condition=new";
        codigo_repuesta=404;// el producto no existe
        cantidad_minima=1; // lo minimo que se puede pedir, paquete despues de 2
    }
    
    public void PantallaUno(){
        try {
            Proxy proxy=new Proxy();
            pantalla_uno = proxy.Consulta(url_pantalla_uno);
            codigo_repuesta=proxy.getCodigoRespuesta();
            // si el api contiene datos
            if (  existeData( 1)
                && validarPatallaUno()
            ){

                producto.setTitulo      ( getTitulo()   );
                producto.setCategoria   ( getCategoria() );
                producto.setSubcategoria( getSubcategoria() );
                producto.setProveedor   ( getProveedor()    );
                producto.setBullets     ( getBullets()     );
                producto.setDescripcion ( getDescripcion() );
                producto.setGetDetallesBullet( getDetallesBullet() );
                producto.setGetInformacionTecnica( getInformacionTecnica());
                
                producto.setPrecio      ( getPrecio()   );
                producto.setCantidad    ( getCantidad() );
                
                // en funcion de la disponibilidad
                producto.setDisponible  ( getDisponibilidad());
                producto.setStatusML();
                
                AmpliarDescripcion();
                producto.setImagenes( getImagenes());
                producto.setTipo( 
                    getVariantesASIN(producto.getAsin()).isEmpty() 
                        ? "SIMPLE" 
                        : "VARIANTE"
                );
                
                // consideraciones cuando el producto es un paquete
                setPaquete();

            }else{
                codigo_repuesta=404;
                pantalla_uno=null;
                System.out.println("no existe producto en pantalla Uno");
                
            }
                
            
        } catch (IOException ex) {
            pantalla_uno=null;
            Logger.getLogger(ScraperAmazon.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(" Amazon(...) ex: "+ ex.getMessage());
        }
    }
    /**
     * 
     */
    
    public void setPaquete(){
        // consideraciones cuando el producto es un paquete
        if(cantidad_minima >1){
            // agregar la cantidad al inicio del titulo
            producto.setTitulo(cantidad_minima+ " " +producto.getTitulo());
            // modificar el precio en funcion de la cantidad minima del paquete
            producto.setPrecio(producto.getPrecio()*cantidad_minima);
        }
    }
    
    public int getCantidadMinima(){ return cantidad_minima;}
    
    /**
     * Esta funcion determinara si la pantalla uno es valida 
     * o no, es decir: para los casos en los cuales no se 
     * pueda extraer la informacion de la pantalla uno el resultado  
     * sera falso, de lo contario true; asin ejemplo B00176B9JC
     * este asi no muestra las columnas relacionadas con los 
     * precios.
     * busca el precio segun la funcion getPrecio
     **/
    public boolean validarPatallaUno(){
        return ( 
                pantalla_uno.getElementById("priceblock_ourprice") != null
            ||  pantalla_uno.getElementById("centerCol") != null
            );
    
    }
    // set
    public void setAsin(String asin){ this.producto.setAsin(asin); }
    
    // get
    public String getAsin(){ return producto.getAsin(); }
    // get
    public int getCodigoRespuesta(){ return codigo_repuesta; }      
    /**
     *  
     **/
    public List<String> getBullets(){
        
        String str;
        String [] lista;
        String bulletx;
        List<String> bullets= null;
        
        try{
            str=pantalla_uno.getElementById("feature-bullets").outerHtml();
            lista=str.split("<li>");
            bullets= new ArrayList<String>();
            for(int i=1; i<lista.length; i++){
                //System.out.println("bullets: "+ lista[i]);
                bulletx=limpiaBullet(lista[i]);
                    bullets.add(bulletx);
            }
            
        }catch(Exception e){
            System.out.println(" getBullets() ex: "+ e.getMessage());
            bullets= null;
        }
        return bullets;
    }
    
    private String limpiaBullet(String valor) {
        return valor.substring(valor.indexOf(">")+1,valor.indexOf("</span>")-1);
    }
    
    /**/
    public Map<String,String> getDetallesBullet(){
        
        Map<String, String> map = new HashMap<String, String>();
        Element information = pantalla_uno.getElementById("productDetails_detailBullets_sections1");
        if(information==null)
            information = pantalla_uno.getElementById("detail-bullets");
        else{
            Elements lineas =information.getElementsByTag("th");
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
    /**/
    public Map<String,String> getInformacionTecnica(){
        Map<String, String> map = new HashMap<String, String>();
        Element informationTech = pantalla_uno.getElementById("prodDetails");
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
    
    /**/
    public float getPrecio(){
        Element price=pantalla_uno.getElementById("priceblock_ourprice");
        String precio="0.0";
        if(price != null){
            precio=price.text();
            String disponibilidad=getDisponibilidad();
            if( disponibilidad != ""
                && disponibilidad.indexOf("Disponible")==0
            ){
                if( precio!=null 
                    && precio.indexOf("$")==0
                ){
                    //List<String> variantes=getVariantesASIN(producto.getAsin());
                    //if(variantes.size()>0){
                        //precio="0.0"; //throw new Exception("Producto tiene Variantes, se procesará luego con todas sus variantes");
                        //producto.setTipo("VARIANTE");
                    //}else{
                        precio=precio.substring(1,precio.length());
                        precio=precio.replace(",", "");
                    //}
                }
            }else{
                precio="0.0";
            }
        }else{
            //extraer los datos del contendor del centro
            Element contendor=pantalla_uno.getElementById("centerCol");
            if(contendor != null){
                Element precio_elemento = contendor.getElementById("priceblock_ourprice");
                if(precio_elemento == null)
                    //este caso es para el asin B07RY6HTF5 tiene un id diferente
                    precio_elemento = contendor.getElementById("priceblock_saleprice");

                if(precio_elemento != null)
                {
                    precio=precio_elemento.text();
                    precio=precio.substring(1,precio.length());
                    precio=precio.replace(",", "");
                }else
                    precio="0.0";
            }else
                // el contenedor central no existe
                precio="0.0";

                
                
        }
        
        return Float.parseFloat(precio);
    }
    
    public float getShipping(){
        Element envio_elemento =pantalla_uno.getElementById("priceblock_ourprice_ifdmsg");
        float envio=0.0f;
        
        if(envio_elemento != null){
            envio=Float.parseFloat(envio_elemento.text());
        }
        
        return envio;
    }
    /**
     * 
     **/
    public String getDisponibilidad(){
        String descripcion="";
        Element disponibilidad = pantalla_uno.getElementById("availability"); //doc.getElementsByClass("a-size-medium a-color-success");//
        if(disponibilidad !=null ){
            Elements elementos = disponibilidad.getElementsByTag("span");
            
            if(elementos!= null && !elementos.isEmpty() ){
                descripcion=elementos.get(0).text();
            }
        }
        return descripcion;
    }
    
    /* este metodo no tiene porque tener acceso a la bd*/
    public List<String> getVariantesASIN(String asinParent){
       
        List<String> variantes=new ArrayList<String>();
        Elements scriptElements = pantalla_uno.getElementsByTag("script");
            for( Element element :scriptElements ){                
                for( DataNode node : element.dataNodes()) {
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
                               //connectDB.agregaVariante(asinParent, asin, labels, valores);
                               //connectDB.updateVariante(asinParent);
                               variantes.add(asin);
                            }
                        }
                    }
            }
        return variantes;
    }    
    
    /**/
    public String getPrecio_old(){
            Element price=pantalla_uno.getElementById("priceblock_ourprice");
            String precio="0.0";
            if(price!=null){
                precio=price.text();
                String disponibilidad=getDisponibilidad();
                if(    disponibilidad != "" 
                    && disponibilidad.indexOf("Disponible")==0
                ){
                    if( precio!=null && precio.indexOf("$")==0){
                        precio=precio.substring(1,precio.length());
                        if(precio.indexOf("-")>0){
                            System.out.println("Producto tiene variantes");
                            List<String> variantes=getVariantesASIN(producto.getAsin());
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
                Elements precios=pantalla_uno.getElementsByClass("a-color-price");
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
    
    /*
    * Genera una lista con las url de las imagenes del producto
    * considera dos casos:
    * cuando el producto tiene mas de una imagen se ignora la ultima y
    * cuando tiene una sola imagen esta es agrega a la lista.
    */
    public List<String> getImagenes(){
        List<String> imagenes= new ArrayList<String>();
        try{
            Element imagenId = pantalla_uno.getElementById("altImages");
            if(imagenId!=null){
                Elements elements=imagenId.getElementsByTag("IMG");
                int i=1;
                int tam=elements.size();
                
                for(Element element: elements){
                    // ignorar la ultima imagen cuando son mas de una
                    if(i == tam && i>1 )
                        break;
                    
                    String urlImag=element.attr("src");
                    String urlImag2=urlImag.substring(0,urlImag.indexOf("._"));
                    String urlImag3=urlImag.substring(urlImag.lastIndexOf("."),urlImag.length());  
                    urlImag=urlImag2+urlImag3;
                    imagenes.add(urlImag);
                    
                    i++;
                }
            }
        }catch(Exception e){
            System.out.println(" getImagenes()() ex: "+ e.getMessage());
            imagenes= null;
        }
       return imagenes;
    }
    /**/
    public String getTitulo(){
        String title ="";
        try{
            title=pantalla_uno.getElementById("productTitle").text();
        }catch(Exception e){}
        
        return title;
    }
    
    /**/
    public boolean existeData(int pantalla){
        Document doc= (pantalla==1)? pantalla_uno :pantalla_dos;
        
        if(doc == null)
            return false;
        else
            //if((doc.html().indexOf("Sorry! We couldn't find that page. Try searching or go to Amazon's home")!=-1) || (doc.html().indexOf("No disponible por el momento")!=-1)){
                //return false;
            //}else
                return true;
    }
    
    public String getMarca(){
        try{
            String title=pantalla_uno.getElementById("bylineInfo_feature_div").text();
            return title;
        }catch(Exception e){
            return "";
        }
    }
    
    public String getCategoria(){
         try{
            String categoria=pantalla_uno.getElementById("searchDropdownBox").html();
            categoria=categoria.substring(categoria.indexOf("selected"),categoria.indexOf("selected")+150);
            String cat=categoria.substring(categoria.indexOf(">")+1,categoria.indexOf("</option>"));
            return cat;
         }catch(Exception e){
            return "";
        }
    }
    
    public String getSubcategoria() {
        try{
            String subcategoria=pantalla_uno.getElementById("wayfinding-breadcrumbs_feature_div").text();
            return subcategoria;    
        }catch(Exception e){
            return "";
        }
    }
    
    public float getReviews() { 
            Element reviews = pantalla_uno.getElementById("averageCustomerReviews");
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

    public float getReviewsLista(Element element) { 
        Elements reviews = element.getElementsByClass("a-icon-alt");
        float reviewPoint=0f;
        if(!reviews.isEmpty()){
            try{
                String valorReview=reviews.text();
                valorReview=valorReview.substring(0,valorReview.indexOf(" "));
                reviewPoint=Float.parseFloat(valorReview.trim());
            }catch(Exception e){
                reviewPoint=0.0f;
            }
        }
        return reviewPoint;
    }    
    public String getDescripcion(){
        String descripcion="";
        try{
            descripcion=pantalla_uno.getElementsByClass("productDescriptionWrapper").text();
        }catch(Exception e){
            
        }
                    /*
            String html=doc.toString();
            int inicio2=html.indexOf("iframeContent");
            int finalText=html.lastIndexOf("onloadCallback"); 
            System.out.println("Descripcion inicio:" +inicio2 +"final "+finalText);
            if(inicio2>0 && finalText>0){
                
                html=html.substring(inicio2,finalText);
                System.out.println("descripcion"+html);
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
        }*/
        
        return descripcion;
    }
    
    public String getProveedor(){
        String disponible="";
        Element information =pantalla_uno.getElementById("merchant-info"); //doc.getElementsByClass("a-size-medium a-color-success");//
        if(information!=null){
          Element info=information;//.get(0);
          disponible=info.text();
        }
        return disponible;

    }

    /**
     * 
     * Este metodo retorna la cantidad de productos disponibles del
     * proveerdor, para ello busca la informacion en el documento
     * retornado por la consulta a la API de AMAZON.
     */
    public BigDecimal getCantidad() {
    	
        BigDecimal cantidad = new BigDecimal(0);	
        BigDecimal maximo = new BigDecimal(0);
        BigDecimal minimo = new BigDecimal(0);
        
        String max_str="";
        String min_str="";
        
        if( pantalla_uno != null){
            Elements selects = pantalla_uno.getElementsByAttributeValue("name","quantity");
            for(Element element : selects) {
                
                Elements opciones = element.getElementsByTag("option");
                if(!opciones.isEmpty() && opciones.size()>1){
                    min_str=getNumeros( opciones.get(1).text().trim());
                    max_str=getNumeros( opciones.get(opciones.size()-1).text().trim());
                    System.out.println(
                             "\n min: " + min_str
                            +"\n max: " + max_str
                    );
                    
                    minimo = new BigDecimal(min_str);
                    maximo = new BigDecimal(max_str);
                    cantidad_minima = minimo.intValue();
                    
                    int cant = maximo.intValue()/minimo.intValue();
                    cantidad = new BigDecimal(cant);
                }
                    
                for(Element opcion: opciones){
                    System.out.println(opcion.text().trim());
                }
                //Element valor = opciones.get(opciones.size()-1);
                //System.out.println(valor.text().trim());
                //cantidad = new BigDecimal(0/*valor.text().trim()*/);
                break;
            }
        }    
   	 
   	return cantidad;
   }
    
    public String getNumeros(String cadena){
        char[] cadena_div= cadena.toCharArray();
        String numeros_str="";
        
        for(char caracter: cadena_div)
            if(Character.isDigit(caracter))
               numeros_str+=caracter;
        
        return numeros_str;
    }
    public Producto getProducto() { return producto; }
   	 
    //public Document getDoc(){ return doc; }
    
    public void PantallaDos(){
        float reviewPoint;
        float menor=99999999999f;
        float impuesto=0.0f;
        float precio=0.0f;
        float envio=0.0f;
        float precio_total =0.0f;
        String vendedor="";
        String disponibilidad_vendedor="";
        
        try {
            System.out.println("url: "+url_pantalla_dos);
            Proxy proxy=new Proxy();
            pantalla_dos = proxy.Consulta(url_pantalla_dos);
            codigo_repuesta=proxy.getCodigoRespuesta();
            // si el api contiene datos
            if ( existeData( 2) ){
                Elements boxPrices = pantalla_dos.getElementsByAttributeValueContaining("class", "a-row a-spacing-mini olpOffer ");
                if(  boxPrices != null ){
                    for (Element element :boxPrices ){
                        // precio
                        precio=getPrecioPantallaDos( element);
                        // envio
                        envio=getPrecioEnvioPantallaDos( element);
                        // reviews
                        reviewPoint = getReviewsLista( element);
                        // impuesto
                        Elements impuestoInfo = element.getElementsByAttributeValueContaining("class", "olpEstimatedTaxText");
                        for(int j=0;j<impuestoInfo.size();j++){
                                String valor=impuestoInfo.get(0).text();
                        }
                        // vendedor
                        Elements vendedor_document = element.getElementsByAttributeValueContaining("class", "a-spacing-none olpSellerName");
                        if(!vendedor_document.isEmpty()){ 
                            String valor=vendedor_document.get(0).text();
                            if(valor==null || valor.length()==0)
                                valor="AMAZON";
                            vendedor=valor;
                        }
                        //disponibilidad
                        /*
                        esta pantalla no muestra la disponibilidad
                        Elements disponibilidad_vendedor_document = pantalla_dos.getElementsByAttributeValueContaining("class", "a-unordered-list a-vertical olpFastTrack");
                        if(!disponibilidad_vendedor_document.isEmpty()){
                            String valor=disponibilidad_vendedor_document.get(0).text();
                            if(valor==null || valor.length()==0)
                                valor="AMAZON";
                            disponibilidad_vendedor=valor;
                        }*/
                        System.out.println("-------------------------------");
                        System.out.println("Precio: "+precio);
                        System.out.println("Envio : "+envio);
                        System.out.println("Inpuestos :     "+impuesto);
                        System.out.println("precio menor :  "+menor);
                        System.out.println("Vendedor :      "+vendedor);
                        //System.out.println("Disponibilidad: "+disponibilidad_vendedor);
                        System.out.println("Review="+reviewPoint);
                        
                        // precio total
                        precio_total = producto.CalcularPrecioTotal(vendedor, precio,envio);
                        System.out.println("precio total :  "+precio_total);
                        
                        if(precio_total<menor){
                            menor=precio_total;
                            System.out.println("menor ");
                            producto.setPrecio(precio);
                            producto.setShipping(
                                    precio_total != precio ? envio: 0.0f
                            );
                            producto.setProveedor(vendedor);
                            //producto.setDisponible(disponibilidad_vendedor);
                            //if(disponibilidad_vendedor.indexOf("Temporalmente agotado")>=0)
                                //producto.setPrecio(1.0f);
                            producto.setReviews(reviewPoint);
                            producto.setCantidad( new BigDecimal(1));
                            producto.setStatusML();
                        }
                        System.out.println("-------------------------------");
                          
                    }
                    //Elements prices = pantalla_dos.getElementsByAttributeValueContaining("class", "a-size-large a-color-price olpOfferPrice a-text-bold");
                    /*
                    if( prices.size() >0 ){
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
                                Elements shippingInfo = pantalla_dos.getElementsByAttributeValueContaining("class", "olpShippingPrice");
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
                                Elements impuestoInfo = pantalla_dos.getElementsByAttributeValueContaining("class", "olpEstimatedTaxText");
                                for(int i=0;i<impuestoInfo.size();i++){
                                    String valor=shippingInfo.get(i).text();
                                }                
                                Elements shippingVendor = pantalla_dos.getElementsByAttributeValueContaining("class", "a-spacing-none olpSellerName");
                                for(int i=0;i<shippingVendor.size();i++){
                                    String valor=shippingVendor.get(i).text();
                                    if(valor==null || valor.length()==0)
                                        valor="AMAZON";
                                    vendors[i]=valor;
                                }
                                Elements disponibilidadVendor = pantalla_dos.getElementsByAttributeValueContaining("class", "a-unordered-list a-vertical olpFastTrack");
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
                                    }* /
                                }
                                producto.setPrecio(precios[pos]);
                                if(vendors[pos].indexOf("Amazon")>=0)
                                    shipping[pos]=0.0f;
                                producto.setShipping(shipping[pos]);
                                producto.setProveedor(vendors[pos]);
                                producto.setDisponible(disponibilidad[pos]);
                                if(disponibilidad[pos].indexOf("Temporalmente agotado")>=0)
                                    producto.setPrecio(1.0f);
                                producto.setReviews(reviewPoint);
                                System.out.println("PRECIO="+precios[pos] + " SHIPPING="+shipping[pos] + " Impuesto="+impuestoInfo + " VENDEDOR="+vendors[pos] + " DISPONIBLE="+disponibilidad[pos]);
                                    if(menor==99999999999f)
                                        menor=1.0f;                               
                                    producto.setPrecio(menor);
                                }else{
                                    producto.setPrecio(1.0f);
                                }
                                System.out.println("EL PRECIO MENOR ES "+producto.getPrecio());
               */
                //
                reviewPoint=0.0f;
                impuesto=0.0f;
                precio=0.0f;
                envio=0.0f;
                vendedor="";
                disponibilidad_vendedor="";
                
                }else{
                    System.out.println("NO TIENE PRECIOS PRIME");
                    producto.setPrecio(1.0f);
                    producto.setShipping(0.0f);
                    producto.setProveedor("");
                    producto.setDisponible("");
                }                
            
            }else
                System.out.println("no existe producto en pantalla dos");
                               
        }catch(Exception e){
            
            System.out.println("ERROR EN LOS PRECIOS: "+ e.getMessage());
        }
    }    

    public float getPrecioPantallaDos(Element element){
        // precio
        Elements pricio = element.getElementsByAttributeValueContaining("class", "a-size-large a-color-price olpOfferPrice a-text-bold");
        String valor=pricio.get(0).text();
        valor=valor.replace(",", "");// , es para los miles    
        
        return Float.parseFloat(valor.substring(1,valor.length())); 
    }
    public float getPrecioEnvioPantallaDos(Element element){
        // precio envio
        float shipping=0;
        Elements shippingInfo = element.getElementsByAttributeValueContaining("class", "olpShippingPrice");
        if(!shippingInfo.isEmpty()){
            String valor=shippingInfo.get(0).text();
            shipping=Float.parseFloat(valor.substring(valor.indexOf("$")+1,valor.length()));
        }
        return shipping;
    }    
    
    public void AmpliarDescripcion(){
        
        String descripcionText="Por favor no olvide preguntar por la disponibilidad del producto antes de realizar la compra, un operador lo atenderá lo más pronto posible.";
                            
        descripcionText +=
            ( producto.getDescripcion() !=null 
              && producto.getDescripcion().indexOf("Por favor no olvide preguntar") <0 
            )
            ? "\n" + producto.getDescripcion()
            : producto.getDescripcion();
        
        
        if( producto.getProveedor() != null  
            && (
                producto.getProveedor().equals("Amazon México")
                || producto.getProveedor().contains("Amazon México")
            )
        ){
            descripcionText+=
                "Envío gratis a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas.(SkyDrop, SpinBox, Redpack, etc.)\n" +
                "El envío no tendrá ningún costo adicional y tomará de 1 a 3 días en llegar a su domicilio. Podrá tomar de 3 a 5 días para algunas zonas rurales.\n" +
                "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
        }else
            if( producto.getProveedor()!=null 
                && (
                    producto.getProveedor().equals("Amazon EE.UU")
                    || producto.getProveedor().contains("Amazon Estados Unidos")
                    || producto.getProveedor().contains("Amazon EE.UU") 
                    ||(
                        producto.getProveedor()!=null 
                        && producto.getProveedor().equals("Amazon Estados Unidos"))
                    )
            ){
                descripcionText+=
                    "Envío gratuito a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas. (SkyDrop, SpinBox, Redpack, etc.)\n" +
                    "Por ser un envío de importación el tiempo estimado de entrega es de 3 a 6 días hábiles dependiendo del despacho aduanal. Podrá tomar de 6 a 9 días para algunas zonas rurales.\n" +
                    "Adicionalmente, si requiere del producto antes del tiempo de entrega estimado, puede solicitar un envío express por un cargo adicional de $199 Pesos tras realizar su compra.\n" +
                    "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
            } else
                if (producto.getProveedor() != null 
                    && (
                        producto.getProveedor().contains("Vendido por")
                        || producto.getProveedor().contains("y enviado por Amazon")
                    )
                ){
                    descripcionText+=
                        "Envío gratis a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas.(SkyDrop, SpinBox, Redpack, etc.)\n" +
                        "El envío no tendrá ningún costo adicional y tomará de 1 a 3 días en llegar a su domicilio. Podrá tomar de 3 a 5 días para algunas zonas rurales.\n" +
                        "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
                }else {
                    descripcionText+=
                        "Envío gratuito a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas. (SkyDrop, SpinBox, Redpack, etc.)\n" +
                        "Por ser un envío de importación el tiempo estimado de entrega es de 6 a 9 días hábiles dependiendo del despacho aduanal. Podrá tomar de 9 a 15 días para algunas zonas rurales.\n" +
                        "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
                }
            producto.setDescripcion(descripcionText);
    }
    
    public String getUrlPantallaUno(){ return this.url_pantalla_uno; }
    
    public String getUrlPantallaDos(){ return this.url_pantalla_dos; }
    
    // para las pruebas 
    public void setPantallaUno(String html_str){
        if(     html_str != null 
            && !html_str.equals("")
        )
        pantalla_uno = Jsoup.parse(html_str);
        else
            pantalla_uno=null;
    
    }
    public void setPantallaDos(String html_str){
        if(     html_str != null 
            && !html_str.equals("")
        )
        pantalla_dos = Jsoup.parse(html_str);
        else
            pantalla_dos=null;
    }
    
}
