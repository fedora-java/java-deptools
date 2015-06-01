package org.fedoraproject.javadeptools.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.fedoraproject.javadeptools.Query;

public class DefaultQuery<T> implements Query<T> {
    private EntityManager em;
    private String hql;
    private Object[] parameters;
    private int limit = 0;
    private int offset = 0;
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
        TypedQuery<T> q = em.createQuery(hql, clazz);
        setQueryParameters(q);
        return q.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    private void setQueryParameters(TypedQuery<?> q) {
        for (int i = 0; i < parameters.length; i++) {
            q.setParameter(i, parameters[i]);
        }
    }

    @Override
    public Query<T> setLimits(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
        return this;
    }

    @Override
    public int getCount() {
        TypedQuery<Long> query = em.createQuery("select count(*) " + hql,
                Long.class);
        setQueryParameters(query);
        return query.getSingleResult().intValue();
    }
}