package io.homecentr.testcontainers.containers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import io.homecentr.testcontainers.images.ImageTagResolver;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.shaded.org.apache.commons.lang.SystemUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.UUID;

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

    public GenericContainerEx<SELF> withTempDirectoryBind(String containerPath, int dirGid) throws IOException {
        return withTempDirectoryBind(containerPath, dirGid, "rwxrwx---");
    }

    public GenericContainerEx<SELF> withTempDirectoryBind(String containerPath, int dirGid, String permissions) throws IOException {
        return withTempDirectoryBind(containerPath, dirGid, permissions, BindMode.READ_WRITE);
    }

    public GenericContainerEx<SELF> withTempDirectoryBind(String containerPath, int dirGid, String permissions, BindMode bindMode) throws IOException {
        Path dirPath = Files.createTempDirectory(UUID.randomUUID().toString());

        if(SystemUtils.IS_OS_LINUX){
            System.out.println("=== IS LINUX");

            Files.setAttribute(dirPath, "unix:gid", dirGid);
            Files.setPosixFilePermissions(dirPath, PosixFilePermissions.fromString(permissions));
        }

        System.out.println("Temp dir: " + dirPath.toAbsolutePath());
        System.out.println(ls());

        return withFileSystemBind(dirPath.toAbsolutePath().toString(), containerPath, bindMode);
    }

    private String ls() {
        String result = null;
        try {
            Runtime r = Runtime.getRuntime();

            Process p = r.exec("ls -l /tmp");

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                result += inputLine;
            }
            in.close();

        } catch (IOException e) {
            System.out.println(e);
        }

        return result;
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

    public Integer getFileOwnerUid(String filePath) throws IOException, InterruptedException {
        ExecResult result = executeShellCommand("stat -c '%u' " + filePath);

        if(result.getExitCode() != 0) {
            throw new FileNotFoundException(filePath);
        }

        String output = result.getStdout().trim();

        return Integer.parseInt(output);
    }

    public Integer getFileOwningGid(String filePath) throws IOException, InterruptedException {
        ExecResult result = executeShellCommand("stat -c '%g' " + filePath);

        if(result.getExitCode() != 0) {
            throw new FileNotFoundException(filePath);
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
