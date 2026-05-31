package org.kerix.karaapi.api.logic;

public final class Once {

    private boolean ran;

    public boolean hasRun() {
        return ran;
    }

    public boolean run(Runnable runnable) {
        if (ran) {
            return false;
        }

        ran = true;
        runnable.run();

        return true;
    }

    public void reset() {
        ran = false;
    }
}
