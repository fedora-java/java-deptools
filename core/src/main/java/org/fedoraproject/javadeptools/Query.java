package org.fedoraproject.javadeptools;

import java.util.List;

/**
 * Type returned from data access methods that allows to further specify generic
 * parameters of a Java Deptools query
 *
 * @author Michael Simacek
 *
 * @param <T>
 *            queried object type
 */
public interface Query<T> {

    /**
     * Returns all results of a query
     *
     * @return a list of all resulting objects
     */
    public List<T> getResults();

    /**
     * Returns limited number of results.
     *
     * @param limit
     *            specifies upper bound of the number of results
     * @return a list of resulting objects of length at most limit
     */
    public List<T> getResults(int limit);

    /**
     * Returns limited number of results with offset to be used for pagination
     *
     * @param offset
     *            query offset
     * @param limit
     *            upper bound of number of results
     * @return limited list of resulting objects
     */
    public List<T> getResults(int offset, int limit);

    /**
     * Returns total count of items for the query without materializing them all
     * in memory
     *
     * @return number of query results
     */
    public long getTotal();
}
