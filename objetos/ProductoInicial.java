/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;



import lombok.Data;

@Data
public class ProductoInicial{

    private int    id;
    private String asin;
    private String sku;
    private String url;
    private String titulo;
    private String descripcion;
    
    private int procesado;
    private int idusuario;
    
    private String fechaupdate;
    private String fechacrea;
    
    private int procesado2;
    private int flagerror; 
    
    public ProductoInicial(
        int id,
        String  asin,
        String  sku,
        String  url,
        String  titulo,
        String  descripcion,
        int usuario_id,
        int procesado,
        int procesado2,
        int flagerror,
        String fecha_crea,
        String fecha_update
        
    ){
        this.id=id;
        this.asin=asin;
        this.sku=sku;
        this.url=url;
        this.titulo=titulo;
        this.idusuario=usuario_id;
        this.descripcion=descripcion;
        this.fechacrea  =fecha_crea;
        this.fechaupdate=fecha_update;
        this.procesado =procesado;
        this.procesado2=procesado2;
        this.flagerror=flagerror; 
    }
    
    public String toString(){
        return
            "\n --------------------"+    
            "\n Producto Inicial"+
            "\n --------------------"+       
            "\n id  : "+ this.id +
            "\n asin: "+this.asin+
            "\n sku : "+this.sku+
            "\n url : "+this.url+
            "\n titulo : "+this.titulo+
            "\n idusuario : "+this.idusuario+
            "\n descripcion : "+this.descripcion+
            "\n fechacrea   : "+this.fechacrea+
            "\n fechaupdate : "+this.fechaupdate+
            "\n procesado   : "+this.procesado+
            "\n procesado2  : "+this.procesado2+
            "\n flagerror   : "+this.flagerror+
            "\n --------------------"; 
    }
}
