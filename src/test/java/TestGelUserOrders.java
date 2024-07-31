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

public class TestGelUserOrders {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
    }


    @Test
    @DisplayName("Get user orders")
    @Description("Get user orders with auth")
    public void testGetOrderAuth() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";
        CreateUserData createUserData = new CreateUserData(email, name, "password");

        String ingredients = "61c0c5a71d1f82001bdaaa73,61c0c5a71d1f82001bdaaa74,61c0c5a71d1f82001bdaaa71,61c0c5a71d1f82001bdaaa6c";
        IngredientsData ingredientsData = new IngredientsData(ingredients.split(","));

        Response responseUser = createUser(createUserData);
        ResponseAuthUser responseAuthUser = responseUser.body().as(ResponseAuthUser.class);
        String token = responseAuthUser.getAccessToken();

        Response response = createOrderAuth(ingredientsData, token);

        OrdersCreateResponseData order = response.body().as(OrdersCreateResponseData.class);
        String orderId = order.getOrder().get_id();

        Response responseOrders = getOrderAuth(token);
        responseOrders.then().statusCode(200);
        responseOrders.then().assertThat().body("success", equalTo(true));
        responseOrders.then().assertThat().body("orders[0]._id", equalTo(orderId));

        deleteUser(responseUser);
    }

    @Test
    @DisplayName("Get user orders with out auth")
    @Description("Get user orders with no auth")
    public void testGetOrderNoAuth() {
        Random random = new Random();
        String name = "something" + random.nextInt(10000000);
        String email = name + "@yandex.ru";

        Response responseOrders = getOrderNoAuth();
        responseOrders.then().statusCode(401);
        responseOrders.then().assertThat().body("success", equalTo(false));
        responseOrders.then().assertThat().body("message", notNullValue());



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

    @Step("Get orders user with auth")
    public Response getOrderAuth(String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .when()
                .get("/orders");
    }

    @Step("Get orders user with out auth")
    public Response getOrderNoAuth() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("/orders");
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
