/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.mlold;

import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.modelo.Producto;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonArrayBuilder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author casa
 */
public class MLCreaProductoOld {
    private final String clienteID="2800159267640390"; //3560705080365987l;//2800159267640390l
    private String secretKey="V0mupWCOLQ7SwAj4TbmUKgQaNjv7iLEs";//"LjwbRgLuFDiPzkrnkxghQbWphxpsSyN0"; //V0mupWCOLQ7SwAj4TbmUKgQaNjv7iLEs
    public static final MediaType JSON= MediaType.parse("application/json; charset=utf-8");
    private Producto producto;
        MySQLConnectionFactory connectDB=new MySQLConnectionFactory();

    private String token;
    public MLCreaProductoOld(Producto p){
        this.producto=p;
    }
    
    public MLCreaProductoOld(){
        token=this.getToken();
        System.out.println("EL TOKEN IS="+token);
    }
    
  
    
  /*  public static void main(String ... a){
        String respuesta="{\n" +
"	\"id\": \"MLM677810810\",\n" +
"	\"site_id\": \"MLM\",\n" +
"	\"title\": \"Item De Prueba - No Comprar\",\n" +
"	\"subtitle\": null,\n" +
"	\"seller_id\": 173935739,\n" +
"	\"category_id\": \"MLM12388\",\n" +
"	\"official_store_id\": null,\n" +
"	\"price\": 1000,\n" +
"	\"base_price\": 1000,\n" +
"	\"original_price\": null,\n" +
"	\"inventory_id\": null,\n" +
"	\"currency_id\": \"MXN\",\n" +
"	\"initial_quantity\": 10,\n" +
"	\"available_quantity\": 10,\n" +
"	\"sold_quantity\": 0,\n" +
"	}";
            JSONObject obj = new JSONObject(respuesta);
            String mlCode=obj.getString("id");
            int precioML=obj.getInt("price");            
            //String mlCode=respuesta.substring(respuesta.indexOf("id"),respuesta.indexOf("id")+16);
            //String precioML=respuesta.substring(respuesta.indexOf("price"),respuesta.indexOf("price")+15);
            System.out.println("CODE="+mlCode + " PRECIO="+precioML);
            
        MLCreaProducto test=new MLCreaProducto();
        test.token=test.getToken();
        Producto p=new Producto();
        p.setASIN("SASASSAS");
        p.setPrecio(2939);
        p.setTitulo("Sensor De Cadencia Marca Wahoo Fitness Rastrea Y Captura");
        p.setDescripcion("Mommy's Helper Toilet Seat Lid-Lok, Seguro para Inodoro");
        List<String> imagenes =new ArrayList<String>();
        imagenes.add("https://images-na.ssl-images-amazon.com/images/I/41HkL8%2BKTdL.jpg");
        imagenes.add("https://images-na.ssl-images-amazon.com/images/I/41btjAkHBKL.jpg");
        imagenes.add("https://images-na.ssl-images-amazon.com/images/I/31gqXo3EADL.jpg");
        imagenes.add("https://images-na.ssl-images-amazon.com/images/I/41jWLAIaZYL.jpg");
        imagenes.add("https://images-na.ssl-images-amazon.com/images/I/51CZ7ozfBIL.jpg");
        p.setImagenes(imagenes);
        p.setMarca("Marca de Prueba");
        p.setModelo("Modelo de Prueba");
        p.setSku("SKU-TEST");
        test.addProducto("TOKEN",p);
        /*double precios[]={472.24,10801,46,2271,2939,254.81,2281.57,372.5,9352.73};
        for(double valor:precios){
            float res=test.calculaPrecioMX((float)valor);
            System.out.println(valor + "==>"+res);
        }
       /*int respuesta=test.updateProducto(test.token,"MLM677810810", "paused");
        if(respuesta==200){
            System.out.println("Se actualizó el producto");
        }else{
            System.out.println("Ocurrió un error... No se actualizó el producto");
        }*/
        
       /* respuesta=test.updateProducto("MLM677810810", 100);
        if(respuesta==200){
            System.out.println("Se actualizó el producto");
        }else{
            System.out.println("Ocurrió un error... No se actualizó el producto");
        }
        
        respuesta=test.updateProducto("MLM677810810", "paused");
        if(respuesta==200){
            System.out.println("Se actualizó el producto");
        }else{
            System.out.println("Ocurrió un error... No se actualizó el producto");
        }
    }*/

    public Producto addProducto(String token,Producto p){
        Producto prodNew=new Producto();
        int cantidad=15;float precio=p.getPrecio();
                
                
        try {
            if(precio==0){
                cantidad=0;
                precio=10;
            }else{
                cantidad=15;
                precio=p.getPrecio();
            }
           
            String categoria=getCategoria(p.getTitulo());
            OkHttpClient client = new OkHttpClient();
            JsonObject descripcion=Json.createObjectBuilder()
            .add("plain_text", p.getDescripcion()).build();
            JsonObject shiping=Json.createObjectBuilder()
            .add("mode", "not_specified")
            .add("local_pick_up",false)
            .add("free_shipping",false)
            .build();
            JsonObject marca=Json.createObjectBuilder()
            .add("id", "BRAND")
            .add("value_name",p.getMarca())
            .build();
            JsonObject modelo=Json.createObjectBuilder()
            .add("id", "MODEL")
            .add("value_name",p.getModelo())
            .build();
            /*JsonObject sku=Json.createObjectBuilder()
            .add("id", "SELLER_SKU")
            .add("value_name",p.getSku())
            .build();*/
             JsonObject tipoG=Json.createObjectBuilder()
            .add("id", "WARRANTY_TYPE")
            .add("value_id","2230280")
            .build();
              JsonObject tiempoG=Json.createObjectBuilder()
            .add("id", "WARRANTY_TIME")
            .add("value_name","30 dias")
            .build();
                 JsonArray seller = Json.createArrayBuilder()
     .add(tipoG)
     .add(tiempoG)
     .build();	
            List<String> imagenes=p.getImagenes();
            List<JsonObject> imagenesURL=new ArrayList<JsonObject>();
            JsonArrayBuilder jsonArray = Json.createArrayBuilder();

            for(String ima:imagenes){
                System.out.println("IMA="+ima);
                JsonObject imagen=Json.createObjectBuilder()
                    .add("source", ima).build();
                jsonArray.add(imagen);
            }
          DecimalFormat decimalFormat = new DecimalFormat("#.00");
           JsonObject json=null;
          if(p.getMarca()!=null && p.getMarca().length()>0 && p.getModelo()!=null && p.getModelo().length()>0){
            JsonArray atributos = Json.createArrayBuilder()
               .add(marca)
               .add(modelo)
               .build();	
                json=Json.createObjectBuilder()
               .add("title", p.getTitulo())
               .add("category_id", categoria)
               .add("price", decimalFormat.format(calculaPrecioMX(precio)))
               .add("currency_id","MXN")
               .add("condition","new")
               .add("available_quantity", cantidad)
               .add("buying_mode", "buy_it_now")
               .add("listing_type_id",  "gold_pro")
               .add("description", descripcion)
               .add("shipping", shiping)
               .add("attributes", atributos)
               .add("sale_terms", seller)
               .add("pictures", jsonArray.build())
               .build();
          }else if((p.getMarca()==null || p.getMarca().isEmpty()) && (p.getModelo()==null || p.getModelo().isEmpty())){
                json=Json.createObjectBuilder()
               .add("title", p.getTitulo())
               .add("category_id", categoria)
               .add("price", decimalFormat.format(calculaPrecioMX(precio)))
               .add("currency_id","MXN")
               .add("condition","new")
               .add("available_quantity", cantidad)
               .add("buying_mode", "buy_it_now")
               .add("listing_type_id",  "gold_pro")
               .add("description", descripcion)
               .add("shipping", shiping)
               .add("sale_terms", seller)
               .add("pictures", jsonArray.build())
               .build();
          }else if((p.getMarca()!=null && p.getMarca().length()>0) && (p.getModelo()==null || p.getModelo().isEmpty())){
                JsonArray atributos = Json.createArrayBuilder()
               .add(marca)
               .build();
                json=Json.createObjectBuilder()
               .add("title", p.getTitulo())
               .add("category_id", categoria)
               .add("price", decimalFormat.format(calculaPrecioMX(precio)))
               .add("currency_id","MXN")
               .add("condition","new")
               .add("available_quantity", cantidad)
               .add("buying_mode", "buy_it_now")
               .add("listing_type_id",  "gold_pro")
               .add("description", descripcion)
               .add("shipping", shiping)
               .add("sale_terms", seller)
               .add("attributes", atributos)
               .add("pictures", jsonArray.build())
               .build();
          }else if((p.getMarca()==null || p.getMarca().isEmpty()) && (p.getModelo()!=null && p.getModelo().length()>0)){
                JsonArray atributos = Json.createArrayBuilder()
               .add(modelo)
               .build();
                json=Json.createObjectBuilder()
               .add("title", p.getTitulo())
               .add("category_id", categoria)
               .add("price", decimalFormat.format(calculaPrecioMX(precio)))
               .add("currency_id","MXN")
               .add("condition","new")
               .add("available_quantity", cantidad)
               .add("buying_mode", "buy_it_now")
               .add("listing_type_id",  "gold_pro")
               .add("description", descripcion)
               .add("shipping", shiping)
               .add("sale_terms", seller)
               .add("attributes", atributos)
               .add("pictures", jsonArray.build())
               .build();
          }
        //  System.out.println("IMAGENES="+imagenesURL.toString());
          System.out.println("JSN="+ json.toString().replace("\\",""));
 
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url("https://api.mercadolibre.com/items?access_token="+this.token)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "67053bf3-5397-e19a-89ad-dfb1903d50c4")
                    .build();
            
            Response response = client.newCall(request).execute();
            int res=response.code();
             String respuesta=response.body().string();
                System.out.println("RES="+respuesta);
            connectDB.updateMLData(p.getASIN(), json.toString(), respuesta);
            if(res==201){
                JSONObject obj = new JSONObject(respuesta);
                String mlCode=obj.getString("id");
                float precioML=obj.getFloat("price");
                prodNew.setMlCode(mlCode);
                prodNew.setPrecioML(precioML);
                System.out.println("CODE="+mlCode + " PRECIO="+precioML);
                return prodNew;
            }else return null;
        } catch (Exception ex) {
            Logger.getLogger(MLCreaProductoOld.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    public String getToken(){
        String token=null;
          try {
            OkHttpClient client = new OkHttpClient();
            RequestBody reqbody = RequestBody.create(null, new byte[0]);  
            Request request = new Request.Builder()
                    .url("https://api.mercadolibre.com/oauth/token?grant_type=client_credentials&client_id="+clienteID +"&client_secret="+secretKey)
                    .post(reqbody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "67053bf3-5397-e19a-89ad-dfb1903d50c4")
                    .build();
            
            Response response = client.newCall(request).execute();
            String respuesta=response.body().string();
            token=respuesta.substring(respuesta.indexOf("APP"),respuesta.indexOf(",")-1);
            System.out.println(token);
        } catch (IOException ex) {
            Logger.getLogger(MLCreaProductoOld.class.getName()).log(Level.SEVERE, null, ex);
            token=null;
        }
          return token;
    }

     public String getCategoria(String titulo){
         String categoria=null;
          try {
              System.out.println("TITULO)="+titulo);
                OkHttpClient client = new OkHttpClient();
                 JsonArray value = Json.createArrayBuilder()
     .add(Json.createObjectBuilder()
         .add("title", titulo))
     .build();
 
            RequestBody body = RequestBody.create(JSON, value.toString());
            Request request = new Request.Builder()
                    .url("https://api.mercadolibre.com/sites/MLM/category_predictor/predict")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "67053bf3-5397-e19a-89ad-dfb1903d50c4")
                    .build();
            
            Response response = client.newCall(request).execute();
             String respuesta=response.body().string();
                        System.out.println("ES="+respuesta);
            respuesta=respuesta.substring(1,respuesta.length()-1);
            JSONObject obj = new JSONObject(respuesta);
            JSONArray arr = obj.getJSONArray("path_from_root");
            categoria=arr.getJSONObject(arr.length()-1).getString("id");
            categoria="MLM3530";
            System.out.println("LA CATEGORIA ES="+categoria);
        } catch (IOException ex) {
            Logger.getLogger(MLCreaProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categoria;
    }
     
    public static void main(String[] args) {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String precioML = decimalFormat.format(12.23);
            System.out.println("precio: "+precioML);
            
    }
     
     public int updateProducto(String token, String idML, int cantidad, float precio, String status){
         int res=0;
          try {
             float priceML=calculaPrecioMX(precio);
            OkHttpClient client = new OkHttpClient();
            System.out.println("Voy a buscar al Articulo "+idML);
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String precioML = decimalFormat.format(priceML);
            if(priceML==0)
                precioML="1.00";
             //float pML=Float.parseFloat(precioML);*/
             JsonObject json=Json.createObjectBuilder()
                     .add("available_quantity", cantidad)
                     .add("price", precioML)
                     .add("status", status)
                     .build();
            RequestBody reqbody = RequestBody.create(JSON,json.toString());  
            System.out.println("JSON="+json.toString());
            Request request = new Request.Builder()
                    .url("https://api.mercadolibre.com/items/"+idML+"?access_token="+this.token)
                    .put(reqbody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "67053bf3-5397-e19a-89ad-dfb1903d50c4")
                    .build();
            
            Response response = client.newCall(request).execute();
            res=response.code();
            System.out.println("ES="+response.body().toString());
            response.close();
        } catch (IOException ex) {
            Logger.getLogger(MLCreaProductoOld.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("EX="+ex.getMessage());
        }
         return res;
     }
     
     public int updateProducto(String token,String idML, int cantidad){
         int res=0;
          try {
            OkHttpClient client = new OkHttpClient();
    
             JsonObject json=Json.createObjectBuilder()
                     .add("available_quantity", cantidad)
                     .build();
            RequestBody reqbody = RequestBody.create(JSON,json.toString()); 
            System.out.println("JSON="+json.toString());
            Request request = new Request.Builder()
                    .url("https://api.mercadolibre.com/items/"+idML+"?access_token="+token)
                    .put(reqbody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "67053bf3-5397-e19a-89ad-dfb1903d50c4")
                    .build();
            System.out.println("RL="+request.url());
            Response response = client.newCall(request).execute();
            res=response.code();
                        response.close();

        } catch (IOException ex) {
            Logger.getLogger(MLCreaProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        }
         return res;
     }
     
          public int updateProducto(String token,String idML, float precio){
         int res=0;
          try {
            OkHttpClient client = new OkHttpClient();
    
             JsonObject json=Json.createObjectBuilder()
                     .add("price", precio)
                     .build();
            RequestBody reqbody = RequestBody.create(JSON,json.toString());  
            Request request = new Request.Builder()
                    .url("https://api.mercadolibre.com/items/"+idML+"?access_token="+token)
                    .put(reqbody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "67053bf3-5397-e19a-89ad-dfb1903d50c4")
                    .build();
            System.out.println("JSON="+json.toString());
            Response response = client.newCall(request).execute();
            res=response.code();
            System.out.println("CODE="+response);
                        response.close();

        } catch (IOException ex) {
            Logger.getLogger(MLCreaProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        }
         return res;
     }
          
      public int updateProducto(String token, String idML,String status){
         int res=0;
          try {
            OkHttpClient client = new OkHttpClient();
    
             JsonObject json=Json.createObjectBuilder()
                     .add("status", status)
                     .build();
            RequestBody reqbody = RequestBody.create(JSON,json.toString());  
            Request request = new Request.Builder()
                    .url("https://api.mercadolibre.com/items/"+idML+"?access_token="+token)
                    .put(reqbody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "67053bf3-5397-e19a-89ad-dfb1903d50c4")
                    .build();
            System.out.println("JSON="+json.toString());
            
            Response response = client.newCall(request).execute();
            System.out.println("RES="+response.toString());
            res=response.code();
                        response.close();

        } catch (IOException ex) {
            Logger.getLogger(MLCreaProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        }
         return res;
     }


     
     public float calculaPrecioMX(float price){
         float margen=0.35f;
         float comisionTemporal=0.17f;
         float comisionVenta=0.13f;
         int montoFijo=100;//100
         float precio=0;
         float costoTotal=0;
         if(price>1 && price<299){
             margen=0.35f;
             comisionTemporal=0.17f;
             comisionVenta=0.13f*price;
             montoFijo=100;
             costoTotal=(price*margen)+price;
             //SUMA(M2)/((100)-(L2*100)/(M2))*(100)+(150)
             precio=costoTotal+(costoTotal*comisionTemporal)+montoFijo;
             System.out.println("MRGEN="+margen + " - CT="+comisionTemporal + " - CV="+comisionVenta + "  COSTOTOT="+costoTotal + " -PRECIO="+precio);
         }else if(price>=299 && price<549){
             margen=0.25f;
             comisionTemporal=0.17f;
             comisionVenta=0.13f*price;
             montoFijo=100;
             costoTotal=(price*margen)+price;
             precio=costoTotal+(costoTotal*comisionTemporal)+montoFijo;
             System.out.println("MRGEN="+margen + " - CT="+comisionTemporal + " - CV="+comisionVenta + "  COSTOTOT="+costoTotal + " -PRECIO="+precio);
         }else if(price>=549 && price<999){
             margen=0.20f;
             comisionTemporal=0.17f;
             comisionVenta=0.13f*price;
             montoFijo=150;
              costoTotal=(price*margen)+price;
              precio=costoTotal+(costoTotal*comisionTemporal)+montoFijo;
             System.out.println("MRGEN="+margen + " - CT="+comisionTemporal + " - CV="+comisionVenta + "  COSTOTOT="+costoTotal + " -PRECIO="+precio);
         }else if(price>=999 && price<1999){
             margen=0.15f;
             comisionTemporal=0.215f;
             comisionVenta=0.175f*price;
             montoFijo=150;
              costoTotal=(price*margen)+price;
              precio=costoTotal+(costoTotal*comisionTemporal)+montoFijo;
             System.out.println("MRGEN="+margen + " - CT="+comisionTemporal + " - CV="+comisionVenta + "  COSTOTOT="+costoTotal + " -PRECIO="+precio);
         }else if(price>=1999 && price<4999){
             margen=0.13f;
             comisionTemporal=0.215f;
             comisionVenta=0.175f*price;
             montoFijo=150;
              costoTotal=(price*margen)+price;
              precio=costoTotal+(costoTotal*comisionTemporal)+montoFijo;
             System.out.println("MRGEN="+margen + " - CT="+comisionTemporal + " - CV="+comisionVenta + "  COSTOTOT="+costoTotal + " -PRECIO="+precio);
         }else if(price>=4999){
             margen=0.10f;
             comisionTemporal=0.215f;
             comisionVenta=0.175f*price;
             montoFijo=200;
              costoTotal=(price*margen)+price;
              precio=costoTotal+(costoTotal*comisionTemporal)+montoFijo;
             System.out.println("MRGEN="+margen + " - CT="+comisionTemporal + " - CV="+comisionVenta + "  COSTOTOT="+costoTotal + " -PRECIO="+precio);
         }
         return precio;
     }

    private JsonValue generaSKU() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

//
