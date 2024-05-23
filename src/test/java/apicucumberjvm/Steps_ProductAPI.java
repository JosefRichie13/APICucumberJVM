package apicucumberjvm;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;

import static org.junit.Assert.*;

public class Steps_ProductAPI {

    Configs configs = new Configs();

    Response responseFromAPI;
    String searchParamFromTable;

    @When("I {string} all the Products")
    public void makeACallToTheProductsAPI(String APIEndpoint){
        switch (APIEndpoint){
            case "get" -> responseFromAPI = RestAssured.get(configs.baseURL + "/productsList");
            case "update" -> responseFromAPI = RestAssured.post(configs.baseURL + "/productsList");
            default -> throw new IllegalArgumentException("Incorrect API Endpoint : " + APIEndpoint);
        }
    }

    @Then("I should {string} the products")
    public void checkTheProductAPIResponse(String APIStatus){
        switch (APIStatus){
            case "not be allowed to update" -> {
                assertEquals(responseFromAPI.jsonPath().getString("message"),
                        "This request method is not supported.");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),
                        "405");
            }
            case "be allowed to see" -> {
                for(int index = 0; index < responseFromAPI.jsonPath().getList("products").size(); index++){
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].id"));
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].name"));
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].price"));
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].brand"));
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].category"));
                }
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),
                        "200");
            }
            default -> throw new IllegalArgumentException("Incorrect API Endpoint : " + APIStatus);
        }
    }

    @Given("I search for a product using")
    public void searchForAProduct(DataTable table){
        searchParamFromTable = table.asMaps().get(0).get("search_product");

        if(StringUtils.isEmpty(searchParamFromTable)){
            responseFromAPI = RestAssured.post(configs.baseURL + "/searchProduct");
        }
        else {
            responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                    .multiPart("search_product", searchParamFromTable).when().post(configs.baseURL + "/searchProduct");
        }
    }

    @Then("I should {string} the product search result")
    public void getTheProductSearchResult(String APIStatus){
        switch (APIStatus){
            case "get" ->{
                for(int index = 0; index < responseFromAPI.jsonPath().getList("products").size(); index++){
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].id"));
                    assertTrue(responseFromAPI.jsonPath().getString("products["+(index)+"].name").contains(searchParamFromTable));
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].price"));
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].brand"));
                    assertNotNull(responseFromAPI.jsonPath().getString("products["+(index)+"].category"));
                }
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),
                        "200");
            }
            case "not get" -> {
                assertEquals(responseFromAPI.jsonPath().getString("message"),
                        "Bad request, search_product parameter is missing in POST request.");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),
                        "400");
            }
            default -> throw new IllegalArgumentException("Incorrect API Status : " + APIStatus);
        }
    }
}
