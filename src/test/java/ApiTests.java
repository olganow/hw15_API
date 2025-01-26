import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;


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
 /*
    1. Make request (GET) to https://reqres.in/api/users/2
        with empty body
    2. Get response with a body "{
    "data": {
        "id": 2,
        "email": "janet.weaver@reqres.in",
        "first_name": "Janet",
        "last_name": "Weaver",
        "avatar": "https://reqres.in/img/faces/2-image.jpg"
    },
    "support": {
        "url": "https://contentcaddy.io?utm_source=reqres&utm_medium=json&utm_campaign=referral",
        "text": "Tired of writing endless social media content? Let Content Caddy generate it for you."
    }
}"
    3. Check the body response and status code 200
  */

    @Test
    @DisplayName("Get Single User")
    void getSingleUserTest() {
        given()
                .log().uri()
                .when()
                .get(USERS + "/" + userId)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.email", equalTo("janet.weaver@reqres.in"))
                .body("data.first_name", equalTo("Janet"))
                .body("data.last_name", equalTo("Weaver"))
                .body("data.avatar", equalTo("https://reqres.in/img/faces/2-image.jpg"))
                .body("support.url", equalTo("https://contentcaddy.io?utm_source=reqres&utm_medium=json&utm_campaign=referral"))
                .body("support.text", equalTo("Tired of writing endless social media content? Let Content Caddy generate it for you."));
    }

    @Test
    @DisplayName("Get Single User by wrong ID")
    void getSingleUserByWrongIdTest() {

        given()
                .log().uri()
                .when()
                .get(USERS + "/" + wrongUserId)
                .then()
                .log().status()
                .log().body()
                .statusCode(404)
                .body(equalTo("{}"));
    }


    /*
   1. Make request (PUT) to https://reqres.in/api/users/2
       with the  body "{
    "name": "morpheus",
    "job": "zion resident"
}"
   2. Get response with a body "{
    "name": "morpheus",
    "job": "zion resident",
    "updatedAt": "2025-01-26T19:57:26.871Z"
}"
   3. Check the body response and status code 200
 */
    @Test
    @DisplayName("Update User's fields")
    void updateUserTest() {
        String requestBody = "{\n" +
                "    \"name\": \"morpheus\",\n" +
                "    \"job\": \"zion resident\"\n" +
                "}";

        given()
                .body(requestBody)
                .contentType(JSON)
                .log().uri()
                .when()
                .put(USERS + "/" + userId)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("zion resident"))
                .body("updatedAt", matchesRegex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
    }

    /*
     1. Make request (DELETE) to https://reqres.in/api/users/2
         with an empty body
     2. Get response
     3. Check the body response and status code 204
   */
    @Test
    @DisplayName("Delete User")
    void deleteUserTest() {
        given()
                .log().uri()
                .when()
                .delete(USERS + "/" + userId)
                .then()
                .log().status()
                .log().body()
                .statusCode(204)
                .body(is(emptyOrNullString()));
    }


    /*
   1. Make request (POST) to https://reqres.in/api/users/2
       with the  body "{
    "name": "morpheus",
    "job": "leader"
}"
   2. Get response with a body "{
    "name": "morpheus",
    "job": "leader",
    "id": "89",
    "createdAt": "2025-01-26T20:15:24.665Z"
}"
   3. Check the body response and status code 201
 */
    @Test
    @DisplayName("Create User")
    void addUserTest() {
        String requestBody = "{\n" +
                "    \"name\": \"morpheus\",\n" +
                "    \"job\": \"leader\"\n" +
                "}";

        given()
                .body(requestBody)
                .contentType(JSON)
                .log().uri()
                .when()
                .post(USERS + "/" + userId)
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("leader"))
                .body("id", is(notNullValue()))
                .body("createdAt", matchesRegex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
    }

}

