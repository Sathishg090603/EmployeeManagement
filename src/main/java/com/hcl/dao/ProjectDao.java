package com.hcl.dao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.hcl.model.Account;
import com.hcl.model.Employee;
import com.hcl.model.Project;
import com.hcl.util.HibernateUtil;

public class ProjectDao {
	
	EmployeeDao employeedao=new EmployeeDao();

	public List<Project> getAll()
	{
		try(Session session=HibernateUtil.getSessionFactory().openSession())
		{
			Query<Project>query=session.createQuery("FROM Project",Project.class);
			return query.list();
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean assignProject(int employeeId,int projectId)
	{
		Transaction transaction=null;
		try(Session session=HibernateUtil.getSessionFactory().openSession()) {
			transaction=session.beginTransaction();
			
			Employee employee=employeedao.getEmployeeById(employeeId);
			Project project=session.get(Project.class,projectId);
			
			if(employee==null || project==null )
				return false;
			
			
			employee.setProject(project);
		    employeedao.updateEmployee(employee);
		    
		    transaction.commit();
		    return true;
		    
		}catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			e.printStackTrace();
			return false;
		}		
	}
	public Project getById(int id)
	{
		try(Session session=HibernateUtil.getSessionFactory().openSession()){
			return session.get(Project.class, id);
		}
	}
	
	public boolean Save(Project project)
	{
	  Transaction transaction=null;
	  try(Session session=HibernateUtil.getSessionFactory().openSession())
	  {
		  transaction=session.beginTransaction();
		  session.save(project);
		  transaction.commit();
		  return true;
	  }catch(Exception e)
	  {
		  if(transaction!=null && transaction.isActive())
		  {
			  transaction.rollback();
		  }
		  return false;
	  }
	}		
	public Project getByName(String name)
	{
		Transaction transaction=null;
		try(Session session=HibernateUtil.getSessionFactory().openSession())
		{
			transaction=session.beginTransaction();
			Query<Project>query=session.createQuery("FROM Project WHERE name=:name",Project.class);
			query.setParameter("name", name);
			return query.uniqueResult();
			
		}catch(Exception e)
		{
			if(transaction!=null && transaction.isActive())
			{
				transaction.rollback();
			}
			return null;
		}
	}
	
	public boolean isNameExists(String name)
	{
		return getByName(name)!=null;
	}
	
	
	public Project getByClient(String name)
	{
		Transaction transaction=null;
		try(Session session=HibernateUtil.getSessionFactory().openSession())
		{
			transaction=session.beginTransaction();
			Query<Project>query=session.createQuery("FROM Project WHERE client=:name",Project.class);
			query.setParameter("name", name);
			return query.uniqueResult();
			
		}catch(Exception e)
		{
			if(transaction!=null && transaction.isActive())
			{
				transaction.rollback();
			}
			return null;
		}
	}
	
	public boolean isClientExists(String name)
	{
		return getByClient(name)!=null;
	}
	
	public boolean updateProject(Project updatedProject) {
		Transaction transaction = null;
		boolean isUpdated = false;
		
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();

			Project existingProject = session.get(Project.class, updatedProject.getId());
			if (existingProject == null)
				return false;

			
			existingProject.setName(updatedProject.getName());
			existingProject.setClient(updatedProject.getClient());
			existingProject.setDescription(updatedProject.getDescription());
			existingProject.setStartDate(updatedProject.getStartDate());
			existingProject.setEndDate(updatedProject.getEndDate());
			existingProject.setrequiredSkill(updatedProject.getrequiredSkill());
			session.update(existingProject);
			transaction.commit();
			isUpdated = true;

		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		}

		return isUpdated;
	}
	
	public boolean delete(int projectId)
	{
		Transaction transaction=null;
		try(Session session=HibernateUtil.getSessionFactory().openSession())
		{
			transaction=session.beginTransaction();
			Project project=session.get(Project.class, projectId);
			if(project!=null)
			{
				session.delete(project);
				transaction.commit();
				return true;
			}
			else {
				
			}
			return false;
			
		}catch(Exception e)
		{
			if(transaction!=null && transaction.isActive())
			  transaction.rollback();
			
			e.printStackTrace();  
			return false;
			
		}
	}
	public boolean hasAssignedEmployees(int projectId)
	{
		List<Employee> employees=employeedao.findByProjectId(projectId);

        System.out.println("Checking assigned employees for project ID: " + projectId);
        System.out.println("Employees found: " + (employees != null ? employees.size() : "null"));

        return employees != null && !employees.isEmpty(); 
	}
	
}
