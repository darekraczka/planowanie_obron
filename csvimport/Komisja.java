/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvimport;

/**
 *
 * @author student
 */
public class Komisja {
    int dzien, slot, id_przew;
    public Komisja(){}
    public Komisja(String d,String s,String id){
    dzien = Integer.parseInt(d);
    slot = Integer.parseInt(s);
    id_przew = Integer.parseInt(id);
    }
    
}
