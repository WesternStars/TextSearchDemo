package org.bigID.aggregator;

import org.bigID.matcher.Position;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Aggregator {

    public Map<String, List<Position>> aggregateByName(List<Map<String, List<Position>>> results) {
        return results.stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(groupingBy(Entry::getKey, flatMapping(e -> e.getValue().stream(), toList())))
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), getSortedList(e)))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private static List<Position> getSortedList(Entry<String, List<Position>> e) {
        return e.getValue().stream().sorted(Comparator.comparingInt(Position::lineOffset)).toList();
    }
}
