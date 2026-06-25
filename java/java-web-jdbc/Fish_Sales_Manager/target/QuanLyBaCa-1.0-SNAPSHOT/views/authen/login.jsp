<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Đăng nhập - FishStore</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
            <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>
        </head>

        <body>
            <main class="container py-4">
                <div class="login-container">
                    <h2>Đăng nhập</h2>

                    <c:if test="${not empty error}">
                        <div class="error">${error}</div>
                    </c:if>

                    <c:if test="${not empty success}">
                        <div class="success">${success}</div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/authen" method="POST" class="col-md-4">
                        <div class="mb-3">
                            <label for="username">Tên đăng nhập:</label>
                            <input type="text" id="username" name="username" value="${param.username}" required>
                        </div>

                        <div class="mb-3">
                            <label for="password">Mật khẩu:</label>
                            <input type="password" id="password" name="password" required>
                        </div>

                        <div class="mb-3">
                            <label>
                                <input type="checkbox" name="remember"> Ghi nhớ đăng nhập
                            </label>
                        </div>

                        <button type="submit" class="btn-login">Đăng nhập</button>
                    </form>

                    <div class="links">
                        <p>Chưa có tài khoản?
                            <a href="${pageContext.request.contextPath}/authen?action=register">Đăng ký ngay</a>
                        </p>
                        <p><a href="${pageContext.request.contextPath}/home">← Quay lại trang chủ</a></p>
                    </div>
                </div>
            </main>
        </body>

        </html>