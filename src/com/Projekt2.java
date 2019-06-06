/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;
import ilog.concert.*;
import ilog.cp.IloCP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author WaMa
 */
public class Projekt2 {

    /**
     * @param args the command line arguments
     * @throws ilog.concert.IloException
     */
    public static void main(String[] args) throws IloException, FileNotFoundException, IOException {
        // TODO code application logic here
        final int[] days = {1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2};
        final int[] slots = {1,1,1,2,2,2,3,4,4,1,1,1,2,2,2,3,3,4,4};
        final int[] leader = {1,2,3,1,2,3,1,1,3,1,2,3,1,2,3,1,3,2,3};
        final int[][] defenses = {{4,5},{6,4},{1,4},{2,5},{4,6},{4,6},{6,3},{3,4},{5,6},{4,5},{5,6},{3,4},{5,6},{4,5},{2,1},{5,4},{6,2},{4,6},{2,1},{6,4}};
        IloCP cp = new IloCP();
        int n = defenses.length;
        int m = days.length;
        IloIntVar[] assignment = new IloIntVar[n];
        IloIntVar[] selected = cp.boolVarArray(n);
        for(int i = 0; i < n; i++) {
            assignment[i] = cp.intVar(0, n-1);
        }
        cp.add(cp.allDiff(assignment));
        cp.addLe(cp.sum(selected), m);
        IloIntExpr[] index = new IloIntExpr[n];
        for(int i =0; i < n; i++) {
            index[i] = cp.sum(selected,0,i);
        }
        
        for(int a = 0; a < n; a++) {
            IloIntExpr va = cp.element(assignment, index[a]);
            cp.add(cp.neq(cp.element(leader, va), defenses[a][0]));
            cp.add(cp.neq(cp.element(leader, va), defenses[a][1]));
            for(int b = 0; b < n; b++) {
                if(b >= a) break;
                int pp = (defenses[a][0] == defenses[b][0]) ? 1 : 0;
                int rr = (defenses[a][1] == defenses[b][1]) ? 1 : 0;
                int pr = (defenses[a][0] == defenses[b][1]) ? 1 : 0;
                int rp = (defenses[a][1] == defenses[b][0]) ? 1 : 0;
                int s = pp + rr + pr + rp;
                if(s > 0) {

                    IloIntExpr vb = cp.element(assignment, index[b]);
                    /*IloConstraint c = cp.and(cp.eq(cp.div(va, TERMINY), cp.div(vb, TERMINY)), cp.eq(cp.abs(cp.diff(va, vb)), 1));
                    score = cp.sum(score, cp.prod(s,c));
                    cp.add(cp.neq(cp.modulo(va, TERMINY), cp.modulo(vb, TERMINY)));*/
                    cp.add(cp.or(new IloConstraint[]{cp.eq(selected[a], 0),cp.eq(selected[b], 0), 
                        cp.neq(cp.element(days, va), 
                        cp.element(days, vb)), 
                        cp.neq(cp.element(slots, va), cp.element(slots,vb))}));
                    
                    
                }
                
            }
        }
        cp.addMaximize(cp.sum(selected));
        cp.setParameter(IloCP.DoubleParam.TimeLimit, 10);
        cp.solve();
        for(int i = 0; i < n; i++) {
            System.out.println(cp.getValue(selected[i]));
        }
        /*int [][][] plan = new int[3][][];
        for(int i = 0; i < n; i++) {
            int x = (int)Math.round(cp.getValue(assignment[i]));
            plan[x/TERMINY][x%TERMINY] = defenses[i];
        }
        for(int t = 0; t < TERMINY; t++) {
            for(int k = 0; k < KOMISJE; k++) {
                if(plan[k][t] != null)
                    System.out.print("(" + plan[k][t][0] + "," + plan[k][t][1] + ")");
                if (k < KOMISJE - 1)
                    System.out.print("\t");
                else
                    System.out.println();
            }
        }*/
        Map<Pair<Integer, Integer>, List<int[]>> plan = new HashMap<>();
        int sum = 0;
        for(int i = 0; i < n; i++) {
            int s = (int)(cp.getValue(selected[i])+0.5);
            if(s == 0) continue;
            int ass = (int)(cp.getValue(assignment[sum])+0.5);
            Pair<Integer, Integer> p = new Pair<>(days[ass], slots[ass]);
            List<int[]> defs = plan.get(p);
            if(defs == null) {
                defs = new ArrayList<>();
                plan.put(p, defs);
            }
            defs.add(new int[]{leader[ass], defenses[i][0], defenses[i][1]});
            sum += s;
        }
       

        
        for (Pair<Integer, Integer> p : plan.keySet()) {
            System.out.print("[" + p.getKey() + "," + p.getValue() + "] : ");
     
            for(int[] d : plan.get(p)) {
                             
                
                System.out.print("(" + d[0] + "," + d[1] + "," + d[2] + "), ");
            }
            
            
            System.out.println();
        }
        //countrows
        int rowcount=5;
         for (Pair<Integer, Integer> p : plan.keySet()) {
            rowcount++;
        }
        // output
        
        //XSSFWorkbook workbook = new XSSFWorkbook();
        
        
        
        int xd=2;
        int xdd=2;
        Object[][] datatypes=new Object[rowcount][12];
        datatypes[0][0]="czas";
        datatypes[0][2]="komisja1";
        datatypes[0][5]="komisja2";
        datatypes[0][8]="komisja3";
        datatypes[1][0]="dzien";
        datatypes[1][1]="slot";
        datatypes[1][2]="przewodniczacy";
        datatypes[1][3]="recenzent";
        datatypes[1][4]="promotor";
        datatypes[1][5]="przewodniczacy";
        datatypes[1][6]="recenzent";
        datatypes[1][7]="promotor";
        datatypes[1][8]="przewodniczacy";
        datatypes[1][9]="recenzent";
        datatypes[1][10]="promotor";
        String FILE_NAME = "MyFirstExcel.xlsx";
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
        
        
        for (Pair<Integer, Integer> p : plan.keySet()) {
        Set<Integer> lump = new HashSet<Integer>();
            datatypes[xdd][0]=p.getKey();
            datatypes[xdd][1]=p.getValue();
            
            for(int[] d : plan.get(p)) {
                datatypes[xdd][xd]=d[0];
                datatypes[xdd][xd+1]=d[1];
                datatypes[xdd][xd+2]=d[2];
                
                
                
                xd+=3;
            }
            xdd++;
            xd=2;
        
        }
        
       
	
        

        int rowNum = 0;
        System.out.println("Creating excel");

        for (Object[] datatype : datatypes) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }
        
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
        sheet.addMergedRegion(new CellRangeAddress(0,0,2,4));
        sheet.addMergedRegion(new CellRangeAddress(0,0,5,7));
        sheet.addMergedRegion(new CellRangeAddress(0,0,8,10));
        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    

        
        
        
        
        //excepty        
        for (Pair<Integer, Integer> p : plan.keySet()) {
        Set<Integer> lump = new HashSet<Integer>();
         
            for(int[] d : plan.get(p)) {
                if (lump.contains(d[0])) {
                    throw new RuntimeException("powrtorka");
                };
                lump.add(d[0]);
                if (lump.contains(d[1])) {
                    throw new RuntimeException("powrtorka");
                };
                lump.add(d[1]);
                if (lump.contains(d[2])) {
                    throw new RuntimeException("powrtorka");
                };
                lump.add(d[2]);
                
            }
        
        }
        
        
        
    }
    
     
    
}
