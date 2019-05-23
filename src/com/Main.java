/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvimport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author student
 */


public class Main {
 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String csvFile = "/komisje.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] komisje = line.split(cvsSplitBy);
                if (komisje.length !=3){
                    
                    System.out.println("zła ilość kolumn");
                    System.exit(1);
                                                        
                }
                else{
                    
                    
                    
                }
               // System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
        // TODO code application logic here
    }
    
}

