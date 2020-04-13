/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public final class AdministradorArchivo {
   
    public static void CrearArchivo(String str, String ruta, String nombre, String extension){
        try {
                nombre = nombre + extension;
                File file = new File(ruta+nombre);
                // Si el archivo no existe es creado
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(str);
                bw.close();
        } catch (Exception e) {
                System.out.println("error al crear el archivo");
        }
    
    }
    
    public static String leerArchivo(String ruta, String nombre, String extension) {
        
        File fichero = new File(ruta+nombre+"."+extension);
        FileReader fr = null;
        BufferedReader br = null;
        
        String str="";
        try {
            fr = new FileReader (fichero);
            br = new BufferedReader(fr);

         // Lectura del fichero
         String linea;
         while((linea=br.readLine())!=null){
                //if(!linea.equals(""))
                   str+="\n"+linea; 	// Guardamos la linea en un String
            }
        } catch (Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
	} finally {
            // Cerramos el fichero tanto si la lectura ha sido correcta o no
            try {
                if ( br != null)
                     br.close();
            } catch (Exception ex2) {
                System.out.println("Ex: " + ex2.getMessage());
            }
	}
        return str;
    }
    
    public static List<String> leerLineasArchivo(String ruta, String nombre, String extension) {
        
        File fichero = new File(ruta+nombre+"."+extension);
        FileReader fr = null;
        BufferedReader br = null;
        
        List<String> lineas=new ArrayList<String>();
        try {
            fr = new FileReader (fichero);
            br = new BufferedReader(fr);

         // Lectura del fichero
         String linea;
         while((linea=br.readLine())!=null){
                //if(!linea.equals(""))
                   lineas.add(linea); 	// Guardamos la linea en un String
            }
        } catch (Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
	} finally {
            // Cerramos el fichero tanto si la lectura ha sido correcta o no
            try {
                if ( br != null)
                     br.close();
            } catch (Exception ex2) {
                System.out.println("Ex: " + ex2.getMessage());
            }
	}
        return lineas;
    }
}
