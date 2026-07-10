package club.nezxenka.netvision.visual.menu.history.model;

import java.util.UUID;

public record HistorySession(UUID targetUuid, String targetName, int currentPage) {}
