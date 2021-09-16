package io.homecentr.testcontainers.containers;

import io.homecentr.testcontainers.containers.GenericContainerEx;
import org.junit.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Container;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GenericContainerEx_withTempDirectoryBindShould {
    @Test
    public void mountDirectoryAsReadWriteByDefault() throws IOException, InterruptedException {
        GenericContainerEx container = new GenericContainerEx<>("centos")
                .withTempDirectoryBind("/test", 9001)
                .withCommand("bash", "-c", "sleep 100s");

        container.start();

        Container.ExecResult result = container.executeShellCommand("touch /test/dummy");

        assertEquals(0, result.getExitCode());
    }

    @Test
    public void mountDirectoryAsReadOnlyWhenSpecified() throws IOException, InterruptedException {
        GenericContainerEx container = new GenericContainerEx<>("centos")
                .withTempDirectoryBind("/test", 9001, "rwxrwxrwx", BindMode.READ_ONLY)
                .withCommand("bash", "-c", "sleep 100s");

        container.start();

        Container.ExecResult result = container.executeShellCommand("touch /test/dummy");

        assertNotEquals(0, result.getExitCode());
    }

    @Test
    public void createDirectoryWithDefaultPermissions() throws IOException, InterruptedException {
        GenericContainerEx container = new GenericContainerEx<>("centos")
                .withTempDirectoryBind("/test", 9001)
                .withCommand("bash", "-c", "sleep 100s");

        container.start();

        Container.ExecResult result = container.executeShellCommand("ls -l / | grep test | awk '{ print $1 }'");

        assertEquals("drwxrwx---", result.getStdout().trim());
    }

    @Test
    public void createDirectoryWithPassedPermissions() throws IOException, InterruptedException {
        GenericContainerEx container = new GenericContainerEx<>("centos")
                .withTempDirectoryBind("/test", 9001, "rwx------")
                .withCommand("bash", "-c", "sleep 100s");

        container.start();

        Container.ExecResult result = container.executeShellCommand("ls -l / | grep test | awk '{ print $1 }'");

        assertEquals("drwx------", result.getStdout().trim());
    }

    @Test
    public void createDirectoryWithPassedGroupOwner() throws IOException, InterruptedException {
        GenericContainerEx container = new GenericContainerEx<>("centos")
                .withTempDirectoryBind("/test", 9001)
                .withCommand("bash", "-c", "sleep 100s");

        container.start();

        Container.ExecResult result = container.executeShellCommand("ls -l / | grep test | awk '{ print $4 }'");

        assertEquals("9001", result.getStdout().trim()); // Returns gid because the group does not exist in /etc/group
    }
}
