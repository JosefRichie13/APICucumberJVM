package apicucumberjvm;

import io.cucumber.java.en.Then;
import net.jimblackler.jsonschemafriend.*;

import java.io.File;
import java.io.IOException;

// We validate the JSON Schema's using this method
// JSON Schema validation is one way of ensuring that the API's responses are consistent without any schema changes
// To do this, we need to create a Schema file for each API endpoint, e.g. BrandsAPI and store it in a JSON file, BrandsAPI_Schema.json
// We then make the API call to that endpoint, BrandsAPI and store the JSON response in a JSON file, BrandsAPI_data.json
// Finally we use the jsonschemafriend library validator to make sure that the JSON response has the same schema as the Schema file
// https://tryjsonschematypes.appspot.com/#validate, this website can help to write JSON Schemas

public class Steps_SchemaValidation {

    SchemaStore schemaStore = new SchemaStore();
    Validator validator = new Validator();

    @Then("I validate the {string} Schema to ensure that it is not changed")
    public void verifySchema(String APIEndpoint) throws GenerationException, ValidationException, IOException {
        switch (APIEndpoint){
            case "Brands API" -> {
                Schema schema = schemaStore.loadSchema(new File("./src/schemas/BrandsAPI_Schema.json"));
                validator.validate(schema, new File("./src/schemas/BrandsAPI_Data.json"));
            }
            case "Products API" -> {
                Schema schema = schemaStore.loadSchema(new File("./src/schemas/ProductAPI_Schema.json"));
                validator.validate(schema, new File("./src/schemas/ProductAPI_Data.json"));
            }
            case "User API" -> {
                Schema schema = schemaStore.loadSchema(new File("./src/schemas/UserAPI_Schema.json"));
                validator.validate(schema, new File("./src/schemas/UserAPI_Data.json"));
            }
            default -> throw new IllegalArgumentException("Incorrect API Endpoint : " + APIEndpoint);
        }
    }
}
