import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestCreateUser {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/auth/";
    }

    @Test
    @Description("Test register user")
    public void testCreateUser () {

        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        Response response = createUser(createUserData);
        response.then().assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user", notNullValue());
        deleteUser(response);
    }

    @Test
    @Description("Test double user")
    public void testCreateDoubleUser() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        Response responseOne = createUser(createUserData);
        responseOne.then().assertThat().statusCode(200);
        Response responseTwo = createUser(createUserData);
        responseTwo.then().assertThat().statusCode(403);

        deleteUser(responseOne);
    }

    @Test
    @Description("Create user with not requirement parameter")
    public void testCreateUserWithOutParams() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        CreateUserData createUserData = new CreateUserData("", name, "password");
        Response response = createUser(createUserData);
        response.then().assertThat().statusCode(403);
    }

    @Step("Create user")
    public Response createUser(CreateUserData createUserData) {


        return given()
                .header("Content-type", "application/json")
                .body(createUserData)
                .post("/register");
    }

    @Step("Delete user")
    public void  deleteUser (Response response) {
        ResponseAuthUser responseAuthUser = response.body()
                .as(ResponseAuthUser.class);
        String token = responseAuthUser.getAccessToken();
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .when()
                .delete("/user");
    }
}
