package dev.implario.games5e.node;

import dev.implario.games5e.QueueProperties;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QueueState {

    private final QueueProperties properties;
    private final List<UUID> players;

}
