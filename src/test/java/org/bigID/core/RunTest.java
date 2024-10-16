package org.bigID.core;

import org.bigID.aggregator.Aggregator;
import org.bigID.matcher.Match;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RunTest {

    @Mock
    private Match match;
    @Mock
    private Aggregator aggregator;
    @Mock
    private ExecutorService executorService;
    @Captor
    private ArgumentCaptor<Match> matchCaptor;
    private MockedStatic<Path> pathMockedStatic;
    private MockedStatic<Files> filesMockedStatic;

    @BeforeEach
    public void setUp() {
        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    public void tearDown() {
        pathMockedStatic.close();
        filesMockedStatic.close();
    }

    @Test
    void shouldReadAllLinesWhenScanWithCorrectPath() {
        Path mockPath = mock(Path.class);
        pathMockedStatic.when(() -> Path.of(Mockito.anyString())).thenReturn(mockPath);

        Run run = new Run("url.txt", "Test");
        run.scan();

        filesMockedStatic.verify(() -> Files.readAllLines(mockPath), atLeastOnce());
    }

    @Test
    void shouldSplitMatchValueWhenGetMatcherInString() throws NoSuchFieldException, IllegalAccessException {
        Path mockPath = mock(Path.class);
        pathMockedStatic.when(() -> Path.of(Mockito.anyString())).thenReturn(mockPath);
        filesMockedStatic.when(() -> Files.readAllLines(mockPath)).thenReturn(getTestText());

        Run run = new Run("url.txt", " Test,Test2", aggregator, executorService);
        run.scan();

        verify(executorService, times(1)).submit(matchCaptor.capture());
        checkMatchValue();
    }

    private void checkMatchValue() throws NoSuchFieldException, IllegalAccessException {
        Match match = matchCaptor.getValue();
        Field matchValueField = match.getClass().getDeclaredField("matchValue");
        matchValueField.setAccessible(true);
        Set<String> matchValue = (Set<String>) matchValueField.get(match);
        assertEquals(Set.of("Test", "Test2"), matchValue);
    }

    private static List<String> getTestText() {
        return List.of("TestLine1", "TestLine2", "TestLine3");
    }
}