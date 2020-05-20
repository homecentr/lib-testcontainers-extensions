package io.homecentr.testcontainers.images;

import org.testcontainers.images.ImagePullPolicy;

public class PullPolicyEx {
    public ImagePullPolicy never() {
        return new NeverPullImagePolicy();
    }
}
