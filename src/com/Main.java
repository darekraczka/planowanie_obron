package obrony;

import ilog.concert.*;
import ilog.cp.*;
import javafx.util.Pair;

import java.util.*;

public class Main {

    public static void main(String[] args) throws IloException {
        Problem problem = new Problem("komisje.csv", "obrony.csv");
        System.out.println();

        // TODO code application logic here
        final int[] days = problem.getDays();
        final int[] slots = problem.getSlots();
        final int[] leader = problem.getLeader();
        final int[][] defenses = problem.getDefenses();
        int n = defenses.length;
        int m = days.length;
        int obj1 = 0;

        for (int cnt : new int[]{0, 1}) {
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
            CTRframe frame = null;
            long tim = 0;
            while (cp.next()) {
                if (frame == null) {
                    tim = System.currentTimeMillis();
                    frame = new CTRframe("Etap " + (cnt + 1), cp, tim);
                    frame.setVisible(true);
                }
                obj1 = (int) (cp.getObjValue() + 0.5);
                long currtim = (System.currentTimeMillis() - tim) / 1000;
                int min = (int) currtim / 60;
                int secs = (int) currtim % 60;
                //System.out.print("Etap" + (cnt+1) + "->" + obj1);
                String stringtim = String.format("[%d] Etap %d -> %d (%d:%02ds)", licz, cnt + 1, obj1, min, secs);
                String s;
                if(cnt == 0) s = String.format("[%d] (%d:%02ds) przydzielonych: %d, nieprzydzielonych: %d", licz, min, secs, obj1, n-obj1);
                else s = String.format("[%d] (%d:%02ds) ocena: %d", licz, min, secs, obj1);
                licz++;
                frame.dispMsg(s + "\n");
                System.out.println(stringtim);
            }

            frame.setVisible(false);
            frame.dispose();

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
                        System.out.print("(" + d[0] + "," + d[1] + "," + d[2] + ") ");
                        g = d[0];
                    }
                    System.out.println();
                }
            }
            cp.end();
        }
    }
}
