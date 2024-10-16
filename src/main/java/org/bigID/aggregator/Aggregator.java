package org.bigID.aggregator;

import org.bigID.matcher.Position;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

// todo Interface and Impl for provider
public class Aggregator {

    public Map<String, List<Position>> aggregateByName(List<Map<String, List<Position>>> results) {
        return results.stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(groupingBy(Entry::getKey, flatMapping(getMapper(), toList())));
    }

    private static Function<Entry<String, List<Position>>, Stream<Position>> getMapper() {
        return e -> e.getValue().stream()
                .sorted(Comparator.comparingInt(Position::lineOffset));
    }
}
