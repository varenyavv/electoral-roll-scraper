package com.raw.scraper.constant;

public enum NepalState {
    KOSHI(1),
    MADHESH(2),
    BAGMATI(3),
    GANDAKI(4),
    LUMBINI(5),
    KARNALI(6),
    SUDURPASHCHIM(7);

    private final int key;

    NepalState(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
