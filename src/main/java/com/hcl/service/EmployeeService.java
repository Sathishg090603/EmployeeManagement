package com.hcl.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hcl.dao.AccountDao;
import com.hcl.dao.EmployeeDao;
import com.hcl.model.Employee;
import com.hcl.model.ErrorResponse;

@Path("/secure/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)  //this is for the methods post and put only needed becuase it will expect the request body objects
public class EmployeeService {

    private EmployeeDao employeeDao = new EmployeeDao();
    private AccountDao accountDao=new AccountDao();
    
    @POST
    public Response createEmployee(Employee employee) {
    	if (employeeDao.isEmailExists(employee.getEmail())) {
    		 return Response.status(Response.Status.CONFLICT)
                     .entity(new ErrorResponse("EMAIL_ALREADY_EXISTS", "Email already exists: "))
                     .build();
        }	
    	
   	 if((accountDao.getByMobileNumber(employee.getAccount().getMobileNumber()))==null)
     {
     	return Response.status(Response.Status.FORBIDDEN)
     			.entity(new ErrorResponse("ACCOUNT NOT FOUND", "Account Not found for Mobile number: "))
                .build();
     }
  
    	    	
        boolean created = employeeDao.save(employee);
        if (created) {
            return Response.status(Response.Status.CREATED).entity(employee).build();
        } else {
        	return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("EMPLOYEE_CREATION_FAILED", "Mobile number already linked with existing Employee"))
                    .build();
        }
    }

    @GET
    public Response getAllEmployees() {
        List<Employee> employees = employeeDao.getAll();
        if (employees != null) {
            return Response.ok(employees).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve employees").build();
        }
    }
    
    @GET
    @Path("/check-email/{email}")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response checkEmailExists(@PathParam("email") String email) {
        try {
            boolean exists = employeeDao.isEmailExists(email);
            if (exists) {
                return Response.status(Response.Status.CONFLICT)
                               .entity(new ErrorResponse("EMAIL_EXISTS", "Email '" + email + "' already exists."))
                               .build();
            } else {
                return Response.ok("{\"exists\": false, \"message\": \"Email not Exist.\"}").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(new ErrorResponse("INTERNAL_SERVER_ERROR", "Error checking email existence: " + e.getMessage()))
                           .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getEmployeeById(@PathParam("id") int id) {
        Employee employee = employeeDao.getEmployeeById(id);
        if (employee != null) {
            return Response.ok(employee).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Employee not found with ID: " + id).build();
        }
    }

    @PUT
    public Response updateEmployee(Employee employee) {
    	
    	 if((accountDao.getByMobileNumber(employee.getAccount().getMobileNumber()))==null)
         {
         	return Response.status(Response.Status.FORBIDDEN)
         			.entity(new ErrorResponse("ACCOUNT NOT FOUND", "Account Not found for Mobile number: "))
                    .build();
         }
    	 
    	 Employee existingEmployeeWithEmail = employeeDao.getByEmail(employee.getEmail());

       
    	 if (existingEmployeeWithEmail != null && existingEmployeeWithEmail.getId() != employee.getId()) 
    		{
    		 return Response.status(Response.Status.CONFLICT)
    		.entity(new ErrorResponse("EMAIL_ALREADY_EXISTS", " This Email already exists! Please Change!!."))
    		.build();
    		}
    	 
    	 	
        boolean isUpdated = employeeDao.updateEmployee(employee);

        if (isUpdated) {
            Employee updatedEmployee = employeeDao.getEmployeeById(employee.getId());
            return Response.ok(updatedEmployee).build();
        }
        else {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
            		.entity(new ErrorResponse("INTERNAL_SERVER_ERROR", "Mobile number already linked with existing Employee"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}") 
    public Response deleteEmployee(@PathParam("id") int id) {
        boolean deleted = employeeDao.delete(id);
        if (deleted) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Employee not found or could not be deleted").build();
        }
    }
}