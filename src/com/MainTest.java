package com;

import java.io.IOException;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        Problem problem = Reader.createProblem(Reader.readDataFromExcel("obrony_2017.xlsx"),Reader.readRestrictionFromExcel("restrykcje.xlsx"));
        System.out.println();
    }
}
