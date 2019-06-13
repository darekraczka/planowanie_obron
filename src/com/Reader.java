package com;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


public class Reader {

    private static Map <String, Integer> dateMap = new TreeMap <>();
    private static Map <String, Integer> timeMap = initTimeMap();
    private static Map <String, Integer> personMap = new TreeMap <>();

    private static Map initTimeMap() {
        Map <String, Integer> tmap = new TreeMap <>();
        int i = 0;
        for (int h = 0; h <= 23; h++) {
            for (int m = 0; m <= 30; m += 30) {
                String time = String.valueOf(h) + ":" + String.valueOf(m);
                tmap.put(time, i++);
            }
        }
        return tmap;
    }

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

    public static List <ExcelData> readDataFromCSV(String filename) {
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

    public static List <ExcelData> readDataFromExcel(String filename) {
        List <ExcelData> excelDataList = new ArrayList <>();
        try {
            File file = new File(filename);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                ExcelData excelData = new ExcelData();
                Date date = sheet.getRow(i).getCell(0).getDateCellValue();
                excelData.setDay(date.getDate());
                excelData.setMonth(date.getMonth() + 1);
                excelData.setYear(date.getYear() + 1900);
                excelData.setLeader(sheet.getRow(i).getCell(2).getStringCellValue());
                Date time = sheet.getRow(i).getCell(3).getDateCellValue();
                excelData.setMinutes(time.getMinutes());
                excelData.setHour(time.getHours());
                excelData.setPro(sheet.getRow(i).getCell(8).getStringCellValue());
                excelData.setRec(sheet.getRow(i).getCell(9).getStringCellValue());
                excelDataList.add(excelData);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return excelDataList;
    }

    public static Problem createProblem(List <ExcelData> excelDataList) {

        List <Obrona> obrony = new ArrayList <>();
        List <Komisja> komisje = new ArrayList <>();

        for (ExcelData e : excelDataList) {
            String date = String.valueOf(e.getDay()) + "." + String.valueOf(e.getMonth()) + "." + String.valueOf(e.getYear());
            if (!dateMap.containsKey(date)) {
                dateMap.put(date, dateMap.size() + 1);
            }
            String time = String.valueOf(e.getHour()) + ":" + String.valueOf(e.getMinutes());
            if (!personMap.containsKey(e.getLeader())) {
                personMap.put(e.getLeader(), personMap.size() + 1);
            }
            if (!personMap.containsKey(e.getPro())) {
                personMap.put(e.getPro(), personMap.size() + 1);
            }
            if (!personMap.containsKey(e.getRec())) {
                personMap.put(e.getRec(), personMap.size() + 1);
            }

            Obrona obrona = new Obrona();
            Komisja komisja = new Komisja();

            obrona.setPro(personMap.get(e.getPro()));
            obrona.setRec(personMap.get(e.getRec()));
            obrony.add(obrona);

            komisja.setId_przew(personMap.get(e.getLeader()));
            komisja.setSlot(timeMap.get(time));
            komisja.setDzien(dateMap.get(date));
            komisje.add(komisja);
        }

        return new

                Problem(komisje, obrony);
    }
    

}
