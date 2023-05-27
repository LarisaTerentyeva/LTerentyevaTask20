import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.example.dto.PassengerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class PassengerTask20 {

    //https://instantwebtools.net/fake-rest-api - test
    //https://api.instantwebtools.net/v1/passenger

    public static String passengerId;

    @BeforeEach
    void createPassenger() {
        PassengerRequest newPassengerRequest = PassengerRequest.builder().name("Steven Thorley the Third").trips(6).airline(3).build();

        given().log().all().
                contentType(ContentType.JSON).
                body(newPassengerRequest).
                when().
                post("https://api.instantwebtools.net/v1/passenger").
                then().log().ifValidationFails().
                statusCode(HttpStatus.SC_OK);
    }

    @BeforeEach
    void getPassengerId() {
        Response response = given().contentType(ContentType.JSON).
                when().
                get("https://api.instantwebtools.net/v1/passenger?page=21&size=1000").
                then().extract().response();

        JsonPath j = new JsonPath(response.asString());

        int s = j.getInt("data.size()");
        for (int i = 0; i < s; i++) {
            String name = j.getString("data[" + i + "].name");
            if (name.equals("Steven Thorley the Third"))
                {passengerId = j.getString("data[" + i + "]._id");
                break;}
            else if ((name.equals("Greg the garlic Farmer")))
                { passengerId = j.getString("data[" + i + "]._id");
                break;}
        }
        System.out.println(passengerId);
    }

    @AfterEach
    void deleteCreatedPassengers() {
        Response response = given().contentType(ContentType.JSON).
                when().
                get("https://api.instantwebtools.net/v1/passenger?page=21&size=1000").
                then().extract().response();

        JsonPath j = new JsonPath(response.asString());

        int s = j.getInt("data.size()");
        for (int i = 0; i < s; i++) {
            String name = j.getString("data[" + i + "].name");
            if (name.equals("Bodger Blodger")) { passengerId = j.getString("data[" + i + "]._id");
            given().log().all().
                    contentType(ContentType.JSON).
                    when().
                    delete("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                    then().log().all().
                    statusCode(HttpStatus.SC_OK);}
            else if ((name.equals("Greg the garlic Farmer"))) { passengerId = j.getString("data[" + i + "]._id");
            given().log().all().
                    contentType(ContentType.JSON).
                    when().
                    delete("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                    then().log().all().
                    statusCode(HttpStatus.SC_OK);}
            else if ((name.equals("Steven Thorley the Third"))) { passengerId = j.getString("data[" + i + "]._id");
                given().log().all().
                        contentType(ContentType.JSON).
                        when().
                        delete("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                        then().log().all().
                        statusCode(HttpStatus.SC_OK);}
        }
    }

    @Test
    void passengerPostTest() {
        PassengerRequest newPassengerRequest = PassengerRequest.builder().name("Bodger Blodger").trips(3).airline(2).build();

        given().log().all().
                contentType(ContentType.JSON).
                body(newPassengerRequest).
                when().
                post("https://api.instantwebtools.net/v1/passenger").
                then().log().ifValidationFails().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void passengerNegativePostTestAirlineData() {
        PassengerRequest newPassengerRequest = PassengerRequest.builder().name("Bodger Blodger").trips(3).airline(555555555).build();

        given().log().all().
                contentType(ContentType.JSON).
                body(newPassengerRequest).
                when().
                post("https://api.instantwebtools.net/v1/passenger").
                then().log().ifValidationFails().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("message", is("valid airline data must submit."));
    }

    @Test
    void passengerNegativePostTestNoData() {
        given().log().all().
                contentType(ContentType.JSON).
                when().
                post("https://api.instantwebtools.net/v1/passenger").
                then().log().ifValidationFails().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("message", is("valid passenger data must submit."));
    }

    @Test
    void passengerGetTest() {
        given().
                when().
                get("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_OK).
                body("name", is("Steven Thorley the Third")).
                body("trips", is(6)).
                body("airline[0].id", is(3));
    }

    @Test
    void passengerPutTest() {
        PassengerRequest updatedPassengerRequest = PassengerRequest.builder().name("Greg the garlic Farmer").trips(33).airline(1).build();

        given().log().all().
                contentType(ContentType.JSON).
                body(updatedPassengerRequest).
                when().
                put("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_OK).
                body("message", is("Passenger data put successfully completed."));

        given().
                when().
                get("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_OK).
                body("name", is("Greg the garlic Farmer")).
                body("trips", is(33)).
                body("airline[0].id", is(1));
    }

    @Test
    void passengerNegativePutTestNoData() {
        given().
                when().
                put("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("message", is("valid passenger data must submit."));
    }

    @Test
    void passengerDeleteTest() {
        given().log().all().
                contentType(ContentType.JSON).
                when().
                delete("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_OK).
                body("message", is("Passenger data deleted successfully."));

        given().
                when().
                get("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }
}