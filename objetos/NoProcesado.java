/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;
import lombok.Data;

@Data
public class NoProcesado{

    private int    id;
    private String asin;
    private String motivo;
    private String fechacrea;
    
    public NoProcesado(
        int id,
        String  asin,
        String  motivo,
        String fecha_crea
    ){
        this.id=id;
        this.asin=asin;
        this.motivo=motivo;
        this.fechacrea  =fecha_crea;
    }

    public NoProcesado() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String toString(){
        return
            "\n --------------------"+    
            "\n No Procesado"+
            "\n --------------------"+       
            "\n id  : "+ this.id +
            "\n asin: "+this.asin+
            "\n motivo: "+this.motivo+    
            "\n fechacrea   : "+this.fechacrea +
            "\n --------------------";
    }

}
