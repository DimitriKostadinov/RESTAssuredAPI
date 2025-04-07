package com.testautomation.apitesting.tests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.utils.FileNameConstans;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class PostAPIRequestUsingClasses {

    @Test
    public void postAPPIRequest() throws IOException {

        String jsonSchema = FileUtils.readFileToString(new File(FileNameConstans.JSON_SCHEMA),"UTF-8");

        try {
            BookingDates bookingDates = new BookingDates("2025-07-12","2025-07-22");

            Booking booking = new Booking("Dimitri","Kostadinov",1300,true,bookingDates,"Lux Hotel");

            //serialization
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);

            //System.out.println(requestBody);

            //de-serialization
            Booking bookingDetails = objectMapper.readValue(requestBody,Booking.class);
            //System.out.println(bookingDetails.getFirstname());
            //System.out.println(bookingDetails.getTotalprice());

            //System.out.println(bookingDetails.getBookingdates().getCheckin());
            //System.out.println(bookingDetails.getBookingdates().getCheckout());

            // Create POST 'Create Booking' API request via RestAssured and get the response

            Response response =
            RestAssured
                .given()
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

            int bookingId = response.path("bookingid");
            System.out.println(jsonSchema);

            // Create 'Get Booking Details' API request via RestAssured

            RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                    .get("/{bookingId}",bookingId)
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body(JsonSchemaValidator.matchesJsonSchema(jsonSchema)); // validate JSON schema in the 'expectedjsonschema.txt'

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
