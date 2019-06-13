/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author WaMa
 */
public class Ocena {

    public static void main(String[] args) {
        Problem problem = new Problem("komisje3.csv", "obrony3.csv",new ArrayList <>(), new TreeMap <>(), new TreeMap <>(), new TreeMap <>());
        final int[] days = problem.getDays();
        final int[] slots = problem.getSlots();
        final int[] leader = problem.getLeader();
        final int[][] defenses = problem.getDefenses();
        int n = defenses.length;
        int m = days.length;
        int points = 0;
        for (int a = 0; a < n; a++) {
            for (int b = 0; b < n; b++) {
                if (a <= b) {
                    continue;
                }
                int pts = (defenses[a][0] == defenses[b][0] || defenses[a][0] == defenses[b][1]) ? 1 : 0;
                pts += (defenses[a][1] == defenses[b][0] || defenses[a][1] == defenses[b][1]) ? 1 : 0;
                if (pts == 0) {
                    continue;
                }
                if (days[a] == days[b]) {
                    points += pts;
                    if (Math.abs(slots[a] - slots[b]) == 1) {
                        points += 2 * pts;
                    }
                }
            }
        }
        System.out.println(points);
    }

}
