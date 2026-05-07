package model.search;

public enum MemberSearchCriteria {
    MEMBER_CODE("Mã hội viên", "member_code", SearchType.LIKE),
    MEMBER_NAME("Tên hội viên", "member_name",SearchType.LIKE),
    AGE("Tuổi", "age",SearchType.EXACT),
    GENDER("Giới tính", "gender",SearchType.LIKE),
    ADDRESS("Địa chỉ", "address",SearchType.LIKE),
    PHONE_NUMBER("Số điện thoại", "phone_number",SearchType.EXACT),
    STATUS("Trạng thái","status",SearchType.LIKE);

    private final String displayName;
    private final String columnName;
    private final SearchType searchType;

    MemberSearchCriteria(String displayName, String columnName, SearchType searchType) {
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
