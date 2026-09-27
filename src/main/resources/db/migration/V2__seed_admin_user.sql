INSERT INTO users (
    username,
    email,
    password_hash,
    first_name,
    last_name,
    phone_number,
    role,
    status
)
VALUES (
    'systemadmin',
    'systemadmin@banking.com',
    '$2a$10$GF7cGK0rIYHfQZcEYY7.D.viDM4uC0YvD/aostplpjWaT1oDR8hNm',
    'System',
    'Admin',
    '9999999999',
    'ADMIN',
    'ACTIVE'
);