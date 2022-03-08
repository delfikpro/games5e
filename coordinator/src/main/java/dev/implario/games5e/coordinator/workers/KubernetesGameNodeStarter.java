package dev.implario.games5e.coordinator.workers;

import com.google.inject.Inject;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.kdev.k8sapi.K8S;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class KubernetesGameNodeStarter implements GameNodeStarter {

    private static final String GAMES5E_NAMESPACE = System.getenv("GAMES5E_NAMESPACE");

    private final ImageConfigurationsProvider<V1Pod, Path> configurationsProvider;

    static {
        if (!K8S.INSTANCE.isPodNamespaceExists(GAMES5E_NAMESPACE)) {
            V1Namespace namespace = new V1Namespace();

            namespace.apiVersion("v1");
            namespace.kind("Namespace");
            namespace.metadata(new V1ObjectMeta().name(GAMES5E_NAMESPACE));

            K8S.INSTANCE.createNamespace(namespace);
        }
    }

    @Override
    @SneakyThrows
    public UUID createGameNode(String imageId) {
        UUID newId = UUID.randomUUID();

        V1Pod pod = configurationsProvider.getConfigurations().get(imageId);

        Optional.ofNullable(pod.getMetadata()).ifPresent(x -> x.name(newId.toString()));

        K8S.INSTANCE.createPod(GAMES5E_NAMESPACE, pod);

        return newId;
    }
}
