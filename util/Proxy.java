/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author root
 */
public class Proxy {

    private static int PUERTO;
    private static String URL;
    private static String USUARIO;
    private static String CLAVE;
    private static String IP;
    private static String TOKEN;
    private static String HOST;
    private static String SCHEME;

    private static String ip;
    private static String agente;
    private static int port;

    public int CodigoRespuesta;
    
    public static int proxyIndex=0;
    
    public static ArrayList listaProxies=new ArrayList();
    public ProxyInfo myProxy=null;

    static {
        IP = "";

        USUARIO = "lum-customer-hl_b815f4e8-zone-static";
        CLAVE = "r84h2nfxdz35";
        SCHEME = "http";
        HOST = "falcon.proxyrotator.com";
        PUERTO = 51337;
        TOKEN = "y8PN7cDmLp95WKZMtrYge4GnFf3JUkAQ";

        URL = "";

        ip = null;
        agente = null;
        port = -1;
        
        listaProxies.add(new ProxyInfo("108.59.14.208", 13041));
        listaProxies.add(new ProxyInfo("37.48.118.90", 13041));
        listaProxies.add(new ProxyInfo("83.149.70.159", 13041));
        listaProxies.add(new ProxyInfo("108.59.14.203", 13041));

        //CodigoRespuesta = 0; // no exisste 
    }

    public Proxy() {
        this.CodigoRespuesta = 0;
        myProxy=(ProxyInfo) listaProxies.get(proxyIndex);
        proxyIndex++;
        proxyIndex=(proxyIndex%listaProxies.size());
    }
    
    

    /**
     * Esta funcion realiza un consulta usando un servidor en funcion de la
     * configuracion por defecto
     *
     */
    public Document Consulta(
            String url_objetivo
    ) throws IOException {
        //  construir el proxy
        return API_ProxyRedirecionLocal(
                url_objetivo
        );
    }

    public int getCodigoRespuesta() {
        return CodigoRespuesta;
    }

    /**
     * Esta funcion realiza un consulta usando un servidor de proxy en la nube,
     * el propio servidor se encarga de asignar la direccion de salida
     *
     */
    public static Document API_ProxyRedirecionRemoto(
            String url_objetivo,
            String agente_Proxy
    ) throws IOException {
        //  construir el proxy
        HttpHost proxy = new HttpHost(
                URL,
                PUERTO
        );
        HttpResponse response = Executor
                .newInstance()
                .auth(
                        proxy,
                        USUARIO,
                        CLAVE
                ).execute(
                        Request
                        .Get(url_objetivo)
                        .viaProxy(proxy)
                        .userAgent(agente_Proxy)
                ).returnResponse();

        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");

        Document doc = Jsoup.parse(responseString);

        // guardar en un fichero el contenido de la respuesta.   
        /*AdministradorArchivo.CrearArchivo(
         responseString,
         "",
         System.currentTimeMillis() + "-html",
         ".txt"
         );*/
        return doc;
    }

    public Document API_ProxyRedirecionLocal(
            String url_objetivo
    ) {
        System.out.println("url_objetivo = " + url_objetivo);
        Document doc = null;


        HttpResponse response;
        try {
            //HttpHost proxy = new HttpHost("108.59.14.208", 13041);
            System.out.println("myProxy.getIp() = " + myProxy.getIp() + ":" + myProxy.getPort());
            HttpHost proxy = new HttpHost(myProxy.getIp(), myProxy.getPort());
            response
                    = Executor
                    .newInstance()
                    .execute(
                            Request
                            .Get(url_objetivo)
                            .viaProxy(proxy)
                            .connectTimeout(5000)
                            .socketTimeout(5000)

                    ).returnResponse();
            
            CodigoRespuesta = response.getStatusLine().getStatusCode();
            System.out.println("Response Status Code: " + CodigoRespuesta);

            if (CodigoRespuesta == 200) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                doc = Jsoup.parse(responseString);
                    //responseString = EntityUtils.toString(entity, "UTF-8");
                // guardar en un fichero el contenido de la respuesta.
                    /*AdministradorArchivo.CrearArchivo(
                 responseString,
                 "",
                 System.currentTimeMillis() + "-html",
                 ".txt"
                 );*/

                //doc = Jsoup.parse(responseString);
            } else if (CodigoRespuesta == 404) {
                // no existe la ruta solicitada
                doc = null;
            } else if (CodigoRespuesta == 503) {
                //el servidor no responde
                doc = null;
            } else {
                doc = null;
            }

        } catch (IOException ex) {
            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);

        }
        return doc;
    }

//    public static Document API_ProxyRedirecionLocal_Old2(
//            String url_objetivo
//    ) {
//        System.out.println("url_objetivo = " + url_objetivo);
//        Document doc = null;
//
//        URIBuilder builder = new URIBuilder();
//
//        builder.setScheme("https").setHost("api.scraperapi.com")
//                //.setParameter("key", "8b13fc651c6c065a295695f3f0887069")
//                .setParameter("key", "47c09d9f388bb8f99a2d2c950b4b157d")
//                .setParameter("url", url_objetivo);
//        URI uri;
//
//        HttpResponse response;
//        try {
//            response = Executor
//                    .newInstance()
//                    .execute(
//                            Request
//                            .Get(
//                                    builder.build()
//                            )
//                    ).returnResponse();
//
//            CodigoRespuesta = response.getStatusLine().getStatusCode();
//            System.out.println("Response Status Code: " + CodigoRespuesta);
//
//            if (CodigoRespuesta == 200) {
//                HttpEntity entity = response.getEntity();
//                String responseString = EntityUtils.toString(entity, "UTF-8");
//
//                doc = Jsoup.parse(responseString);
//                    //responseString = EntityUtils.toString(entity, "UTF-8");
//                // guardar en un fichero el contenido de la respuesta.
//                    /*AdministradorArchivo.CrearArchivo(
//                 responseString,
//                 "",
//                 System.currentTimeMillis() + "-html",
//                 ".txt"
//                 );*/
//
//                //doc = Jsoup.parse(responseString);
//            } else if (CodigoRespuesta == 404) {
//                // no existe la ruta solicitada
//                doc = null;
//            } else if (CodigoRespuesta == 503) {
//                //el servidor no responde
//                doc = null;
//            } else {
//                doc = null;
//            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
//
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
//
//        }
//        return doc;
//    }

    /**
     * Esta funcion realiza un consulta usando un servidor proxy en la nube, el
     * servidor busca un conexion disponible y responde con los detalles del
     * proxy encontrado el cliente tiene que realizar la consulta con los
     * detalles de la respuesta
     *
     */
//    public static Document API_ProxyRedirecionLocal_Old(
//            String url_objetivo
//    ) {
//        Document doc = null;
//        HttpHost proxy;
//        HttpEntity entity;
//        CloseableHttpClient httpclient;
//        HttpResponse response;
//        String responseString;
//
//        for (int j = 1; j <= 3; j++) {
//            // Consultar al servidor proxy
//            System.out.println("j: " + j);
//            BuscarProxy();
//            // utilizar proxy temporal
//            if (ip != null) {
//                for (int i = 1; i <= 1; i++) {
//                    System.out.println("i: " + i);
//                    try {
//                        //httpclient = HttpClients.createDefault();
//                        try {
//                            CodigoRespuesta = 0;
//                            proxy = new HttpHost(ip, port, "http");
//                            response
//                                    = Executor
//                                    .newInstance()
//                                    .execute(
//                                            Request
//                                            .Get(url_objetivo)
//                                            .viaProxy(proxy)
//                                            .connectTimeout(5000)
//                                            .socketTimeout(5000)
//                                    //.userAgent( agente)
//
//                                    ).returnResponse();
//
//                            // verificar si la respuesta es 200
//                            CodigoRespuesta = response.getStatusLine().getStatusCode();
//                            System.out.println("Response Status Code: " + CodigoRespuesta);
//                            if (CodigoRespuesta == 200) {
//                                entity = response.getEntity();
//                                responseString = EntityUtils.toString(entity, "UTF-8");
//                                // guardar en un fichero el contenido de la respuesta.
//                                AdministradorArchivo.CrearArchivo(
//                                        responseString,
//                                        "",
//                                        System.currentTimeMillis() + "-html",
//                                        ".txt"
//                                );
//
//                                doc = Jsoup.parse(responseString);
//                                j = 4;
//                                break;
//                            } else if (CodigoRespuesta == 404) {
//                                // no existe la ruta solicitada
//                                doc = null;
//                                j = 4;
//                                break;
//                            } else if (CodigoRespuesta == 503) {
//                                //el servidor no responde
//                                doc = null;
//                                //j=0;
//                                break;
//                            } else {
//                                doc = null;
//                            }
//
//                        } catch (ConnectTimeoutException ex) {
//                            System.out.println("ConnectTimeoutException ex [" + i + "]");
//                            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
//                            break;
//                        } finally {
//                            //httpclient.close();
//                        }
//                    } catch (IOException ex) {
//                        System.out.println("IOException ex [" + i + "]");
//                        Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
//                        break;
//                    }
//                }// i    
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }// j    
//        return doc;
//    }

    /**
     *
     *
     */

    private static void BuscarProxy() {

        URI uri;
        HttpEntity entity;
        HttpGet httpget;
        CloseableHttpClient httpclient;
        CloseableHttpResponse response;
        String responseString;

        JSONParser jsonParser;
        Object obj;
        JSONObject json;

        boolean estado = false;
        for (int i = 0; i <= 0; i++) {
            System.out.println("buscar i: " + i);
            ip = null;
            agente = null;
            port = -1;

            try {
                // Consultar al servidor proxy
                uri = new URIBuilder()
                        .setScheme(SCHEME)
                        .setHost(HOST)
                        .setPort(PUERTO)
                        .setPath("/")
                        .setParameter("apiKey", TOKEN)
                        .setParameter("get", "true")
                        .build();
                httpget = new HttpGet(uri);

                httpclient = HttpClients.createDefault();
                response = httpclient.execute(httpget);

                try {

                    entity = response.getEntity();
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity, "UTF-8");

                        jsonParser = new JSONParser();
                        try {

                            obj = jsonParser.parse(responseString);
                            json = (JSONObject) obj;

                            ip = (String) json.get("ip");
                            agente = (String) json.get("randomUserAgent");
                            port = Integer.parseInt((String) json.get("port"));

                            System.out.println(
                                    "-------- "
                                    + "\n Proxy "
                                    + "\n IP     :" + ip
                                    + "\n Port   :" + port
                                    + "\n Agente :" + agente
                                    + "\n-------- "
                            );

                            // salir de ciclo porque todo esta bien
                            break;
                        } catch (ParseException ex) {
                            Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } finally {
                    response.close();
                    httpclient.close();
                }

            } catch (URISyntaxException ex) {

                System.out.println("URISyntaxException ex [" + i + "]");
                Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                System.out.println("IOException ex [" + i + "]");
                Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }// ++i

    }
}
