package model.search;

public enum UserSearchCriteria {
    USER_CODE("Mã tài khoản", "user_code", SearchType.LIKE),
    USERNAME("Tên đăng nhập", "username",SearchType.BINARY_EQUAL),
    FULL_NAME("Tên người dùng", "full_name",SearchType.LIKE),
    AGE("Tuổi", "age",SearchType.EXACT),
    GENDER("Giới tính", "gender",SearchType.LIKE),
    ADDRESS("Địa chỉ", "address",SearchType.LIKE),
    EMAIL("Email", "email",SearchType.LIKE),
    PHONE_NUMBER("Số điện thoại", "phone_number",SearchType.EXACT),
    ROLE("Vai trò","role",SearchType.LIKE),
    STATUS("Trạng thái","status",SearchType.LIKE);

    private final String displayName;
    private final String columnName;
    private final SearchType searchType;

    UserSearchCriteria(String displayName, String columnName, SearchType searchType) {
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
