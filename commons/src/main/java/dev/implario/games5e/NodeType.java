package dev.implario.games5e;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum NodeType {

    BUKKIT(s -> s.startsWith("bukkit")),
    DOCKER(s -> s.startsWith("docker"));

    private final Predicate<String> matcher;

}
