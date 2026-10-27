USE LibraryDB;
GO

-- C01/G5: generated ID and inserted row
SELECT TOP (20) id, email, first_name, last_name, marks
FROM dbo.STUDENTS
ORDER BY id DESC;

-- C02/G13: must return zero rows
SELECT email, COUNT(*) AS cnt
FROM dbo.STUDENTS
GROUP BY email
HAVING COUNT(*) > 1;

-- G8: inspect students, shared books, and join rows
SELECT * FROM dbo.STUDENT_BOOKS ORDER BY student_id, book_id;
SELECT * FROM dbo.BOOKS ORDER BY id;

-- T01/G9: replace the value with the email used by the rollback experiment; expected 0
DECLARE @rollback_email nvarchar(120) = N'rollback@example.edu.vn';
SELECT COUNT(*) AS rows_after_rollback
FROM dbo.STUDENTS
WHERE email = @rollback_email;

-- Constraint evidence
SELECT t.name AS table_name, i.name AS unique_index, c.name AS column_name
FROM sys.indexes i
JOIN sys.tables t ON t.object_id = i.object_id
JOIN sys.index_columns ic ON ic.object_id = i.object_id AND ic.index_id = i.index_id
JOIN sys.columns c ON c.object_id = ic.object_id AND c.column_id = ic.column_id
WHERE t.name IN (N'STUDENTS', N'BOOKS') AND i.is_unique = 1;
