CREATE TABLE politician (
    id              VARCHAR(255) PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    given_name      VARCHAR(255),
    family_name     VARCHAR(255),
    party           VARCHAR(255),
    image_url       VARCHAR(500),
    email           VARCHAR(255),
    gender          VARCHAR(50),
    birth_date      VARCHAR(20),
    openstates_url  VARCHAR(500),
    extra_data      JSONB,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE politician_role (
    id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    politician_id       VARCHAR(255) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    org_classification  VARCHAR(50),
    district            VARCHAR(100),
    jurisdiction_name   VARCHAR(255),
    jurisdiction_id     VARCHAR(255),
    CONSTRAINT fk_politician_role_politician
        FOREIGN KEY (politician_id) REFERENCES politician(id) ON DELETE CASCADE
);

CREATE INDEX idx_politician_role_politician_id ON politician_role(politician_id);
