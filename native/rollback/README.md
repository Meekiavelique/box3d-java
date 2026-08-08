# rollback

contact impulses and sleep state, saved and put back so a replayed step doesn't solve its contacts
cold. not upstream box3d.

they sit here instead of in `native/box3d` because that's a submodule pointing at upstream. anything
left in there is untracked working tree, and a fresh clone or a `git clean` takes it with them, which
leaves the build with nothing to compile.

`graftRollbackSources` copies this over the submodule before cmake configures and adds the one line to
its `src/CMakeLists.txt`. run it twice, nothing happens the second time.
