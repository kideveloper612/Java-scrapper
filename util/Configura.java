/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;



import com.scrapper.controller.ProcesaScrapper;
import com.scrapper.db.MySQLConnectionFactory;
import java.util.List;
import lombok.Data;

@Data
public class Configura{

    private static Configura instance = null;
    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProcesaScrapper.class);
    MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
   
    public static Configura getInstance() {
        if (Configura.instance == null) {
            Configura.instance = new Configura();
        }

        return Configura.instance;
    }

    private Integer maximoConsulta;

    /**
     * What's the maximum length of content to keep in memory?
     */
    private Integer productosxMinuto;

    /**
     * How should the log files be sorted?
     */
    private Integer minutosEspera;
    
    private Integer tiempoEspera;

    private Integer maximoUpdate;
    private Integer tiempoUpdate;
    
    private Integer threads_process;
    private Integer threads_agentes;
    
    
    private Configura() {
        
       List<Integer> valores=connectDB.getConfiguracion();
       this.maximoConsulta=valores.get(0);
       this.productosxMinuto=valores.get(1);
       this.minutosEspera=valores.get(2);
       this.tiempoEspera=valores.get(3);
       this.maximoUpdate=valores.get(4);
       this.tiempoUpdate=valores.get(5);
       this.threads_process=valores.get(6);
       this.threads_agentes=valores.get(7);
       
    }
    public String toString(){
        return 
            "\n maximo Consulta   "+ maximoConsulta  +
            "\n productosx Minuto "+ productosxMinuto+
            "\n minutos Espera    "+ minutosEspera+
            "\n tiempo Espera     "+ tiempoEspera +
            "\n maximo Update     "+ maximoUpdate +
            "\n tiempo Update     "+ tiempoUpdate +
            "\n threads process     "+ threads_process +
            "\n threads agentes     "+ threads_agentes;
    }
}
