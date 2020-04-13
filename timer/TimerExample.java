/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.timer;

import java.util.Date;
import java.util.TimerTask;

public class TimerExample extends TimerTask{
private String name ;
public TimerExample(String n){
  this.name=n;
}
@Override
public void run() {
    System.out.println(Thread.currentThread().getName()+" "+name+" the task has executed successfully "+ new Date());
  
      try {
      Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

}
}