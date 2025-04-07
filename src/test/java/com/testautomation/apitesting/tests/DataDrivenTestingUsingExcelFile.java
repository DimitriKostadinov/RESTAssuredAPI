package com.testautomation.apitesting.tests;

import java.io.*;

import org.apache.poi.hpsf.Decimal;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.utils.FileNameConstans;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DataDrivenTestingUsingExcelFile {

    @Test(dataProvider = "ExcelTestData")
    public void DataDrivenTesting(Map<String, String> testData) {

        double totalprice = Double.parseDouble(testData.get("TotalPrice"));

        try {

            // Преобразуване на "true"/"false" от String към boolean
            boolean depositPaid = Boolean.parseBoolean(testData.get("DepositPaid"));

            // Създаване на BookingDates от testData
            BookingDates bookingDates = new BookingDates(
                    testData.get("Checkin"),
                    testData.get("Checkout")
            );

            // Взимане на допълнителни данни
            String additionalNeeds = testData.get("AdditionalNeeds");

            // Създаване на Booking обект
            Booking booking = new Booking(
                    testData.get("FirstName"),
                    testData.get("LastName"),
                    totalprice,
                    depositPaid,
                    bookingDates,
                    additionalNeeds
            );


            //serialization
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);

            Response response =
                    RestAssured
                        .given().filter(new RestAssuredListener())
                            .contentType(ContentType.JSON)
                            .body(requestBody)
                            .baseUri("https://restful-booker.herokuapp.com/booking")
                        .when()
                            .post()
                        .then()
                            .assertThat()
                            .statusCode(200)
                        .extract()
                            .response();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "ExcelTestData")
    public Object[][] getTestData() {
        List<Map<String, String>> testDataList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(FileNameConstans.EXCEL_TEST_DATA));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Четем заглавния ред и съхраняваме ключовете
            List<String> headers = new ArrayList<>();
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue());
                }
            }

            // Четем останалите редове
            DataFormatter formatter = new DataFormatter();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = formatter.formatCellValue(cell);
                    rowData.put(headers.get(i), cellValue);
                }
                testDataList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ако няма данни, връщаме празен масив, за да избегнем null
        if (testDataList.isEmpty()) {
            return new Object[0][1];
        }

        Object[][] objArray = new Object[testDataList.size()][1];
        for (int i = 0; i < testDataList.size(); i++) {
            objArray[i][0] = testDataList.get(i);
        }
        return objArray;
    }
}