package com;

import java.util.List;

public class Problem {
    private int[] slots;

    public int[] getSlots() {
        return slots;
    }

    public int[] getDays() {
        return days;
    }

    public int[] getLeader() {
        return leader;
    }

    public int[][] getDefenses() {
        return defenses;
    }

    private int[] days;
    private int[] leader;
    int [][] defenses;

    public Problem(String komisjeFileName, String obronyFileName){
        List<Komisja> komisje = Reader.readKomisje(komisjeFileName);
        List<Obrona> obrony = Reader.readObrony(obronyFileName);

        slots = new int[komisje.size()];
        days = new int[komisje.size()];
        leader = new int[komisje.size()];
        for(int i=0; i<komisje.size();i++){
            slots[i]=komisje.get(i).getSlot();
            days[i]=komisje.get(i).getDzien();
            leader[i]=komisje.get(i).getId_przew();
        }

        defenses = new int[obrony.size()][2];
        for(int i=0; i<obrony.size();i++){
            defenses[i][0] = obrony.get(i).getRec();
            defenses[i][1] = obrony.get(i).getPro();
        }

    }
}