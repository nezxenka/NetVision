package club.nezxenka.netvision.visual.menu.history.model.page;

import java.util.UUID;

public record PageState(UUID targetUuid, String targetName, int currentPage, int totalPages) {}
