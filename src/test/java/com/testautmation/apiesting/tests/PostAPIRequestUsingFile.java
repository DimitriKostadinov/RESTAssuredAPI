package com.testautmation.apiesting.tests;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstans;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class PostAPIRequestUsingFile extends BaseTest {

    @Test
    public void postAPIRequest(){

        try {
            String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstans.POST_API_REQUEST_BODY),"UTF-8");

            /*RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                        .body(postAPIRequestBody)
                        .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                        .post()
                    .then()
                        .assertThat()
                        .statusCode(200);*/

           Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(postAPIRequestBody)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                        .post()
                    .then()
                        .assertThat()
                        .statusCode(200)
                    .extract()
                        .response();

           // Use this site to make JSON expression https://jsonpath.com/
        JSONArray jsonArrayFirstName = JsonPath.read(response.body().asString(),"$.booking..firstname");
        String firstname = (String) jsonArrayFirstName.get(0); // Cast JSON to string
        Assert.assertEquals(firstname,"Dimitri");

        JSONArray jsonArrayLastName = JsonPath.read(response.body().asString(),"$.booking..lastname");
        String lastname = (String) jsonArrayLastName.get(0);
        Assert.assertEquals(lastname,"Kostadinov");

        JSONArray jsonArraycheckin = JsonPath.read(response.body().asString(),"$.booking..bookingdates..checkin");
        String checkin = (String) jsonArraycheckin.get(0);
        Assert.assertEquals(checkin,"2025-07-01");

        int bookingId = JsonPath.read(response.body().asString(),"$.bookingid"); // get the id from the post request

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                    .get("/{bookingId}",bookingId) // Pass the id parameter via get() method to Get request
                .then()
                    .assertThat()
                    .statusCode(200);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
