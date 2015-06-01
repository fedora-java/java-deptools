package org.fedoraproject.javadeptools;

import java.util.List;

public interface Query<T> {
    public List<T> getResults();

    public Query<T> setLimits(int offset, int limit);

    public int getCount();
}
