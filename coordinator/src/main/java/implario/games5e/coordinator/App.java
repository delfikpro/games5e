package implario.games5e.coordinator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import implario.games5e.packets.PacketNodeHandshakeV1;

public class App {

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(binder -> {
            binder.bind(Balancer.class).to(SimpleBalancer.class);
        });
        injector.getInstance(PacketNodeHandshakeV1.class);

    }

}
