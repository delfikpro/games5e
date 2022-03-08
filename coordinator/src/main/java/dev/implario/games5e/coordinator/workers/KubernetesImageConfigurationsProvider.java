package dev.implario.games5e.coordinator.workers;

import com.google.gson.Gson;
import com.google.inject.Inject;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Yaml;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class KubernetesImageConfigurationsProvider implements ImageConfigurationsProvider<V1Pod, Path> {

    private final Map<String, V1Pod> configurations = new HashMap<>();

    private final Gson gson;

    @Override
    @SneakyThrows
    public void parseFrom(Path path) {
        String content = String.join("\n", Files.readAllLines(path));
        Map<String, String> rawConfigurations = gson.fromJson(content, Map.class);

        rawConfigurations.forEach((k, v) -> {
            try {
                configurations.put(k, (V1Pod) Yaml.load(v));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
