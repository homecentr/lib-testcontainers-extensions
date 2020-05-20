package io.homecentr.testcontainers.containers;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogAnalyzer_containsShould {
    @Test
    public void returnFalseWhenLogsDoesNotContainSubstring() {
        LogAnalyzer analyzer = new LogAnalyzer("...something...");

        assertFalse(analyzer.contains("else"));
    }

    @Test
    public void returnTrueWhenLogsContainSubstring() {
        LogAnalyzer analyzer = new LogAnalyzer("some text some other");

        assertTrue(analyzer.contains("text"));
    }

    @Test
    public void returnFalseWhenLogsContainsSubstringLessTimesThanSpecified() {
        LogAnalyzer analyzer = new LogAnalyzer("some text some other");

        assertFalse(analyzer.contains("text", 2));
    }

    @Test
    public void returnTrueWhenLogsContainsSubstringMoreTimesThanSpecified() {
        LogAnalyzer analyzer = new LogAnalyzer("some text some text other text some");

        assertTrue(analyzer.contains("text", 2));
    }

    @Test
    public void returnTrueWhenLogsContainsSubstringEqualTimesToSpecified() {
        LogAnalyzer analyzer = new LogAnalyzer("some text some text other");

        assertTrue(analyzer.contains("text", 2));
    }
}
