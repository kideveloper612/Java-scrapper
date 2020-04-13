/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author root
 */
public class Modelo {
    
    protected  String tabla;
    
   
    
    public  boolean EliminarTodo(){
        boolean result=false;
        String query = "DELETE FROM " +tabla;
        
        try{
            Connection conn = ConnectionPool.getInstance().getConnection(); 
            PreparedStatement st = conn.prepareStatement(query);
            st.execute();
            conn.close();
            result=true;
            
        }
        catch (Exception ex){
            System.out.println("Ex: "+ ex.getMessage());
        }
        return result;        
    }
    public  boolean EliminarAsin(String asin){
        boolean result=false;
        String query = "DELETE FROM " +tabla +" WHERE asin='"+ asin+"'";
        
        try{
            Connection conn = ConnectionPool.getInstance().getConnection(); 
            PreparedStatement st = conn.prepareStatement(query);
            st.execute();
            conn.close();
            result=true;
            
        }
        catch (Exception ex){
            System.out.println("Ex: "+ ex.getMessage());
        }
        return result;        
    }
        
    public String   getNombreTabla(){ return tabla;}
    

}
