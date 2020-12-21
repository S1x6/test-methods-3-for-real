package org.nsu.fit.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.ContactPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;

public class CreateCustomerTest {

    private AccountTokenPojo adminToken;
    private RestClient restClient;

    @BeforeClass
    public void authAsAdminTest() {
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
    }

    @Test(description = "Create customer.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer.")
    public void createCustomer() {
        CustomerPojo customerPojo = restClient.createCustomer(adminToken, RestClient.generateRandomCustomer());
        Assert.assertTrue(restClient.getCustomers(adminToken).contains(customerPojo));
    }

    @Test(description = "Create customer with easy password.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Create customer.")
    public void createCustomerWithEasyPassword() {
        ContactPojo contactPojo = RestClient.generateRandomCustomer();
        contactPojo.pass = "123qwe";
        Assert.assertThrows(BadRequestException.class, () ->
                restClient.createCustomer(adminToken, contactPojo));
    }
}
