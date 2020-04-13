/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;



import lombok.Data;

@Data
public class Bullet{

    private int    id;
    private String asin;
    private String descripcion;
    private String fechacrea;
    
    public Bullet(
        int id,
        String  asin,
        String  descripcion,
        String fecha_crea
    ){
        this.id=id;
        this.asin=asin;
        this.descripcion=descripcion;
        this.fechacrea  =fecha_crea;
    }
    
    public String toString(){
        return
            "\n --------------------"+    
            "\n Bullet"+
            "\n --------------------"+       
            "\n id  : "+ this.id +
            "\n asin: "+this.asin+
            "\n descripcion : "+this.descripcion+
            "\n fechacrea   : "+this.fechacrea +
            "\n --------------------";
    }
}
