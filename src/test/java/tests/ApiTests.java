package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.UserRequestModel;
import models.UserResponseModel;
import models.UserSingleResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import specs.ApiSpec;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.ApiSpec.baseRequestSpec;


@Tag("simple")
public class ApiTests {

    public static final String BASE_URL = "https://reqres.in";
    public static final String API = "/api";
    public static final String USERS = "/users";
    private String userId = "2";
    private String wrongUserId = "99999";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.basePath = API;
    }

    @Test
    @DisplayName("Get Single User")
    void getSingleUserTest() {
        final Response response = step("Get Single User", () ->
                given(baseRequestSpec)
                        .when()
                        .get(USERS + "/" + userId)
                        .then()
                        .spec(ApiSpec.successResponseSpec)
                        .extract()
                        .response()
        );

        step("Map response to UserResponseModel", () -> {
            UserSingleResponseModel userResponse = response.as(UserSingleResponseModel.class);

            step("Check user data", () -> {
                assertThat(userResponse.getData().getId()).isEqualTo(2);
                assertThat(userResponse.getData().getEmail()).isEqualTo("janet.weaver@reqres.in");
                assertThat(userResponse.getData().getFirst_name()).isEqualTo("Janet");
                assertThat(userResponse.getData().getLast_name()).isEqualTo("Weaver");
                assertThat(userResponse.getData().getAvatar()).isEqualTo("https://reqres.in/img/faces/2-image.jpg");
            });

            step("Check support information", () -> {
                assertThat(userResponse.getSupport().getUrl()).isEqualTo("https://contentcaddy.io?utm_source=reqres&utm_medium=json&utm_campaign=referral");
                assertThat(userResponse.getSupport().getText()).isEqualTo("Tired of writing endless social media content? Let Content Caddy generate it for you.");
            });
        });
    }


    @Test
    @DisplayName("Get Single User by wrong ID")
    void getSingleUserByWrongIdTest() {
        final Response response = step("Get Single User by wrong ID", () ->
                given(baseRequestSpec)
                        .when()
                        .get(USERS + "/" + wrongUserId)
                        .then()
                        .spec(ApiSpec.notFoundErrorSpec)
                        .extract()
                        .response()
        );

        step("Check response", () -> {
            assertThat(response.statusCode()).isEqualTo(404);
            assertThat(response.body().asString()).isEqualTo("{}");

            step("Map response to UserResponseModel", () -> {
                UserResponseModel userResponse = response.as(UserResponseModel.class);

                //Эти  проверки  для демонстрации использования модели
                assertThat(userResponse.getName()).isNull();
                assertThat(userResponse.getJob()).isNull();
                assertThat(userResponse.getId()).isNull();
                assertThat(userResponse.getCreatedAt()).isNull();
                assertThat(userResponse.getUpdatedAt()).isNull();
            });
        });
    }


    @Test
    @DisplayName("Update User's fields")
    void updateUserTest() {
        UserRequestModel requestData = new UserRequestModel();
        requestData.setName("morpheus");
        requestData.setJob("zion resident");

        UserResponseModel response = step("Update a user information", () ->
                given(baseRequestSpec)
                        .body(requestData)
                        .when()
                        .put(USERS + "/" + userId)
                        .then()
                        .spec(ApiSpec.successResponseSpec)
                        .extract().as(UserResponseModel.class)
        );

        step("Validate that a response has correct fields", () -> {
            assertThat(response.getName()).isEqualTo("morpheus");
            assertThat(response.getJob()).isEqualTo("zion resident");
            assertThat(response.getUpdatedAt()).matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$");
        });
    }


    @Test
    @DisplayName("Delete User")
    void deleteUserTest() {
        Response response = step("Delete the user", () ->
                given(baseRequestSpec)
                        .when()
                        .delete(USERS + "/" + userId)
                        .then()
                        .spec(ApiSpec.deleteResponseSpec)
                        .extract()
                        .response()
        );


        step("Check response", () -> {
            assertThat(response.statusCode()).isEqualTo(204);
            assertThat(response.body().asString()).isNullOrEmpty();

        });
    }


    @Test
    @DisplayName("Create User")
    void addUserTest() {
        UserRequestModel requestModel = new UserRequestModel();
        requestModel.setName("morpheus");
        requestModel.setJob("leader");

        UserResponseModel response = step("Add a user", () ->
                given(baseRequestSpec)
                        .body(requestModel)
                        .when()
                        .post(USERS)
                        .then()
                        .spec(ApiSpec.createdResponseSpec)
                        .extract().as(UserResponseModel.class)
        );

        step("Validate that a response has correct fields", () -> {
            assertThat(response.getName()).isEqualTo("morpheus");
            assertThat(response.getJob()).isEqualTo("leader");
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCreatedAt()).matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$");
        });
    }

}
