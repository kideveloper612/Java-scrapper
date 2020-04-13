/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;

/**
 *
 * @author casa
 */
public class ProductoNoExisteException extends Exception{
    
    public ProductoNoExisteException(String msg){
        super(msg);
    }
    
}
