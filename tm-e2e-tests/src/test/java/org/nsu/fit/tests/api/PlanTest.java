package org.nsu.fit.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.nsu.fit.services.rest.data.PlanPojo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PlanTest {

    private AccountTokenPojo adminToken;
    private RestClient restClient;

    @BeforeClass
    public void authAsAdminTest() {
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
    }

    @Test(description = "Create plan.")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Create plan.")
    public void createPlan() {
        PlanPojo planPojo = restClient.createPlan(adminToken, RestClient.generateRandomPlan());;
        Assert.assertTrue(restClient.getPlans(adminToken).contains(planPojo));
    }

    @Test(description = "Delete plan.")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Delete plan.")
    public void deletePlan() {
        PlanPojo planPojo = restClient.createPlan(adminToken, RestClient.generateRandomPlan());
        restClient.deletePlan(adminToken, planPojo);
        Assert.assertFalse(restClient.getPlans(adminToken).contains(planPojo));
    }

}
