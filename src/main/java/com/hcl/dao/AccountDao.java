package com.hcl.dao;
 

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.hcl.model.Account;
import com.hcl.util.HibernateUtil;

public class AccountDao {
 
    public Account getByMobileNumber(String MobileNumber) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Account account = session.createQuery("FROM Account WHERE mobileNumber = :mobNum", Account.class)
                                 .setParameter("mobNum", MobileNumber)
                                 .uniqueResult();
        session.close();
        return account;
    }
 
    public Account getById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Account account = session.get(Account.class, id);
        session.close();
        return account;
    }
 
    public boolean save(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(account);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
 
    public boolean update(Account account) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Account> query = session.createQuery("FROM Account a WHERE a.mobileNumber = :mobileNum", Account.class);
            query.setParameter("mobileNum", account.getMobileNumber());
            Account existingAccount = query.uniqueResult();
            if(existingAccount!=null)
            {
            	existingAccount.setAccountNumber(account.getAccountNumber());
            	existingAccount.setBankName(account.getBankName());
            	existingAccount.setBranchAddress(account.getBranchAddress());
            	existingAccount.setIfscCode(account.getIfscCode());
            	transaction.commit(); // Commit the transaction
            	return true;
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
		return false;
            
        }
 
    public boolean deleteByMobileNumber(String mobileNumber) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // First, retrieve the account by mobile number
            Query<Account> query = session.createQuery("FROM Account WHERE mobileNumber = :mobNum", Account.class);
            query.setParameter("mobNum", mobileNumber);
            Account accountToDelete = query.uniqueResult();

            if(accountToDelete != null) {
                session.delete(accountToDelete); // Delete the retrieved account
                transaction.commit();
                return true;
            } else {
                if (transaction != null) transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
}
}