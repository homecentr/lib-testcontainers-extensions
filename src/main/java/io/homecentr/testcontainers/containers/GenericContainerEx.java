package io.homecentr.testcontainers.containers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import io.homecentr.testcontainers.images.ImageTagResolver;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.shaded.com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.HttpURLConnection;
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

    public GenericContainerEx(ImageFromDockerfile image) {
        super(image);
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
        ExecResult result = executeShellCommand("stat -c '%u' /proc/$(ps axf | pgrep -f '^"+ processName +"$' |  awk -v def=\"not-found\" '{ print $1 } END { if(NR==0) {print def} }')");

        if(result.getExitCode() != 0) {
            throw new ProcessNotFoundException(processName);
        }

        String output = result.getStdout().trim();

        return Integer.parseInt(output);
    }

    public Integer getProcessGid(String processName) throws IOException, InterruptedException, ProcessNotFoundException {
        ExecResult result = executeShellCommand("stat -c '%g' /proc/$(ps axf | pgrep -f '^"+ processName +"$' |  awk -v def=\"not-found\" '{ print $1 } END { if(NR==0) {print def} }')");

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

    public int getMappedUdpPort(int containerPort) {
        if(!isRunning()){
            throw new IllegalStateException("The container must be started to retrieve tha mapped UDP port.");
        }

        if(containerPort < 0 || containerPort > 65535){
            throw new IllegalArgumentException("The port must be between 0 and 65535.");
        }

        Ports.Binding[] portMapping = getContainerInfo().getNetworkSettings().getPorts().getBindings().get(ExposedPort.udp(containerPort));

        if(portMapping == null || portMapping.length == 0){
            throw new IllegalArgumentException("The port " + containerPort + " is not mapped by the container.");
        }

        return Integer.parseInt(portMapping[0].getHostPortSpec());
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
