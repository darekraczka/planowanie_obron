/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt2;
import ilog.concert.*;
import ilog.cp.IloCP;
/**
 *
 * @author WaMa
 */
public class Projekt2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IloException {
        // TODO code application logic here
        final int KOMISJE = 3;
        final int[] days = {1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2};
        final int[] slots = {1,1,1,2,2,2,3,4,4,1,1,1,2,2,2,3,3,4,4};
        final int[] leader = {1,2,3,1,2,3,1,1,3,1,2,3,1,2,3,1,3,2,3};
        final int[][] defenses = {{4,5},{6,4},{1,4},{5,5},{4,6},{4,6},{6,3},{3,4},{5,6},{4,5},{5,6},{3,4},{5,6},{4,5},{2,1},{5,4},{6,2},{4,6},{2,1},{6,4}};
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
        IloIntExpr score = cp.constant(0);
        
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
        cp.setParameter(IloCP.DoubleParam.TimeLimit, 60);
        cp.solve();
        for(int i = 0; i < n; i++) {
            System.out.println(cp.getValue(selected[i]));
        }
        /*int [][][] plan = new int[KOMISJE][TERMINY][];
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
    }
    
}
