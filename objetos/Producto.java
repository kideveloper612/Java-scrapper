/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.objetos;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Data;
@Data 
public class Producto {
    private int id;
    private String asin;
    private String titulo="";
    private String descripcion="";
    
    private float precio;
    private float precioML;
    private float shipping;
    private float reviews=0.0f;
    private float precioMLNew;
    
    private String categoria;
    private String subcategoria;
    
    private String marca="";
    private String modelo="";
    
    private String status="INACTIVO";
    private String statusml="paused";
    
    private String disponible="";
    private String proveedor;
    private Timestamp fechaUpdate;
    
    private List<String> imagenes;
    private List<String> bullets;
    
    private Map<String,String> getDetallesBullet;
    private Map<String,String> getInformacionTecnica; 
    
    private String sku;
    private String mlCode;
    private boolean changed;
    private BigDecimal cantidad;
    
    private String fechaupdate;
    private String fechacrea;
    
    private String tipo="SIMPLE";
    private String url="url";
    
    public Producto(
        int id,
        String  asin,
        String  sku,
        String  titulo,
        String  descripcion,
        String  marca,
        String  modelo,
        String  categoria,
        String  subcategoria,
        String  proveedor,
        String  disponible,
        String  status,
        String  mlcode,
        String  statusml,
        float   precio,
        float   precioml,
        float   reviews,
        BigDecimal cantidad,
        float   shipping,
        float   preciomlnew,
        String  fecha_crea,
        String  fecha_update
    ){
        this.id=id;
        this.asin=asin;
        this.sku=sku;
        this.precio=precio;
        this.titulo=titulo;
        this.descripcion=descripcion;
        this.marca=marca;
        this.modelo=modelo;
        this.categoria=categoria;
        this.subcategoria=subcategoria;
        this.proveedor=proveedor;
        this.disponible=disponible;
        this.cantidad=cantidad;
        
        this.status=status;
        this.mlCode=mlcode;
        this.statusml=statusml;
        this.precioML=precioml;
        this.reviews=reviews;
        this.shipping=shipping;
        this.precioMLNew=preciomlnew;
        
        this.fechacrea=fecha_crea;
        this.fechaupdate=fecha_update; 
        
        getDetallesBullet=null;
        getInformacionTecnica=null;
        imagenes=null;
        bullets=null;
    }    

    public Producto(){
    }
    /* construir  producto en funcion de amazon*/
    
    public void AmpliarDescripcion(){
        
        String descripcionText="Por favor no olvide preguntar por la disponibilidad del producto antes de realizar la compra, un operador lo atenderá lo más pronto posible.";
                            
        descripcionText +=
            ( getDescripcion() !=null 
              && getDescripcion().indexOf("Por favor no olvide preguntar") <0 
            )
            ? "\n" + getDescripcion()
            : getDescripcion();
        
        
        if( getProveedor() != null  
            && (
                getProveedor().equals("Amazon México")
                || getProveedor().contains("Amazon México")
            )
        ){
            descripcionText+=
                "Envío gratis a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas.(SkyDrop, SpinBox, Redpack, etc.)\n" +
                "El envío no tendrá ningún costo adicional y tomará de 1 a 3 días en llegar a su domicilio. Podrá tomar de 3 a 5 días para algunas zonas rurales.\n" +
                "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
        }else
            if( getProveedor()!=null 
                && (
                    getProveedor().equals("Amazon EE.UU")
                    || getProveedor().contains("Amazon Estados Unidos")
                    || getProveedor().contains("Amazon EE.UU") 
                    ||(
                        getProveedor()!=null 
                        && getProveedor().equals("Amazon Estados Unidos"))
                    )
            ){
                descripcionText+=
                    "Envío gratuito a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas. (SkyDrop, SpinBox, Redpack, etc.)\n" +
                    "Por ser un envío de importación el tiempo estimado de entrega es de 3 a 6 días hábiles dependiendo del despacho aduanal. Podrá tomar de 6 a 9 días para algunas zonas rurales.\n" +
                    "Adicionalmente, si requiere del producto antes del tiempo de entrega estimado, puede solicitar un envío express por un cargo adicional de $199 Pesos tras realizar su compra.\n" +
                    "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
            } else
                if (getProveedor() != null 
                    && (
                        getProveedor().contains("Vendido por")
                        || getProveedor().contains("y enviado por Amazon")
                    )
                ){
                    descripcionText+=
                        "Envío gratis a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas.(SkyDrop, SpinBox, Redpack, etc.)\n" +
                        "El envío no tendrá ningún costo adicional y tomará de 1 a 3 días en llegar a su domicilio. Podrá tomar de 3 a 5 días para algunas zonas rurales.\n" +
                        "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
                }else {
                    descripcionText+=
                        "Envío gratuito a toda la república Méxicana por DHL, FedEx, UPS o Paqueterías privadas. (SkyDrop, SpinBox, Redpack, etc.)\n" +
                        "Por ser un envío de importación el tiempo estimado de entrega es de 6 a 9 días hábiles dependiendo del despacho aduanal. Podrá tomar de 9 a 15 días para algunas zonas rurales.\n" +
                        "Por favor pregunte por la disponibilidad de inventario antes de realizar la compra.\n";
                }
            setDescripcion(descripcionText);

    }
    
    public void setGetInformacionTecnica(Map<String,String> informacion_tecnica){
        this.getInformacionTecnica=informacion_tecnica;
    }
    
    public void setGetDetallesBullet(Map<String,String> detalles_bullet){
        this.getDetallesBullet=detalles_bullet;
    }    
    public void setAsin(String asin){
        this.asin=asin;
    }
    
    public void setDescripcion(String descripcion){
        this.descripcion=descripcion;
    }
    
    public void setDisponible(String disponible){
        this.disponible=disponible;
    }
    
    public void setCantidad(BigDecimal cantidad){
        this.cantidad= cantidad;
    }
    public boolean validarCantidad(){
        boolean result=false;
        cantidad =(cantidad != null) ? cantidad: new BigDecimal(0) ;
        
        if(cantidad.intValue() >=3 ){
            setStatus("ACTIVO");
            Activar_ML();
            result=true;
        }else{
            cantidad=new BigDecimal(0);
            setStatus("INACTIVO");
            Pausar_ML();
        }
        
        return result;
    }
    public void setProveedor(String proveedor){
        this.proveedor=proveedor;
    }
    
    public void setCategoria(String categoria){
        this.categoria=categoria;
    }
    public void setSubcategoria(String subcategoria){
        this.subcategoria=subcategoria;
    }    
    public void setPrecio(float precio){
        this.precio=precio;
    }
    public void setStatusML(String statusml){
        this.statusml=statusml;
    }
    public void Pausar_ML(){
        setStatusML("paused");
        setStatus("INACTIVO");
    }
    
    public void Activar_ML(){
        setStatusML("active");
        setStatus("ACTIVO");
    }
    
    public void setStatus(String status){
        this.status=status;
    }
    public void setStatusML(){
        if( disponible.equals("No disponible por el momento.")){
            setPrecio  ( 0);
            setCantidad( new BigDecimal(0));
            Pausar_ML();
        }else{
            // 
            if( precio >0
                && precio > shipping
            ){
                if(    (disponible.equals("Disponible a través de estos vendedores.") && cantidad.intValue() >= 1)
                    || (disponible.equals("Disponible.") && cantidad.intValue() >= 3) 
                ){
                    Activar_ML();
                }else{
                    Pausar_ML();
                }
            }    
        }
    }
    
    public void setTitulo(String titulo){
        this.titulo=titulo;
    }

    public void setBullets(List<String> bullets){
        this.bullets=bullets;
    }
    public void setImagenes(List<String> imagenes){
        this.imagenes=imagenes;
    }    
    /* get */
    public String getDescripcion(){ return descripcion;}
    public String getProveedor()  { return proveedor;  }
    public float getPrecio()  { return precio;  }
    public String getDisponible()   { return disponible;  }    
    public BigDecimal getCantidad() { return cantidad;    }
    public String getCategoria()    { return categoria;   }
    public String getSubCategoria() { return subcategoria;}
    public String getTitulo() { return titulo;}
    public String getAsin()   { return asin;}
    public String getTipo(){ return tipo;}
    public boolean getChanged(){ return changed;}
    public List<String> getImagenes()   { return imagenes;}
    public List<String> getBullets()   { return bullets;}
    
    public String getStatus()  { return status;}
    public int    getID()      { return id;}
    public String getCodigoML(){ return mlCode;}
    public float getPrecioML() { return precioML;}
    public String getStatusML()  { return statusml;}
    public float   getReviewsML(){ return reviews;}
    public float  getShippingML(){ return shipping;}
    public float  getPrecioNewML(){ return precioMLNew; }
    public float  getShipping(){ return shipping;}

        
    public float CalcularPrecioTotal(){
        return CalcularPrecioTotal(getProveedor(),getPrecio(),getShipping());
    }

    public float CalcularPrecioTotal(String proveedor, float precio, float envio){
        float total=0.0f;
        total+=precio;
        // agregar precio de envio si no es amazon 
        if(proveedor != null && proveedor.indexOf("Amazon")== -1){
            total+=envio;
        }
        return total;
    }
    
    @Override
    public String toString(){
        Iterator it;
        String key="";
        String result= 
            "\n --------------------"+    
            "\n Producto"+
            "\n --------------------"+     
            "\n Id:           " + getID()         +
            "\n ASIN:         " + getAsin()         +
            "\n Titulo:       " + getTitulo()       +
            "\n Categoria:    " + getCategoria()    +
            "\n Subcategoria: " + getSubCategoria() +
            "\n Precio :      " + getPrecio()   
            +"\n Proveedor:    " + getProveedor()
            +"\n Disponible:   " + getDisponible()
            +"\n Cantidad:     " + getCantidad()
            +"\n Tipo:        " + getTipo()
            +"\n Status:     " + getStatus()
            +"\n Shipping:" + getShippingML()
            +"\n Mercado Libre"
            +"\n Codigo :" + getCodigoML()
            +"\n Precio :" + getPrecioML()
            +"\n Status :" + getStatusML()
            +"\n Reviews:" + getReviewsML()
            +"\n precio New :" + getPrecioNewML()    
            +"\n Descripcion: \n" + getDescripcion()
        ;
        
        result+="\n Imagenes:\n";
        if(imagenes != null)
            for(String imagen : imagenes)
                result+="\n"+imagen;
        
        result+="\n Bullets:\n";
        if(bullets != null)
            for(String bullet : bullets)
                result+="\n"+bullet;
        
        result+="\n getDetallesBullet:\n";
        if(getDetallesBullet !=null && !getDetallesBullet.isEmpty()){
            it = getDetallesBullet.keySet().iterator();
        
            while(it.hasNext()){
                key= (String) it.next();
                result +="\n" + key +":"+ getDetallesBullet.get(key);
            }
        }    
        result+=
            "\n Informacion Tecnica:\n";
        if(getInformacionTecnica !=null && !getInformacionTecnica.isEmpty()){
            it = getInformacionTecnica.keySet().iterator();
            while(it.hasNext()){
                key= (String) it.next();
                result +="\n " + key +" :" + getInformacionTecnica.get(key);
            }
        }
        result += "\n --------------------"; 
               
        return result;
    }
    
    
}