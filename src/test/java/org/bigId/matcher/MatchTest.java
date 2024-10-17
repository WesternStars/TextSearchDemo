package org.bigId.matcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private List<Map.Entry<Integer, String>> lines;

    @BeforeEach
    void setUp() {
        lines = List.of(
                new AbstractMap.SimpleEntry<>(0, "This is a test line"),
                new AbstractMap.SimpleEntry<>(1, "Another test line for matching"),
                new AbstractMap.SimpleEntry<>(2, "Testing line again")
        );
    }

    @Test
    void shouldGetMatches() {
        Set<String> matchValues = Set.of("test", "line", "notfound");
        Match match = new Match(lines, matchValues);
        Map<String, List<Position>> result = match.get();

        assertTrue(result.containsKey("test"));
        assertTrue(result.containsKey("line"));
        assertFalse(result.containsKey("notfound"));

        List<Position> testPositions = result.get("test");
        assertEquals(2, testPositions.size());
        assertTrue(testPositions.contains(new Position(1, 11)));
        assertTrue(testPositions.contains(new Position(2, 9)));
    }

    @Test
    void shouldGetEmptyWhenNoMatches() {
        Set<String> noMatches = Set.of("nonexistent");
        Match matchNoResults = new Match(lines, noMatches);
        Map<String, List<Position>> result = matchNoResults.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGetMultipleOccurrences() {
        List<Map.Entry<Integer, String>> newLines = List.of(
                new AbstractMap.SimpleEntry<>(1, "test test test"),
                new AbstractMap.SimpleEntry<>(2, "another line"),
                new AbstractMap.SimpleEntry<>(3, "line test")
        );

        Match match = new Match(newLines, Set.of("test"));
        Map<String, List<Position>> result = match.get();

        assertTrue(result.containsKey("test"));
        List<Position> positions = result.get("test");
        assertEquals(4, positions.size());
    }
}