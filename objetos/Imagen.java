/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;



import lombok.Data;

@Data
public class Imagen{

    private int    id;
    private String asin;
    private String url;
    private String fechacrea;
    
    public Imagen(
        int id,
        String  asin,
        String  url,
        String fecha_crea
    ){
        this.id=id;
        this.asin=asin;
        this.url=url;
        this.fechacrea  =fecha_crea;
    }
    
    public String toString(){
        return
            "\n --------------------"+    
            "\n imagene"+
            "\n --------------------"+     
            "\n id  : "+ this.id +
            "\n asin: "+this.asin+
            "\n url : "+this.url+
            "\n fechacrea   : "+this.fechacrea+
            "\n --------------------";
    }
}
