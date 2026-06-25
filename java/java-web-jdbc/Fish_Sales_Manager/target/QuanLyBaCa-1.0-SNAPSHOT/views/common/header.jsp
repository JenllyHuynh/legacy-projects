<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta name="context-path" content="${pageContext.request.contextPath}">
            <title>${param.title}</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/footer.css">
            <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>
        </head>

        <body>
            <!-- HEADER -->
            <header class="header">
                <!-- Top Bar -->
                <div class="header-top">
                    <div class="container">
                        <div class="header-top-content">
                            <div class="header-contact">
                                <span><i class="fas fa-phone"></i> Hotline: 1908 36</span>
                                <span><i class="fas fa-envelope"></i> support@Group8.com</span>
                            </div>
                            <div class="header-auth">
                                <c:choose>
                                    <c:when test="${empty sessionScope.username}">
                                        <a href="${pageContext.request.contextPath}/views/authen/login.jsp"
                                            class="auth-link">
                                            <i class="fas fa-user"></i> Đăng nhập
                                        </a>
                                        <span class="divider">|</span>
                                        <a href="${pageContext.request.contextPath}/views/authen/register.jsp"
                                            class="auth-link">
                                            <i class="fas fa-user-plus"></i> Đăng ký
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/views/account.jsp"
                                            class="auth-link">
                                            <i class="fas fa-user-circle"></i> Xin chào, ${sessionScope.username}
                                        </a>
                                        <c:if
                                            test="${sessionScope.role == 'Admin' || sessionScope.role == 'Nhân viên'}">
                                            <span class="divider">|</span>
                                            <a href="${pageContext.request.contextPath}/views/admin/dashboard.jsp"
                                                class="auth-link">
                                                <i class="fas fa-cog"></i> Quản trị
                                            </a>
                                        </c:if>
                                        <span class="divider">|</span>
                                        <a href="${pageContext.request.contextPath}/authen?action=logout"
                                            class="auth-link">
                                            <i class="fas fa-sign-out-alt"></i> Đăng xuất
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Main Header -->
                <div class="header-main">
                    <div class="container">
                        <div class="header-main-content">
                            <!-- Logo -->
                            <div class="logo">
                                <a href="${pageContext.request.contextPath}/views/outside/home.jsp">
                                    <i class="fas fa-fish"></i>
                                    <span class="logo-text">Fish<span class="highlight">Store</span></span>
                                </a>
                            </div>

                            <!-- Search Bar -->
                            <div class="search-box">
                                <form action="${pageContext.request.contextPath}/home" method="GET">
                                    <input type="hidden" name="action" value="search">
                                    <input type="text" name="keyword" placeholder="Tìm kiếm cá tươi sống..."
                                        value="${param.keyword}" class="search-input">
                                    <button type="submit" class="search-btn">
                                        <i class="fas fa-search"></i>
                                    </button>
                                </form>
                            </div>

                            <!-- Cart & User Actions -->
                            <div class="header-actions">
                                <a href="${pageContext.request.contextPath}/cart?action=view"
                                    class="action-item cart-icon">
                                    <i class="fas fa-shopping-cart"></i>
                                    <span class="badge" id="cart-count">
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.cartCount}">${sessionScope.cartCount}
                                            </c:when>
                                            <c:otherwise>0</c:otherwise>
                                        </c:choose>
                                    </span>
                                    <span class="action-text">Giỏ hàng</span>
                                </a>
                            </div>

                            <!-- Navigation -->
                            <nav class="navbar">
                                <div class="container">
                                    <ul class="nav-menu">
                                        <li>
                                            <a href="${pageContext.request.contextPath}/views/outside/home.jsp"
                                                class="${pageContext.request.requestURI.endsWith('home.jsp') ? 'active' : ''}">
                                                <i class="fas fa-home"></i> Trang chủ
                                            </a>
                                        </li>
                                        <li class="dropdown">
                                            <a href="${pageContext.request.contextPath}/product"
                                                class="${pageContext.request.requestURI.endsWith('product') ? 'active' : ''}">
                                                <i class="fas fa-fish"></i> Sản phẩm <i class="fas fa-chevron-down"></i>
                                            </a>
                                            <ul class="dropdown-menu">
                                                <li><a
                                                        href="${pageContext.request.contextPath}/product?category=1">Cá
                                                        nước ngọt</a></li>
                                                <li><a
                                                        href="${pageContext.request.contextPath}/product?category=2">Cá
                                                        biển</a></li>
                                                <li><a
                                                        href="${pageContext.request.contextPath}/product">Tất
                                                        cả sản phẩm</a></li>
                                            </ul>
                                        </li>
                                    </ul>
                                </div>
                            </nav>
            </header>

            <!-- Mobile Menu Toggle -->
            <button class="mobile-menu-toggle" onclick="toggleMobileMenu()">
                <i class="fas fa-bars"></i>
            </button>

            <!-- Main Content Wrapper -->
            <main class="main-content">