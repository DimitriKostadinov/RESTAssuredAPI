package com.testautomation.apitesting.utils;

import io.restassured.RestAssured;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    public void beforeMethod(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); // will log request and respons if fail
    }

    @AfterMethod
    public void afterMethod(ITestResult result){

        if (result.getStatus() == ITestResult.FAILURE) {
            Throwable t = result.getThrowable(); // Will get the fail reason

            StringWriter error = new StringWriter();
            t.printStackTrace(new PrintWriter(error));

            logger.info(error.toString());
        }
    }
}
