package br.ce.evrasouza.rest;

import static io.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.junit.Test;

import io.restassured.http.ContentType;

public class VerbosTest {
	
	@Test
	public void deveSalvarUsuario() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"José\", \"age\": 50 }")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("José"))	
			.body("age", is(50))
		;
	}
	
	@Test
	public void deveSalvarUsuarioUsandoMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Usuario via MAP");
		params.put("age", 25);
				
		given()
			.log().all()
			.contentType("application/json")
			.body(params)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via MAP"))
			.body("age", is(25))
		;
	}
	
	@Test
	public void deveSalvarUsuarioUsandoObjeto() {
		User user =  new User("Usuario via objeto", 35);
				
		given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via objeto"))
			.body("age", is(35))
		;
	}
	
	@Test
	public void deveDeserializarObjetoAoSalvarUsuario() {
		User user =  new User("Usuario deserializado", 35);
				
		User usuarioInserido = given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		
		System.out.println(usuarioInserido);
		assertThat(usuarioInserido.getId(), notNullValue());
		assertEquals("Usuario deserializado", usuarioInserido.getName());
		assertThat(usuarioInserido.getAge(), is(35));
	}
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{ \"age\": 50 }")
	.when()
		.post("https://restapi.wcaquino.me/users")
	.then()
		.log().all()
		.statusCode(400)
		.body("id", is(nullValue()))
		.body("error", is("Name é um atributo obrigatório"))
	;
	}

	@Test
	public void deveSalvarUsuarioViaXML() {
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user><name>Jose</name><age>50</age></user>")
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			//.body("id", is(notNullValue()))
			//.body("name", is("José"))
			//.body("age", is(50))
		;
	}
	
	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario Alterado\", \"age\": 70 }")
		.when()
			.put("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(70))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveCustomizarURL() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario Alterado\", \"age\": 70 }")
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}", "users","1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(70))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveCustomizarURLPart2() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario Alterado\", \"age\": 70 }")
			.pathParam("entidade", "users")
			.pathParam("userId", 1)
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(70))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}
	
	@Test
	public void aoDeveRemoverUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
}