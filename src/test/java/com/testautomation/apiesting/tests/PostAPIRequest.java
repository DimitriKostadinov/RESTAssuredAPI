package com.testautomation.apiesting.tests;

import com.testautomation.apitesting.utils.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class PostAPIRequest extends BaseTest {

    @Test
    public void createBooking(){

        // repare request body
        JSONObject booking = new JSONObject();
        JSONObject bookingDates = new JSONObject();

        booking.put("firstname", "Dimitri"); // the first parameter is the parameter name and the second is the parameter value
        booking.put("lastname", "Kostadinov"); // the parameters name are case-sensitive ! if they are not the same the response code will be with
        // status 500
        booking.put("totalprice", 1000);
        booking.put("depositpaid", true);
        booking.put("additionalneeds", "breakfast");
        booking.put("bookingdates", bookingDates);

        bookingDates.put("checkin","2025-06-18");
        bookingDates.put("checkout","2025-06-30");

        // Create a booking record (Execute the POST request)
        /*RestAssured.given().contentType(ContentType.JSON)
                .body(booking.toString()) // cast the JSON object booking to string
                .baseUri("https://restful-booker.herokuapp.com/booking") // request URL
                .when()
                .post().then().assertThat().statusCode(200); */// request type POST with status code 200

        // Validate the API parameters values
       /* RestAssured.given().contentType(ContentType.JSON)
                .body(booking.toString()) // cast the JSON object booking to string
                .baseUri("https://restful-booker.herokuapp.com/booking") // request URL
                .when()
                .post().then().assertThat().statusCode(200)
                .body("booking.firstname", Matchers.equalTo("Dimitri"))// validate string value
                .body("booking.totalprice", Matchers.equalTo(1000)) // validate int value
                .body("booking.bookingdates.checkin", Matchers.equalTo("2025-06-18"));*/ // validate nested JSON value

        // Print API request in console output
        Response response = RestAssured.given().contentType(ContentType.JSON)
                .body(booking.toString()) // cast the JSON object booking to string
                .baseUri("https://restful-booker.herokuapp.com/booking") // request URL
                //.log().body() // print the request body
                //.log().headers() // print the request headers
                //.log().all() // print all request
                .when()
                .post().then().assertThat()
                //.log().body() // print all body response
                //.log().headers() // print the headers in the response
                //.log().all() // print the response headers and body
                //.log().ifValidationFails() //will log the response if one of the validations fail
                .statusCode(200)
                .body("booking.firstname", Matchers.equalTo("Dimitri"))// validate string value
                .body("booking.totalprice", Matchers.equalTo(1000)) // validate int value
                .body("booking.bookingdates.checkin", Matchers.equalTo("2025-06-18")) // validate nested JSON value
                .extract().response();

        int bookingId = response.path("bookingid"); // get the booking id from the POST request

        RestAssured.given().contentType(ContentType.JSON).pathParam("bookingID", bookingId) // pass the POST bookingid parameter to GET API
                .baseUri("https://restful-booker.herokuapp.com/booking")// GET API (The API URL is copied without the Postman parameter in the URL)
                .when()
                .get("{bookingID}")
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname",Matchers.equalTo("Dimitri"))
                .body("lastname",Matchers.equalTo("Kostadinov"));
    }
}
