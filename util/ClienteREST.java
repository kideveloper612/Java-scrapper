/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;


//import static com.google.common.base.Charsets.UTF_8;
import org.slf4j.Logger;
//import org.onlab.packet.IpAddress;
import org.slf4j.Logger;


//import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.slf4j.LoggerFactory.getLogger;
/**
 *
 * @author casa
 */
public class ClienteREST {
    private static final String idAPP="2800159267640390";
    private static final String apiKEY="V0mupWCOLQ7SwAj4TbmUKgQaNjv7iLEs";
   // Logger log = Logger.(ClienteREST.class.getName());

     /**
     * Builds a REST client and fetches XOS mapping data in JSON format.
     *
     * @return the vBNG map if REST GET succeeds, otherwise return null
     */
   /* public ObjectNode getRest(String url) {
        Invocation.Builder builder = getClientBuilder(url);
        Response response = builder.get();

        if (response.getStatus() != HTTP_OK) {
          //  log.info("REST GET request returned error code {}",
                     //response.getStatus());
            return null;
        }

        String jsonString = builder.get(String.class);
        //log.info("Fetched JSON string: {}", jsonString);

        JsonNode node=null;
        /*try {
            node = MAPPER.readTree(jsonString);
        } catch (IOException e) {
            log.error("Failed to read JSON string", e);
            return null;
        }

        return (ObjectNode) node;
    }
    /**
 * Gets a client web resource builder.
 *
 * @param localUrl the URL to access remote resource
 * @return web resource builder
 */
/*public Invocation.Builder getClientBuilder(String localUrl) {
//    log.info("URL: {}"+localUrl);
    Client client = ClientBuilder.newClient();
    WebTarget wt = client.target(localUrl);
    return wt.request(MediaType.APPLICATION_JSON_TYPE);
}
    
    
    
    
  /*  private Client client = ClientBuilder.newClient();
    public static void main(String a[]){
        try {
            ClienteREST client=new ClienteREST();
                StringWriter writer = new StringWriter();

            //String code=client.getCode(idAPP);
            String url="https://auth.mercadolibre.com.mx/authorization?response_type=code&client_id="+idAPP;
            ClientRequest request = new ClientRequest(url);
            
            //Set the accept header to tell the accepted response format
            request.body("application/json", writer.getBuffer().toString());
            
            //Send the request
            ClientResponse response = request.post();
            
            //First validate the api status code
            int apiResponseCode = response.getResponseStatus().getStatusCode();
            System.out.println("RESPONSE CODE="+apiResponseCode);
        } catch (Exception ex) {
            Logger.getLogger(ClienteREST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getToken(String id, String apiKey,String code, String urlCallback){
        String token="";
        String url="https://api.mercadolibre.com/oauth/token?grant_type=authorization_code&client_id="+id+"&client_secret="+apiKey+"&code="+code+"&redirect_uri="+urlCallback;
        WebTarget target = client.target(url);
        JsonArray response = target.request(MediaType.APPLICATION_JSON).get(JsonArray.class);
        System.out.println(response);
        return token;
        
    }
    
    public String getCode(String id){
        String code="";
    String url="https://auth.mercadolibre.com.mx/authorization?response_type=code&client_id="+id;
 
        
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(url));
       JsonArray response =target.request(MediaType.APPLICATION_JSON).get(JsonArray.class);
        System.out.println(response);
        return code;
    }
    */
}



