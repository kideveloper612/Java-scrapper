/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.timer;

import java.util.Timer;
public class ExecuteTimer {
  public static void main(String[] args){
       TimerExample te1=new TimerExample("Task1");
       TimerExample te2=new TimerExample("Task2");
      Timer t=new Timer();
      t.scheduleAtFixedRate(te1, 0,5*1000);
      t.scheduleAtFixedRate(te2, 0,1000);
   }
}