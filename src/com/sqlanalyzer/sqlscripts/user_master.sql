-- Table creation --
CREATE TABLE [dbo].[user_master](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[username] [varchar](50) NOT NULL,
	[password] [varchar](50) NOT NULL,
	[email] [varchar](100) NOT NULL
) ON [PRIMARY]

GO

CREATE UNIQUE NONCLUSTERED INDEX [idxUnique_user_master_username] ON [dbo].[user_master] 
(
	[username] ASC
)WITH (PAD_INDEX  = ON, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 80) ON [PRIMARY]
GO

CREATE UNIQUE NONCLUSTERED INDEX [idxUnique_user_master_email] ON [dbo].[user_master] 
(
	[email] ASC
)WITH (PAD_INDEX  = ON, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 80) ON [PRIMARY]
GO

-- Table creation --

-- Record insertion --
INSERT INTO user_master (username, password, email) VALUES('vicky.thakor','password','vicky.thakor@javaquery.com')
-- Record insertion --