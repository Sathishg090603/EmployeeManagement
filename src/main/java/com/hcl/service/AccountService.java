package com.hcl.service;

import com.hcl.dao.AccountDao;
import com.hcl.dao.EmployeeDao;
import com.hcl.model.Account;
import com.hcl.model.Employee;
import com.hcl.model.ErrorResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/secure/accounts")
@Produces(MediaType.APPLICATION_JSON) // This resource will produce JSON responses
@Consumes(MediaType.APPLICATION_JSON)
public class AccountService {

    private AccountDao accountDao = new AccountDao();

    @GET                     
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        try {
            Account account = accountDao.getById(id);
            if (account != null) {
                return Response.ok(account).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Account not found for number: " + id)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving account: " + e.getMessage())
                    .build();
        }
    }
    
    
    @GET
    @Path("/mobile/{mobNum}") 
    public Response getByMobileNumber(@PathParam("mobNum") String mobileNumber) {
        try {
            Account account = accountDao.getByMobileNumber(mobileNumber);
            if (account != null) {
                return Response.ok(account).build();
            } else {
            	
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Account not found for mobile number: " + mobileNumber)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving account by mobile number: " + e.getMessage())
                    .build();
        }
    }
    
    
    @POST
    public Response saveAccount(Account account) {
        try {
            if (accountDao.getByMobileNumber(account.getMobileNumber()) != null) {
        		return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse("MOBILE_NUMBER_ALREADY_EXISTS", "mobile number already linked with existing Employee : " ) )
                        .build();

            }

            boolean saved = accountDao.save(account);

            if (saved) {
               
                return Response.created(UriBuilder.fromResource(AccountService.class)
                                .path(String.valueOf(account.getId())).build())
                        .entity(account) 
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to save account due to an unexpected  issue.")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error saving account: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    public Response updateAccount(Account account) {
        try {
            boolean isUpdated = accountDao.update(account);

            if (isUpdated) {
                return Response.ok(account).entity("Account updated successfully.").build();
                
              
            }
            else {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("Account not found with mobile number: " + account.getMobileNumber())
                               .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Failed to update account due to an internal server error.")
                           .build();
        }
    }
    
    @DELETE
    @Path("/mobile/{mobNum}")
    public Response deleteAccountByMobileNumber(@PathParam("mobNum") String mobileNumber) {
        try {
            boolean isDeleted = accountDao.deleteByMobileNumber(mobileNumber);

            if (isDeleted) {
                 return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Account not found for mobile number: " + mobileNumber)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete account due to an internal server error: " + e.getMessage())
                    .build();
        }
    }


}