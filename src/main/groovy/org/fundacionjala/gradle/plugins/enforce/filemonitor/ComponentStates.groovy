package org.fundacionjala.gradle.plugins.enforce.filemonitor

enum ComponentStates {
     ADDED('Added'),
    CHANGED('Changed'),
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
