-- Add composite primary key to user_roles
ALTER TABLE user_roles ADD PRIMARY KEY (user_id, role);
