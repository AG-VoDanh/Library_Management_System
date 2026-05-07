package model.search;

import java.util.function.Function;

public enum SearchType {
    EXACT("=", value -> value),
    LIKE("LIKE", value -> "%" + value + "%"),
    STARTS_WITH("LIKE", value -> value + "%"),
    ENDS_WITH("LIKE", value -> "%" + value),
    BINARY_EQUAL("COLLATE utf8mb4_bin =",value -> value),
    BETWEEN("BETWEEN",null);

    private final String operator;
    private final Function<String, String> formatter;

    SearchType(String operator, Function<String, String> formatter) {
        this.operator = operator;
        this.formatter = formatter;
    }

    public String getOperator() {
        return operator;
    }

    public String format(String value) {
        return formatter.apply(value);
    }
}
