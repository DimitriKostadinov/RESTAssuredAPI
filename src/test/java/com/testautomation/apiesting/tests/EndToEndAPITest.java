package com.testautomation.apiesting.tests;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstans;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndToEndAPITest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(EndToEndAPITest.class);

    @Test
    public void testEndToEnd(){

        logger.info("testEndToEnd test execution started !");

        try {
            String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstans.POST_API_REQUEST_BODY),"UTF-8");

            String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstans.TOKEN_API_REQUEST_BODY),"UTF-8");

            String putAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstans.PUT_API_REQUEST_BODY),"UTF-8");

            String patchAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstans.PATCH_API_REQUEST_BODY),"UTF-8");

            Response response = RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .body(postAPIRequestBody)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                    .post() // post api call (Create request)
                .then()
                    .assertThat()
                    .statusCode(200)
                .extract()
                    .response();

            // Use this site to make JSON expression https://jsonpath.com/
            JSONArray jsonArrayFirstName = JsonPath.read(response.body().asString(),"$.booking..firstname");
            String firstname = (String) jsonArrayFirstName.get(0); // Cast JSON to string
            Assert.assertEquals(firstname,"Dimitri");

            int bookingId = JsonPath.read(response.body().asString(),"$.bookingid"); // get the id from the post request

            System.out.println("The booking record is created BookingId: " + bookingId);

            // Get API call (Get request)
            RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                    .get("/{bookingId}",bookingId) // Pass the id parameter via get() method to Get request
                .then()
                    .assertThat()
                    .statusCode(200);

            // token generation
            Response tokenAPIResponse =
                    RestAssured
                        .given().filter(new RestAssuredListener())
                            .contentType(ContentType.JSON)
                            .body(tokenAPIRequestBody)
                            .baseUri("https://restful-booker.herokuapp.com/auth")
                        .when()
                            .post()
                        .then()
                            .assertThat()
                            .statusCode(200)
                        .extract()
                            .response();

            String token = JsonPath.read(tokenAPIResponse.body().asString(),"$.token");

            System.out.println("The token is : " + token);

            // put API call (Update request full record update)
            RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .body(putAPIRequestBody)
                    .header("Cookie","token="+token)
                    .baseUri("https://restful-booker.herokuapp.com/booking/")
                .when()
                    .put("{bookingId}",bookingId)
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname", Matchers.equalTo("Dimitri"))
                    .body("lastname", Matchers.equalTo("Kostadinov"));

            System.out.println("PUT API - The token and bookingId are " + token + " " + bookingId);

            // patch api call (Update request partial record update)
            RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .body(patchAPIRequestBody)
                    .header("Cookie","token="+token)
                    .baseUri("https://restful-booker.herokuapp.com/booking/")
                .when()
                    .patch("{bookingId}",bookingId)
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname", Matchers.equalTo("Mitko"));

            System.out.println("PATCH API - The token and bookingId are " + token + " " + bookingId);

            // Delete api call (Delete the record)

            System.out.println("DELETE API - The token and bookingId are " + token + " " + bookingId);
            RestAssured
                .given().filter(new RestAssuredListener())
                    .contentType(ContentType.JSON)
                    .header("Cookie","token="+token)
                    .baseUri("https://restful-booker.herokuapp.com/booking/")
                .when()
                    .delete("{bookingId}",bookingId)
                .then()
                    .assertThat()
                    .statusCode(201);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("testEndToEnd test execution ended !");
    }
}
