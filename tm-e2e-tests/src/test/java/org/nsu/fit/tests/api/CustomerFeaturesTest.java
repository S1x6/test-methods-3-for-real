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

import java.util.List;
import java.util.stream.Collectors;

public class CustomerFeaturesTest {

    private AccountTokenPojo token;
    private AccountTokenPojo adminToken;
    private RestClient restClient;
    private CustomerPojo customerPojo;

    @BeforeClass
    public void authAsCustomerTest() {
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
        customerPojo = new RestClient().createCustomer(adminToken, RestClient.generateRandomCustomer());
        token = new RestClient().authenticate(customerPojo.login, customerPojo.pass);
    }

    @Test(description = "Top up customer balance.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Top up customer balance.")
    public void addBalance() {
        final int ADD_MONEY = 100;
        restClient.addBalance(token, ADD_MONEY, customerPojo);
        List<CustomerPojo> list = restClient.getCustomers(token).stream().filter(c -> c.id.equals(customerPojo.id)).collect(Collectors.toList());
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).balance, customerPojo.balance + ADD_MONEY);
    }

    @Test(description = "Check available plans.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Check available plans.")
    public void checkAvailablePlans() {
        PlanPojo planPojo = restClient.createPlan(adminToken, RestClient.generateRandomPlan());
        List<PlanPojo> list = restClient.checkAvailablePlans(token);
        Assert.assertTrue(list.contains(planPojo));
    }
}
