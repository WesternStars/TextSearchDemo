package org.bigID.aggregator;

import org.bigID.matcher.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregatorTest {

    private Aggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new Aggregator();
    }

    @Test
    void shouldGetBackEmptyWhenEmptyInput() {
        List<Map<String, List<Position>>> results = Collections.emptyList();

        Map<String, List<Position>> aggregated = aggregator.aggregateByName(results);

        assertTrue(aggregated.isEmpty());
    }

    @Test
    void shouldAggregateByNameWhenNoCommonKeys() {
        List<Map<String, List<Position>>> results = List.of(
                Map.of("key1", List.of(new Position(1, 1))),
                Map.of("key2", List.of(new Position(2, 2)))
        );

        Map<String, List<Position>> aggregated = aggregator.aggregateByName(results);

        assertEquals(2, aggregated.size());
        assertTrue(aggregated.containsKey("key1"));
        assertTrue(aggregated.containsKey("key2"));
    }

    @Test
    void shouldAggregateByNameWhenDuplicateKeys() {
        List<Map<String, List<Position>>> results = List.of(
                Map.of("key1", List.of(new Position(20, 5))),
                Map.of("key1", List.of(new Position(1, 3), new Position(10, 2)))
        );

        Map<String, List<Position>> aggregated = aggregator.aggregateByName(results);

        assertEquals(1, aggregated.size());
        List<Position> positions = aggregated.get("key1");
        assertEquals(3, positions.size());
        assertEquals(new Position(1, 3), positions.get(0));
        assertEquals(new Position(10, 2), positions.get(1));
        assertEquals(new Position(20, 5), positions.get(2));
    }
}