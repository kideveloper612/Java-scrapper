/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;

import java.net.*;
import java.util.Scanner;

 

class ProxiTest {
  public static void main(String[] args) throws Exception {
   String str1="192.168.0.201";
String str2="255.255.255.0";
String[] command1 = { "netsh", "interface", "ip", "set", "address",
"name=", "Local Area Connection" ,"source=static", "addr=",str1,
"mask=", str2};
Process pp = java.lang.Runtime.getRuntime().exec(command1);

      /* InetSocketAddress proxyAddress = new InetSocketAddress("192.168.0.14", 1212); // Set proxy IP/port.
    Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
    URL url = new URL("https://www.google.com/");
    URLConnection urlConnection = url.openConnection(proxy);
    Scanner scanner = new Scanner(urlConnection.getInputStream());
    System.out.println(scanner.next());
    scanner.close();*/
      /*
       URL url = new URL("http://google.com");
Proxy proxy = new Proxy(Proxy.Type.DIRECT,
    new InetSocketAddress(
        InetAddress.getByAddress(
                new byte[]{your, ip, interface, here}), yourTcpPortHere));
URLConnection conn = url.openConnection(proxy);
      */
  }
}