CREATE TABLE IF NOT EXISTS players (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    joined_at DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    player_id UUID NULL REFERENCES players(id),
    paid_at DATE NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    type TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS expenses (
    id UUID PRIMARY KEY,
    spent_at DATE NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    description TEXT NOT NULL
);
