package com.testautomation.apitesting.listener;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestAssuredListener implements Filter{

    private static final Logger logger = LogManager.getLogger(RestAssuredListener.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec, FilterContext context)
    {
        Response response = context.next(requestSpec,responseSpec);

        if (response.getStatusCode() != 200 & response.getStatusCode() != 201) {
            logger.error("\n Method:  "+requestSpec.getMethod()+
                    "\n URI: "+requestSpec.getURI()+
                    "\n Request body: "+requestSpec.getBody()+
                    "\n Response body: "+response.getBody().prettyPrint());
        }

        return response;
    }
}
