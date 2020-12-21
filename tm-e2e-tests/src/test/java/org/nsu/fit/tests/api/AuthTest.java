package org.nsu.fit.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthTest {
    private AccountTokenPojo adminToken;

    // Лабораторная 3: Разобраться с аннотациями, как они влияют на итоговый отчет.
    @Test(description = "Authenticate as admin.")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication feature.")
    public void authAsAdminTest() {
        adminToken = new RestClient().authenticate("admin", "setup");
        Assert.assertTrue(adminToken.authorities.contains("ADMIN"));
    }

    @Test(description = "Authenticate as customer.", dependsOnMethods = "authAsAdminTest")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Authentication feature.")
    public void authAsCustomerTest() {
        CustomerPojo customerPojo = new RestClient().createCustomer(adminToken, RestClient.generateRandomCustomer());

        AccountTokenPojo customerToken = new RestClient().authenticate(customerPojo.login, customerPojo.pass);
        Assert.assertTrue(customerToken.authorities.contains("CUSTOMER"));
    }
}
