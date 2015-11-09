package org.fedoraproject.javadeptools.data;

import java.util.Iterator;
import java.util.List;

import org.fedoraproject.javadeptools.Query;

public abstract class AbstractQuery<T> implements Query<T> {

    @Override
    public List<T> getResults() {
        return getResults(0, 0);
    }

    @Override
    public List<T> getResults(int limit) {
        return getResults(0, limit);
    }

    @Override
    public T getSingleResult() {
        Iterator<T> it = getResults(0).iterator();
        if (it.hasNext())
            return it.next();
        return null;
    }
}
