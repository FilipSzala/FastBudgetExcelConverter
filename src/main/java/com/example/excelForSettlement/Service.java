package com.example.excelForSettlement;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

@org.springframework.stereotype.Service
public class Service {
    public Service() {
    }

    private static int lastNumberOfRowForFarmExpense = 1;
    private static int lastNumberOfRowForPrivateExpense = 1;
    private static int lastNumberOfRowForRentRealEstate = 1;
    private static int lastNumberOfRowForPrivateFlat = 1;
    private static int lastNumberOfRowForDebt = 1;
    private static int lastNumberOfRowForSpecialColumn = 1;
    private static int lastNumberForIncome = 1;
    private static Workbook workbookTemplate;
    private static CellStyle zlFormat;
    private static CellStyle blackEdgesStyle;

    public static void createExcel(MultipartFile loadFile, HttpServletResponse response) throws IOException {


        ArrayList<Settlement> settlements = new ArrayList<>();
        try {
            loadFiles(settlements, loadFile);
            Workbook workbook = saveFiles(settlements);


            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
            }
        } catch (IOException e) {
            throw new IOException("Błąd podczas tworzenia pliku Excel: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Nieoczekiwany błąd: " + e.getMessage(), e);
        } finally {
            setDefaultValueForRows();
        }
    }

    private static Workbook saveFiles(ArrayList<Settlement> settlements) throws IOException {
        try {
            ClassPathResource templateResource = new ClassPathResource("template.xlsx");
            workbookTemplate = new XSSFWorkbook(templateResource.getInputStream());
            createZlFormat();
            createBlackEdgesStyle();
            Sheet sheet = workbookTemplate.getSheetAt(0);
            for (Settlement settlement : settlements) {
                setCellValue(settlement, sheet);
            }
            workbookTemplate.setForceFormulaRecalculation(true);
            return workbookTemplate;
        } catch (IOException e) {
            throw new IOException("Błąd podczas zapisywania pliku Excel: " + e.getMessage(), e);
        }
    }

    private static void createZlFormat() {
        zlFormat = workbookTemplate.createCellStyle();
        zlFormat.setDataFormat(workbookTemplate.createDataFormat().getFormat("#,##0.00 zł"));
    }

    public static void setCellValue(Settlement settlement, Sheet sheet) {
        if(settlement.value<0) {
            switch (settlement.account) {
                case "Wydatki prywatne":
                    updateSheetForExpense(sheet, settlement, lastNumberOfRowForPrivateExpense, 8, 9);
                    lastNumberOfRowForPrivateExpense++;
                    break;
                case "Wydatki prywatne mieszkanie":
                    updateSheetForExpense(sheet, settlement, lastNumberOfRowForPrivateFlat, 10, 11);
                    lastNumberOfRowForPrivateFlat++;
                    break;
                case "Wydatki nieruchomość":
                    updateSheetForExpense(sheet, settlement, lastNumberOfRowForRentRealEstate, 6, 7);
                    lastNumberOfRowForRentRealEstate++;
                    break;
                case "Wydatki gospodarstwo":
                    updateSheetForExpense(sheet, settlement, lastNumberOfRowForFarmExpense, 4, 5);
                    lastNumberOfRowForFarmExpense++;
                    break;
            }
        }
        else {
            updateSheetForIncome(sheet, settlement, lastNumberForIncome);
            lastNumberForIncome++;
        }

    }


    private static void createBlackEdgesStyle () {
        blackEdgesStyle = workbookTemplate.createCellStyle();
        blackEdgesStyle.setBorderBottom(BorderStyle.THIN);
        blackEdgesStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        blackEdgesStyle.setBorderLeft(BorderStyle.THIN);
        blackEdgesStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        blackEdgesStyle.setBorderRight(BorderStyle.THIN);
        blackEdgesStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        blackEdgesStyle.setBorderTop(BorderStyle.THIN);
        blackEdgesStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }
    private static void updateSheetForIncome (Sheet sheet, Settlement settlement, int rowNumber){
        Row row;
        row = createRow(sheet, rowNumber);
        row.createCell(0).setCellValue(settlement.getName());
        row.getCell(0).setCellStyle(blackEdgesStyle);
        createCellWithZlFormat(1, row, settlement.getValue());
        row.getCell(1).setCellStyle(blackEdgesStyle);
    }

    private static void updateSheetForExpense(Sheet sheet, Settlement settlement, int rowNumber, int nameCellIndex, int valueCellIndex) {

        Row row;
        double cost = settlement.getValue() * -1;
        String costName = settlement.getName();
        String divadeInformation = settlement.divadeInformation;
        double specificPrice;
        double specificPrices[];
        row = createRow(sheet, rowNumber);
        ExpenseType expenseType = StringExpense.ExpenseType(settlement.divadeInformation);


        switch (expenseType) {
            case WITHOUTSPLIT:
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, cost);
                break;
            case SPLITINHALF:
                cost = splitInHalf(cost);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, cost);
                updateDebtColumn(cost, sheet, lastNumberOfRowForDebt, settlement.getName());
                break;
            case SPLITINROKSANA:
                updateDebtColumn(cost, sheet, lastNumberOfRowForDebt, costName);
                checkAccountAndDecreaseIt(settlement.account);
                break;
            case FILIPSPECIFICPRICEROKSANAREMAINING:
                specificPrice = StringExpense.extractSingleNumber(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, specificPrice);
                updateDebtColumn((cost - specificPrice), sheet, lastNumberOfRowForDebt, costName);
                break;
            case ROKSANASPECIFICPRICEFILIPREMAINING:
                specificPrice = StringExpense.extractSingleNumber(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, cost - specificPrice);
                updateDebtColumn(specificPrice, sheet, lastNumberOfRowForDebt, costName);
                break;
            case FILIPSPLITMORE:
                specificPrice = StringExpense.extractSingleNumber(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, (cost / 2) + (specificPrice / 2));
                updateDebtColumn((cost / 2) - (specificPrice / 2), sheet, lastNumberOfRowForDebt, costName);
                break;
            case ROKSANASPLITMORE:
                specificPrice = StringExpense.extractSingleNumber(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, (cost / 2) - (specificPrice / 2));
                updateDebtColumn((cost / 2) + (specificPrice / 2), sheet, lastNumberOfRowForDebt, costName);
                break;
            case SPLITSPECIFICEPRICESFILIPFIRST:
                specificPrices = StringExpense.extractTwoDoubles(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, specificPrices[0]);
                updateDebtColumn(specificPrices[1], sheet, lastNumberOfRowForDebt, costName);
                break;
            case SPLITSPECIFICEPRICESROKSANAFIRST:
                specificPrices = StringExpense.extractTwoDoubles(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, specificPrices[1]);
                updateDebtColumn(specificPrices[0], sheet, lastNumberOfRowForDebt, costName);
                break;
            case SPLITSPECIFICESINGLEFILIPPRICERESTSPLITINHALF:
                specificPrice = StringExpense.extractSingleNumber(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, ((cost-specificPrice)/2)+specificPrice);
                updateDebtColumn(((cost-specificPrice)/2),sheet,lastNumberOfRowForDebt,costName);
                break;

            case SPLITSPECIFICESINGLEROKSANAPRICERESTSPLITINHALF:
                specificPrice = StringExpense.extractSingleNumber(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, ((cost-specificPrice)/2));
                updateDebtColumn((((cost-specificPrice)/2)+specificPrice),sheet,lastNumberOfRowForDebt,costName);
                break;

            case SPLITSPECIFICEPRICESFILIPFIRSTRESTSPLITINHALF:
                specificPrices = StringExpense.extractTwoDoubles(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, specificPrices[0] + ((cost - specificPrices[0] - specificPrices[1]) / 2));
                updateDebtColumn(specificPrices[1] + ((cost - specificPrices[0] - specificPrices[1]) / 2), sheet, lastNumberOfRowForDebt, costName);
                break;
            case SPLITSPECIFICEPRICESROKSANAFIRSTRESTSPLITINHALF:
                specificPrices = StringExpense.extractTwoDoubles(divadeInformation);
                row.createCell(nameCellIndex).setCellValue(costName);
                createCellWithZlFormat(valueCellIndex, row, specificPrices[1] + ((cost - specificPrices[0] - specificPrices[1]) / 2));
                updateDebtColumn(specificPrices[0] + ((cost - specificPrices[0] - specificPrices[1]) / 2), sheet, lastNumberOfRowForDebt, costName);
                break;
            case OTHER:
                updateSpecialColumn(cost, sheet, lastNumberOfRowForSpecialColumn, costName);
                checkAccountAndDecreaseIt(settlement.account);
                break;
        }
    }



    private static void createCellWithZlFormat(int valueCellIndex, Row row, double cost) {
        row.createCell(valueCellIndex).setCellValue(cost);
        row.getCell(valueCellIndex).setCellStyle(zlFormat);
    }

    private static void checkAccountAndDecreaseIt(String account) {
        switch (account) {
            case "Wydatki prywatne":
                lastNumberOfRowForPrivateExpense--;
                break;
            case "Wydatki prywatne mieszkanie":
                lastNumberOfRowForPrivateFlat--;
                break;
            case "Wydatki nieruchomość":
                lastNumberOfRowForRentRealEstate--;
                break;
            case "Wydatki gospodarstwo":
                lastNumberOfRowForFarmExpense--;
                break;
        }
    }

    private static Row createRow(Sheet sheet, int rowNumber) {
        Row row;
        if (sheet.getPhysicalNumberOfRows() > rowNumber) {
            row = sheet.getRow(rowNumber);
        } else {
            row = sheet.createRow(rowNumber);
        }
        return row;
    }

    private static void updateSpecialColumn(double cost, Sheet sheet, int rowNumber, String costName) {
        Row row = createRow(sheet, rowNumber);
        row.createCell(14).setCellValue(costName);
        row.createCell(15).setCellValue(cost);
        lastNumberOfRowForSpecialColumn++;
    }

    private static void updateDebtColumn(double cost, Sheet sheet, int rowNumber, String costName) {
        Row row = createRow(sheet, rowNumber);
        row.createCell(12).setCellValue(costName);
        createCellWithZlFormat(13, row, cost);
        lastNumberOfRowForDebt++;
    }

    private static void loadFiles(ArrayList<Settlement> settlements, MultipartFile file) throws IOException {
        Workbook workbookForLoad = new HSSFWorkbook(file.getInputStream());
        Sheet sheet = workbookForLoad.getSheetAt(0);

        int lastRow = -1;
        Settlement settlement = new Settlement();

        for (Row row : sheet) {
            if (row.getRowNum() > 4) {

                for (Cell cell : row) {
                    int columneIndex = cell.getColumnIndex();
                    if (isNewRow(cell, lastRow)) {
                        settlement = createSettlement(settlements);
                    }
                    if (isCorrectIndex(columneIndex)) {
                        setupSettlement(columneIndex, settlement, cell);
                    }
                    lastRow = cell.getRowIndex();
                }

            }
        }
    }

    private static Settlement createSettlement(ArrayList<Settlement> settlements) {
        Settlement settlement = new Settlement();
        settlements.add(settlement);
        return settlement;
    }

    private static boolean isNewRow(Cell cell, int lastRow) {
        return cell.getRowIndex() != lastRow;
    }

    private static boolean isCorrectIndex(int columneIndex) {
        return columneIndex == 0 || columneIndex == 3 || columneIndex == 5 || columneIndex == 6;
    }

    public static void setupSettlement(int columntIndex, Settlement settlement, Cell cell) {
        if (columntIndex == 0) {
            settlement.setValue(cell.getNumericCellValue());
        }
        if (columntIndex == 3) {
            settlement.setAccount(cell.getStringCellValue());
        }
        if (columntIndex == 5) {
            settlement.setName(cell.getStringCellValue());
        }
        if (columntIndex == 6) {
            settlement.setDivadeInformation(cell.getStringCellValue());
        }
    }

    private static void setDefaultValueForRows() {
        lastNumberOfRowForFarmExpense = 1;
        lastNumberOfRowForPrivateExpense = 1;
        lastNumberOfRowForRentRealEstate = 1;
        lastNumberOfRowForPrivateFlat = 1;
        lastNumberOfRowForDebt = 1;
        lastNumberOfRowForSpecialColumn = 1;
        lastNumberForIncome =1;
    }

    private static double splitInHalf(double number) {
        return number / 2;
    }
}
