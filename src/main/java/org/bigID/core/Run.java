package org.bigID.core;

import org.bigID.aggregator.Aggregator;
import org.bigID.matcher.Match;
import org.bigID.matcher.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Run {

    private static final int LINES_IN_BLOCK = 1000;

    private final String resourcePath;
    private final Set<String> matchValue;
    private final Aggregator aggregator;
    private final ExecutorService executor;

    public Run(String link, String searchWords) {
        this(link, searchWords, new Aggregator());
    }

    public Run(String link, String searchWords, Aggregator aggregator) {
        this(link, searchWords, aggregator, Executors.newFixedThreadPool(5));
    }

    public Run(String link, String searchWords, Aggregator aggregator, ExecutorService executor) {
        resourcePath = link;
        matchValue = Arrays.stream(searchWords.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        this.executor = executor;
        this.aggregator = aggregator;
    }

    public void scan() {
        Path path = Path.of(resourcePath);
        AtomicInteger count = new AtomicInteger(1);
        List<Future<Map<String, List<Position>>>> futures;
        try {
            futures = Files.readAllLines(path)
                    .stream()
                    .map(line -> Map.entry(count.getAndIncrement(), line))
                    .collect(Collectors.groupingBy(e -> e.getKey() / LINES_IN_BLOCK))
                    .values()
                    .stream()
                    .map(e -> executor.submit(new Match(e, matchValue)))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

        echo(aggregator.aggregateByName(futures));
    }

    private void echo(Map<String, List<Position>> groupedPositions) {
        groupedPositions.forEach((key, value) -> {
            String format = String.format("%s --> %s", key, value);
            System.out.println(format);
        });
    }
}
