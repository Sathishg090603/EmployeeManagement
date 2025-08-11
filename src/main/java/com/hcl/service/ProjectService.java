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

import com.hcl.dao.ProjectDao;
import com.hcl.model.Employee;
import com.hcl.model.ErrorResponse;
import com.hcl.model.Project;

@Path("/secure/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) 
public class ProjectService {
	
	ProjectDao projectdao=new ProjectDao();
	 @GET
	    public Response getAllEmployees() {
	        List<Project> employees = projectdao.getAll();
	        if (employees != null) {
	            return Response.ok(employees).build();
	        } else {
	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                    .entity("Failed to retrieve Projects").build();
	        }
	    }


	 @PUT
	 @Path("/assign/{employeeId}/{projectId}")
	 public Response AssignProjects(@PathParam("employeeId") int eid, @PathParam("projectId") int pid) {
	     try {
	         boolean success = projectdao.assignProject(eid, pid);

	         if (success) {
	             return Response.ok("{\"message\":\"Project assigned successfully\"}").build();
	         } else {
	             return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                            .entity(new ErrorResponse("Project assignment failed", "Invalid employee or project ID"))
	                            .build();
	         }
	     } catch (Exception e) {
	         e.printStackTrace(); // Optional: log the error
	         return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                        .entity(new ErrorResponse("Unexpected error", e.getMessage()))
	                        .build();
	     }
	 }
	 
	 @POST
	 public Response saveProjects(Project project)
	 {
		
		 if (projectdao.isNameExists(project.getName())) {
			 
			 
			 if (projectdao.isClientExists(project.getClient())) {
	    		 return Response.status(Response.Status.CONFLICT)
	                     .entity(new ErrorResponse("PROJECT WITH SAME CLIENT ALREADY_EXISTS", "project with same Client already exists: "))
	                     .build();
			 }
		    
    		 return Response.status(Response.Status.CONFLICT)
                     .entity(new ErrorResponse("Project_ALREADY_EXISTS", "project already exists: "))
                     .build();
			 }
		
		try {
			boolean success=projectdao.Save(project);
			
			if(success) {
				return Response.ok("{\"message\":\"Project created successfully\"}").build();
			}
			else
			{
				return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse("Project creation failed", "Invalid project Details"))
                        .build();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			 return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity(new ErrorResponse("Unexpected error", e.getMessage()))
                     .build();
		}
		
	 }
	    @GET
	    @Path("/{id}")
	    public Response getProjectById(@PathParam("id") int id) {
	        Project project = projectdao.getById(id);
	        if (project != null) {
	            return Response.ok(project).build();
	        } else {
	            return Response.status(Response.Status.NOT_FOUND)
	                    .entity("Project not found with ID: " + id).build();
	        }
	    }
	 
	    @PUT
	    public Response updateProject(Project project) {
	    	
	    	String trimmedName=project.getName().trim();

	    	Project ExistingProjectWithName=projectdao.getByName(trimmedName);
	    	
	    	if(ExistingProjectWithName!=null && ExistingProjectWithName.getId()!=project.getId())
	    	{
	    	    return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("PROJECT EXISTS","Changed Project Name already Exist")).build();
	    	}
	   
	   	     boolean isUpdated = projectdao.updateProject(project);

	        if (isUpdated) {
	           Project updatedEmployee = projectdao.getById(project.getId());
	            return Response.ok(updatedEmployee).build();
	        }
	        else {
	            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
	            		.entity(new ErrorResponse("INTERNAL_SERVER_ERROR", "Unexpected Error occured"))
	                    .build();
	        }
	    }
	    @DELETE
        @Path("/{id}")
	    public Response deleteProject(@PathParam("id") int id)
	    {
	    	
	    	if(projectdao.hasAssignedEmployees(id))
			{

	           return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("Cannot Delete project","Project Deletion Failed! Deallocate Employees First!!")).build();
			}
	    	
	    	boolean deleted=projectdao.delete(id);
	    	
	    	if(deleted)
	    	{
	    		return Response.ok().build();
	    	}
	    	else
	    		return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("DELETE FAILED","Failde to Delete Project")).build();
	   }
}
