package org.bigID.aggregator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByKey;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class Aggregator {

    private static final int TIMEOUT = 200;

    public Map<String, List<Entry<Integer, Integer>>> aggregateByName(List<Future<Map<String, List<Entry<Integer, Integer>>>>> futures) {
        return sortPositions(groupingByName(futures));
    }

    private static Map<String, List<Entry<Integer, Integer>>> sortPositions(
            Map<String, List<Stream<Entry<Integer, Integer>>>> streamsGroupedByName
    ) {
        return streamsGroupedByName.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), sortPositions(entry.getValue())))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static Map<String, List<Stream<Entry<Integer, Integer>>>> groupingByName(
            List<Future<Map<String, List<Entry<Integer, Integer>>>>> futures
    ) {
        return futures.stream()
                .map(Aggregator::getFutureValue)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(groupingBy(Entry::getKey, mapping(getMapper(), toList())));
    }

    private static List<Entry<Integer, Integer>> sortPositions(List<Stream<Entry<Integer, Integer>>> entry) {
        return entry.stream()
                .flatMap(identity())
                .sorted(comparingByKey())
                .toList();
    }

    private static Function<Entry<String, List<Entry<Integer, Integer>>>, Stream<Entry<Integer, Integer>>> getMapper() {
        return e -> e.getValue().stream()
                .map(ent -> Map.entry(ent.getKey(), ent.getValue()));
    }

    private static Map<String, List<Entry<Integer, Integer>>> getFutureValue(
            Future<Map<String, List<Entry<Integer, Integer>>>> future
    ) {
        try {
            return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
