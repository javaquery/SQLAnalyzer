CREATE TABLE message
(
  id bigserial NOT NULL,
  user_id bigint,
  message_text text,
  CONSTRAINT message_primary_key PRIMARY KEY (id),
  CONSTRAINT fk_message_user_master FOREIGN KEY (user_id)
      REFERENCES user_master (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)