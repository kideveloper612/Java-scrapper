/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.mlold;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MLController {

/*Replace with your application Client Id, Client Secret and RedirectUri*/
/*
    static Long clientId = 2800159267640390l;  
    static String clientSecret = "V0mupWCOLQ7SwAj4TbmUKgQaNjv7iLEs";
    static String redirectUri = "https://your_url.com";
    
    private final DefaultApi api = new DefaultApi();
        private String accessToken;

    private static String getAuthUrl() throws UnsupportedEncodingException {
                StringBuilder sb = new StringBuilder();
        try {
            
            DefaultApi api = new DefaultApi(new ApiClient(), clientId, clientSecret);
        String response = api.getAuthUrl(redirectUri, Configuration.AuthUrls.MLM);
        sb.append(Configuration.AuthUrls.MLM.getValue());   
        sb.append("/authorization?response_type=code&client_id=");
        sb.append(clientId);
        sb.append("&redirect_uri=");
            sb.append(URLEncoder.encode(redirectUri, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            sb.append(redirectUri);
        } catch (ApiException ex) {
            Logger.getLogger(MLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
           
    }
    
    private static void getAccessToken() throws UnsupportedEncodingException {
        try {
            DefaultApi api = new DefaultApi(new ApiClient(), clientId, clientSecret);
            String code = "{your_code}";
            AccessToken response = api.authorize(code, getAuthUrl());
            System.out.println(response.getAccess_token());
        } catch (ApiException ex) {
            Logger.getLogger(MLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    private static void refreshToken() throws UnsupportedEncodingException {
        try {
            DefaultApi api = new DefaultApi(new ApiClient(), clientId, clientSecret);
            String refreshToken = "{your_refresh_token}";
            RefreshToken response = api.refreshAccessToken(refreshToken);
        } catch (ApiException ex) {
            Logger.getLogger(MLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void GET() throws ApiException {
            DefaultApi api = new DefaultApi(new ApiClient(), clientId, clientSecret);
            String resource = "{api_resource}";
            Object response = api.defaultGet(resource);
    }
    
    public void POST() throws ApiException {
            String resource = "{api_resource}";
           // Object body = new Object();
           // body.field("{some_value}");
            Object response = api.defaultPost(accessToken, resource, new Object());
    }
        
    public void PUT() throws ApiException {
                String id = "{object_id}";
                String resource = "{api_resource}";
                //Object body = new Object();
                //body.field("{some_value}");
                Object response = api.defaultPut(resource, id, accessToken, new Object());
    }
    
    public void DELETE() throws ApiException {
                 String id = "{object_id}";
                 String resource = "{api_resource}";
                 Object response = api.defaultDelete(resource, id, accessToken);
    }

    public static void main(String a[]){
        try {
            MLController prueba=new MLController();
            System.out.println("ML=" +MLController.getAuthUrl());
            
                
           // MLController.getAccessToken();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
    
}