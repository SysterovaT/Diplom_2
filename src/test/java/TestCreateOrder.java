import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestCreateOrder {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
    }

    @Test
    @DisplayName("Create order")
    @Description("test create order with auth")
    public void testCreateOrder() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        String ingredients = "61c0c5a71d1f82001bdaaa73,61c0c5a71d1f82001bdaaa74,61c0c5a71d1f82001bdaaa71,61c0c5a71d1f82001bdaaa6c";
        IngredientsData ingredientsData = new IngredientsData(ingredients.split(","));

        Response responseUser = createUser(createUserData);
        responseUser.getBody().prettyPrint();
        ResponseAuthUser responseAuthUser = responseUser.body().as(ResponseAuthUser.class);
        String token = responseAuthUser.getAccessToken();

        Response response = createOrderAuth(ingredientsData, token);
        response.then()
                .assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("order", notNullValue());
        deleteUser(responseUser);
    }

    @Test
    @DisplayName("Create order")
    @Description("test create order with auth")
    public void testCreateOrderNoAuth() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        String ingredients = "61c0c5a71d1f82001bdaaa73,61c0c5a71d1f82001bdaaa74,61c0c5a71d1f82001bdaaa71,61c0c5a71d1f82001bdaaa6c";
        IngredientsData ingredientsData = new IngredientsData(ingredients.split(","));

        Response responseUser = createUser(createUserData);

        Response response = createOrderNoAuth(ingredientsData);
        response.then()
                .assertThat().statusCode(401)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", notNullValue());

        deleteUser(responseUser);
    }

    @Test
    @DisplayName("Create order with invalid hash")
    @Description("test create order with invalid hash")
    public void testCreateOrderInvalidHash() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        String ingredients = "61c0c5a71d1f82001bdaaa73,61a71d1f82001bdaaa74,61c0c5a71d1f82001bdaaa71,61c0c5a71d1f82001bdaaa6c";
        IngredientsData ingredientsData = new IngredientsData(ingredients.split(","));

        Response responseUser = createUser(createUserData);
        ResponseAuthUser responseAuthUser = responseUser.body().as(ResponseAuthUser.class);
        String token = responseAuthUser.getAccessToken();

        Response response = createOrderAuth(ingredientsData, token);
        response.then()
                .assertThat().statusCode(500);
        deleteUser(responseUser);
    }

    @Test
    @DisplayName("Create order with out ingredients")
    @Description("test create order with out ingredients")
    public void testCreateOrderNoIngredients() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        String[] ingredients = {};
        IngredientsData ingredientsData = new IngredientsData(ingredients);

        Response responseUser = createUser(createUserData);
        ResponseAuthUser responseAuthUser = responseUser.body().as(ResponseAuthUser.class);
        String token = responseAuthUser.getAccessToken();
        Response response = createOrderAuth(ingredientsData, token);
        response.then()
                .assertThat().statusCode(400);
        deleteUser(responseUser);
    }

    @Step("Create order with auth")
    public Response createOrderAuth(IngredientsData ingredientsData, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(ingredientsData)
                .when()
                .post("/orders");
    }

    @Step("Create order with out auth")
    public Response createOrderNoAuth(IngredientsData ingredientsData) {
        return given()
                .header("Content-type", "application/json")
                .body(ingredientsData)
                .when()
                .post("/orders");
    }

    @Step("Create user")
    public Response createUser(CreateUserData createUserData) {
        return given()
                .header("Content-type", "application/json")
                .body(createUserData)
                .post("/auth/register");
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
                .delete("/auth/user");
    }
}
