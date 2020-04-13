/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;

import com.google.gson.JsonArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ClienteRest02 {
    private static final String idAPP="2815118482388169";//2800159267640390
    private static final String apiKEY="VB2NJcOlfX7qB7hVfjKDKIdJXqI2czEw"; //V0mupWCOLQ7SwAj4TbmUKgQaNjv7iLEs
	// http://localhost:8080/RESTfulExample/json/product/get
	public static void main(String[] args) {

                ClienteRest02 cliente=new ClienteRest02();
                cliente.getCode(idAPP);
	}
        
           public String getToken(String id, String apiKey,String code, String urlCallback){
               String token="";
        
         try {

		URL url = new URL("https://api.mercadolibre.com/oauth/token?grant_type=authorization_code&client_id="+id+"&client_secret="+apiKey+"&code="+code+"&redirect_uri="+urlCallback);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }

        return token;
    }
    
    public String getCode(String id){
        String code="";
        
         try {

		URL url = new URL("https://auth.mercadolibre.com.ar/authorization?response_type=code&client_id="+id);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }

        return code;
        
        
    }
    

}