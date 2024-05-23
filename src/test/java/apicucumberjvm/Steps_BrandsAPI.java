package apicucumberjvm;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Steps_BrandsAPI {

    Configs configs = new Configs();

    Response responseFromAPI;

    @When("I {string} all the Brands")
    public void makeACallToTheBrandsAPI(String APIEndpoint) throws IOException {
        switch (APIEndpoint){
            case "get" -> responseFromAPI = RestAssured.get(configs.baseURL + "/brandsList");
            case "update" -> responseFromAPI = RestAssured.put(configs.baseURL + "/brandsList");
            case "get_save" -> {
                responseFromAPI = RestAssured.get(configs.baseURL + "/brandsList");
                FileWriter fileWriter = new FileWriter("./src/schemas/BrandsAPI_Data.json");
                fileWriter.write(responseFromAPI.body().print());
                fileWriter.close();
            }
            default -> throw new IllegalArgumentException("Incorrect API Endpoint : " + APIEndpoint);
        }
    }

    @Then("I should {string} the Brands")
    public void checkTheBrandsAPIResponse(String APIStatus){
        switch (APIStatus){
            case "be allowed to see" -> {
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"200");
                for(int index = 0; index < responseFromAPI.jsonPath().getList("brands").size(); index++){
                    assertNotNull(responseFromAPI.jsonPath().getString("brands["+(index)+"].id"));
                    assertNotNull(responseFromAPI.jsonPath().getString("brands["+(index)+"].brand"));
                }
            }
            case "not be allowed to update" -> {
                assertEquals(responseFromAPI.jsonPath().getString("message"),
                        "This request method is not supported.");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),
                        "405");
            }
            default -> throw new IllegalArgumentException("Incorrect API Status : " + APIStatus);
        }
    }
}
