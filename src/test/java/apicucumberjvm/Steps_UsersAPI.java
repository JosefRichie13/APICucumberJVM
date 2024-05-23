package apicucumberjvm;

import com.github.javafaker.Faker;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;

public class Steps_UsersAPI {

    Configs configs = new Configs();
    Faker faker = new Faker();

    Response responseFromAPI;
    String generatedEmail;
    String generatedPassword;
    String generatedUserName;

    public String generateRandomEmail(){
        return generatedEmail = faker.internet().emailAddress();
    }

    public String generateRandomPassword(){
        return generatedPassword = faker.internet().password();
    }

    public void generateRandomUserName(){
        generatedUserName = faker.name().firstName();
    }


    @Given("I verify that a random user {string}")
    public void verifyTheUserStatus(String UserStatus){
        switch (UserStatus){
            case "does not exist" -> {
                responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                        .multiPart("email", generateRandomEmail()).multiPart("password", generateRandomPassword())
                        .when().post(configs.baseURL + "/verifyLogin");
                assertEquals(responseFromAPI.jsonPath().getString("message"),"User not found!");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"404");
                System.out.println("Created email is : "+ generatedEmail);
                System.out.println("Created password is : "+ generatedPassword);
            }
            case "exists" -> {
                responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                        .multiPart("email", generatedEmail).multiPart("password", generatedPassword)
                        .when().post(configs.baseURL + "/verifyLogin");
                assertEquals(responseFromAPI.jsonPath().getString("message"),"User exists!");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"200");
            }
            case "cannot be searched without email" -> {
                responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                        .multiPart("password", generatedPassword)
                        .when().post(configs.baseURL + "/verifyLogin");
                assertEquals(responseFromAPI.jsonPath().getString("message"),
                        "Bad request, email or password parameter is missing in POST request.");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"400");
            }
            case "cannot be deleted with the DELETE method" -> {
                responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                        .multiPart("email", generatedEmail)
                        .multiPart("password", generatedPassword)
                        .when().delete(configs.baseURL + "/verifyLogin");
                assertEquals(responseFromAPI.jsonPath().getString("message"),
                        "This request method is not supported.");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"405");
            }
            case "cannot be searched with incorrect details" -> {
                responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                        .multiPart("email", generatedEmail + generatedEmail)
                        .multiPart("password", generatedPassword + generatedPassword)
                        .when().post(configs.baseURL + "/verifyLogin");
                assertEquals(responseFromAPI.jsonPath().getString("message"),"User not found!");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"404");
            }
            default -> throw new IllegalArgumentException("Incorrect API Endpoint : " + UserStatus);
        }
    }

    @When("I create a random user")
    public void createARandomUser(){

        generateRandomUserName();

        responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                .multiPart("name", generatedUserName)
                .multiPart("email", generatedEmail)
                .multiPart("password", generatedPassword)
                .multiPart("title", "Mr")
                .multiPart("birth_date", 4)
                .multiPart("birth_month", 5)
                .multiPart("birth_year", 2014)
                .multiPart("firstname", generatedUserName)
                .multiPart("lastname", generatedUserName)
                .multiPart("company", generatedUserName)
                .multiPart("address1", generatedUserName)
                .multiPart("address2", generatedUserName)
                .multiPart("country", "USA")
                .multiPart("zipcode", generatedUserName)
                .multiPart("state", generatedUserName)
                .multiPart("city", generatedUserName)
                .multiPart("mobile_number", generatedUserName)
                .when().post(configs.baseURL + "/createAccount");

        assertEquals(responseFromAPI.jsonPath().getString("message"),"User created!");
        assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"201");
    }

    @When("I update the random user")
    public void updateRandomUser(){
        responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                .multiPart("email", generatedEmail)
                .multiPart("password", generatedPassword)
                .multiPart("country", "India")
                .when().put(configs.baseURL + "/updateAccount");

        assertEquals(responseFromAPI.jsonPath().getString("message"),"User updated!");
        assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"200");
    }

    @And("I verify the user {string}")
    public void verifyTheUser(String userAction){
        switch (userAction){
            case "update" -> {
                responseFromAPI = RestAssured.get(configs.baseURL + "/getUserDetailByEmail?email="+generatedEmail);
                assertEquals(responseFromAPI.jsonPath().getString("user.country"),"India");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"200");
            }
            case "delete" -> {
                responseFromAPI = RestAssured.get(configs.baseURL + "/getUserDetailByEmail?email="+generatedEmail);
                assertEquals(responseFromAPI.jsonPath().getString("message"),
                        "Account not found with this email, try another email!");
                assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"404");
            }
            default -> throw new IllegalArgumentException("Incorrect UserAction : " + userAction);
        }
    }

    @Then("I delete a random user")
    public void deleteTheRandomUser(){
        responseFromAPI = RestAssured.given().contentType("multipart/form-data")
                .multiPart("email", generatedEmail)
                .multiPart("password", generatedPassword)
                .when().delete(configs.baseURL + "/deleteAccount");
        assertEquals(responseFromAPI.jsonPath().getString("message"),"Account deleted!");
        assertEquals(responseFromAPI.jsonPath().getString("responseCode"),"200");
    }
}
