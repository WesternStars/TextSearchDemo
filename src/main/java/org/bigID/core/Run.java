package org.bigID.core;

import org.bigID.aggregator.Aggregator;
import org.bigID.matcher.Match;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Run {

    private static final int LINES_IN_BLOCK = 1000;

    private final String link;
    private final String searchWords;
    private final Aggregator aggregator;

    public Run(String link, String searchWords) {
        this(link, searchWords, new Aggregator());
    }

    public Run(String link, String searchWords, Aggregator aggregator) {
        this.link = link;
        this.searchWords = searchWords;
        this.aggregator = aggregator;
    }

    public void scan() {
        Objects.requireNonNull(link, "Link argument is null");
        Objects.requireNonNull(searchWords, "Searching words argument is null");
        Objects.requireNonNull(aggregator, "Aggregator argument is null");

        Path resourcePath = Path.of(link);
        AtomicInteger count = new AtomicInteger();
        Set<String> matchValue = Arrays.stream(searchWords.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        if (matchValue.isEmpty()) {
            throw new IllegalArgumentException("Search words argument is empty");
        }

        try (var lines = Files.lines(resourcePath); var executor = Executors.newThreadPerTaskExecutor(Thread::new)) {
            lines.map(line -> Map.entry(count.getAndIncrement(), line))
                    .collect(Collectors.groupingBy(e -> e.getKey() / LINES_IN_BLOCK))
                    .values()
                    .parallelStream()
                    .map(e -> CompletableFuture.supplyAsync(new Match(e, matchValue), executor))
                    .map(CompletableFuture::join)
                    .collect(collectingAndThen(toList(), aggregator::aggregateByName))
                    .forEach((key, value) -> System.out.printf("%s --> %s%n", key, value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
