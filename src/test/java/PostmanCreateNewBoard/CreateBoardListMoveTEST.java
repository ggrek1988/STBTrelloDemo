package PostmanCreateNewBoard;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateBoardListMoveTEST extends BaseTest {

    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String cardId;


    @Test
    @Order(1)
    public void createNewBoard() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "testNewBoard")
                .queryParam("defaultLists", false)
                .when()
                .post(BASE_URL + "/" + BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        boardId = json.get("id");
        assertThat(json.getString("name")).isEqualTo("testNewBoard");
    }

    @Test
    @Order(2)
    public void createFirsList() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "first list")
                .queryParam("idBoard", boardId)
                .when()
                .post(BASE_URL + "/" + LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        firstListId = json.get("id");
        String firstListName = json.getString("name");
        assertThat(firstListName).isEqualTo("first list");
    }

    @Test
    @Order(3)
    public void createSecondList() {
        //create second list
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "second list")
                .queryParam("idBoard", boardId)
                .when()
                .post(BASE_URL + "/" + LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        secondListId = json.get("id");
        assertThat(json.getString("name")).isEqualTo("second list");
    }

    @Test
    @Order(4)
    public void createCardonTheFirstList() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "first list carts")
                .queryParam("idList", firstListId)
                .when()
                .post(BASE_URL + "/" + CARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        cardId = json.get("id");

        assertThat(json.getString("name")).isEqualTo("first list carts");
    }

    @Test
    @Order(5)
    public void moveCardonTheSecondList() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("idBoard", boardId)
                .queryParam("idList", secondListId)
                .when()
                .put(BASE_URL + "/" + CARDS + "/" + cardId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("idList")).isEqualTo(secondListId);

        //delete board
        given()
                .spec(reqSpec)
                .queryParam("idBoard", boardId)
                .when()
                .delete(BASE_URL + "/" + BOARDS + "/" + boardId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

}
