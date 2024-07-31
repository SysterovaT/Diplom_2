import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestEditUserData {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/auth/";
    }

    @DisplayName("Test edit user data")
    @Test
    @Description("Edit user Data")
    public void testEditUserData() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        Response response = createUser(createUserData);
        ResponseAuthUser responseAuthUser = response.body()
                .as(ResponseAuthUser.class);
        String token = responseAuthUser.getAccessToken();

        String editName = "something" + random.nextInt(10000000);
        String editEmail = name + "@yandex.ru";

        UserData userData = new UserData(editEmail, editName);
        Response responseEditData = editUserData(userData, token);
        responseEditData.then().assertThat().statusCode(200);
        responseEditData.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user", notNullValue());
        deleteUser(response);
    }

    @DisplayName("Test edit user data with out auth")
    @Test
    @Description("Edit user Data with out auth")
    public void testEditUserDataWithOutAuth() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        Response response = createUser(createUserData);
        ResponseAuthUser responseAuthUser = response.body()
                .as(ResponseAuthUser.class);
        String token = responseAuthUser.getAccessToken();

        String editName = "something" + random.nextInt(10000000);
        String editEmail = name + "@yandex.ru";

        UserData userData = new UserData(editEmail, editName);
        Response responseEditDataWithOutAuth = editUserDataWithOutAuth(userData, token);
        responseEditDataWithOutAuth.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", notNullValue());
        responseEditDataWithOutAuth.then().assertThat().statusCode(401);
        deleteUser(response);
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

    @Step("Edit user data")
    public Response editUserData(UserData userData, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .when()
                .body(userData)
                .patch("/user");
    }

    @Step("Edit user data with out authorization")
    public Response editUserDataWithOutAuth(UserData userData, String token) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .body(userData)
                .patch("/user");
    }

}
