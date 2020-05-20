package io.homecentr.testcontainers.containers;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogAnalyzer_matchesShould {
    @Test
    public void returnTrueWhenLogsMatchPattern() {
        LogAnalyzer analyzer = new LogAnalyzer("some very smart text");

        assertTrue(analyzer.matches(".*smart.*"));
    }

    @Test
    public void returnFalseWhenLogsDoesNotContainSubstring() {
        LogAnalyzer analyzer = new LogAnalyzer("some very long text");

        assertFalse(analyzer.matches(".*smart.*"));
    }

    @Test
    public void returnFalseWhenLogsHaveLessMatchesThanSpecified() {
        LogAnalyzer analyzer = new LogAnalyzer("some text some other");

        assertFalse(analyzer.matches("(.*?)text(.*?)", 2));
    }

    @Test
    public void returnTrueWhenLogsContainsSubstringMoreTimesThanSpecified() {
        LogAnalyzer analyzer = new LogAnalyzer("some text some text other text some");

        assertTrue(analyzer.matches("(.*?)text(.*?)", 2));
    }

    @Test
    public void returnTrueWhenLogsContainsSubstringEqualTimesToSpecified() {
        LogAnalyzer analyzer = new LogAnalyzer("some text some text other");

        assertTrue(analyzer.matches("(.*?)text(.*?)", 2));
    }
}