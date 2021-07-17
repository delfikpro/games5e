package implario.games5e.coordinator;

import dev.implario.nettier.NettierRemote;
import implario.games5e.GameInfo;
import implario.games5e.Games5eGameState;
import implario.games5e.ImageType;
import implario.games5e.packets.PacketCreateGame;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractGameNode implements GameNode {

    private final NettierRemote remote;
    private final List<RunningGame> runningGames;
    private final ImageType type;


    @Override
    public void startGame(GameInfo info, Image image) {

        if (type != image.getType())
            throw new IllegalArgumentException("Can't run " + image + " image on " + type + " node ");

        runningGames.add(new RunningGame(info, Games5eGameState.INITIALIZING));
        remote.send(new PacketCreateGame(info));
    }

}
