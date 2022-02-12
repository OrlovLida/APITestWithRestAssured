package reqres;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class ReqresTest {
private final static String URL = "https://reqres.in/";

    @Test
public void checkAvatarsTest(){
    Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecOK200());
        Response response = (Response) given()
                .when()
                .get("/api/users?page=2")
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");

        for (int i=0; i< avatars.size(); i++){
            Assert.assertTrue((avatars.get(i).contains(ids.get(i).toString())));
        }

    }
   @Test
   public void successUserRegistrationTest(){
       Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecOK200());
       Map<String, String> user = new HashMap<>();
       user.put("email","eve.holt@reqres.in");
       user.put("password", "pistol");
       Response response = (Response) given()
               .body(user)
               .when()
               .post("/api/register")
               .then().log().all()
               .body("id",equalTo(4))
               .body("token", equalTo("QpwL5tke4Pnpja7X4"))
               .extract().response();
       JsonPath jsonPath = response.jsonPath();
       int id = jsonPath.get("id");
       String token = jsonPath.get("token");
       Assert.assertEquals(4,id);
       Assert.assertEquals("QpwL5tke4Pnpja7X4", token);

   }
    @Test
    public void unsuccessUserRegistrationTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL),Specifications.responseSpecError400());
        Map<String, String> user = new HashMap<>();
        user.put("email","sydney@fife");
        given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .body("error", equalTo("Missing password"));
        }

    }
