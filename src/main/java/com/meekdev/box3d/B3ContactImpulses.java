package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;
import com.meekdev.box3d.ffi.rollback.box3d_rollback_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

/// A snapshot of the warm starting impulses on every contact of one body.
///
/// Contact manifolds live in solver memory that the next step overwrites, so the snapshot copies them.
/// Restoring matches manifold points by feature id, which means a contact that changed shape between
/// the snapshot and the restore keeps whatever the solver just produced for its new points.
public final class B3ContactImpulses implements AutoCloseable {

    private final Arena arena = Arena.ofShared();
    private final List<Entry> entries = new ArrayList<>();
    private boolean closed;

    void record(MemorySegment contactId, MemorySegment manifolds, int manifoldCount, long manifoldSize) {
        if (manifolds.equals(MemorySegment.NULL) || manifoldCount <= 0) {
            return;
        }
        MemorySegment idCopy = arena.allocate(contactId.byteSize());
        idCopy.copyFrom(contactId);
        MemorySegment manifoldCopy = arena.allocate(manifoldSize * manifoldCount);
        manifoldCopy.copyFrom(manifolds.reinterpret(manifoldSize * manifoldCount));
        entries.add(new Entry(idCopy, manifoldCopy, manifoldCount));
    }

    /// Writes the saved impulses back onto whichever of those contacts still exist.
    /// @return the number of manifold points restored
    public int restore() {
        if (closed) {
            return 0;
        }
        int restored = 0;
        for (Entry entry : entries) {
            if (box3d_h.b3Contact_IsValid(entry.contactId())) {
                restored += box3d_rollback_h.b3Contact_RestoreImpulses(entry.contactId(),
                        entry.manifolds(), entry.manifoldCount());
            }
        }
        return restored;
    }

    public int contactCount() {
        return entries.size();
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        entries.clear();
        arena.close();
    }

    private record Entry(MemorySegment contactId, MemorySegment manifolds, int manifoldCount) {
    }
}
