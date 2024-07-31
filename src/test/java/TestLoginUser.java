import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestLoginUser {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/auth/";
    }

    @Test
    @DisplayName("Test login user")
    @Description("Test login user")
    public void testLoginUser () {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");
        Response response = createUser(createUserData);

        LoginUserData loginUserData = new LoginUserData(email, "password");
        Response responseLogin = loginUser(loginUserData);

        responseLogin.then().assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user", notNullValue());

        deleteUser(response);
    }

    @Test
    @DisplayName("Test login user with incorrect password")
    @Description("Test login user with incorrect password")
    public void testIncorrectLoginUser () {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        LoginUserData loginUserData = new LoginUserData(email, "password-");
        Response responseLogin = loginUser(loginUserData);

        responseLogin.then().assertThat().statusCode(401);
    }

    @Step("Login User")
    public Response loginUser(LoginUserData loginUserData) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .body(loginUserData)
                .post("/login");
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
