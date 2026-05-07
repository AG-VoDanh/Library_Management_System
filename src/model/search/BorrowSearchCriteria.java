package model.search;

public enum BorrowSearchCriteria {
    BORROW_CODE("Mã lượt mượn", "borrow_code", SearchType.LIKE),
    MEMBER_CODE("Mã hội viên", "borrower_code",SearchType.LIKE),
    MEMBER_NAME("Tên hội viên", "borrower_name",SearchType.LIKE),
    BORROW_DATE("TG mượn sách","borrow_date",SearchType.BETWEEN),
    DUE_DATE("TG dự kiến trả sách","due_date",SearchType.BETWEEN),
    RETURN_DATE("TG trả sách","return_date",SearchType.BETWEEN),
    STATUS("Trạng thái","trangThai",SearchType.LIKE),
    BOOK_CODE("Mã Sách","borrowed_book_code",SearchType.LIKE),
    BOOK_NAME("Tên sách", "borrowed_book_name",SearchType.LIKE),
    PHONE_NUMBER("Số điện thoại","borrower_phone_number",SearchType.EXACT);

    private final String displayName;
    private final String columnName;
    private final SearchType searchType;

    BorrowSearchCriteria(String displayName, String columnName, SearchType searchType) {
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
