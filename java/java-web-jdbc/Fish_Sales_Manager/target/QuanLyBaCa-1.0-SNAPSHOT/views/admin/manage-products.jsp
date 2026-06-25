<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lý sản phẩm - Admin</title>
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

            .admin-actions {
                margin-bottom: 20px;
            }

            .btn {
                padding: 10px 15px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                text-decoration: none;
                display: inline-block;
                font-size: 14px;
            }

            .btn-primary {
                background: #3498db;
                color: white;
            }

            .btn-success {
                background: #27ae60;
                color: white;
            }

            .btn-warning {
                background: #f39c12;
                color: white;
            }

            .btn-danger {
                background: #e74c3c;
                color: white;
            }

            .product-table {
                background: white;
                border-radius: 8px;
                overflow: hidden;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .product-table table {
                width: 100%;
                border-collapse: collapse;
            }

            .product-table th,
            .product-table td {
                padding: 12px 15px;
                text-align: left;
                border-bottom: 1px solid #eee;
            }

            .product-table th {
                background: #f8f9fa;
                font-weight: 600;
                color: #333;
            }

            .product-table tr:hover {
                background: #f8f9fa;
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
        </style>
    </head>
    <body>
        <div class="header">
            <div class="header-content">
                <h1>Quản lý sản phẩm</h1>
                <div class="nav-links">
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

            <!-- Nút thêm sản phẩm -->
            <div class="admin-actions">
                <a href="${pageContext.request.contextPath}/admin?action=add" class="btn btn-success">
                    + Thêm sản phẩm mới
                </a>
            </div>

            <!-- Bảng sản phẩm -->
            <div class="product-table">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên sản phẩm</th>
                            <th>Mã danh mục</th>
                            <th>Đơn vị tính</th>
                            <th>Giá bán</th>
                            <th>Hình ảnh</th>
                            <th>Mô tả</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="product" items="${products}">
                            <tr>
                                <td>${product.maLoaiCa}</td>
                                <td>${product.tenLoaiCa}</td>
                                <td>
                                    <c:if test="${not empty product.danhMuc}">
                                        ${product.danhMuc.maDanhMuc}
                                    </c:if>
                                </td>
                                <td>${product.donViTinh}</td>
                                <td>${product.giaBan} VND</td>
                                <td>

                                    <c:if test="${not empty product.hinhAnh}">
                                        <img src="${pageContext.request.contextPath}/assets/images/${product.hinhAnh}" 
                                             alt="${product.tenLoaiCa}" 
                                             style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
                                    </c:if>

                                </td>
                                <td>
                                    <c:if test="${not empty product.moTa}">
                                        ${product.moTa}
                                    </c:if>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 5px;">
                                        <a href="${pageContext.request.contextPath}/admin?action=edit&id=${product.maLoaiCa}" 
                                           class="btn btn-warning">Sửa</a>
                                        <a href="${pageContext.request.contextPath}/admin?action=delete&id=${product.maLoaiCa}" 
                                           class="btn btn-danger"
                                           onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')">Xóa</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty products}">
                            <tr>
                                <td colspan="8" style="text-align: center; padding: 20px;">
                                    Không có sản phẩm nào. 
                                    <a href="${pageContext.request.contextPath}/admin?action=add">Thêm sản phẩm mới</a>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <script>
            // Xử lý xóa sản phẩm
            document.querySelectorAll('.btn-danger').forEach(button => {
                button.addEventListener('click', function (e) {
                    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
                        e.preventDefault();
                    }
                });
            });
        </script>
    </body>
</html>