package org.bigID.core;

import org.bigID.aggregator.Aggregator;
import org.bigID.matcher.Match;

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
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public Run(String link, String r) {
        resourcePath = link;
        matchValue = Arrays.stream(r.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public void scan() {
        Path path = Path.of(resourcePath);
        AtomicInteger count = new AtomicInteger(1);
        List<Future<Map<String, List<Map.Entry<Integer, Integer>>>>> futures;
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

        new Aggregator().aggregate(futures);
    }
}
