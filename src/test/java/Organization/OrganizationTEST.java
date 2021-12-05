package Organization;


import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrganizationTEST extends BaseTest {

    @Test
    @Order(1)
    public void CreateNewOrganization() {

        Response response = given()
                .queryParam("displayName", "new name organization")
                .queryParam("desc", "the description for the organizations")
                .queryParam("name", "name_123")
                .queryParam("website", "http://www.exaple.pl")
                .spec(reqSpec)
                .when()
                .post(BASE_URL + "/" + ORGANIZATION)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();


        JsonPath json = response.jsonPath();
        //checking the field displayName
        assertThat(json.getString("displayName")).isNotEmpty().isEqualTo("new name organization");

        //checking the field desc  is correct and not empty
        assertThat(json.getString("desc")).isNotEmpty().isEqualTo("the description for the organizations");

        //checking the field name , have a string with a length of at least 3 and not empty
        assertThat(json.getString("name")).isNotEmpty().hasSizeGreaterThan(3);

        //checking the field name , have Only lowercase letter and contains have number and underscores

        assertThat(json.getString("name")).isLowerCase();
        assertThat(json.getString("name")).contains("_", "123");

        //check if website starts with https, doesnt contains com at the end and contains .pl

        assertThat(json.getString("website")).startsWith("http");
        assertThat(json.getString("website")).contains(".pl");
        assertThat(json.getString("website")).doesNotContain(".com");

        String organizationID = json.getString("id");
        given()
                .pathParam("boardId", organizationID)
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + "/" + ORGANIZATION + "/{boardId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();


    }
}
