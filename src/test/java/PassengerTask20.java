import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.example.dto.PassengerRequest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class PassengerTask20 {

    //https://instantwebtools.net/fake-rest-api - test
    //https://api.instantwebtools.net/v1/passenger

    public static String passengerId;

    void createPassenger(String passengerName) {
        PassengerRequest newPassengerRequest = PassengerRequest.builder().name(passengerName).trips(6).airline(3).build();

        given().log().all().
                contentType(ContentType.JSON).
                body(newPassengerRequest).
                when().
                post("https://api.instantwebtools.net/v1/passenger").
                then().log().ifValidationFails().
                statusCode(HttpStatus.SC_OK);
    }

    void getPassengerId(String passengerName) {
        Response response = given().contentType(ContentType.JSON).
                when().
                get("https://api.instantwebtools.net/v1/passenger?page=345&size=100").
                then().extract().response();

        JsonPath j = new JsonPath(response.asString());

        int s = j.getInt("data.size()");
        for (int i = 0; i < s; i++) {
            String name = j.getString("data[" + i + "].name");
            if (name.equals(passengerName))
                {passengerId = j.getString("data[" + i + "]._id");
                break;}
        }
        System.out.println(passengerId);
    }

    void deleteCreatedPassenger(String passengerId) {
        given().log().all().
                contentType(ContentType.JSON).
                when().
                delete("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void passengerPostTest() {
        String passengerName;
        passengerName = "Bodger Blodger";

        PassengerRequest newPassengerRequest = PassengerRequest.builder().name(passengerName).trips(3).airline(2).build();

        given().log().all().
                contentType(ContentType.JSON).
                body(newPassengerRequest).
                when().
                post("https://api.instantwebtools.net/v1/passenger").
                then().log().ifValidationFails().
                statusCode(HttpStatus.SC_OK).
                extract().response();

        getPassengerId(passengerName);
        deleteCreatedPassenger(passengerId);

    }

    @Test
    void passengerNegativePostTestAirlineData() {
        String passengerName;
        passengerName = "Bodger Blodger";
        PassengerRequest newPassengerRequest = PassengerRequest.builder().name(passengerName).trips(3).airline(555555555).build();

        given().log().all().
                contentType(ContentType.JSON).
                body(newPassengerRequest).
                when().
                post("https://api.instantwebtools.net/v1/passenger").
                then().log().ifValidationFails().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("message", is("valid airline data must submit."));

        getPassengerId(passengerName);
        deleteCreatedPassenger(passengerId);
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
        String passengerName;
        passengerName = "Steven Thorley the Third";
        createPassenger(passengerName);
        getPassengerId(passengerName);
        given().
                when().
                get("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_OK).
                body("name", is("Steven Thorley the Third")).
                body("trips", is(6)).
                body("airline[0].id", is(3));
        deleteCreatedPassenger(passengerId);
    }

    @Test
    void passengerPutTest() {
        String originalPassengerName = "Greg the garlic Farmer";
        createPassenger(originalPassengerName);
        String updatedPassengerName = "Baradun the High Sorcerer";
        getPassengerId(originalPassengerName);

        PassengerRequest updatedPassengerRequest = PassengerRequest.builder().name(updatedPassengerName).trips(33).airline(1).build();
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
                body("name", is("Baradun the High Sorcerer")).
                body("trips", is(33)).
                body("airline[0].id", is(1));
        deleteCreatedPassenger(passengerId);
    }

    @Test
    void passengerNegativePutTestNoData() {
        String passengerName;
        passengerName = "Bodger Blodger";
        createPassenger(passengerName);
        getPassengerId(passengerName);
        given().
                when().
                put("https://api.instantwebtools.net/v1/passenger/" + passengerId).
                then().log().all().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("message", is("valid passenger data must submit."));
        deleteCreatedPassenger(passengerId);
    }

    @Test
    void passengerDeleteTest() {
        String passengerName;
        passengerName = "Steven Thorley the Third";
        createPassenger(passengerName);
        getPassengerId(passengerName);

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