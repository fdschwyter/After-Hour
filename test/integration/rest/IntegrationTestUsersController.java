package integration.rest;

import com.fasterxml.jackson.databind.JsonNode;
import junit.framework.TestCase;
import models.users.Gender;
import models.users.User;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Created by Fabian on 18.05.17.
 */
public class IntegrationTestUsersController extends WithApplication {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testGetExistingUser() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/users/1");
        final Result result = route(request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testGetNonExistingUser() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/users/123123123");
        final Result result = route(request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testLoginCorrect(){
        Map<String, String[]> loginData = new TreeMap<>();
        loginData.put("email", new String[]{"silvio.berlusconi@italy.it"});
        loginData.put("password", new String[]{"123456"});
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/users/login")
                .bodyFormArrayValues(loginData);
        final Result result = route(request);
        TestCase.assertTrue(Helpers.contentAsString(result).contains("silvio.berlusconi@italy.it"));
    }

    @Test
    public void testLoginCorrectWithCharReplacement(){
        Map<String, String[]> loginData = new TreeMap<>();
        loginData.put("email", new String[]{"silvio.berlusconi%40italy.it"});
        loginData.put("password", new String[]{"123456"});

        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/users/login")
                .bodyFormArrayValues(loginData);
        final Result result = route(request);
        TestCase.assertTrue(Helpers.contentAsString(result).contains("silvio.berlusconi@italy.it"));
    }

    @Test
    public void testLoginIncorrectWithIncorrectPassword(){
        Map<String, String[]> loginData = new TreeMap<>();
        loginData.put("email", new String[]{"silvio.berlusconi@italy.it"});
        loginData.put("password", new String[]{"1234567"});
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/users/login")
                .bodyFormArrayValues(loginData);
        final Result result = route(request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testLoginWithNotExistingUser(){
        Map<String, String[]> loginData = new TreeMap<>();
        loginData.put("email", new String[]{"daniela.meier@plaza.de"});
        loginData.put("password", new String[]{"1234566"});
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/users/login")
                .bodyFormArrayValues(loginData);
        final Result result = route(request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void testCreateUserFromJson() throws ParseException {
        final JsonNode json = Json.toJson(new User(1, "elon.musk@hsr.ch", "Musk", "Elon", dateFormat.parse("1971-06-28"), Gender.MALE));
        final User user = new User(1, "elon.musk@hsr.ch", "Musk", "Elon", dateFormat.parse("1971-06-28"), Gender.MALE);
        final User userResult = Json.fromJson(json, User.class);
        assertEquals(user.getEmail(), userResult.getEmail());
    }

    @Test
    public void testRegisterUser() throws ParseException {
        String email = "elon.musk@hsr.ch";

        final User createdUser = new User(null,
                email,
                "Musk",
                "Elon",
                dateFormat.parse("1971-06-28"),
                Gender.MALE);
        final JsonNode createdUserJson = Json.toJson(createdUser);

        final Http.RequestBuilder createUserRequest = new Http.RequestBuilder()
                .method(POST)
                .uri("/users/register")
                .bodyJson(createdUserJson);
        final Result createUserResult = route(createUserRequest);
        assertEquals(OK, createUserResult.status());

        final Http.RequestBuilder checkUserRequest = new Http.RequestBuilder()
                .method(GET)
                .uri("/users/mail/elon.musk@hsr.ch");
        final Result checkUserResult = route(checkUserRequest);
        String resultUserString = Helpers.contentAsString(checkUserResult);
        assertTrue(resultUserString.contains(email));
    }

    @Test
    public void testRegisterUserWithWrongJson() throws ParseException {
        final String createdUser = "Bla";
        final JsonNode createdUserJson = Json.toJson(createdUser);

        final Http.RequestBuilder createUserRequest = new Http.RequestBuilder()
                .method(POST)
                .uri("/users/register")
                .bodyJson(createdUserJson);
        final Result createUserResult = route(createUserRequest);
        assertEquals(BAD_REQUEST, createUserResult.status());
    }

    @Test
    public void testRegisterUserWithNoPostBody(){
        final Http.RequestBuilder createUserRequest = new Http.RequestBuilder()
                .method(POST)
                .uri("/users/login");
        final Result createUserResult = route(createUserRequest);
        assertEquals(BAD_REQUEST, createUserResult.status());
    }

    @Test
    public void testGetProfileImage() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/users/1/image");
        final Result result = route(request);
        assertEquals("image/jpeg", result.contentType().get());
        assertEquals(OK, result.status());
    }
}
