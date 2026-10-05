-- Shop products: individual items sold on the website (notes, PDFs, worksheets...).

CREATE SEQUENCE shop_products_seq START 1 INCREMENT 1;

CREATE TABLE shop_products (
    id                BIGINT       PRIMARY KEY,
    title             VARCHAR(255) NOT NULL,
    description       TEXT,
    type              VARCHAR(50)  NOT NULL,
    format            VARCHAR(20)  NOT NULL,
    price             FLOAT(53)    NOT NULL DEFAULT 0,
    status            VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    featured          BOOLEAN      NOT NULL DEFAULT FALSE,
    preview           TEXT,
    level_id          BIGINT,
    class_id          BIGINT,
    subject_id        BIGINT,
    chapter_id        BIGINT,
    content_html      TEXT,
    file_key          VARCHAR(500),
    page_count        INTEGER,
    storage_provider  VARCHAR(30),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    version           BIGINT,
    is_deleted        BOOLEAN      DEFAULT FALSE,

    CONSTRAINT fk_shop_product_level   FOREIGN KEY (level_id)   REFERENCES levels (id),
    CONSTRAINT fk_shop_product_class   FOREIGN KEY (class_id)   REFERENCES classes (id),
    CONSTRAINT fk_shop_product_subject FOREIGN KEY (subject_id) REFERENCES subjects (id),
    CONSTRAINT fk_shop_product_chapter FOREIGN KEY (chapter_id) REFERENCES chapters (id)
);

CREATE INDEX idx_shop_product_status  ON shop_products(status);
CREATE INDEX idx_shop_product_featured ON shop_products(featured);
CREATE INDEX idx_shop_product_level   ON shop_products(level_id);
CREATE INDEX idx_shop_product_class   ON shop_products(class_id);
CREATE INDEX idx_shop_product_subject ON shop_products(subject_id);
CREATE INDEX idx_shop_product_chapter ON shop_products(chapter_id);
