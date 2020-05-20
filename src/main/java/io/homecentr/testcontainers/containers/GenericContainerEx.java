package io.homecentr.testcontainers.containers;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericContainerEx<SELF extends GenericContainerEx<SELF>> extends GenericContainer<SELF> {
    public GenericContainerEx(String imageTag) {
        super(imageTag);
    }

    public Integer getProcessUid(String processName) throws IOException, InterruptedException, ProcessNotFoundException {
        ExecResult result = executeShellCommand("stat -c '%u' /proc/$(ps axf | grep '"+ processName +"' | grep -v grep | awk '{print $1}')");

        String output = result.getStdout().trim();

        if(output.isEmpty() || output.equals("0")) {
            throw new ProcessNotFoundException(processName);
        }

        return Integer.parseInt(output);
    }

    public Integer getProcessGid(String processName) throws IOException, InterruptedException, ProcessNotFoundException {
        ExecResult result = executeShellCommand("stat -c '%g' /proc/$(ps axf | grep '"+ processName +"' | grep -v grep | awk '{print $1}')");

        String output = result.getStdout().trim();

        if(output.isEmpty() || output.equals("0")) {
            throw new ProcessNotFoundException(processName);
        }

        return Integer.parseInt(output);
    }

    public ExecResult executeShellCommand(String command) throws IOException, InterruptedException {
        return this.execInContainer(
                getShellExecutable(),
                "-c",
                command); // TODO: Escape?
    }

    public LogAnalyzer getLogsAnalyzer(OutputFrame.OutputType... outputTypes) {
        String logs = this.getLogs(outputTypes);

        return new LogAnalyzer(logs);
    }

    @NotNull
    private String getShellExecutable() throws IOException, InterruptedException {
        if(this.execInContainer("which", "bash").getExitCode() == 0) {
            return "bash";
        }

        if(this.execInContainer("which", "ash").getExitCode() == 0) {
            return "ash";
        }

        throw new IOException("No known shell found in the container.");
    }
}
