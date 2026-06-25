<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Admin</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .header {
            background: #2c3e50;
            color: white;
            padding: 1rem 0;
            margin-bottom: 20px;
        }
        
        .header-content {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .dashboard-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
        }
        
        .stat-card h3 {
            margin: 0 0 10px 0;
            color: #333;
            font-size: 14px;
            text-transform: uppercase;
        }
        
        .stat-number {
            font-size: 2em;
            font-weight: bold;
            color: #2c3e50;
        }
        
        .admin-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-top: 30px;
        }
        
        .action-btn {
            display: block;
            padding: 15px;
            background: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            text-align: center;
            transition: background 0.3s;
        }
        
        .action-btn:hover {
            background: #2980b9;
        }
        
        .alert {
            padding: 12px 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .nav-links {
            display: flex;
            gap: 15px;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            padding: 5px 10px;
            border-radius: 3px;
        }
        
        .nav-links a:hover {
            background: #34495e;
        }
        
        .welcome-section {
            background: white;
            padding: 30px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>Dashboard Quản lý</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/admin?action=list">Quản lý sản phẩm</a>
                <a href="${pageContext.request.contextPath}/admin?action=add">Thêm sản phẩm</a>
                <a href="${pageContext.request.contextPath}/home">Về trang chủ</a>
            </div>
        </div>
    </div>
    
    <div class="container">
        <!-- Thông báo -->
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- Welcome Section -->
        <div class="welcome-section">
            <h2>Chào mừng đến trang quản lý</h2>
            <p>Quản lý cửa hàng bán cá của bạn một cách dễ dàng</p>
        </div>
        <!-- Thống kê đơn giản -->
        <div class="dashboard-stats">
            <div class="stat-card">
                <h3>Truy cập hệ thống</h3>
                <div class="stat-number">Admin</div>
            </div>
            <div class="stat-card">
                <h3>Chức năng</h3>
                <div class="stat-number">Quản lý</div>
            </div>         
        </div>
        
        <!-- Quick Actions -->
        <div class="admin-actions">
            <a href="${pageContext.request.contextPath}/admin?action=list" class="action-btn">
                Quản lý sản phẩm
            </a>
            <a href="${pageContext.request.contextPath}/admin?action=add" class="action-btn">
                Thêm sản phẩm mới
            </a>
            <a href="${pageContext.request.contextPath}/home" class="action-btn">
                Xem trang chủ
            </a>
        </div>
        
        <!-- Hướng dẫn nhanh -->
        <div class="welcome-section">
            <h3>Hướng dẫn sử dụng:</h3>
            <ul>
                <li><strong>Quản lý sản phẩm:</strong> Xem, sửa, xóa các sản phẩm hiện có</li>
                <li><strong>Thêm sản phẩm:</strong> Thêm sản phẩm mới vào hệ thống</li>
                <li><strong>Về trang chủ:</strong> Quay lại trang người dùng</li>
            </ul>
        </div>
    </div>
</body>
</html>