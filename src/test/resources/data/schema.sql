CREATE TABLE SensitiveWords (
                                id BIGINT PRIMARY KEY,
                                word NVARCHAR(255) NOT NULL,
                                isActive BIT NOT NULL,
                                dateCreated DATETIME2 NOT NULL,
                                lastUpdated DATETIME2 NOT NULL
);