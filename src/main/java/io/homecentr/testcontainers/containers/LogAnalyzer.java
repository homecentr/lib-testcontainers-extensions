package io.homecentr.testcontainers.containers;

import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzer {
    private final String _logs;

    public LogAnalyzer(String output) {
        _logs = output;
    }

    public boolean contains(String substring) {
        return contains(substring, 1);
    }

    public boolean contains(String substring, int times) {
        int matches = StringUtils.countMatches(_logs, substring);

        return matches >= times;
    }

    public boolean matches(String regex) {
        return matches(regex, 1);
    }

    public boolean matches(String regex, int times) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(_logs);

        int count = 0;
        while (matcher.find()) {
            count++;

            if(count >= times) {
                return true;
            }
        }

        return false;
    }
}