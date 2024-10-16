package org.bigID.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Match implements Supplier<Map<String, List<Position>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Match.class);

    private final Set<String>  matchValue;
    private final List<Entry<Integer, String>> lines;
    private final Map<String, List<Position>> resultMatch = new HashMap<>();

    public Match(List<Entry<Integer, String>> lines, Set<String> matchValue) {
        this.lines = lines;
        this.matchValue = matchValue;
    }

    @Override
    public Map<String, List<Position>> get() {
        for (String m : matchValue) {
            var entries = checkBlock(m);
            if (!entries.isEmpty()) {
                resultMatch.put(m, entries);
            }
        }
        LOGGER.debug(resultMatch.toString());
        return resultMatch;
    }

    private List<Position> checkBlock(String search) {
        return lines.stream()
                .flatMap(line -> {
                    List<Integer> inLine = checkLine(search, line.getValue(), 0).toList();
                    return collecting(line.getKey() + 1, inLine);
                })
                .toList();
    }

    private Stream<Integer> checkLine(String search, String line, int cursor) {
        int index = line.indexOf(search, cursor);
        if (index < 0) {
            return Stream.empty();
        }
        return Stream.concat(Stream.of(index + 1), checkLine(search, line, index + search.length()));
    }

    private static Stream<Position> collecting(Integer lineNumber, List<Integer> inLine) {
        return inLine.stream().map(i -> new Position(lineNumber, i));
    }
}
