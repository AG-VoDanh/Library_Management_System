package model.search;

public enum ImportSearchCriteria {
    IMPORT_CODE("Mã phiếu nhập sách", "import_code", SearchType.LIKE),
    USER_CODE("Mã tài khoản", "importer_code",SearchType.LIKE),
    USERNAME("Tên tài khoản", "importer_username",SearchType.BINARY_EQUAL),
    IMPORT_DATE("Thời gian nhập sách", "import_date",SearchType.BETWEEN),
    BOOK_CODE("Mã sách", "imported_book_code",SearchType.LIKE),
    BOOK_NAME("Tên sách", "imported_book_name",SearchType.LIKE);

    private final String displayName;
    private final String columnName;
    private final SearchType searchType;

    ImportSearchCriteria(String displayName, String columnName, SearchType searchType) {
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
        return  displayName;
    }
}
