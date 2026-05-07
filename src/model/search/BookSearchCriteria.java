package model.search;

public enum BookSearchCriteria {
    BOOK_CODE("Mã Sách", "book_code", SearchType.LIKE),
    BOOK_NAME("Tên Sách", "book_name",SearchType.LIKE),
    AUTHOR("Tác giả", "author",SearchType.LIKE),
    PUBLICATION_YEAR("Năm xuất bản", "publication_year",SearchType.EXACT),
    CATEGORY("Thể loại", "category",SearchType.LIKE);

    private final String displayName;
    private final String columnName;
    private final SearchType searchType;

    BookSearchCriteria(String displayName, String columnName, SearchType searchType) {
        this.displayName = displayName;
        this.columnName = columnName;
        this.searchType = searchType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColumnName() {
        return columnName;
    }

    public SearchType getSearchType(){
        return searchType;
    }

    @Override
    public String toString(){
        return displayName;
    }
}
