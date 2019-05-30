package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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


}
