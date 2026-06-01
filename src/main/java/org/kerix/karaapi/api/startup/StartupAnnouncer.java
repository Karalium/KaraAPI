package org.kerix.karaapi.api.startup;


import java.util.logging.Logger;
import java.util.List;
import java.util.Objects;

public final class StartupAnnouncer {

    private final Logger log;
    private final StartupProfile profile;

    public StartupAnnouncer(Logger log, StartupProfile profile) {
        this.log = Objects.requireNonNull(log, "log");
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    public void announceStart() {
        log.info("========================================");
        log.info(profile.name() + " is starting...");
        log.info("Version: " + profile.version());
        log.info("Authors: " + formatAuthors(profile.authors()));
        log.info("Powered by KaraAPI");
        log.info("========================================");
    }

    public void announceStarted() {
        log.info(profile.name() + " has started successfully.");
    }

    public void announceStop() {
        log.info(profile.name() + " is stopping...");
    }

    public void announceStopped() {
        log.info(profile.name() + " has stopped.");
    }

    private String formatAuthors(List<String> authors) {
        if (authors == null || authors.isEmpty()) {
            return "Unknown";
        }

        return String.join(", ", authors);
    }
}
