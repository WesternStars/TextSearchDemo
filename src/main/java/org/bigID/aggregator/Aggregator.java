package org.bigID.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

public class Aggregator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Aggregator.class);

    public void aggregate(List<Future<Map<String, List<Map.Entry<Integer, Integer>>>>> futures) {
        var list = futures.stream()
                .map(Aggregator::getFutureValue)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(
                                set -> set.getValue().stream()
                                        .map(entry -> Map.entry(entry.getKey(), entry.getValue())),
                                Collectors.toList()
                        )
                ));

        var collect = list.entrySet().stream()
                .map(entry -> {
                    List<Map.Entry<Integer, Integer>> v = entry.getValue().stream()
                            .flatMap(identity())
                            .sorted(Map.Entry.comparingByKey())
                            .toList();
                    return Map.entry(entry.getKey(), v);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(collect);
    }

    private static Map<String, List<Map.Entry<Integer, Integer>>> getFutureValue(
            Future<Map<String, List<Map.Entry<Integer, Integer>>>> future
    ) {
        try {
            return future.get(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
