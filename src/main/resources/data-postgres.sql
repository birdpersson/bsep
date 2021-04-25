
INSERT INTO USERS (username, password, enabled, role) VALUES ('admin', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', true, 'ADMIN'),
                                                             ('user', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', true, 'USER');

INSERT INTO AUTHORITY (name) VALUES ('ROLE_ADMIN'),
                                    ('ROLE_USER');

INSERT INTO PERMISSION (name) VALUES ('CREATE'),
                                     ('READ'),
                                     ('UPDATE'),
                                     ('DELETE');

INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (1, 1),
                                                          (2,2);

INSERT INTO AUTHORITY_PERMISSION (authority_id, permission_id) VALUES (1, 1),
                                                                      (1,2),
                                                                      (1,3),
                                                                      (1,4),
                                                                      (2,2),
                                                                      (2,3);
