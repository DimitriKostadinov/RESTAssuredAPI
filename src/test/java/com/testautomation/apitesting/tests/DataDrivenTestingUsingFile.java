package com.testautomation.apitesting.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.utils.FileNameConstans;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;


public class DataDrivenTestingUsingFile {

    @Test(dataProvider = "getTestData")
    public void DataDrivenTestingUsingJson(LinkedHashMap<String,String> testData)throws JsonProcessingException{

        BookingDates bookingDates = new BookingDates("2023-03-25", "2023-03-30");
        Booking booking = new Booking(testData.get("firstname"), testData.get("lastname"), 1800,true,bookingDates,testData.get("additionalneeds"));

        //serialization
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);

        //de-serialization
        Booking bookingDetails = objectMapper.readValue(requestBody,Booking.class);

        // Create POST 'Create Booking' API request via RestAssured and get the response

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
    }

    @DataProvider(name = "getTestData")
    public Object[] getTestDataUsingJsoon(){

        Object[] obj = null;
        String jsonTestData = null;

        try {

            jsonTestData = FileUtils.readFileToString(new File(FileNameConstans.JSON_TEST_DATA),"UTF-8");

            JSONArray jsonArray = JsonPath.read(jsonTestData,"$");

            obj = new Object[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
               obj[i] = jsonArray.get(i);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return obj;
    }
}
