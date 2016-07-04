-- Table creation --
CREATE TABLE [dbo].[message](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[message_text] [varchar](max) NULL
 CONSTRAINT [PK_message] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [fk_message_user_master] FOREIGN KEY([user_id])
REFERENCES [dbo].[user_master] ([id])
GO

ALTER TABLE [dbo].[message] CHECK CONSTRAINT [fk_message_user_master]
GO
-- Table creation --
