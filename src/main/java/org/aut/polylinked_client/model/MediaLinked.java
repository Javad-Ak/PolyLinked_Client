package org.aut.polylinked_client.model;

import org.jetbrains.annotations.NotNull;


public interface MediaLinked extends JsonSerializable, Comparable<MediaLinked> {
    String SERVER_PREFIX = "https://localhost:8080/resources/";

    String getMediaURL();

    @Override
    default int compareTo(@NotNull MediaLinked o) {
        return 0;
    }
}