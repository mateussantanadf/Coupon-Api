INSERT INTO cupons (
  id,
  code,
  description,
  discount_value,
  expiration_date,
  status,
  published,
  redeemed
) VALUES (
  RANDOM_UUID(),
  'BEMVINDO10',
  'Cupom boas-vindas',
  10.00,
  TIMESTAMP '2028-12-31 23:59:59',
  'ACTIVE',
  TRUE,
  FALSE
);
