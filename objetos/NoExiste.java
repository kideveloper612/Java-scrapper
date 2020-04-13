/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;



import lombok.Data;

@Data
public class NoExiste{

    private int    id;
    private String asin;
    private String fechacrea;
    
    public NoExiste(
        int id,
        String  asin,
        String fecha_crea
    ){
        this.id=id;
        this.asin=asin;
        this.fechacrea  =fecha_crea;
    }
    
    public String toString(){
        return
            "\n --------------------"+    
            "\n No Exiete"+
            "\n --------------------"+       
            "\n id  : "+ this.id +
            "\n asin: "+this.asin+
            "\n fechacrea   : "+this.fechacrea +
            "\n --------------------";
    }
}
