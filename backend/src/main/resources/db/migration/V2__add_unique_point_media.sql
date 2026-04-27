ALTER TABLE "point_media"
    ADD CONSTRAINT unique_point_media UNIQUE (point_id, sort_order);