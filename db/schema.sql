create table if not exists users(
    id bigserial primary key,
   first_name varchar(255),
    last_name varchar(255),
    email varchar(255) UNIQUE NOT NULL
)

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_id ON users(id);
CREATE INDEX IF NOT EXISTS idx_users_first_name ON users(first_name);
