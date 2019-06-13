/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt111;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntervalVar;
import ilog.cp.IloCP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author student
 */
public class Projekt111 {

    /*
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IloException {

        //Zmienne decyzyjne
        final int[][][] jsp = {
            {{4, 5}, {2, 3}, {1, 4}, {3, 2}, {2, 2}},
            {{2, 4}, {4, 5}, {1, 5}},
            {{1, 3}, {2, 4}, {3, 3}, {4, 2}},
            {{3, 5}, {2, 3}, {1, 3}, {3, 5}}};
        IloCP cp = new IloCP();
        List<List<IloIntervalVar>> allOps = new ArrayList<>();
        Map<Integer, List<IloIntervalVar>> mto = new HashMap<>();

        for (int[][] job : jsp) {
            List<IloIntervalVar> jobOps = new ArrayList<>();
            allOps.add(jobOps);
            IloIntervalVar prevOp = null;

            for (int[] op : job) {
                int mId = op[0];
                int pt = op[1];
                IloIntervalVar opInterval = cp.intervalVar(pt);
                if (prevOp != null) {
                    cp.add(cp.endBeforeStart(prevOp, opInterval));
                }
                prevOp = opInterval;
                jobOps.add(opInterval);
                List<IloIntervalVar> machOps = mto.get(mId);
                if (machOps == null) {
                    machOps = new ArrayList<>();
                    mto.put(mId, machOps);
                }
                machOps.add(opInterval);
            }
        }

        for (List<IloIntervalVar> machOps : mto.values()) {
            cp.add(cp.noOverlap(machOps));
        }

        List<IloIntExpr> jobEndTimes = new ArrayList();
        for (List<IloIntervalVar> jobOps : allOps) {
            IloIntervalVar lastOp = jobOps.get(jobOps.size() - 1);
            jobEndTimes.add(cp.endOf(lastOp));
        }

        cp.addMinimize(cp.max(jobEndTimes));
        cp.solve();
    }
}
