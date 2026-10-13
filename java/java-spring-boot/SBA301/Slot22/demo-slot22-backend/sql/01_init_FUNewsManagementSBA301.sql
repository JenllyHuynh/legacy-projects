IF DB_ID('FUNewsManagementSBA301') IS NULL
    CREATE DATABASE FUNewsManagementSBA301;
GO
USE FUNewsManagementSBA301;
GO

/*  Category */
IF OBJECT_ID('dbo.Category','U') IS NULL
BEGIN
    CREATE TABLE dbo.Category(
        CategoryID          SMALLINT IDENTITY(1,1) PRIMARY KEY,
        CategoryName        NVARCHAR(100) NOT NULL,
        CategoryDesciption  NVARCHAR(250) NOT NULL,
        ParentCategoryID    SMALLINT NULL,
        IsActive            BIT NULL,
        CONSTRAINT FK_Category_Parent FOREIGN KEY (ParentCategoryID) REFERENCES dbo.Category(CategoryID)
    );
END;
GO

/* SystemAccount */
IF OBJECT_ID('dbo.SystemAccount','U') IS NULL
BEGIN
    CREATE TABLE dbo.SystemAccount(
        AccountID       SMALLINT PRIMARY KEY,
        AccountName     NVARCHAR(100) NOT NULL,
        AccountEmail    NVARCHAR(70)  NOT NULL,
        AccountRole     INT NULL,
        AccountPassword NVARCHAR(70)  NOT NULL
    );
END;
GO

/*  NewsArticle  */
IF OBJECT_ID('dbo.NewsArticle','U') IS NULL
BEGIN
    CREATE TABLE dbo.NewsArticle(
        NewsArticleID   NVARCHAR(20) PRIMARY KEY,
        NewsTitle       NVARCHAR(400) NULL,
        Headline        NVARCHAR(150) NOT NULL,
        CreatedDate     DATETIME NULL,
        NewsContent     NVARCHAR(4000) NULL,
        NewsSource      NVARCHAR(400) NULL,
        CategoryID      SMALLINT NULL,
        NewsStatus      BIT NULL,
        CreatedByID     SMALLINT NULL,
        UpdatedByID     SMALLINT NULL,
        ModifiedDate    DATETIME NULL,
        CONSTRAINT FK_NewsArticle_Category      FOREIGN KEY (CategoryID)  REFERENCES dbo.Category(CategoryID),
        CONSTRAINT FK_NewsArticle_SystemAccount FOREIGN KEY (CreatedByID) REFERENCES dbo.SystemAccount(AccountID)
    );
END;
GO

/*  Seed  */
IF NOT EXISTS (SELECT 1 FROM dbo.Category)
BEGIN
    INSERT dbo.Category(CategoryName,CategoryDesciption,ParentCategoryID,IsActive)
    VALUES
    (N'Academic news',  N'Academic announcements',        NULL, 1),
    (N'Student Affairs',N'Student activities and events', NULL, 1),
    (N'Campus Safety',  N'Campus safety information',     NULL, 1);
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.SystemAccount)
BEGIN
    INSERT dbo.SystemAccount(AccountID,AccountName,AccountEmail,AccountRole,AccountPassword)
    VALUES (1, N'Staff One', N'staff1@funews.edu', 1, N'@1');
END;
GO

/* NewsArticle tham chiếu Category 1 -> dùng cho test T10 (DELETE category 1 phải trả 409) */
IF NOT EXISTS (SELECT 1 FROM dbo.NewsArticle)
BEGIN
    INSERT dbo.NewsArticle(NewsArticleID,NewsTitle,Headline,CreatedDate,NewsContent,NewsSource,CategoryID,NewsStatus,CreatedByID)
    VALUES (N'1', N'Welcome semester', N'Welcome new semester', GETDATE(), N'Content', N'FU', 1, 1, 1);
END;
GO

SELECT * FROM dbo.Category ORDER BY CategoryID;
SELECT NewsArticleID, Headline, CategoryID FROM dbo.NewsArticle;
