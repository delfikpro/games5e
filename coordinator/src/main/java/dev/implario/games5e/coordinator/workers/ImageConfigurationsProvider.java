package dev.implario.games5e.coordinator.workers;

import java.util.Map;

public interface ImageConfigurationsProvider<CONFIG, PARSE_DATA> {

    Map<String, CONFIG> getConfigurations();

    void parseFrom(PARSE_DATA object);

}
