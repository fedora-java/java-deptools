package org.fedoraproject.javadeptools.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.fedoraproject.javadeptools.Query;

public class DefaultQuery<T> extends AbstractQuery<T> {
    private EntityManager em;
    private String hql;
    private Object[] parameters;
    private Class<T> clazz;

    public DefaultQuery(EntityManager em, Class<T> clazz, String hql,
            Object... parameters) {
        this.em = em;
        this.clazz = clazz;
        this.hql = hql;
        this.parameters = parameters;
    }

    @Override
    public List<T> getResults(int offset, int limit) {
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
    public int getCount() {
        TypedQuery<Long> query = em.createQuery("select count(*) " + hql,
                Long.class);
        setQueryParameters(query);
        return query.getSingleResult().intValue();
    }
}