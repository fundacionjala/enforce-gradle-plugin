package org.fundacionjala.gradle.plugins.enforce.filemonitor

public interface ComponentComparable<T> {
    ResultTracker compare(T componentTracker)
}