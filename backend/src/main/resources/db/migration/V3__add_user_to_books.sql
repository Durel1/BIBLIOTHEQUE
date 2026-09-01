ALTER TABLE books
ADD COLUMN user_id BIGINT;

ALTER TABLE books
ADD CONSTRAINT fk_books_user
FOREIGN KEY (user_id)
REFERENCES users(id);
