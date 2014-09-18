CREATE TABLE IF NOT EXISTS INT_MESSAGE (MESSAGE_ID CHAR(36) NOT NULL PRIMARY KEY,	REGION VARCHAR(100), CREATED_DATE DATETIME NOT NULL, MESSAGE_BYTES BLOB, IDENTIFIER CHAR(50)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS INT_MESSAGE_GROUP (MESSAGE_ID CHAR(36) NOT NULL, GROUP_KEY CHAR(36) NOT NULL, REGION VARCHAR(100),	MARKED BIGINT, COMPLETE BIGINT,	LAST_RELEASED_SEQUENCE BIGINT, CREATED_DATE DATETIME NOT NULL, UPDATED_DATE DATETIME DEFAULT NULL, MESSAGE_BYTES BLOB, constraint MESSAGE_GROUP_PK primary key (MESSAGE_ID, GROUP_KEY)) ENGINE=InnoDB;
CREATE INDEX INT_MSG_INDX ON INT_MESSAGE (IDENTIFIER, REGION);
CREATE INDEX INT_MSG_INDX_ID ON INT_MESSAGE (MESSAGE_ID, REGION);
CREATE INDEX INT_MSG_GRP_INDX ON INT_MESSAGE_GROUP (MESSAGE_ID, REGION);