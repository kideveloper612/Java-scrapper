/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.modelo;

import com.scrapper.db.ConnectionPool;
import com.scrapper.objetos.NoProcesado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author root
 */
public class NoProcesadoModelo extends Modelo{
    
    public NoProcesadoModelo(){
        tabla="noprocesado2";
    }
    public NoProcesado BuscarAsin(String asin)
    {
        NoProcesado result=null;
        
        String query = "SELECT * FROM " +tabla+ " WHERE asin = ?";
        try{
            
            Connection conn = ConnectionPool.getInstance().getConnection(); 
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, asin);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                result = new NoProcesado(
                        rs.getInt("id"),
                        rs.getString("asin"),
                        rs.getString("motivo"),
                        rs.getString("fechacrea")
                    );
            }
            conn.close();
            
        }catch (Exception ex){
            System.out.println("BuscarAsin Ex: "+ ex.getMessage());
        }
        
        return result;
    }
    public boolean Crear(NoProcesado obj) {
        return Crear(obj.getAsin(), obj.getMotivo()); 
    }
    public boolean Crear(String asin, String motivo){
        boolean result=false;
        
        String query = "INSERT INTO "+tabla+" (asin,motivo) VALUES (?,?)";
	try{
            Connection conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, asin);
            statement.setString(2, motivo);
            statement.execute();
            result=true;
            conn.close();
	}catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("NoProcesadoModelo: Producto repetido " + asin);
        }
        return result;
    }    
}
