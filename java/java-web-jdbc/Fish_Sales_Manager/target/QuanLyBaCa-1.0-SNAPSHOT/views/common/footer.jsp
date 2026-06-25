<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

    </main>

    <!-- FOOTER -->
    <footer class="footer">
        <!-- Footer Main -->
        <div class="footer-main">
            <div class="container">
                <div class="footer-grid">
                    <!-- Column 1: About -->
                    <div class="footer-col">
                        <h3 class="footer-title">
                            <i class="fas fa-fish"></i> Fish<span class="highlight">Store</span>
                        </h3>
                        <p class="footer-desc">
                            Chuyên cung cấp các loại cá tươi sống chất lượng cao,
                            đảm bảo an toàn vệ sinh thực phẩm. Giao hàng tận nơi
                            trong vòng 2 giờ.
                        </p>
                    </div>

                    <!-- Column 2: Quick Links -->
                    <div class="footer-col">
                        <h4 class="footer-heading">Liên kết nhanh</h4>
                        <ul class="footer-links">
                            <li><a href="${pageContext.request.contextPath}/views/outside/home.jsp">
                                <i class="fas fa-angle-right"></i> Trang chủ</a>
                            </li>
                            <li><a href="${pageContext.request.contextPath}/product">
                                <i class="fas fa-angle-right"></i> Sản phẩm</a>
                            </li>
                        </ul>
                    </div>

                    <!-- Column 3: Categories -->
                    <div class="footer-col">
                        <h4 class="footer-heading">Danh mục</h4>
                        <ul class="footer-links">
                                <li><a href="${pageContext.request.contextPath}/product?category=1">
                                <i class="fas fa-angle-right"></i> Cá nước ngọt</a>
                            </li>
                            <li><a href="${pageContext.request.contextPath}/product?category=2">
                                <i class="fas fa-angle-right"></i> Cá biển</a>
                            </li>
                            <li><a href="${pageContext.request.contextPath}/product">
                                <i class="fas fa-angle-right"></i> Xem tất cả</a>
                            </li>
                        </ul>
                    </div>

                    <!-- Column 4: Contact -->
                    <div class="footer-col">
                        <h4 class="footer-heading">Thông tin liên hệ</h4>
                        <ul class="footer-contact">
                            <li>
                                <i class="fas fa-map-marker-alt"></i>
                                <span>123 Đường Nguyễn Văn Linh, Ninh Kiều, Cần Thơ</span>
                            </li>
                            <li>
                                <i class="fas fa-phone"></i>
                                <div>
                                    <div>Hotline: <strong>1908 36</strong></div>
                                    <div>Đặt hàng: <strong>8091 63 36</strong></div>
                                </div>
                            </li>
                            <li>
                                <i class="fas fa-envelope"></i>
                                <span>support@Group8.com</span>
                            </li>
                            <li>
                                <i class="fas fa-clock"></i>
                                <div>
                                    <div>Thứ 2 - Thứ 6: 7:00 - 20:00</div>
                                    <div>Thứ 7 - CN: 7:00 - 18:00</div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <!-- Main JS -->
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
    ${param.addScripts}
</body>
</html>