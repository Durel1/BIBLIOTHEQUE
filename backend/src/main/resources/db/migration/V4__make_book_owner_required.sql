-- Every book must belong to a user.
-- Existing orphan books must be handled before this migration.
ALTER TABLE books
ALTER COLUMN user_id SET NOT NULL;
