package com.jdktomcat.showcase.ai.impact.analysis.domain;

/**
 * Relation types in the impact chain.
 */
public enum RelationType {
    DECLARES,
    CALLS,
    DEPENDS_ON,
    EXTENDS,
    IMPLEMENTS,
    ANNOTATED_BY,
    RETURNS,
    HAS_PARAM_TYPE,
    INJECTS,
    INVOKES,
    TRIGGERS,
    PUBLISHES,
    CONSUMES,
    ACCESSES
}
