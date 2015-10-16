package org.fedoraproject.javadeptools;

import java.util.List;

public interface Query<T> {
    public List<T> getResults();

    public List<T> getResults(int limit);

    public List<T> getResults(int offset, int limit);

    public int getCount();
}
