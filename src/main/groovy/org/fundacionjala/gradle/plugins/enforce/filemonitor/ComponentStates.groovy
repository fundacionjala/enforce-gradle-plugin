package org.fundacionjala.gradle.plugins.enforce.filemonitor

enum ComponentStates {
    CHANGED('Changed'),
    ADDED('Added'),
    DELETED('Deleted'),
    NOT_CHANGED('Not changed')

    ComponentStates(String value) {
        this.value = value
    }

    private final String value

    public String value() {
        return value
    }
}
