package com.jdktomcat.showcase.project.exchange.wallet.common.enums;

/**
 * Wallet mutation types used for idempotency and ledger classification.
 */
public enum WalletOperationType {

    /** Open a wallet account for user + currency. */
    CREATE_ACCOUNT,

    /** On-chain / bank deposit, increases available balance. */
    DEPOSIT,

    /** Phase-1 withdraw: move available to frozen. */
    WITHDRAW_FREEZE,

    /** Phase-2 withdraw success: deduct frozen from total. */
    WITHDRAW_CONFIRM,

    /** Withdraw failed or cancelled: release frozen back to available. */
    WITHDRAW_CANCEL,

    /** Place order: lock quote/base asset from available. */
    FREEZE,

    /** Cancel order: release frozen back to available. */
    UNFREEZE,

    /** Trade settlement: consume frozen balance (buy side / fee). */
    SETTLE_DEBIT,

    /** Trade settlement: credit available balance (sell side / rebate). */
    SETTLE_CREDIT,

    /** Atomic transfer: debit sender and credit receiver. */
    TRANSFER
}
