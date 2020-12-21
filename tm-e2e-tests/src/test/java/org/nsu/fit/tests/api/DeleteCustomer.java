package org.nsu.fit.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DeleteCustomer {

    private AccountTokenPojo adminToken;
    private RestClient restClient;

    @BeforeClass
    public void authAsAdminTest() {
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
    }

    @Test(description = "Delete customer.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete customer.")
    public void deleteCustomer() {
        CustomerPojo customerPojo = restClient.createCustomer(adminToken, RestClient.generateRandomCustomer());
        restClient.deleteCustomer(adminToken, customerPojo);
        Assert.assertFalse(restClient.getCustomers(adminToken).contains(customerPojo));
    }

    @Test(description = "Delete customer.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Delete customer.")
    public void deleteCustomerWhichDoesNotExistShouldNotFail() {
        CustomerPojo customerPojo = restClient.createCustomer(adminToken, RestClient.generateRandomCustomer());
        restClient.deleteCustomer(adminToken, customerPojo);
        restClient.deleteCustomer(adminToken, customerPojo);
        Assert.assertFalse(restClient.getCustomers(adminToken).contains(customerPojo));
    }

}
