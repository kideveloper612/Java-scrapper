/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;
import lombok.Data;

@Data
public class HistoricoPrecio{

    private int id;
    public Producto poducto_nuevo;
    public Producto poducto_viejo;
    
    public HistoricoPrecio(
        int id,
        Producto poducto_viejo,
        Producto poducto_nuevo
    ){
        this.id=id;
        this.poducto_nuevo=poducto_nuevo;
        this.poducto_viejo=poducto_viejo;
    }
    
}
