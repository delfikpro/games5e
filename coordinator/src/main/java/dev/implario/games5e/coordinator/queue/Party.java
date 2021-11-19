package dev.implario.games5e.coordinator.queue;

import lombok.Data;
import lombok.experimental.Delegate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class Party implements List<UUID> {

    @Delegate
    private final List<UUID> list;

    private final Map<String, List<String>> bannedOptions;

    private final boolean allowSplit;
    private final boolean allowExtra;

}
