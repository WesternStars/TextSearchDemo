package org.bigID.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class Match implements Callable<Map<String, List<Position>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Match.class);

    private final Set<String>  matchValue;
    private final List<Entry<Integer, String>> lines;
    private final Map<String, List<Position>> resultMatch = new HashMap<>();

    public Match(List<Entry<Integer, String>> lines, Set<String> matchValue) {
        this.lines = lines;
        this.matchValue = matchValue;
    }

    @Override
    public Map<String, List<Position>> call() {
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
                    List<Integer> inLine = checkLine(search, line.getValue());
                    return collecting(line.getKey() + 1, inLine);
                })
                .toList();
    }

    private List<Integer> checkLine(String search, String line) {
        int index = 0;
        List<Integer> lineResult = new ArrayList<>();
        while (true) {
            index = line.indexOf(search, index);
            if (index < 0) {
                break;
            }
            lineResult.add(index + 1);
            index++;
        }
        return lineResult;
    }

    private static Stream<Position> collecting(Integer lineNumber, List<Integer> inLine) {
        return inLine.stream().map(i -> new Position(lineNumber, i));
    }
}
