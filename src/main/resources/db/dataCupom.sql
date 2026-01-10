CREATE TABLE cupons (
  id UUID PRIMARY KEY,
  code VARCHAR(6) NOT NULL UNIQUE,
  description VARCHAR(50),
  discount_value DECIMAL(5,2) NOT NULL,
  expiration_date TIMESTAMP NOT NULL,
  status VARCHAR(10) NOT NULL,
  published BOOLEAN NOT NULL DEFAULT false,
  redeemed BOOLEAN NOT NULL DEFAULT false
);
