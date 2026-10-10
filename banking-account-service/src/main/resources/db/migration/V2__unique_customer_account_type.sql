ALTER TABLE accounts
ADD CONSTRAINT uk_accounts_customer_account_type
UNIQUE (customer_id, account_type);
