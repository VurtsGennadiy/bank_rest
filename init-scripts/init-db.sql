CREATE TABLE users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  role VARCHAR(30),
  CONSTRAINT "UQ_USER_EMAIL" UNIQUE ("email")
);

CREATE TABLE cards (
  number VARCHAR(16) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  expiration DATE NOT NULL,
  status VARCHAR(30),
  balance DECIMAL NOT NULL DEFAULT 0,
  CONSTRAINT "fk_cards_user" FOREIGN KEY ("user_id") REFERENCES "users"("id")
);

-- запросы пользователей на блокировку карты
CREATE TABLE "card_blocking_requests" (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id BIGINT,
  card_number VARCHAR(16) UNIQUE,
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  solved BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT "fk_blocking_requests_user" FOREIGN KEY ("user_id") REFERENCES "users"("id"),
  CONSTRAINT "fk_blocking_requests_card" FOREIGN KEY ("card_number") REFERENCES "cards"("number")
);
