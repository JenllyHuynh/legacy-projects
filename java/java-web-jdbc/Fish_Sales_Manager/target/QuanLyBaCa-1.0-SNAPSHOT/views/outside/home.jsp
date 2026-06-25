<%@page contentType="text/html" pageEncoding="UTF-8" %>

<%-- Include header --%>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Trang chủ - FishStore" />
</jsp:include>

<!-- HERO BANNER -->
<section class="hero-section">
    <div class="container">
        <div class="hero-content">
            <h1>CÁ TƯƠI SỐNG CHẤT LƯỢNG CAO</h1>
            <p>Giao hàng tận nơi trong 2 giờ - Đảm bảo tươi ngon 100%</p>
            <a href="${pageContext.request.contextPath}/product"
                class="btn btn-primary btn-lg">
                <i class="fas fa-shopping-bag"></i> MUA NGAY
            </a>
        </div>
    </div>
</section>

<!-- FEATURES SECTION -->
<section class="features-section">
    <div class="container">
        <div class="features-grid">
            <div class="feature-item">
                <div class="feature-icon">
                    <i class="fas fa-shipping-fast"></i>
                </div>
                <div class="feature-content">
                    <h4>Giao hàng siêu tốc</h4>
                    <p>Giao trong 2h nội thành, đảm bảo độ tươi</p>
                </div>
            </div>
            <div class="feature-item">
                <div class="feature-icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <div class="feature-content">
                    <h4>Chất lượng đảm bảo</h4>
                    <p>Kiểm tra nghiêm ngặt, an toàn vệ sinh</p>
                </div>
            </div>
            <div class="feature-item">
                <div class="feature-icon">
                    <i class="fas fa-dollar-sign"></i>
                </div>
                <div class="feature-content">
                    <h4>Giá cả hợp lý</h4>
                    <p>Giá tại kho, không qua trung gian</p>
                </div>
            </div>
            <div class="feature-item">
                <div class="feature-icon">
                    <i class="fas fa-headset"></i>
                </div>
                <div class="feature-content">
                    <h4>Hỗ trợ 24/7</h4>
                    <p>Hotline: 1908 36</p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- CATEGORIES SECTION -->
<section class="section">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Danh Mục Sản Phẩm</h2>
            <p class="section-subtitle">Khám phá các loại cá tươi sống đa dạng</p>
        </div>
        <div class="category-grid">
            <div class="category-card">
                <div class="category-image"
                    style="background: linear-gradient(135deg, #00FFCC 20%, #F0F8FF 100%);">
                    <i class="fas fa-water"></i>
                </div>
                <div class="category-content">
                    <h3 class="category-title">Cá Nước Ngọt</h3>
                    <p class="category-desc">Cá tra, cá lóc, cá diêu hồng tươi sống</p>
                    <a href="${pageContext.request.contextPath}/product?category=1"
                        class="category-link">
                        Xem ngay <i class="fas fa-arrow-right"></i>
                    </a>
                </div>
            </div>
            <div class="category-card">
                <div class="category-image"
                    style="background: linear-gradient(135deg, #0099FF 10%, #E3F2FD 100%);">
                    <i class="fas fa-umbrella-beach"></i>
                </div>
                <div class="category-content">
                    <h3 class="category-title">Cá Biển</h3>
                    <p class="category-desc">Cá thu, cá hồng, cá chim đại dương</p>
                    <a href="${pageContext.request.contextPath}/product?category=2"
                        class="category-link">
                        Xem ngay <i class="fas fa-arrow-right"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- FEATURED PRODUCT -->
<section class="section" style="background: #f8f9fa;">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Sản Phẩm Nổi Bật</h2>
            <p class="section-subtitle">Những loại cá được yêu thích nhất</p>
        </div>
        <div class="product-grid">
            <!-- Product 1 -->
            <div class="product-card">
                <div class="product-badge">Bán chạy</div>
                <div class="product-image">
                    <a href="#">
                        <img src="${pageContext.request.contextPath}/assets/images/ca_tra.jpg"
                            alt="Cá Tra Tươi Sống" />
                    </a>
                </div>
                <div class="product-content">
                    <span class="product-category">Cá nước ngọt</span>
                    <h3 class="product-name">Cá Tra</h3>
                    <div class="product-rating">
                        <div class="stars">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star-half-alt"></i>
                        </div>
                        <span class="rating-count">(128)</span>
                    </div>
                    <div class="product-price">
                        45.000₫<span class="product-price-old">55.000₫</span>
                    </div>
                    <div class="product-actions">
                        <button class="btn-add-cart" onclick="addToCart(1)">
                            <i class="fas fa-cart-plus"></i> Thêm giỏ hàng
                        </button>
                        <button class="btn-wishlist">
                            <i class="far fa-heart"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- Product 2 -->
            <div class="product-card">
                <div class="product-badge">Mới</div>
                <div class="product-image">
                    <a href="#">
                        <img src="${pageContext.request.contextPath}/assets/images/ca_thu.jpg"
                            alt="Cá Thu Biển Tươi" />
                    </a>
                </div>
                <div class="product-content">
                    <span class="product-category">Cá biển</span>
                    <h3 class="product-name">Cá Thu</h3>
                    <div class="product-rating">
                        <div class="stars">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="far fa-star"></i>
                        </div>
                        <span class="rating-count">(95)</span>
                    </div>
                    <div class="product-price">
                        95.000₫<span class="product-price-old">110.000₫</span>
                    </div>
                    <div class="product-actions">
                        <button class="btn-add-cart" onclick="addToCart(2)">
                            <i class="fas fa-cart-plus"></i> Thêm giỏ hàng
                        </button>
                        <button class="btn-wishlist">
                            <i class="far fa-heart"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- Product 3 -->
            <div class="product-card">
                <div class="product-image">
                    <a href="#">
                        <img src="${pageContext.request.contextPath}/assets/images/ca_loc.jpg"
                            alt="Cá Lóc Đồng Tươi" />
                    </a>
                </div>
                <div class="product-content">
                    <span class="product-category">Cá nước ngọt</span>
                    <h3 class="product-name">Cá Lóc</h3>
                    <div class="product-rating">
                        <div class="stars">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                        </div>
                        <span class="rating-count">(156)</span>
                    </div>
                    <div class="product-price">
                        120.000₫
                    </div>
                    <div class="product-actions">
                        <button class="btn-add-cart" onclick="addToCart(3)">
                            <i class="fas fa-cart-plus"></i> Thêm giỏ hàng
                        </button>
                        <button class="btn-wishlist">
                            <i class="far fa-heart"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- Product 4 -->
            <div class="product-card">
                <div class="product-badge">-15%</div>
                <div class="product-image">
                    <a href="#">
                        <img src="${pageContext.request.contextPath}/assets/images/ca_hoi.jpg"
                            alt="Cá Hồi Na Uy" />
                    </a>
                </div>
                <div class="product-content">
                    <span class="product-category">Hải sản cao cấp</span>
                    <h3 class="product-name">Cá Hồi Na Uy</h3>
                    <div class="product-rating">
                        <div class="stars">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star-half-alt"></i>
                        </div>
                        <span class="rating-count">(203)</span>
                    </div>
                    <div class="product-price">
                        280.000₫<span class="product-price-old">330.000₫</span>
                    </div>
                    <div class="product-actions">
                        <button class="btn-add-cart" onclick="addToCart(4)">
                            <i class="fas fa-cart-plus"></i> Thêm giỏ hàng
                        </button>
                        <button class="btn-wishlist">
                            <i class="far fa-heart"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

<div class="text-center" style="margin-top: 3rem;">
    <a href="${pageContext.request.contextPath}/product"
        class="btn btn-outline btn-lg">
        <i class="fas fa-eye"></i> Xem Tất Cả Sản Phẩm
    </a>
</div>
</section>

<!-- AI CHAT BOX -->
<div class="ai-chat-box">
    <h4>Trợ Lý Thông Minh</h4>
    <div class="ai-chat-input-row">
        <input type="text" id="testInput" class="ai-chat-input" placeholder="Nhập câu hỏi...">
        <button id="testButton" class="ai-chat-button">Gửi</button>
    </div>
    <div id="testResult" class="ai-chat-result"></div>
</div>

<%-- Include footer --%>
<jsp:include page="../common/footer.jsp" />