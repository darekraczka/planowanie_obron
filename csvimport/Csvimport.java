/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvimport;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
/**
 *
 * @author student
 */


public class Csvimport {
   
    static List<Komisja> komisje;
    static List<Obrony> obrony;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String csvFile1 = "komisje.csv";
        String csvFile2 = "obrony.csv";
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        int iter1=0;
        int iter2=0;
        String line1 = "";
        String line2 = "";
        String cvsSplitBy = ",";
        
        //Komisja[] komisje = null;
        //Obrony[] obrony = null;
        
        komisje = new ArrayList<>();
        obrony = new ArrayList<>();
        
        try {

            br2 = new BufferedReader(new FileReader(csvFile2));
            while ((line2 = br2.readLine()) != null) {

                // use comma as separator
                String[] obron = line2.split(cvsSplitBy);
                if (obron.length !=3){
                    System.out.println(obron[0]);
                 
                    System.out.println("zła ilość kolumn");
                    //System.exit(1);
                                                        
                }
                else{
                    obrony.add(new Obrony());
                    
                }
               // System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br2 != null) {
                try {
                    br2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {

            br1 = new BufferedReader(new FileReader(csvFile1));
            while ((line1 = br1.readLine()) != null) {

                // use comma as separator
                String[] komisj = line1.split(cvsSplitBy);
                if (komisj.length !=3){
                    
                    System.out.println("zła ilość kolumn kom");
                    System.exit(1);
                                                        
                }
                else{
                    komisje.add(new Komisja(komisj[0],komisj[1],komisj[2]));
                  
                    
                }
               // System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br1 != null) {
                try {
                    br1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
          for(int i = 0; i < obrony.size(); i++) {
            System.out.println(obrony.get(i).id_rec);
        }
          for(int i = 0; i < komisje.size(); i++) {
            System.out.println(komisje.get(i).id_przew);
        }
        
        
        // TODO code application logic here
    }
    
}

