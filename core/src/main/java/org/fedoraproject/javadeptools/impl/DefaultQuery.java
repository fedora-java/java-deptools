package org.fedoraproject.javadeptools.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.fedoraproject.javadeptools.Query;

public class DefaultQuery<T> implements Query<T> {
    private EntityManager em;
    private String hql;
    private Object[] parameters;
    private int to = 0;
    private int from = 0;
    private Class<T> clazz;

    public DefaultQuery(EntityManager em, Class<T> clazz, String hql,
            Object... parameters) {
        this.em = em;
        this.clazz = clazz;
        this.hql = hql;
        this.parameters = parameters;
    }

    @Override
    public List<T> getResults() {
        javax.persistence.TypedQuery<T> q = em.createQuery(hql, clazz);
        for (int i = 0; i < parameters.length; i++) {
            q.setParameter(i, parameters[i]);
        }
        return q.setFirstResult(from).setMaxResults(to).getResultList();
    }

    @Override
    public Query<T> setRange(int from, int to) {
        this.from = from;
        this.to = to;
        return this;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

}
