package org.fedoraproject.javadeptools.data;

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

}
