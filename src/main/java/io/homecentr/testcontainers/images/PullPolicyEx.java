package io.homecentr.testcontainers.images;

import org.testcontainers.images.ImagePullPolicy;
import org.testcontainers.images.PullPolicy;

public class PullPolicyEx {
    private ImagePullPolicy never() {
        return new NeverPullImagePolicy();
    }
}
