INSERT INTO USERS (username, password, enabled, role) VALUES ('isa59202021@gmail.com', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', true, 'ADMIN');

INSERT INTO AUTHORITY (name) VALUES ('ROLE_ADMIN');

INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (1, 1);
