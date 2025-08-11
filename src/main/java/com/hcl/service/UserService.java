package com.hcl.service;

import com.hcl.dao.UserDao;
import com.hcl.model.User;
import com.hcl.util.PasswordHasher;
import com.hcl.util.jwtUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

	private UserDao userDao = new UserDao();

	@POST
	@Path("/register")
	public Response registerUser(User user) {
		if (user == null || user.getUsername() == null || user.getPassword() == null || user.getUsername().isEmpty()
				|| user.getPassword().isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Username and password are required for registration.").build();
		}

		if (userDao.isUsernameExists(user.getUsername())) {
			return Response.status(Response.Status.CONFLICT)
					.entity("Username '" + user.getUsername() + "' already exists.").build();
		}

		try {
			String plainTextPassword = user.getPassword();
			String hashedPassword = PasswordHasher.hashPassword(plainTextPassword);
			user.setPassword(hashedPassword);

			boolean created = userDao.save(user);

			if (created) {
				user.setPassword(null);
				return Response.status(Response.Status.CREATED).entity(user).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("Failed to register user due to a database error.").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Failed to register user due to a security processing error.").build();
		}
	}

	@POST
	@Path("/login")
	public Response loginUser(User loginUser) {
		if (loginUser == null || loginUser.getUsername() == null || loginUser.getPassword() == null
				|| loginUser.getUsername().isEmpty() || loginUser.getPassword().isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Username and password are required for login.")
					.build();
		}

		User storedUser = userDao.getByUsername(loginUser.getUsername());

		if (storedUser != null) {
			String plainTextLoginPassword = loginUser.getPassword();
			String storedHashedPassword = storedUser.getPassword();

			if (PasswordHasher.checkPassword(plainTextLoginPassword, storedHashedPassword)) {
				// Authentication successful
				storedUser.setPassword(null);
				System.out.println(storedUser.getRole());
				String token = jwtUtil.generateToken(storedUser.getUsername(),storedUser.getRole());
				String Refreshtoken = jwtUtil.generateRefreshToken(storedUser.getUsername());
                
				return Response.ok().header("Authorization",token).header("Refresh-Token", Refreshtoken)
						.entity(storedUser).build();
			}
		}

		return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password.").build();
	}

}