package com;

import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Reader {
    public static List <Komisja> readKomisje(String filename) {
        File file = new File(filename);
        List <Komisja> komisje = new ArrayList <>();
        List <String> lines = new ArrayList <>();
        try {
            Scanner scanner = new Scanner(file);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            for (String line : lines) {
                if (!line.isEmpty()) {
                    scanner = new Scanner(line);
                    scanner.useDelimiter(",");
                    Komisja komisja = new Komisja();
                    while (scanner.hasNext()) {
                        komisja.setDzien(Integer.parseInt(scanner.next()));
                        komisja.setSlot(Integer.parseInt(scanner.next()));
                        komisja.setId_przew(Integer.parseInt(scanner.next()));
                    }
                    komisje.add(komisja);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Nie znaleziono pliku" + filename);
        }
        return komisje;
    }

    public static List <Obrona> readObrony(String filename) {
        File file = new File(filename);
        List <Obrona> obrony = new ArrayList <>();
        List <String> lines = new ArrayList <>();
        try {
            Scanner scanner = new Scanner(file);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            for (String line : lines) {
                if (!line.isEmpty()) {
                    scanner = new Scanner(line);
                    scanner.useDelimiter(",");
                    Obrona obrona = new Obrona();
                    while (scanner.hasNext()) {
                        obrona.setRec(Integer.parseInt(scanner.next()));
                        obrona.setPro(Integer.parseInt(scanner.next()));
                    }
                    obrony.add(obrona);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Nie znaleziono pliku" + filename);
        }
        return obrony;
    }

    private static List<ExcelData> readExcelData(String filename) {
        File file = new File(filename);
        List <String> lines = new ArrayList <>();
        List <ExcelData> excelDataList = new ArrayList <>();
        try {
            Scanner scanner = new Scanner(file);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            for (String line : lines) {
                if (!line.isEmpty()) {
                    scanner = new Scanner(line);
                    scanner.useDelimiter(";");
                    ExcelData excelData = new ExcelData();
                    String date = scanner.next();
                    String[] dates = date.split("\\.");
                    excelData.setDay(dates[0]);
                    excelData.setMonth(dates[1]);
                    excelData.setYear(dates[2]);
                    scanner.next();
                    excelData.setLeader(scanner.next());
                    String time = scanner.next();
                    String[] times = time.split(":");
                    excelData.setHour(times[0]);
                    excelData.setMinutes(times[1]);
                    scanner.next();
                    scanner.next();
                    scanner.next();
                    scanner.next();
                    excelData.setPro(scanner.next());
                    excelData.setRec(scanner.next());
                    excelDataList.add(excelData);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Nie znaleziono pliku" + filename);
        }


        return excelDataList;
    }

    public static Problem createProblem(String filename){

        List<ExcelData> excelDataList = readExcelData(filename);

        Map<String, Integer> dateMap = new TreeMap <>();
        Map<String, Integer> timeMap = new TreeMap <>();
        Map<String, Integer> leaderMap = new TreeMap <>();
        Map<String,Integer> proMap = new TreeMap <>();
        Map<String,Integer> recMap = new TreeMap <>();
        List<Obrona> obrony = new ArrayList <>();
        List<Komisja> komisje = new ArrayList <>();

        int i = 0;
        for(int h = 0; h<=23; h++){
            for(int m = 0; m<=30 ; m+=30){
                String time = String.valueOf(h)+":"+String.valueOf(m);
                timeMap.put(time, i++);
            }
        }

        for(ExcelData e : excelDataList){
            String date = String.valueOf(e.getDay())+"."+String.valueOf(e.getMonth())+"."+String.valueOf(e.getYear());
            if(!dateMap.containsKey(date)){
                dateMap.put(date,dateMap.size()+1);
            }
           String time = String.valueOf(e.getHour())+":"+String.valueOf(e.getMinutes());
            if(!leaderMap.containsKey(e.getLeader())){
                leaderMap.put(e.getLeader(),leaderMap.size()+1);
            }
            if(!proMap.containsKey(e.getPro())){
                proMap.put(e.getPro(),proMap.size()+1);
            }
            if(!recMap.containsKey(e.getRec())){
                recMap.put(e.getRec(),recMap.size()+1);
            }

            Obrona obrona = new Obrona();
            Komisja komisja = new Komisja();

            obrona.setPro(proMap.get(e.getPro()));
            obrona.setRec(recMap.get(e.getRec()));
            obrony.add(obrona);

            komisja.setId_przew(leaderMap.get(e.getLeader()));
            komisja.setSlot(timeMap.get(time));
            komisja.setDzien(dateMap.get(date));
            komisje.add(komisja);
        }

        return new Problem(komisje,obrony);
    }

}
