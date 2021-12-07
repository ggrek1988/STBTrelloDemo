package Organization;


import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.Argument;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import trello.Organization;

import java.util.stream.Stream;

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
        assertThat(json.getString("desc")).isEqualTo("the description for the organizations");

        //checking the field name , have a string with a length of at least 3 and not empty
        assertThat(json.getString("name")).isNotEmpty().hasSizeGreaterThanOrEqualTo(3);

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
    //==================================================================
    //definiowanie zmiennej parametrów
    private static Stream<Arguments> createOrganizationDate() {
        return Stream.of(
                Arguments.of("This is display name", "This is desc", "name_123", "http://example.pl"),
                Arguments.of("This is display name", "This is desc", "123", "https://example.pl"),
                Arguments.of("This is display name", "This is desc", "name_123", "https://example.pl"),
                Arguments.of("This is display name", "This is desc", "123", "https://example.pl")
        );
    }
    //definiowanie zmiennej parametrów
    private static Stream<Arguments> createOrganizationDateCode400() {
        return Stream.of(
                Arguments.of("This is display name", "This is desc", "na", "http://example.pl"),
                Arguments.of("This is display name", "This is desc", "ABC", "https://example.pl"),
                Arguments.of("This is display name", "This is desc", "name##123", "https://example.pl"),
                Arguments.of("This is display name", "This is desc", "ABC*!@#", "https://example.pl")
        );
    }
    @DisplayName("Create organization with valid data")
    @ParameterizedTest(name = "Display name: {0}, name: {2}, website: {3}")
    //@ParameterizedTest
    @MethodSource("createOrganizationDate")
    //CreateNewOrganizationWithInvalidDate cod 400
    public void CreateNewOrganizationUseObjectClass(String displayName, String desc,String name, String website  ) {

        Organization organization = new Organization();
        organization.setDisplayName(displayName);
        organization.setDesc(desc);
        organization.setName(name);
        organization.setWebsite(website);


        Response response = given()
                .queryParam("displayName", organization.getDisplayName())
                .queryParam("desc", organization.getDesc())
                .queryParam("name", organization.getName())
                .queryParam("website", organization.getWebsite())
                .spec(reqSpec)
                .when()
                .post(BASE_URL + "/" + ORGANIZATION)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();


        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo(organization.getDisplayName());

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

    @DisplayName("Create organization with invalid data field name")
    @ParameterizedTest(name = "Display name: {0}, name: {2}, website: {3}")
    //@ParameterizedTest
    @MethodSource("createOrganizationDateCode400")
    //CreateNewOrganizationWithInvalidDate cod 400
    public void CreateNewOrganizationWithInvalidDate(String displayName, String desc,String name, String website  ) {

        Organization organization = new Organization();
        organization.setDisplayName(displayName);
        organization.setDesc(desc);
        organization.setName(name);
        organization.setWebsite(website);


        Response response = given()
                .queryParam("displayName", organization.getDisplayName())
                .queryParam("desc", organization.getDesc())
                .queryParam("name", organization.getName())
                .queryParam("website", organization.getWebsite())
                .spec(reqSpec)
                .when()
                .post(BASE_URL + "/" + ORGANIZATION)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();


        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo(organization.getDisplayName());

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
