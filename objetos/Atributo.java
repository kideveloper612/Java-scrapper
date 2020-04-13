/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;



import lombok.Data;

@Data
public class Atributo{

    private int    id;
    private String asin;
    private String nombre;
    private String valor;
    private String fechacrea;
    
    public Atributo(
        int id,
        String  asin,
        String  nombre,
        String  valor,
        String fecha_crea
    ){
        this.id=id;
        this.asin=asin;
        this.nombre=nombre;
        this.valor=valor;
        this.fechacrea  =fecha_crea;
    }
    
    public String toString(){
        return
            "\n --------------------"+    
            "\n Atributo"+
            "\n --------------------"+    
            "\n id     : "+ this.id +
            "\n asin   : "+this.asin+
            "\n nombre : "+this.nombre+
            "\n valor  : "+this.valor+    
            "\n fechacrea  : "+this.fechacrea;
    }
}
