CREATE EXTENSION IF NOT EXISTS postgis;

CREATE
    OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at
        = now();
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS "user"
(
    "id"         BIGSERIAL PRIMARY KEY,
    "username"   VARCHAR(50) UNIQUE  NOT NULL,
    "name"       VARCHAR(50)         NOT NULL,
    "email"      VARCHAR(255) UNIQUE NOT NULL,
    "lang"       VARCHAR(10)         NOT NULL
        CHECK ( lang IN ('RU', 'EN')),
    "role"       VARCHAR(32)         NOT NULL
        CHECK ( role IN ('ADMIN', 'USER')),
    "is_active"  BOOLEAN             NOT NULL DEFAULT true,
    "created_at" TIMESTAMPTZ         NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMPTZ         NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_user_updated_at
    BEFORE UPDATE
    ON "user"
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS "password"
(
    "user_id"       BIGINT PRIMARY KEY REFERENCES "user" ("id") ON DELETE CASCADE,
    "password_hash" VARCHAR(255) NOT NULL,
    "updated_at"    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_password_updated_at
    BEFORE UPDATE
    ON "password"
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS "point_category"
(
    "id"         BIGSERIAL PRIMARY KEY,
    "name"       VARCHAR(50)        NOT NULL,
    "slug"       VARCHAR(50) UNIQUE NOT NULL,
    "created_at" TIMESTAMPTZ        NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_point_category_slug
    ON "point_category" ("slug");

CREATE TABLE IF NOT EXISTS "point"
(
    "id"             BIGSERIAL PRIMARY KEY,
    "category_id"    BIGINT           NOT NULL REFERENCES "point_category" ("id") ON DELETE RESTRICT,
    "title"          VARCHAR(255)     NOT NULL,
    "description"    TEXT,
    "address"        VARCHAR(255),
    "latitude"       DOUBLE PRECISION NOT NULL
        CHECK ( latitude BETWEEN -90 AND 90),
    "longitude"      DOUBLE PRECISION NOT NULL
        CHECK ( longitude BETWEEN -180 AND 180),
    "geom"           geography(Point, 4326) GENERATED ALWAYS AS ( ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography ) STORED,
    "visit_time_min" INTEGER,
    "working_hours"  VARCHAR(255),
    "is_active"      BOOLEAN          NOT NULL DEFAULT true,
    "created_at"     TIMESTAMPTZ      NOT NULL DEFAULT now(),
    "updated_at"     TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_point_updated_at
    BEFORE UPDATE
    ON "point"
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_point_geom
    ON "point" USING GIST ("geom");

CREATE INDEX IF NOT EXISTS idx_point_category_id
    ON "point" ("category_id");

CREATE TABLE IF NOT EXISTS "point_media"
(
    "id"         BIGSERIAL PRIMARY KEY,
    "point_id"   BIGINT       NOT NULL REFERENCES "point" ("id") ON DELETE CASCADE,
    "object_key" VARCHAR(500) NOT NULL,
    "media_type" VARCHAR(64)  NOT NULL
        CHECK ( media_type IN ('PHOTO', 'VIDEO', 'AUDIO') ),
    "sort_order" INTEGER      NOT NULL DEFAULT 0,
    "created_at" TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_point_media_point_id
    ON "point_media" ("point_id", "sort_order");

CREATE TABLE IF NOT EXISTS "excursion"
(
    "id"           BIGSERIAL PRIMARY KEY,
    "owner_id"     BIGINT REFERENCES "user" ("id") ON DELETE CASCADE,
    "route_type"   VARCHAR(32)  NOT NULL
        CHECK ( route_type IN ('CUSTOM', 'PREBUILT') ),
    "visibility"   VARCHAR(32)  NOT NULL
        CHECK ( visibility IN ('PUBLIC', 'PRIVATE') ),
    "title"        VARCHAR(255) NOT NULL,
    "description"  TEXT,
    "distance"     INTEGER      NOT NULL,
    "duration_min" INTEGER      NOT NULL,
    "created_by"   BIGINT       REFERENCES "user" ("id") ON DELETE SET NULL,
    "created_at"   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    "updated_at"   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CHECK (route_type = 'PREBUILT' OR owner_id IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_excursion_route_type
    ON "excursion" ("route_type");

CREATE INDEX IF NOT EXISTS idx_excursion_owner_id
    ON "excursion" ("owner_id");

CREATE INDEX IF NOT EXISTS idx_excursion_visibility
    ON "excursion" ("visibility");

CREATE TRIGGER trg_excursion_updated_at
    BEFORE UPDATE
    ON "excursion"
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS "excursion_point"
(
    "id"           BIGSERIAL PRIMARY KEY,
    "excursion_id" BIGINT  NOT NULL REFERENCES "excursion" (id) ON DELETE CASCADE,
    "point_id"     BIGINT  NOT NULL REFERENCES "point" ("id") ON DELETE RESTRICT,
    "order_index"  INTEGER NOT NULL,
    CHECK ( order_index > 0 ),
    UNIQUE (excursion_id, order_index),
    UNIQUE (excursion_id, point_id)
);

CREATE INDEX IF NOT EXISTS idx_excursion_point_excursion_id
    ON excursion_point ("excursion_id");

CREATE TABLE IF NOT EXISTS "favorite_excursion"
(
    "id"           BIGSERIAL PRIMARY KEY,
    "user_id"      BIGINT      NOT NULL REFERENCES "user" ("id") ON DELETE CASCADE,
    "excursion_id" BIGINT      NOT NULL REFERENCES "excursion" ("id") ON DELETE CASCADE,
    "favorite_at"  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, excursion_id)
);

CREATE INDEX IF NOT EXISTS idx_favorite_excursion_user_id
    ON favorite_excursion ("user_id");
