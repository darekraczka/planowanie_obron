package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Problem {
    private int[] slots;

    private List<Restriction> restrictions;

    private Map<String, Integer> dateMap = new TreeMap<>();

    public List <Restriction> getRestrictions() {
        return restrictions;
    }

    public Map <String, Integer> getDateMap() {
        return dateMap;
    }

    public Map <String, Integer> getTimeMap() {
        return timeMap;
    }

    public Map <String, Integer> getPersonMap() {
        return personMap;
    }

    private Map <String, Integer> timeMap = new TreeMap <>();
    private Map <String, Integer> personMap = new TreeMap <>();

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

    public Problem(String komisjeFileName, String obronyFileName, List<Restriction> restrictionList, Map<String, Integer> dateMap, Map<String, Integer> personMap, Map<String, Integer> timeMap){
        List<Komisja> komisje = Reader.readKomisje(komisjeFileName);
        List<Obrona> obrony = Reader.readObrony(obronyFileName);

        restrictions = restrictionList;
        this.timeMap = timeMap;
        this.personMap = personMap;
        this.dateMap = dateMap;

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

    public Problem(List<Komisja> komisje,  List<Obrona> obrony, List<Restriction> restrictionList, Map<String, Integer> dateMap, Map<String, Integer> personMap, Map<String, Integer> timeMap){
        slots = new int[komisje.size()];
        days = new int[komisje.size()];
        leader = new int[komisje.size()];
        restrictions = restrictionList;
        this.timeMap = timeMap;
        this.personMap = personMap;
        this.dateMap = dateMap;

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
