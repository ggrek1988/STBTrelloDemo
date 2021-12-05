package Board;

import base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;


public class BoardTEST extends BaseTest {


    @Test
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

        String boardId = json.getString("id");

        given()
                .pathParam("boardId", boardId)
                .spec(reqSpec)
                .when()
                .delete(BASE_URL+"/"+BOARDS+"/{boardId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();


    }

    @Test
    public void CreateBoardWithemptyBoardName() {
        given()
                .queryParam("name", "")
                .spec(reqSpec)
                .when()
                .post(BASE_URL+"/"+BOARDS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

    }

    @Test
    public void CreateBoardWithDefaultList() {
        Response response = given()
                .queryParam("name", "Rest")
                .queryParam("defaultLists", "false")
                .spec(reqSpec)
                .when()
                .post(BASE_URL+"/"+BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo("Rest");
        //assertEquals("Rest", json.get("name"));
        String boardId = json.getString("id");

        Response response1 = given()
                .pathParam("id", boardId)
                .spec(reqSpec)
                .when()
                .get(BASE_URL+"/"+BOARDS+"/{id}/"+LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        JsonPath jsonGet = response1.jsonPath();
        List<Object> idList = jsonGet.getList("id");

        Assertions.assertThat(idList).hasSize(0);
        //Assertions.assertEquals(0,idList.size());


        given()
                .pathParam("boardId", boardId)
                .spec(reqSpec)
                .when()
                .delete(BASE_URL+"/"+BOARDS+"/{boardId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();


    }

    @Test
    public void CreateBoardWithDefaultListTrue() {
        Response response = given()
                .queryParam("name", "Rest1")
                .queryParam("defaultLists", "true")
                .spec(reqSpec)
                .when()
                .post(BASE_URL+"/"+BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        //String json1 = json.get("name");
        //isEqualTo - jest równa
        assertThat(json.getString("name")).isEqualTo("Rest1");
        //assertEquals("Rest1", json.get("name"));
        String boardId = json.getString("id");

        //RestAssure
        Response response1 = given()
                .pathParam("id", boardId)
                .spec(reqSpec)
                .when()
                .get(BASE_URL+"/"+BOARDS+"/{id}/"+LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath jsonGet = response1.jsonPath();
        List<String> idList = jsonGet.getList("name");
        //System.out.println(jsonGet.prettyPrint());
        //assetion with use assertj
        //contains - zawiera
        assertThat(idList).hasSize(3).contains("Do zrobienia", "W trakcie", "Zrobione");

//        //assetion with use junit
//        Assertions.assertEquals(3,idList.size());
//        List<String> nameList = jsonGet.getList("name");
//        Assertions.assertEquals("To Do",nameList.get(0));
//        Assertions.assertEquals("Doing ",nameList.get(1));
//        Assertions.assertEquals("Done",nameList.get(2));


        given()
                .pathParam("boardId", boardId)
                .spec(reqSpec)
                .when()
                .delete("https://api.trello.com/1/boards/{boardId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();


    }


}
