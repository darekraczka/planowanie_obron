/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author WaMa
 */
public class CSVwriter {

    public static void write(Map<Pair<Integer, Integer>, List<int[]>> plan, List<Pair<Integer, Integer>> k) {
        try (BufferedWriter fw = Files.newBufferedWriter(
                Paths.get("komisje3.csv"), Charset.defaultCharset())) {
            fw.write("data,przew,godz");
            fw.newLine();
        for(Pair<Integer, Integer> p : k) {
            int d = p.getKey();
            int s = p.getValue();
            for(int[] tab: plan.get(p)) {
                int l = tab[0];
                fw.write(d+","+s+","+l);
                fw.newLine();
            }
        }
        } catch (IOException ex) {
            Logger.getLogger(CSVwriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (BufferedWriter fw = Files.newBufferedWriter(
                Paths.get("obrony3.csv"), Charset.defaultCharset())) {
            fw.write("opiekun,recenzent");
            fw.newLine();
        for(Pair<Integer, Integer> p : k) {
            for(int[] tab: plan.get(p)) {
                int o = tab[1];
                int r = tab[2];
                fw.write(o+","+r);
                fw.newLine();
            }
        }
        } catch (IOException ex) {
            Logger.getLogger(CSVwriter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
