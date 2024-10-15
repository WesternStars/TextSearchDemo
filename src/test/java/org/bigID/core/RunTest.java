package org.bigID.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.mockito.Mockito.verify;

class RunTest {

//    @Mock
//    private Match mockDataService;

    @Captor
    ArgumentCaptor<Integer> matchCaptor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void scan() {
        Run run = new Run("./src/test/resources/bigWithBlankLineAtTheEnd.txt", "Test");
        run.scan();
    }
}