-- Access grants (entitlements) and the separate shop payment ledger.
-- Shop payments are intentionally separate from the existing chapter payments table:
-- a shop payment buys a product or a bundle, not a chapter.

CREATE SEQUENCE shop_entitlements_seq START 1 INCREMENT 1;
CREATE SEQUENCE shop_payments_seq     START 1 INCREMENT 1;

CREATE TABLE shop_entitlements (
    id          BIGINT      PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    product_id  BIGINT,
    bundle_id   BIGINT,
    source      VARCHAR(30) NOT NULL DEFAULT 'PAYMENT',
    granted_at  TIMESTAMP   NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    version     BIGINT,
    is_deleted  BOOLEAN     DEFAULT FALSE,

    -- Exactly one of product_id / bundle_id must be set.
    CONSTRAINT ck_shop_entitlement_target CHECK ((product_id IS NOT NULL) <> (bundle_id IS NOT NULL)),
    CONSTRAINT fk_shop_entitlement_user    FOREIGN KEY (user_id)    REFERENCES users (id),
    CONSTRAINT fk_shop_entitlement_product FOREIGN KEY (product_id) REFERENCES shop_products (id),
    CONSTRAINT fk_shop_entitlement_bundle  FOREIGN KEY (bundle_id)  REFERENCES shop_bundles (id)
);

CREATE INDEX idx_shop_entitlement_user    ON shop_entitlements(user_id);
CREATE INDEX idx_shop_entitlement_product ON shop_entitlements(product_id);
CREATE INDEX idx_shop_entitlement_bundle  ON shop_entitlements(bundle_id);

CREATE TABLE shop_payments (
    id               BIGINT       PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    product_id       BIGINT,
    bundle_id        BIGINT,
    amount           FLOAT(53)    NOT NULL,
    sender_number    VARCHAR(255) NOT NULL,
    transaction_id   VARCHAR(255),
    payment_method   VARCHAR(255),
    status           VARCHAR(255) NOT NULL,
    verified_at      TIMESTAMP,
    verified_by      BIGINT,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    version          BIGINT,
    is_deleted       BOOLEAN      DEFAULT FALSE,

    CONSTRAINT uk_shop_payment_transaction_id UNIQUE (transaction_id),
    CONSTRAINT fk_shop_payment_user      FOREIGN KEY (user_id)     REFERENCES users (id),
    CONSTRAINT fk_shop_payment_product   FOREIGN KEY (product_id)  REFERENCES shop_products (id),
    CONSTRAINT fk_shop_payment_bundle    FOREIGN KEY (bundle_id)   REFERENCES shop_bundles (id),
    CONSTRAINT fk_shop_payment_verifier  FOREIGN KEY (verified_by) REFERENCES users (id)
);

CREATE INDEX idx_shop_payment_status ON shop_payments(status);
CREATE INDEX idx_shop_payment_user   ON shop_payments(user_id);
