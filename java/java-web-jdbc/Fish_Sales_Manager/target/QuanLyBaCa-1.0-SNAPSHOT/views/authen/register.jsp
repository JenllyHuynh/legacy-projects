<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - FishStore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/register.css">
    <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>
</head>
<body class="auth-body">
    <div class="register-container">
        <h2>Đăng ký tài khoản</h2>
        
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/authen?action=register" method="POST">
            <div class="form-group">
                <label for="hoTen">Họ và tên <span class="required">*</span></label>
                <input type="text" id="hoTen" name="hoTen" value="${param.hoTen}" required>
            </div>

            <div class="form-group">
                <label for="username">Tên đăng nhập <span class="required">*</span></label>
                <input type="text" id="username" name="username" value="${param.username}" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="password">Mật khẩu <span class="required">*</span></label>
                    <input type="password" id="password" name="password" required>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Xác nhận mật khẩu <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>
            </div>

            <div class="form-group">
                <label for="email">Email <span class="required">*</span></label>
                <input type="email" id="email" name="email" value="${param.email}" required>
            </div>

            <div class="form-group">
                <label for="soDienThoai">Số điện thoại</label>
                <input type="tel" id="soDienThoai" name="soDienThoai" value="${param.soDienThoai}">
            </div>

            <button type="submit" class="btn-register">Đăng ký tài khoản</button>
        </form>

        <div class="links">
            <p>Đã có tài khoản? 
                <a href="${pageContext.request.contextPath}/authen">Đăng nhập ngay</a>
            </p>
            <p><a href="${pageContext.request.contextPath}/home">← Quay lại trang chủ</a></p>
        </div>
    </div>
</body>
</html>