/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.scrapper.util;

/**
 *
 * @author cuellito
 */
public class ProxyInfo {
    public String ip="";
    public int port=0;

    public ProxyInfo(String ip,int port) {
        this.ip=ip;
        this.port=port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    
    
}
