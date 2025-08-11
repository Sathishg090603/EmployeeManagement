package com.hcl.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.hcl.model.User;
import com.hcl.util.HibernateUtil;

public class UserDao {

    
    public boolean save(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(user); // Saves the plain text password
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
   
    public User getByUsername(String username) { 
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult(); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

  
    public boolean isUsernameExists(String username) {
        return getByUsername(username) != null;
    }

}