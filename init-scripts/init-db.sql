CREATE TABLE users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(30),
  CONSTRAINT "UQ_USER_EMAIL" UNIQUE ("email")
);

CREATE TABLE cards (
  number VARCHAR(16) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  expiration DATE NOT NULL,
  status VARCHAR(30),
  balance DECIMAL NOT NULL DEFAULT 0,
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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

-- денежные переводы
CREATE TABLE money_transfer (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    from_card_number VARCHAR(16) NOT NULL,
    to_card_number VARCHAR(16) NOT NULL,
    amount DECIMAL NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (from_card_number) REFERENCES cards(number),
    FOREIGN KEY (to_card_number) REFERENCES cards(number)
);

