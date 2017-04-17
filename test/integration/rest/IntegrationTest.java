package integration.rest;

import com.fasterxml.jackson.databind.JsonNode;
import dal.users.UsersRepository;
import dal.users.UsersRepositoryMock;
import junit.framework.TestCase;
import models.users.Gender;
import models.users.User;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

public class IntegrationTest {

    private Application application;

    @Before
    public void initalize() {
        this.application = new GuiceApplicationBuilder()
            .overrides(bind(UsersRepository.class).to(UsersRepositoryMock.class))
            .build();
    }

    @Test
    public void testGetExistingUser() {
        Helpers.running(application, () -> {
            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/users/1");
            Result result = route(request);
            TestCase.assertEquals(OK, result.status());
        });
    }

    @Test
    public void testGetNonExistingUser() {
        Helpers.running(application, () -> {
            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/users/123123123");
            Result result = route(request);
            TestCase.assertEquals(NOT_FOUND, result.status());
        });
    }

    @Test
    public void testGetEvents() {
        Helpers.running(application, () -> {
            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/events");
            Result result = route(request);
            TestCase.assertEquals(OK, result.status());
        });
    }

    /*

    @Test
    public void testCreateUserFromJson(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

        try {
            JsonNode json = Json.parse("{\"id\":\"1\", " +
                    "\"email\":\"elon.musk@hsr.ch\", " +
                    "\"lastName\":\"Musk\", " +
                    "\"firstName\":\"Elon\"," +
                    "\"dateOfBirth\":\"1971-06-28\"," +
                    "\"gender\":\"MALE\"}");

            User user = new User(1, "elon.musk@hsr.ch", "Musk", "Elon", dateFormat.parse("1971-06-28"), Gender.MALE);

            User userResult = Json.fromJson(json, User.class);

            TestCase.assertEquals(user, userResult);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }*/

    @Test
    public void testRegisterUser() {
        JsonNode json = Json.parse("{\"id\":\"1\", " +
                "\"email\":\"elon.musk@hsr.ch\", " +
                "\"lastName\":\"Musk\", " +
                "\"firstName\":\"Elon\"," +
                "\"dateOfBirth\":\"1971-06-28\"," +
                "\"gender\":\"MALE\"}");
        Helpers.running(application, () -> {
            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(POST)
                    .uri("/register")
                    .bodyJson(json);
            Result result = route(request);

            TestCase.assertEquals(OK, result.status());

        });
    }

//    @Test
//    public void testGetUser() {
//        running(fakeApplication(), () -> {
//            Integer id = 1;
//
//            Http.RequestBuilder mockActionRequest = fakeRequest(controllers.routes.UserController.getUser(id));
//            Result result = route(mockActionRequest);
//            // Todo: Revert the test result to test for OK
//            assertEquals(NOT_FOUND, result.status());
//        });
//    }

//    @Test
//    public void testGetAllEvents() {
//        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
//            browser.goTo("http://localhost:3333/events");
//            assertEquals(Json.toJson(serverData.getEvents()).toString(), browser.pageSource().toString());
//        });
//    }

}
