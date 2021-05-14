package implario.games.node.test;

import clepto.bukkit.world.Area;
import clepto.bukkit.world.Label;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class SimpleArea implements Area {

    private final String name;
    private final CraftWorld world;
    private final List<Label> labels = new ArrayList<>();

    @Override
    public String getTag() {
        return "";
    }

    @Override
    public boolean contains(Location location) {
        return location.getWorld() == this.getWorld();
    }
}
