package org.kg.secure.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class Application {

    private UUID id;
    private String name;
    private String author;
    private String version;
}
