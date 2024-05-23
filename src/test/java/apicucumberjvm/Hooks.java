package apicucumberjvm;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    @Before
    public void executedBefore(){
        System.out.println("Executing the Before Hook");
    }

    @After
    public void executedAfter(Scenario scenario) {
        System.out.println("Executing the After Hook");
    }

}