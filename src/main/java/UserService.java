package main.java;

/**
 * @author Ieuan Skinner
 */

import java.sql.*;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("/users")
public class UserService {

	private static final String jdbcDriver = "com.mysql.jdbc.Driver";
	private static final String dbURL = "jdbc:mysql://localhost/USERS";
	
	private static final String username = "YOUR_USERNAME";
	private static final String password = "YOUR_PASSWORD";
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers()
	{	
		String output = getUsers(0).toString();
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("param") String userId) {
		JSONArray user = this.getUsers(Integer.parseInt(userId));
		return Response.status(200).entity(user.get(0).toString()).build();
	}
	
	@POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addUser(String url) {
		// toJSON will return a JSON object containing encrypted data
		JSONObject data = toJSON(url);
		String id = "0";
		
		String sql = "INSERT INTO USERS.USER (first, last, age, location, email) VALUES (?, ?, ?, ?, ?)";
		
		Connection conn = this.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, data.getString("firstname"));
			pstmt.setString(2, data.getString("lastname"));
			pstmt.setString(3, data.getString("age"));
			pstmt.setString(4, data.getString("location"));
			pstmt.setString(5, data.getString("email"));
			
			int rows = pstmt.executeUpdate();
			
			if(rows > 0) {
				ResultSet generatedKeys = pstmt.getGeneratedKeys();
	
	            if (generatedKeys.next()) {
	                id = generatedKeys.getLong(1) + "";
	            } else {
					throw new SQLException("New User Not Created!");
	            }
			} else {
				throw new SQLException("New User Not Created!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// Return the id of the newly added user.
		return Response.status(201).entity(id).build();
	}
	
	@DELETE
	@Path("/{param}")
	public Response removeUser(@PathParam("param") String userId) {
		Connection conn = this.getConnection();
		PreparedStatement pstmt = null; 
		
		try {
			pstmt = conn.prepareStatement("DELETE FROM USERS.USER WHERE id = ?");
			pstmt.setInt(1, Integer.parseInt(userId));
			pstmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
				
				if(pstmt != null) {
					pstmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}

		return Response.status(200).build();
	}
	
	public JSONObject toJSON(String url) {
		JSONObject json = new JSONObject();
		String[] inputs = url.split("&");
		
		for(String input : inputs) {
			String key = input.split("=")[0];
			String value = input.split("=")[1];
			// Encrypt user data to be placed in store via public key.
			try {
				AsymmetricCryptography ac = new AsymmetricCryptography();
				PublicKey pk = ac.getPublic("/Users/TehMrSkinner/eclipse-workspace/STC_REST_API/KeyPair/publicKey");
				json.put(key, ac.encryptText(value, pk));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return json;
	}
	
	private JSONArray getUsers(int id) {
		JSONArray json = new JSONArray(); 

		Connection conn = this.getConnection();
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM USERS.USER" + (id != 0 ? " WHERE id=" + id : ""));
			while(rs.next()) {
				JSONObject obj = new JSONObject();
				
				AsymmetricCryptography ac = new AsymmetricCryptography();
				PrivateKey pk = ac.getPrivate("/Users/TehMrSkinner/eclipse-workspace/STC_REST_API/KeyPair/privateKey");
				
				obj.put("id", rs.getInt("id"));
				obj.put("firstname", ac.decryptText(rs.getString("first"), pk));
				obj.put("lastname", ac.decryptText(rs.getString("last"), pk));
				obj.put("age", Integer.parseInt(ac.decryptText(rs.getString("age"), pk)));
				obj.put("location", ac.decryptText(rs.getString("location"), pk));
				obj.put("email", ac.decryptText(rs.getString("email"), pk));
				
				json.put(obj);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
				
				if(conn != null) {
					conn.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return json;
	}
	
	private Connection getConnection() {
		Connection conn = null;
		
		try {
			Class.forName(jdbcDriver);
			
			conn = DriverManager.getConnection(dbURL);//, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
}
