package dev.implario.games5e.node.linker;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Identity checks are used to distinguish between
 * different connections (sessions) of the same user
 */
@RequiredArgsConstructor
public class IdentityUUID {

    private final UUID id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentityUUID that = (IdentityUUID) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
