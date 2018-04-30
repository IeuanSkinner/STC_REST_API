package test.java;

import main.java.UserService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONObject;
import org.junit.Test;

public class UserServiceTest extends JerseyTest {
		@Override
		protected Application configure()
		{
			return new ResourceConfig(UserService.class);
		}
	
		/**
		 * The following test will iterate over the life cycle of creating a user,
		 * getting the information of said user and subsequently deleting the user.
		 * 
		 * Should obviously be expanded to include updating user information via PUT
		 * method but didn't have time.
		 */
		
		@Test
		public void testUserLifecycle() {
			// POST to create new user
			Response resp = target("users").request().post(Entity.entity(
				"firstname=John&lastname=Smith&age=42&location=London&email=john.smith42%40gmail.com",
				MediaType.APPLICATION_FORM_URLENCODED));
			
			assertEquals(201, resp.getStatus());
			assertNotNull(resp.getEntity());
			
			int userId = Integer.parseInt(resp.readEntity(String.class));
			assertTrue(userId > 0); // If userId == 0, user was not created!
			
			this.testGET(userId);
			this.testDELETE(userId);
		}

		public void testGET(int userId) {
			// GET the user we've already created
			Response resp = target("users/" + userId).request().get();
			
			assertEquals(200, resp.getStatus());
			assertNotNull(resp.getEntity());
			
			JSONObject user = new JSONObject(resp.readEntity(String.class));
			assertEquals("John", user.getString("firstname"));
			assertEquals("Smith", user.getString("lastname"));
			assertEquals(42, user.getInt("age"));
			assertEquals("London", user.getString("location"));
			assertEquals("john.smith42%40gmail.com", user.getString("email"));
		}
		
		public void testDELETE(int userId) {
			// DELETE the user we've already created
			Response resp = target("users/" + userId).request().delete();
			assertEquals(200, resp.getStatus());
		}
		
		@Test
	    public void testGETAll() {
			// GET all users
	        Response resp = target("users").request().get();
	        assertEquals(200, resp.getStatus());
	    }
}
