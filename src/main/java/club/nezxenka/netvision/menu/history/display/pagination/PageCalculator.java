package club.nezxenka.netvision.menu.history.display.pagination;

public class PageCalculator {
  private final int entriesPerPage;
  private final int maxPages;

  public PageCalculator(int entriesPerPage, int maxPages) {
    this.entriesPerPage = entriesPerPage;
    this.maxPages = maxPages;
  }

  public int computeTotalPages(int totalEntries) {
    return Math.max(1, Math.min((int) Math.ceil((double) totalEntries / entriesPerPage), maxPages));
  }

  public int clampPage(int page, int totalPages) {
    if (page < 1) return 1;
    if (page > totalPages) return totalPages;
    return page;
  }

  public int startIndex(int page) {
    return (page - 1) * entriesPerPage;
  }

  public int endIndex(int page, int total) {
    return Math.min(startIndex(page) + entriesPerPage, total);
  }
}
