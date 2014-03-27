/*
 * Copyright (c) 2014 Martin Senne, Marvin Hansen.
*/
package org.remotefutures.utils;

import java.util.*;

/**
 * @author Marvin Hansen
 */
public class SortableValueMap<Int, Double> extends LinkedHashMap<Int, Double> {

    private final Comparator SORTER = new ValueComparator();

    /**
     * @param size size of the map
     */
    public SortableValueMap(int size) {
        super(size);
    }

    /**
     * Sorts values of the map in ascending order.
     * key-value association remains unchanged.
     */
    public void sortByAscendingValue() {

        final int size = entrySet().size();

        final List<Map.Entry<Int, Double>> meView = new ArrayList<>(size);

        meView.addAll(entrySet());
        Collections.sort(meView, SORTER);

        clear();// clears the map

        //adds the newly sorted values
        for (Map.Entry<Int, Double> entry : meView) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /***/
    public void sortByDescendingValue() {

        final int size = entrySet().size();

        final List<Map.Entry<Int, Double>> mapView = new ArrayList<>(size);

        mapView.addAll(entrySet());
        Collections.sort(mapView, SORTER);
        Collections.reverse(mapView); // reverse order

        clear();// clears the map

        for (Map.Entry<Int, Double> entry : mapView) {

            put(entry.getKey(), entry.getValue());
        }
    }
}

/**
 * @param <V> value ot Type T
 */
final class ValueComparator<V extends Comparable<? super V>>

        implements Comparator<Map.Entry<?, V>> {

    @Override
    public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
        return o1.getValue().compareTo(o2.getValue());
    }
}

