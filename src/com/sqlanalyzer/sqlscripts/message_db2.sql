CREATE TABLE SQLANALYZER.MESSAGE
(ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL,
USER_ID INTEGER NOT NULL,
MESSAGE_TEXT VARCHAR(1000),
CONSTRAINT fk_message_user_master FOREIGN KEY(user_id) REFERENCES SQLANALYZER.USER_MASTER(ID) ON DELETE CASCADE,
PRIMARY KEY (ID));