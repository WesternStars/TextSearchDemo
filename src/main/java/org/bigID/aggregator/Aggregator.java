package org.bigID.aggregator;

import org.bigID.matcher.Position;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class Aggregator {

    public Map<String, List<Position>> aggregateByName(List<Map<String, List<Position>>> futures) {
        return sortPositions(groupingByName(futures));
    }

    private static Map<String, List<Position>> sortPositions(Map<String, List<Stream<Position>>> streamsGroupedByName) {
        return streamsGroupedByName.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), sortPositions(entry.getValue())))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static Map<String, List<Stream<Position>>> groupingByName(List<Map<String, List<Position>>> futures) {
        return futures.stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(groupingBy(Entry::getKey, mapping(getMapper(), toList())));
    }

    private static List<Position> sortPositions(List<Stream<Position>> entry) {
        return entry.stream()
                .flatMap(identity())
                .sorted(Comparator.comparingInt(Position::lineOffset))
                .toList();
    }

    private static Function<Entry<String, List<Position>>, Stream<Position>> getMapper() {
        return e -> e.getValue().stream();
    }
}
