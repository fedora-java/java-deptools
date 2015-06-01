package org.fedoraproject.javadeptools;

import java.util.List;

public interface Query<T> {
public List<T> getResults();
public Query<T> setRange(int from, int to);
public int getCount();
}
