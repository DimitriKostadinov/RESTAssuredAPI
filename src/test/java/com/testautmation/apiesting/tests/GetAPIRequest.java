package com.testautmation.apiesting.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetAPIRequest {

    @Test
    public void getAllBookings(){
        // Connect to the server
        RestAssured.given().contentType(ContentType.JSON).baseUri("https://restful-booker.herokuapp.com/booking").when().get();

        // Check if the StatusCode is (200) - 'ok' for the request
        RestAssured.given().contentType(ContentType.JSON).baseUri("https://restful-booker.herokuapp.com/booking").when().get()
                .then().assertThat().statusCode(200); // The test will pass if the status code is 200 and fail if it is not

        // Check the status line
        RestAssured.given().contentType(ContentType.JSON).baseUri("https://restful-booker.herokuapp.com/booking").when().get()
                .then().assertThat().statusCode(200).statusLine("HTTP/1.1 200 OK");

        // Check the headers from response
        RestAssured.given().contentType(ContentType.JSON).baseUri("https://restful-booker.herokuapp.com/booking").when().get()
                .then().assertThat().header("Content-Type","application/json; charset=utf-8");

        // Get the response
        Response response = RestAssured.given().contentType(ContentType.JSON).baseUri("https://restful-booker.herokuapp.com/booking").when().get()
                .then().assertThat().extract().response();

        System.out.println(response.getBody().asString()); // print all response

        // RestAssured.get("https://jsonplaceholder.typicode.com/posts/2").prettyPrint(); // print one specific response depends on the index

        Assert.assertTrue(response.getBody().asString().contains("bookingid"));

    }

}
