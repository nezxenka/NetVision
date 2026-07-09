package club.nezxenka.netvision.menu.history.model;

import java.util.UUID;

public record HistorySession(UUID targetUuid, String targetName, int currentPage) {}
