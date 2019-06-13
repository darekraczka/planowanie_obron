package com;

import java.io.IOException;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        List<ExcelData> excelDataList = Reader.readDataFromExcel("obrony_2017.xlsx");
        Problem problem = Reader.createProblem(excelDataList);
        System.out.println();
    }
}
