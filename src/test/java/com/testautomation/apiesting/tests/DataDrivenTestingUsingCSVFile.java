package com.testautomation.apiesting.tests;

import java.io.*;
import java.util.*;

import org.testng.annotations.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.testautomation.apitesting.listener.RestAssuredListener;


import com.testautomation.apitesting.utils.FileNameConstans;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DataDrivenTestingUsingCSVFile {

    @Test(dataProvider = "CSVTestData")
    public void DataDrivenTesting(Map<String,String> testData) {

        int totalprice = Integer.parseInt(testData.get("totalprice"));
        try {
            BookingDates bookingDates = new BookingDates("2023-03-25", "2023-03-30");
            Booking booking = new Booking(testData.get("firstname"), testData.get("lastname"), totalprice,true,bookingDates,testData.get("additionalneeds"));

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

    @DataProvider(name = "CSVTestData")
    public Object[][] getTestData(){

        Object[][] objArray = null;
        Map<String,String> map = null;
        List<Map<String,String>> testDataList = null;

        try {
            CSVReader csvReader = new CSVReader(new FileReader(FileNameConstans.CSV_TEST_DATA));

            testDataList = new ArrayList<Map<String,String>>();

            String[] line = null;

            int count = 0;

            while((line = csvReader.readNext())!=null) {

                if(count == 0) {
                    count++;
                    continue;
                }

                map = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);

                map.put("firstname", line[0]);
                map.put("lastname", line[1]);
                map.put("totalprice", line[2]);

                testDataList.add(map);
            }

            objArray = new Object[testDataList.size()][1];

            for (int i = 0; i < testDataList.size(); i++) {
                objArray[i][0] = testDataList.get(i);
            }


        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (CsvValidationException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return objArray;

    }
}