/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;

import lombok.Data;

@Data
public class Configuracion{

    private int maximoConsulta;
    /**
     * What's the maximum length of content to keep in memory?
     */
    private int productosxMinuto;
    /**
     * How should the log files be sorted?
     */
    private int id=-1;
    private int minutosEspera;
    private int tiempoEspera;
    private int maximoUpdate;
    private int tiempoUpdate;
    
    public Configuracion(
            int id,
            int maximoconsulta,
            int productosxminuto,
            int minutosespera,
            int tiempoespera,
            int maximoupdate,
            int tiempoupdate
    ){
       this.id=id;
       this.maximoConsulta=maximoconsulta;
       this.productosxMinuto=productosxminuto;
       this.minutosEspera=minutosespera;
       this.tiempoEspera=tiempoespera;
       this.maximoUpdate=maximoupdate;
       this.tiempoUpdate=tiempoupdate;
    }
    public String toString(){
        return
            "\n --------------------"+    
            "\n Configura"+
            "\n --------------------"+     
            "\n id               : "+ this.id +
            "\n minutos Espera   : "+ this.minutosEspera+
            "\n productosxMinuto : "+ this.productosxMinuto+
            "\n minutos Espera   : "+ this.minutosEspera+
            "\n tiempo Espera    : "+ this.tiempoEspera+
            "\n maximo Update    : "+ this.maximoUpdate+
            "\n tiempo Update    : "+ this.tiempoUpdate+    
            "\n --------------------";
    }
}
