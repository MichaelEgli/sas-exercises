package com.bfh.domi.catalog.catalog.common.testdata;

public enum TestInputValues {
    VALID_ISBN("9780134892075"),
    ISBN_NOT_FOUND("9999999999999"),
    ISBN_NEW_BOOK("9780099999991"),
    ISBN_GOOGLE_BOOK("9783037921173"), // This book is only available via Google Books API
    SINGLE_KEYWORD("spring"),
    MULTI_KEYWORD1("publications"),
    MULTI_KEYWORD2("Action"),
    MULTI_KEYWORD3("Craig"),
    KEYWORDS("python,boring"),
    PYTHON_BOOK_ISBN("9781718503410");

    private final String value;

    TestInputValues(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
