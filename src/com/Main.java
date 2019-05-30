package com;


import ilog.concert.*;
import ilog.cp.*;
import javafx.util.Pair;

import java.util.*;

public class Main {

    public static void main(String[] args) throws IloException {
        Problem problem = new Problem("komisje.csv","obrony.csv");
        System.out.println();

        // TODO code application logic here
        final int[] days = problem.getDays();
        final int[] slots = problem.getSlots();
        final int[] leader = problem.getLeader();
        final int[][] defenses = problem.getDefenses();
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
                    cp.add(cp.or(new IloConstraint[]{cp.eq(selected[a], 0),cp.eq(selected[b], 0),
                            cp.neq(cp.element(days, va), cp.element(days, vb)),
                            cp.neq(cp.element(slots, va), cp.element(slots,vb))}));


                }

            }
        }

        Map<Integer,List<Integer>> leaderIdToBoardId = new HashMap<>();
        for (int x : leader) {

            if(!leaderIdToBoardId.containsKey(x)) {
                List<Integer> terms = new ArrayList<>();
                for(int i = 0; i < leader.length; i++) {
                    if(x == leader[i]) terms.add(i);
                    Set<Pair<Integer, Integer>> s = new HashSet<>();
                    for(int ii = 0; ii < m; ii++) {
                        if(x == leader[ii]) s.add(new Pair<>(days[ii], slots[ii]));
                    }
                    for(int ii = 0; ii < m; ii++) {
                        Pair<Integer, Integer> t = new Pair<>(days[ii], slots[ii]);
                        if(s.contains(t)) terms.add(ii);
                    }
                }
                leaderIdToBoardId.put(x, terms);
            }
        }
        List<Integer> w = new ArrayList<>();
        for(int i = 0; i < defenses.length; i++){
            Set<Integer> forbid = new HashSet<>();
            List<Integer> list = leaderIdToBoardId.get(defenses[i][0]);
            if(list != null) forbid.addAll(list);
            list = leaderIdToBoardId.get(defenses[i][1]);
            if(list != null) forbid.addAll(list);
            int[] ftab = new int[forbid.size()];
            int in = 0;
            for(int x : forbid) {
                ftab[in++] = x;
            }
            if(!forbid.isEmpty()) cp.add(cp.or(cp.forbiddenAssignments(cp.element(assignment, index[i]), ftab), cp.eq(selected[i], 0)));
        }

        cp.addMaximize(cp.sum(selected));
        cp.setParameter(IloCP.DoubleParam.TimeLimit, 30);
        cp.solve();
        for(int i = 0; i < n; i++) {
            System.out.println(cp.getValue(selected[i]));
        }
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
    }
}
