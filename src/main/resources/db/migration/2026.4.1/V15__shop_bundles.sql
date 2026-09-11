-- Shop bundles: packs of products sold together (e.g. a teacher/coaching download pack).

CREATE SEQUENCE shop_bundles_seq START 1 INCREMENT 1;

CREATE TABLE shop_bundles (
    id           BIGINT       PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    audience     VARCHAR(30)  NOT NULL DEFAULT 'GENERAL',
    access_mode  VARCHAR(30)  NOT NULL DEFAULT 'ONLINE',
    price        FLOAT(53)    NOT NULL DEFAULT 0,
    status       VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    level_id     BIGINT,
    subject_id   BIGINT,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    version      BIGINT,
    is_deleted   BOOLEAN      DEFAULT FALSE,

    CONSTRAINT fk_shop_bundle_level   FOREIGN KEY (level_id)   REFERENCES levels (id),
    CONSTRAINT fk_shop_bundle_subject FOREIGN KEY (subject_id) REFERENCES subjects (id)
);

CREATE INDEX idx_shop_bundle_status ON shop_bundles(status);
CREATE INDEX idx_shop_bundle_level  ON shop_bundles(level_id);

-- Which products belong to which bundle.
CREATE TABLE shop_bundle_products (
    bundle_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    CONSTRAINT uk_shop_bundle_product UNIQUE (bundle_id, product_id),
    CONSTRAINT fk_shop_bundle_product_bundle  FOREIGN KEY (bundle_id)  REFERENCES shop_bundles (id) ON DELETE CASCADE,
    CONSTRAINT fk_shop_bundle_product_product FOREIGN KEY (product_id) REFERENCES shop_products (id) ON DELETE CASCADE
);

CREATE INDEX idx_shop_bundle_products_product ON shop_bundle_products(product_id);
