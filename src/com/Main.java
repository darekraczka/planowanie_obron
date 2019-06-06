package com;


import ilog.concert.*;
import ilog.cp.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.util.Pair;

import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

    public static void main(String[] args) throws IloException {
        Problem problem = new Problem("komisje.csv","obrony.csv");
        System.out.println();

        // TODO code application logic here
        final int[] days = problem.getDays();
        final int[] slots = problem.getSlots();
        final int[] leader = problem.getLeader();
        final int[][] defenses = problem.getDefenses();
        int n = defenses.length;
        int m = days.length;
        int obj1 = 0;
        for(int cnt : new int[]{0,1}) {
            IloCP cp = new IloCP();
            IloIntVar[] assignment = new IloIntVar[n];
            IloIntVar[] selected = cp.boolVarArray(n);
            for (int i = 0; i < n; i++) {
                assignment[i] = cp.intVar(0, n - 1);
            }
            cp.add(cp.allDiff(assignment));
            cp.addLe(cp.sum(selected), m);
            IloIntExpr[] index = new IloIntExpr[n];
            for (int i = 0; i < n; i++) {
                index[i] = cp.sum(selected, 0, i);
            }
            IloIntExpr score = cp.constant(0);
            for (int a = 0; a < n; a++) {
                IloIntExpr va = cp.element(assignment, index[a]);
                cp.add(cp.neq(cp.element(leader, va), defenses[a][0]));
                cp.add(cp.neq(cp.element(leader, va), defenses[a][1]));
                for (int b = 0; b < n; b++) {
                    IloIntExpr vb = cp.element(assignment, index[b]);
                    if (b >= a) break;
                    int pp = (defenses[a][0] == defenses[b][0]) ? 1 : 0;
                    int rr = (defenses[a][1] == defenses[b][1]) ? 1 : 0;
                    int pr = (defenses[a][0] == defenses[b][1]) ? 1 : 0;
                    int rp = (defenses[a][1] == defenses[b][0]) ? 1 : 0;
                    int pts = (defenses[a][0] == defenses[b][0] || defenses[a][0] == defenses[b][1]) ? 1 : 0;
                    pts += (defenses[a][1] == defenses[b][0] || defenses[a][1] == defenses[b][1]) ? 1 : 0;
                    int s = pp + rr + pr + rp;
                    if (s > 0) {

                    /*IloConstraint c = cp.and(cp.eq(cp.div(va, TERMINY), cp.div(vb, TERMINY)), cp.eq(cp.abs(cp.diff(va, vb)), 1));
                    score = cp.sum(score, cp.prod(s,c));
                    cp.add(cp.neq(cp.modulo(va, TERMINY), cp.modulo(vb, TERMINY)));*/
                        cp.add(cp.or(new IloConstraint[]{cp.eq(selected[a], 0), cp.eq(selected[b], 0),
                                cp.neq(cp.element(days, va), cp.element(days, vb)),
                                cp.neq(cp.element(slots, va), cp.element(slots, vb))}));

                    }
                    if (pts > 0) {
                        IloConstraint cd = cp.and(new IloConstraint[]{
                                cp.eq(selected[a], 1),
                                cp.eq(selected[b], 1),
                                cp.eq(cp.element(days, va), cp.element(days, vb))});
                        IloConstraint cs = cp.and(new IloConstraint[]{
                                cp.eq(selected[a], 1),
                                cp.eq(selected[b], 1),
                                cp.eq(cp.abs(cp.diff(cp.element(slots, va), cp.element(slots, vb))), 1)});
                        score = cp.sum(new IloIntExpr[]{score, cp.prod(cd, pts), cp.prod(cs, 2 * pts)});
                    }

                }
            }

            Map<Integer, List<Integer>> leaderIdToBoardId = new HashMap<>();
            for (int x : leader) {

                if (!leaderIdToBoardId.containsKey(x)) {
                    List<Integer> terms = new ArrayList<>();
                    for (int i = 0; i < leader.length; i++) {
                        if (x == leader[i]) terms.add(i);
                        Set<Pair<Integer, Integer>> s = new HashSet<>();
                        for (int ii = 0; ii < m; ii++) {
                            if (x == leader[ii]) s.add(new Pair<>(days[ii], slots[ii]));
                        }
                        for (int ii = 0; ii < m; ii++) {
                            Pair<Integer, Integer> t = new Pair<>(days[ii], slots[ii]);
                            if (s.contains(t)) terms.add(ii);
                        }
                    }
                    leaderIdToBoardId.put(x, terms);
                }
            }
            List<Integer> w = new ArrayList<>();
            for (int i = 0; i < defenses.length; i++) {
                Set<Integer> forbid = new HashSet<>();
                List<Integer> list = leaderIdToBoardId.get(defenses[i][0]);
                if (list != null) forbid.addAll(list);
                list = leaderIdToBoardId.get(defenses[i][1]);
                if (list != null) forbid.addAll(list);
                int[] ftab = new int[forbid.size()];
                int in = 0;
                for (int x : forbid) {
                    ftab[in++] = x;
                }
                if (!forbid.isEmpty())
                    cp.add(cp.or(cp.forbiddenAssignments(cp.element(assignment, index[i]), ftab), cp.eq(selected[i], 0)));
            }
            if(cnt==0) {
                cp.addMaximize(cp.sum(selected));
            }
            else{
                cp.addEq(cp.sum(selected), obj1);
                cp.addMaximize(score);}

            cp.setParameter(IloCP.DoubleParam.TimeLimit, 15);
            cp.setOut(null);
            cp.startNewSearch();
            while(cp.next()) {
                obj1 = (int) (cp.getObjValue() + 0.5);
                System.out.println("Etap" + (cnt+1) + "->" + obj1);
            }



            if(cnt==1) {
                System.out.println("obrony = " + n + " sloty = " + m);
                for (int i = 0; i < n; i++) {
                    //System.out.println(cp.getValue(selected[i]));
                }
                Map<Pair<Integer, Integer>, List<int[]>> plan = new HashMap<>();
                int sum = 0;
                for (int i = 0; i < n; i++) {
                    int s = (int) (cp.getValue(selected[i]) + 0.5);
                    if (s == 0) continue;
                    int ass = (int) (cp.getValue(assignment[sum]) + 0.5);
                    Pair<Integer, Integer> p = new Pair<>(days[ass], slots[ass]);
                    List<int[]> defs = plan.get(p);
                    if (defs == null) {
                        defs = new ArrayList<>();
                        plan.put(p, defs);
                    }
                    defs.add(new int[]{leader[ass], defenses[i][0], defenses[i][1]});
                    sum += s;
                }
                List<Pair<Integer,Integer>> ds = new ArrayList(plan.keySet());
                Collections.sort(ds, new Comparator<Pair<Integer, Integer>>() {
                    @Override
                    public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                        int c1 = Integer.compare(o1.getKey(), o2.getKey());
                        if(c1 != 0) return c1;
                        else return Integer.compare(o1.getValue(), o2.getValue());
                    }
                });
                for (Pair<Integer, Integer> p : ds) {
                    System.out.print("[" + p.getKey() + "," + p.getValue() + "] : ");
                    List<int[]> defs = plan.get(p);
                    Collections.sort(defs, new Comparator<int[]>() {
                        @Override
                        public int compare(int[] o1, int[] o2) {
                            return Integer.compare(o1[0], o2[0]);
                        }
                    });
                    for (int[] d : defs) {
                        System.out.print("\t(" + d[0] + "," + d[1] + "," + d[2] + ") ");
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
            cp.end();
        }
    }
}
