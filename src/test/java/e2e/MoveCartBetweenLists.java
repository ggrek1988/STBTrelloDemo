package e2e;
import base.BaseTest;
import base.BaseTest.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


import static io.restassured.RestAssured.given;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoveCartBetweenLists extends BaseTest {

    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String cardId;


    @Test
    @Order(1)
    public void CreateNewBoards() {

        Response response = given()
                .queryParam("name", "RestAssured")
                .spec(reqSpec)
                .when()
                .post(BASE_URL+"/"+BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("RestAssured");
        //assertEquals("RestAssured", json.get("name"));

        boardId = json.getString("id");

        //https://api.trello.com/1/lists?name={name}&idBoard=5abbe4b7ddc1b351ef961414


    }

    @Test
    @Order(2)
    public void CreateNewLists(){
        Response response = given()
                .queryParam("name", "First List")
                .queryParam("idBoard", boardId)
                .spec(reqSpec)
                .when()
                .post(BASE_URL+"/"+LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("First List");

        firstListId = json.getString("id");
    }

    @Test
    @Order(3)
    public void CreateSecondLists(){
        Response response = given()
                .queryParam("name", "Second List")
                .queryParam("idBoard", boardId)
                .spec(reqSpec)
                .when()
                .post(BASE_URL+"/"+LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Second List");

        secondListId = json.getString("id");
    }

    @Test
    @Order(4)
    public void addCardToFirstList() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("idList", firstListId)
                .queryParam("name", "My e2e card")
                .when()
                .post(BASE_URL + "/" + CARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("My e2e card");

        cardId = json.getString("id");
    }

    @Test
    @Order(5)
    public void moveCardToSecondList() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("idList", secondListId)
                .when()
                .put(BASE_URL + "/" + CARDS + "/" + cardId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        Assertions.assertThat(json.getString("idList")).isEqualTo(secondListId);
    }

    @Test
    @Order(6)
    public void deleteBoard() {

        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + "/" + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);
    }
}
