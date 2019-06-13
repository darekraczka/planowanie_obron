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
        Problem problem = Reader.createProblem(Reader.readDataFromExcel("obrony_2017.xlsx"));
        //Problem problem1 = new Problem(Reader.readKomisje("komisje2.csv"), Reader.readObrony("obrony2.csv"));

        System.out.println();

        // TODO code application logic here
        final int[] days = problem.getDays();
        final int[] slots = problem.getSlots();
        final int[] leader = problem.getLeader();
        final int[][] defenses = problem.getDefenses();
        int n = defenses.length;
        int m = days.length;
        int obj1 = 0;
        CTRframe frame = null;
        for (int cnt : new int[]{0, 1}) {
            IloCP cp = new IloCP();
            long tim = System.currentTimeMillis();
            if (cnt == 0) {
                frame = new CTRframe();
            }
            frame.setCp(cp);
            frame.setVisible(true);
            frame.setSt(tim);
            frame.dispMsg("\n");
            frame.setTitle("Planowanie Obron: Etap " + (cnt+1));
            frame.setEtap("Oczekiwanie...");
            frame.buttonEnable(false);
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
                    if (b >= a) {
                        break;
                    }
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
                        if (x == leader[i]) {
                            terms.add(i);
                        }
                        Set<Pair<Integer, Integer>> s = new HashSet<>();
                        for (int ii = 0; ii < m; ii++) {
                            if (x == leader[ii]) {
                                s.add(new Pair<>(days[ii], slots[ii]));
                            }
                        }
                        for (int ii = 0; ii < m; ii++) {
                            Pair<Integer, Integer> t = new Pair<>(days[ii], slots[ii]);
                            if (s.contains(t)) {
                                terms.add(ii);
                            }
                        }
                    }
                    leaderIdToBoardId.put(x, terms);
                }
            }
            List<Integer> w = new ArrayList<>();
            for (int i = 0; i < defenses.length; i++) {
                Set<Integer> forbid = new HashSet<>();
                List<Integer> list = leaderIdToBoardId.get(defenses[i][0]);
                if (list != null) {
                    forbid.addAll(list);
                }
                list = leaderIdToBoardId.get(defenses[i][1]);
                if (list != null) {
                    forbid.addAll(list);
                }
                int[] ftab = new int[forbid.size()];
                int in = 0;
                for (int x : forbid) {
                    ftab[in++] = x;
                }
                if (!forbid.isEmpty()) {
                    cp.add(cp.or(cp.forbiddenAssignments(cp.element(assignment, index[i]), ftab), cp.eq(selected[i], 0)));
                }
            }
            if (cnt == 0) {
                cp.addMaximize(cp.sum(selected));
            } else {
                cp.addEq(cp.sum(selected), obj1);
                cp.addMaximize(score);
            }

            //cp.setParameter(IloCP.DoubleParam.TimeLimit, 30);
            cp.setOut(null);

            cp.startNewSearch();
            int licz = 1;

            while (cp.next()) {
                obj1 = (int) (cp.getObjValue() + 0.5);
                long currtim = (System.currentTimeMillis() - tim) / 1000;
                int min = (int) currtim / 60;
                int secs = (int) currtim % 60;
                //System.out.print("Etap" + (cnt+1) + "->" + obj1);
                String stringtim = String.format("[%d] Etap %d -> %d (%d:%02ds)", licz, cnt + 1, obj1, min, secs);
                String s;
                if (cnt == 0) {
                    s = String.format("[%d] (%d:%02ds) przydzielonych: %d, nieprzydzielonych: %d", licz, min, secs, obj1, n - obj1);
                } else {
                    s = String.format("[%d] (%d:%02ds) ocena: %d", licz, min, secs, obj1);
                }
                frame.setEtap("");
                frame.buttonEnable(true);
                licz++;
                frame.dispMsg(s + "\n");
                System.out.println(stringtim);
            }

            if (cnt == 1) {
                System.out.println("obrony = " + n + " sloty = " + m);
                for (int i = 0; i < n; i++) {
                    //System.out.println(cp.getValue(selected[i]));
                }
                Map<Pair<Integer, Integer>, List<int[]>> plan = new HashMap<>();
                int sum = 0;
                for (int i = 0; i < n; i++) {
                    int s = (int) (cp.getValue(selected[i]) + 0.5);
                    if (s == 0) {
                        continue;
                    }
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
                List<Pair<Integer, Integer>> ds = new ArrayList(plan.keySet());
                Collections.sort(ds, new Comparator<Pair<Integer, Integer>>() {
                    @Override
                    public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                        int c1 = Integer.compare(o1.getKey(), o2.getKey());
                        if (c1 != 0) {
                            return c1;
                        } else {
                            return Integer.compare(o1.getValue(), o2.getValue());
                        }
                    }
                });
                for (Pair<Integer, Integer> p : ds) {
                    System.out.print("[" + p.getKey() + "," + p.getValue() + "] : \t");
                    List<int[]> defs = plan.get(p);
                    Collections.sort(defs, new Comparator<int[]>() {
                        @Override
                        public int compare(int[] o1, int[] o2) {
                            return Integer.compare(o1[0], o2[0]);
                        }
                    });
                    int g = 0;
                    for (int[] d : defs) {
                        //for(int pp = 0; pp < d[0] - g+1; pp++) System.out.print("\t\t");
                        System.out.print("(" + d[0] + "," + d[1] + "," + d[2] + "   |   " + g +") ");
                        g++;
                    }
                    System.out.println();
                }
            /* ---------- wynik do excela --------------*/
            Map <String, Integer> maptemp = problem.getDateMap();
            Map <Integer, String> dataMap = new HashMap();
            for(String k : maptemp.keySet())
            {
                dataMap.put(maptemp.get(k), k);
            }
            maptemp = problem.getPersonMap();
            Map <Integer, String> personMap = new HashMap();
            for(String k : maptemp.keySet())
            {
                personMap.put(maptemp.get(k), k);
            }
            maptemp = problem.getTimeMap();
            Map <Integer, String> timeMap = new HashMap();
            for(String k : maptemp.keySet())
            {
                timeMap.put(maptemp.get(k), k);
            }
                
            int rowcount=5;
            rowcount = plan.keySet().stream().map((_item) -> 1).reduce(rowcount, Integer::sum);
            
            int xdd=1;
            Object[][] datatypes=new Object[rowcount][12];
            datatypes[0][0]="data";
            datatypes[0][1]="przew";
            datatypes[0][2]="godzina";
            datatypes[0][3]="student";
            datatypes[0][4]="opiekun";
            datatypes[0][5]="recenzent";
            String FILE_NAME = "test.xlsx";

            XSSFWorkbook workbook;
                    workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

            for (Pair<Integer, Integer> p : ds) {
                List<int[]> defs = plan.get(p);
                    Collections.sort(defs, new Comparator<int[]>() {
                        @Override
                        public int compare(int[] o1, int[] o2) {
                            return Integer.compare(o1[0], o2[0]);
                        }
                    });
                    int xd=0;
                    for (int[] d : defs) {
                        
                        datatypes[xdd+xd][0]=dataMap.get(p.getKey());
                        String temp = timeMap.get(p.getValue());
                        temp = temp.replace(":0",":00");
                        datatypes[xdd+xd][2]=temp;
                        //for(int pp = 0; pp < d[0] - g+1; pp++) System.out.print("\t\t");
                        datatypes[xdd+xd][1]=personMap.get(d[0]);
                        datatypes[xdd+xd][3]="-";
                        datatypes[xdd+xd][4]=personMap.get(d[2]);
                        datatypes[xdd+xd][5]=personMap.get(d[1]);
                        
                        //System.out.print("(" + d[0] + "," + d[1] + "," + d[2] + ") ");
                        xd++;
                    }
            
            xdd++;
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

          
            try {
                FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
                workbook.write(outputStream);
                workbook.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }

            System.out.println("Done");
             /* ---------- wynik do excela --------------*/
            }
            cp.end();
            if (cnt == 1) {
                frame.setVisible(false);
                frame.dispose();
            }
        }

    }
}
