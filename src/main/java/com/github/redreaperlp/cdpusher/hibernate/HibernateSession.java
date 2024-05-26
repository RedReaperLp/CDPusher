package com.github.redreaperlp.cdpusher.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSession {
    private static HibernateSession instance;
    private SessionFactory sessionFactory;

    private HibernateSession() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public static HibernateSession getInstance() {
        if (instance == null) {
            instance = new HibernateSession();
        }
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void beginTransaction() {
        sessionFactory.getCurrentSession().beginTransaction();
    }

    public void commitTransaction() {
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    public void rollbackTransaction() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

    public void closeSession() {
        sessionFactory.getCurrentSession().close();
    }
}