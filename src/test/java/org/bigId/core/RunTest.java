package org.bigId.core;

import org.bigId.aggregator.Aggregator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RunTest {

    private static final String TEST_FILE = "testFile.txt";

    @BeforeEach
    public void setUp() throws IOException {
        Path testFilePath = Paths.get(TEST_FILE);
        Files.write(testFilePath, "James\napple\nJohn\n".getBytes());
    }

    @Test
    public void shouldThrowExceptionWhenNullLink() {
        Exception exception = assertThrows(NullPointerException.class, () -> new Run(null, "James,John").scan());
        assertEquals("Link argument is null", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenNullSearchWords() {
        Exception exception = assertThrows(NullPointerException.class, () -> new Run(TEST_FILE, null).scan());
        assertEquals("Searching words argument is null", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenNullSearchWordsNullAggregator() {
        Exception exception = assertThrows(NullPointerException.class, () -> new Run(TEST_FILE, "James,John", null).scan());
        assertEquals("Aggregator argument is null", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenEmptySearchWords() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Run(TEST_FILE, "").scan());
        assertEquals("Search words argument is empty", exception.getMessage());
    }

    @Test
    public void shouldBeSuccessfulWhenValidInput() {
        Aggregator mockAggregator = mock(Aggregator.class);
        when(mockAggregator.aggregateByName(anyList())).thenReturn(Map.of("James", List.of(), "John", List.of()));

        Run runWithMock = new Run(TEST_FILE, "James,John", mockAggregator);
        runWithMock.scan();

        verify(mockAggregator).aggregateByName(anyList());
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }
}