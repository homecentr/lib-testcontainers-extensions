package io.homecentr.testcontainers.containers;

import io.homecentr.testcontainers.images.ImageTagResolver;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenericContainerEx<SELF extends GenericContainerEx<SELF>> extends GenericContainer<SELF> {

    public GenericContainerEx(ImageTagResolver resolver) {
        super(resolver.getImageTag());
    }

    public GenericContainerEx(String imageTag) {
        super(imageTag);
    }

    public GenericContainerEx<SELF> withRelativeFileSystemBind(Path path, String containerPath) {
        return withRelativeFileSystemBind(path.toString(), containerPath);
    }

    public GenericContainerEx<SELF> withRelativeFileSystemBind(String relativePath, String containerPath) {
        return withRelativeFileSystemBind(relativePath, containerPath, BindMode.READ_WRITE);
    }

    public GenericContainerEx<SELF> withRelativeFileSystemBind(String relativePath, String containerPath, BindMode bindMode) {
        String fullHostPath = Paths.get(System.getProperty("user.dir"), relativePath).normalize().toString();

        return withFileSystemBind(fullHostPath, containerPath, bindMode);
    }

    public Integer getProcessUid(String processName) throws IOException, InterruptedException, ProcessNotFoundException {
        ExecResult result = executeShellCommand("stat -c '%u' /proc/$(ps axf | grep '"+ processName +"$' | grep -v grep |  awk -v def=\"not-found\" '{ print $1 } END { if(NR==0) {print def} }')");

        if(result.getExitCode() != 0) {
            throw new ProcessNotFoundException(processName);
        }

        String output = result.getStdout().trim();

        return Integer.parseInt(output);
    }

    public Integer getProcessGid(String processName) throws IOException, InterruptedException, ProcessNotFoundException {
        ExecResult result = executeShellCommand("stat -c '%g' /proc/$(ps axf | grep '"+ processName +"$' | grep -v grep |  awk -v def=\"not-found\" '{ print $1 } END { if(NR==0) {print def} }')");

        if(result.getExitCode() != 0) {
            throw new ProcessNotFoundException(processName);
        }

        String output = result.getStdout().trim();

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

    public LogAnalyzer getLogsAnalyzer() {
        String logs = this.getLogs();

        return new LogAnalyzer(logs);
    }

    public HttpResponse makeHttpRequest(int port, String pathAndQuery) throws IOException {
        return makeHttpRequest("http", port, pathAndQuery);
    }

    public HttpResponse makeHttpRequest(String protocol, int port, String pathAndQuery) throws IOException {
        int mappedPort = getMappedPort(port);
        URL target = new URL(String.format("%s://%s:%d%s", protocol, getContainerIpAddress(), mappedPort, pathAndQuery));

        HttpURLConnection connection = (HttpURLConnection)target.openConnection();

        connection.connect();

        return new HttpResponse(connection);
    }

    @NotNull
    private String getShellExecutable() throws IOException, InterruptedException {
        if(this.execInContainer("bash", "--help").getExitCode() == 0) {
            return "bash";
        }

        if(this.execInContainer("which", "ash").getExitCode() == 0) {
            return "ash";
        }

        throw new IOException("No known shell found in the container.");
    }
}
