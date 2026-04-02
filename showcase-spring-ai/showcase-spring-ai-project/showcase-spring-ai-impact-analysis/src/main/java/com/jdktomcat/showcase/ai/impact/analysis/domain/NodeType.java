package com.jdktomcat.showcase.ai.impact.analysis.domain;

/**
 * Node types in the impact chain.
 */
public enum NodeType {
    ENTRY_POINT,
    MODULE,
    PACKAGE,
    TYPE,
    METHOD,
    FIELD,
    ANNOTATION,
    EXTERNAL_SERVICE
}
