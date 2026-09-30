CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,

    account_number VARCHAR(20) NOT NULL UNIQUE,

    customer_id BIGINT NOT NULL,

    account_type VARCHAR(20) NOT NULL,

    balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,

    currency VARCHAR(3) NOT NULL DEFAULT 'INR',

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_account_type
        CHECK (account_type IN ('SAVINGS', 'CURRENT')),

    CONSTRAINT chk_account_status
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),

    CONSTRAINT chk_balance
        CHECK (balance >= 0)
);