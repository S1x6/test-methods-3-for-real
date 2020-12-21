package org.nsu.fit.services.rest.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPojo extends ContactPojo{
    @JsonProperty("id")
    public UUID id;

    @Override
    public int hashCode() {
        return Objects.hashCode(id.toString() + login + pass + firstName + lastName + balance);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof  CustomerPojo)) {
            return false;
        }
        CustomerPojo given = (CustomerPojo) obj;
        return given.id.equals(id) && given.login.equals(login)
                && given.lastName.equals(lastName) && given.firstName.equals(firstName) && given.pass.equals(pass)
                && given.balance == balance;
    }
}
