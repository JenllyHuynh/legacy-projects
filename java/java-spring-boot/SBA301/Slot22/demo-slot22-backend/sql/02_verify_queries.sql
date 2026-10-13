USE FUNewsManagementSBA301;
GO
-- Evidence sau POST / PUT / DELETE
SELECT TOP 10 * FROM dbo.Category ORDER BY CategoryID DESC;
SELECT * FROM dbo.Category ORDER BY CategoryID;

-- Category nào đang được NewsArticle dùng (không được xóa)
SELECT c.CategoryID, c.CategoryName, COUNT(n.NewsArticleID) AS NewsCount
FROM dbo.Category c LEFT JOIN dbo.NewsArticle n ON n.CategoryID = c.CategoryID
GROUP BY c.CategoryID, c.CategoryName ORDER BY c.CategoryID;

-- Break-It #2: so Entity với schema thật
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Category';
