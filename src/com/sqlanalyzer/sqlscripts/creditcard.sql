-- Table creation --
CREATE TABLE [dbo].[creditcard](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[credit_card_number] [bigint] NOT NULL,
 CONSTRAINT [PK_creditcard] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[creditcard]  WITH CHECK ADD  CONSTRAINT [fk_creditcard_user_master] FOREIGN KEY([user_id])
REFERENCES [dbo].[user_master] ([id])
GO

ALTER TABLE [dbo].[creditcard] CHECK CONSTRAINT [fk_creditcard_user_master]
GO
-- Table creation --