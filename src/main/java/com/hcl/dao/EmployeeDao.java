package com.hcl.dao;

import org.hibernate.Session;


import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.hcl.model.Account;
import com.hcl.model.Employee;
import com.hcl.util.HibernateUtil;

import java.util.List;

import javax.validation.ConstraintViolationException;

public class EmployeeDao {

	private AccountDao accountDao = new AccountDao();

	public boolean save(Employee employee) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			transaction = session.beginTransaction();

			String mobileNumberToFind = employee.getAccount().getMobileNumber();

			Account account = accountDao.getByMobileNumber(mobileNumberToFind);

			if (account == null) {
				transaction.rollback();
				return false;
			}
			employee.setAccount(account);

			session.save(employee);

			transaction.commit();
			return true;
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			e.printStackTrace();
			return false;

		}
	}

	public Employee getByEmail(String email) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Query<Employee> query = session.createQuery("FROM Employee WHERE email = :email", Employee.class);
			query.setParameter("email", email);
			return query.uniqueResult(); // Returns null if no employee with that email exists
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isEmailExists(String email) {
		return getByEmail(email) != null;
	}

	public boolean updateEmployee(Employee updatedData) {
		Transaction transaction = null;
		boolean isUpdated = false;
		
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();

			Employee existingEmployee = session.get(Employee.class, updatedData.getId());
			if (existingEmployee == null)
				return false;

			if (updatedData.getAccount() != null && updatedData.getAccount().getMobileNumber() != null
					&& !updatedData.getAccount().getMobileNumber().isEmpty()) {

				
				Account newAccountToAssociate =accountDao.getByMobileNumber(updatedData.getAccount().getMobileNumber());

				if (newAccountToAssociate != null) {
					existingEmployee.setAccount(newAccountToAssociate);
				} else {
					System.out.println("Associated Account not found for mobile number: "
							+ updatedData.getAccount().getMobileNumber());
					if (transaction != null)
						transaction.rollback();
					return false;
				}
			}
			existingEmployee.setName(updatedData.getName());
			existingEmployee.setDob(updatedData.getDob());
			existingEmployee.setGender(updatedData.getGender());
			existingEmployee.setEmail(updatedData.getEmail());
			existingEmployee.setSkills(updatedData.getSkills());
			existingEmployee.setDoj(updatedData.getDoj());
			existingEmployee.setDepartment(updatedData.getDepartment());
			existingEmployee.setProject(updatedData.getProject());
			session.update(existingEmployee);
			transaction.commit();
			isUpdated = true;

		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		}

		return isUpdated;
	}

	public Employee getEmployeeById(int id) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.get(Employee.class, id);
		}
	}

	public boolean delete(int employeeId) {
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();

			Employee employee = session.get(Employee.class, employeeId);
			if (employee != null) {
				session.delete(employee);
				transaction.commit();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
			return false;
		}
	}



	public List<Employee> getAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Query<Employee> query = session.createQuery("FROM Employee", Employee.class);
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Employee> findByProjectId(int projectId) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Query<Employee> query = session.createQuery("FROM Employee where project_id=:projectId", Employee.class);
			query.setParameter("projectId", projectId);
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}