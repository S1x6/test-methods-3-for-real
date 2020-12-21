package org.nsu.fit.services.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.glassfish.jersey.client.ClientConfig;
import org.nsu.fit.services.log.Logger;
import org.nsu.fit.services.rest.data.*;
import org.nsu.fit.shared.JsonMapper;
import org.openqa.selenium.By;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class RestClient {
    private static final String REST_URI = "http://localhost:8080/tm-backend/rest";

    private static Client client = ClientBuilder.newClient(new ClientConfig().register(RestClientLogFilter.class));

    public AccountTokenPojo authenticate(String login, String pass) {
        CredentialsPojo credentialsPojo = new CredentialsPojo();

        credentialsPojo.login = login;
        credentialsPojo.pass = pass;

        return post("authenticate", JsonMapper.toJson(credentialsPojo, true), AccountTokenPojo.class, null);
    }

    public static ContactPojo generateRandomCustomer() {
        FakeValuesService fakeValuesService = new FakeValuesService(
                new Locale("en-GB"), new RandomService());
        ContactPojo contactPojo = new ContactPojo();
        contactPojo.firstName = fakeValuesService.letterify("??????");
        contactPojo.lastName = fakeValuesService.letterify("????????");
        contactPojo.login = fakeValuesService.bothify("??????_##@????.com");
        contactPojo.pass = fakeValuesService.bothify("??##?#???");
        return contactPojo;
    }

    public static PlanPojo generateRandomPlan() {
        FakeValuesService fakeValuesService = new FakeValuesService(
                new Locale("en-GB"), new RandomService());
        PlanPojo planPojo = new PlanPojo();
        planPojo.details = fakeValuesService.bothify("???###");
        planPojo.fee = Integer.parseInt(fakeValuesService.numerify("####"));
        planPojo.id = UUID.randomUUID();
        planPojo.name = fakeValuesService.letterify("???????");
        return planPojo;
    }

    public CustomerPojo createCustomer(AccountTokenPojo accountToken, ContactPojo contactPojo) {
        // Done
        // Лабораторная 3: Добавить обработку генерацию фейковых имен, фамилий и логинов.
        // * Исследовать этот вопрос более детально, возможно прикрутить специальную библиотеку для генерации фейковых данных.


        return post("customers", JsonMapper.toJson(contactPojo, true), CustomerPojo.class, accountToken);
    }

    private static <R> R post(String path, String body, Class<R> responseType, AccountTokenPojo accountToken) {
        // Лабораторная 3: Добавить обработку Responses и Errors. Выводите их в лог.
        Invocation.Builder request = client
                .target(REST_URI)
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (accountToken != null) {
            request.header("Authorization", "Bearer " + accountToken.token);
        }

        String response = request.post(Entity.entity(body, MediaType.APPLICATION_JSON), String.class);
        if (response.equals("")) {
            JsonMapper.fromJson("{}", responseType);
        }
        return JsonMapper.fromJson(response, responseType);
    }

    private static void post(String path, String body, AccountTokenPojo accountToken) {
        Invocation.Builder request = client
                .target(REST_URI)
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (accountToken != null) {
            request.header("Authorization", "Bearer " + accountToken.token);
        }

        String response = request.post(Entity.entity(body, MediaType.APPLICATION_JSON), String.class);

    }


    private static <R> R get(String path, TypeReference<R> responseType, AccountTokenPojo accountToken) {
        Invocation.Builder request = client
                .target(REST_URI)
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (accountToken != null) {
            request.header("Authorization", "Bearer " + accountToken.token);
        }

        String response = request.get(String.class);

        return JsonMapper.fromJson(response, responseType);
    }

    private static String delete(String path, AccountTokenPojo accountToken) {
        Invocation.Builder request = client
                .target(REST_URI)
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if (accountToken != null) {
            request.header("Authorization", "Bearer " + accountToken.token);
        }

        return request.delete(String.class);
    }

    public List<CustomerPojo> getCustomers(AccountTokenPojo accountToken) {
        return get("customers", new TypeReference<List<CustomerPojo>>() {
        }, accountToken);
    }

    public void deleteCustomer(AccountTokenPojo accountTokenPojo, CustomerPojo customerPojo) {
        delete("customers/" + customerPojo.id, accountTokenPojo);
    }

    public PlanPojo createPlan(AccountTokenPojo adminToken, PlanPojo planPojo) {
        return post("plans", JsonMapper.toJson(planPojo, true), PlanPojo.class, adminToken);
    }

    public List<PlanPojo> getPlans(AccountTokenPojo adminToken) {
        return get("plans", new TypeReference<List<PlanPojo>>() {
        }, adminToken);
    }

    public void deletePlan(AccountTokenPojo adminToken, PlanPojo planPojo) {
        delete("/plans/" + planPojo.id, adminToken);
    }

    public void addBalance(AccountTokenPojo adminToken, int addMoney, CustomerPojo customerPojo) {
        TopUpBalancePojo topUpBalancePojo = new TopUpBalancePojo();
        topUpBalancePojo.customerId = customerPojo.id;
        topUpBalancePojo.money = addMoney;
        post("/customers/top_up_balance", JsonMapper.toJson(topUpBalancePojo, true), adminToken);
    }

    public List<PlanPojo> checkAvailablePlans(AccountTokenPojo accountTokenPojo) {
        return get("/available_plans", new TypeReference<List<PlanPojo>>() {}, accountTokenPojo);
    }

    private static class RestClientLogFilter implements ClientRequestFilter, ClientResponseFilter {
        @Override
        public void filter(ClientRequestContext requestContext) {
            if (requestContext.hasEntity()) {
                Logger.debug("REQUEST BODY: " + requestContext.getEntity().toString());
            }
            StringBuilder sb = new StringBuilder("REQUEST METHOD: " + requestContext.getMethod() + ". HEADERS: ");
            requestContext.getHeaders().forEach((header, objs) -> {
                sb.append(header).append(" - ").append(objs.stream().reduce("", (acc, element) ->
                        acc.toString() + ", " + element.toString()));
                sb.append("; ");
            });
            Logger.debug(sb.toString());
            // Done
            // Лабораторная 3: разобраться как работает данный фильтр
            // и добавить логирование METHOD и HEADERS.
        }

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            Logger.debug("RESPONSE STATUS: " + responseContext.getStatus());
            // нельзя вычитывать поток с энтити, иначе опустеет и дальше упадет
            if (responseContext.hasEntity()) {
                String body = new BufferedReader(new InputStreamReader(responseContext.getEntityStream()))
                        .lines().collect(Collectors.joining("\n"));
                Logger.debug("RESPONSE BODY: " + body);
                // пришлось писать в него обратно
                responseContext.setEntityStream(new ByteArrayInputStream(body.getBytes()));
            }
            StringBuilder sb = new StringBuilder("RESPONSE HEADERS: ");
            responseContext.getHeaders().forEach((header, objs) -> {
                sb.append(header).append(" - ").append(objs.stream().reduce("", (acc, element) ->
                        acc + ", " + element));
                sb.append("; ");
            });
            Logger.debug(sb.toString());
        }
    }
}
