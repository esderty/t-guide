CREATE TABLE IF NOT EXISTS "excursion_review" (
    "id" BIGSERIAL PRIMARY KEY,
    "excursion_id" BIGINT NOT NULL REFERENCES excursion(id) ON DELETE CASCADE,
    "user_id" BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    "rating" SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    "review_text" TEXT,
    "visit_date" DATE NOT NULL,
    "is_active" BOOLEAN NOT NULL DEFAULT TRUE,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (excursion_id, user_id)
);

CREATE TRIGGER trg_excursion_review_updated_at
    BEFORE UPDATE
    ON "excursion_review"
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
