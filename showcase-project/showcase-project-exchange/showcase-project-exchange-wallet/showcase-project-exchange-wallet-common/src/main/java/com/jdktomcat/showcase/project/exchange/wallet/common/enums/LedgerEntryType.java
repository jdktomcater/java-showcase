package com.jdktomcat.showcase.project.exchange.wallet.common.enums;

/**
 * Fine-grained ledger direction for audit trail.
 */
public enum LedgerEntryType {

    CREDIT_AVAILABLE,
    DEBIT_AVAILABLE,
    FREEZE,
    UNFREEZE,
    DEBIT_FROZEN,
    TRANSFER_OUT,
    TRANSFER_IN
}
