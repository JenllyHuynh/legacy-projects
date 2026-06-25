<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="${pageTitle}" />
</jsp:include>

<style>
/* Page Header */
.page-header {
    margin-bottom: 40px;
    text-align: center;
}

.page-header h1 {
    font-size: 2.5rem;
    color: #212529;
    margin-bottom: 10px;
}

.result-count {
    color: #6c757d;
    font-size: 1.1rem;
}

/* Product Controls */
.product-controls {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 40px;
    gap: 30px;
}

.filter-section h3 {
    margin-bottom: 15px;
    font-size: 1.1rem;
    color: #212529;
}

.category-filters {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

.filter-btn {
    padding: 8px 16px;
    background: #f8f9fa;
    border: 2px solid #dee2e6;
    border-radius: 20px;
    color: #6c757d;
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s;
}

.filter-btn:hover,
.filter-btn.active {
    background: #0066cc;
    color: white;
    border-color: #0066cc;
}

.sort-section {
    display: flex;
    align-items: center;
    gap: 10px;
}

.sort-section label {
    font-weight: 600;
    color: #212529;
}

.sort-section select {
    padding: 8px 12px;
    border: 1px solid #dee2e6;
    border-radius: 5px;
    background: white;
    cursor: pointer;
}

/* No Results */
.no-results {
    text-align: center;
    padding: 60px 20px;
    color: #6c757d;
}

.no-results h3 {
    margin-bottom: 10px;
    color: #495057;
}

.no-results .btn {
    margin-top: 15px;
}

/* Product Grid */
.product-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 30px;
    margin-top: 20px;
}

.product-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    position: relative;
    border: 1px solid #dee2e6;
}

.product-card:hover {
    transform: translateY(-6px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.product-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: #ff6b35;
    color: #fff;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 600;
    z-index: 1;
}

.product-image {
    height: 200px;
    background: #f8f9fa;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

.product-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
}

.product-card:hover .product-image img {
    transform: scale(1.05);
}

.product-content {
    padding: 20px;
}

.product-category {
    font-size: 0.8rem;
    color: #6c757d;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.product-name {
    font-size: 1.1rem;
    font-weight: 600;
    color: #212529;
    margin: 8px 0;
    line-height: 1.3;
}

.product-desc {
    color: #6c757d;
    font-size: 0.9rem;
    line-height: 1.4;
    margin: 10px 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.product-rating {
    display: flex;
    align-items: center;
    gap: 6px;
    margin: 10px 0;
}

.stars {
    color: #ffc107;
    font-size: 0.9rem;
}

.rating-count {
    color: #6c757d;
    font-size: 0.8rem;
}

.product-price {
    font-size: 1.4rem;
    font-weight: 700;
    color: #ff6b35;
    margin: 12px 0;
    display: flex;
    align-items: center;
    gap: 8px;
}

.product-price-old {
    font-size: 1rem;
    color: #6c757d;
    text-decoration: line-through;
}

.product-actions {
    display: flex;
    gap: 10px;
    margin-top: 15px;
}

.btn-add-cart {
    flex: 1;
    padding: 10px 16px;
    background: #0066cc;
    color: #fff;
    border: none;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    font-size: 0.9rem;
}

.btn-add-cart:hover {
    background: #00b4d8;
}

.btn-wishlist {
    width: 42px;
    height: 42px;
    background: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 6px;
    color: #6c757d;
    cursor: pointer;
    transition: all 0.3s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-wishlist:hover {
    background: #ff6b35;
    color: #fff;
    border-color: #ff6b35;
}

.btn-wishlist.active {
    background: #ff6b35;
    color: #fff;
    border-color: #ff6b35;
}

@keyframes slideInRight {
    from {
        transform: translateX(100%);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}

@keyframes slideOutRight {
    from {
        transform: translateX(0);
        opacity: 1;
    }
    to {
        transform: translateX(100%);
        opacity: 0;
    }
}

/* Responsive */
@media (max-width: 768px) {
    .product-controls {
        flex-direction: column;
    }
    
    .page-header h1 {
        font-size: 2rem;
    }
    
    .product-grid {
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 20px;
    }
}

@media (max-width: 576px) {
    .product-grid {
        grid-template-columns: 1fr;
    }
    
    .category-filters {
        justify-content: center;
    }
}
</style>

<div class="container" style="padding: 40px 0;">

    <!-- Page Header -->
    <div class="page-header">
        <h1>
            <c:choose>
                <c:when test="${not empty currentCategory}">
                    ${currentCategory.tenDanhMuc}
                </c:when>
                <c:when test="${not empty keyword}">
                    Tìm kiếm: "${keyword}"
                </c:when>
                <c:otherwise>
                    Tất cả sản phẩm
                </c:otherwise>
            </c:choose>
        </h1>
        <c:if test="${not empty resultCount}">
            <p class="result-count">Tìm thấy ${resultCount} sản phẩm</p>
        </c:if>
    </div>

    <!-- Filter & Sort Section -->
    <div class="product-controls">
        <div class="filter-section">
            <h3>Lọc theo danh mục</h3>
            <div class="category-filters">
                <a href="${pageContext.request.contextPath}/product" 
                   class="filter-btn ${empty param.category ? 'active' : ''}">
                    Tất cả
                </a>
                <c:forEach var="category" items="${categories}">
                    <a href="${pageContext.request.contextPath}/product?category=${category.maDanhMuc}" 
                       class="filter-btn ${param.category == category.maDanhMuc ? 'active' : ''}">
                        ${category.tenDanhMuc}
                    </a>
                </c:forEach>
            </div>
        </div>

        <div class="sort-section">
            <label for="sortSelect">Sắp xếp:</label>
            <select id="sortSelect" onchange="sortProducts(this.value)">
                <option value="price_asc">Giá: Thấp đến cao</option>
                <option value="price_desc">Giá: Cao đến thấp</option>
                <option value="name">Tên A-Z</option>
            </select>
        </div>
    </div>

    <!-- Products Grid -->
    <div class="product-grid" id="productGrid">
        <c:choose>
            <c:when test="${not empty products}">
                <c:forEach var="product" items="${products}">
                    <div class="product-card" 
                         data-price="${product.giaBan}" 
                         data-name="${product.tenLoaiCa}" 
                         data-date="${product.ngayTao.time}">
                        
                        <!-- Badge -->
                        <div class="product-badge">
                            <c:choose>
                                <c:when test="${product.giaBan < 50000}">Giá tốt</c:when>
                                <c:otherwise>Mới</c:otherwise>
                            </c:choose>
                        </div>
                        
                        <div class="product-image">
                            <a href="#">
                                <img src="${pageContext.request.contextPath}/assets/images/${product.hinhAnh}" 
                                     alt="${product.tenLoaiCa}" 
                                     onerror="this.src='${pageContext.request.contextPath}/assets/images/default.jpg'">
                            </a>
                        </div>
                        
                        <div class="product-content">
                            <span class="product-category">${product.danhMuc.tenDanhMuc}</span>
                            <h3 class="product-name">${product.tenLoaiCa}</h3>
                            
                            <div class="product-rating">
                                <div class="stars">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star-half-alt"></i>
                                </div>
                                <span class="rating-count">(${product.maLoaiCa * 15})</span>
                            </div>
                            
                            <c:if test="${not empty product.moTa}">
                                <p class="product-desc">${product.moTa}</p>
                            </c:if>
                            
                            <div class="product-price">
                                <fmt:formatNumber value="${product.giaBan}" pattern="#,###"/>₫
                                <c:if test="${product.giaBan > 100000}">
                                    <span class="product-price-old">
                                        <fmt:formatNumber value="${product.giaBan * 1.2}" pattern="#,###"/>₫
                                    </span>
                                </c:if>
                            </div>
                            
                            <div class="product-actions">
                                <button class="btn-add-cart" 
                                        data-product-id="${product.maLoaiCa}" 
                                        onclick="addToCartFromProductPage(this)">
                                    <i class="fas fa-cart-plus"></i> Thêm giỏ hàng
                                </button>
                                <button class="btn-wishlist" 
                                        data-product-id="${product.maLoaiCa}" 
                                        onclick="toggleWishlist(this)">
                                    <i class="far fa-heart"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            
            <c:otherwise>
                <div class="no-results">
                    <i class="fas fa-fish" style="font-size: 3rem; color: #6c757d; margin-bottom: 1rem;"></i>
                    <h3>Không có sản phẩm nào</h3>
                    <p>
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                Không tìm thấy sản phẩm nào phù hợp với từ khóa "<strong>${keyword}</strong>"
                            </c:when>
                            <c:when test="${not empty currentCategory}">
                                Hiện tại chưa có sản phẩm trong danh mục "${currentCategory.tenDanhMuc}"
                            </c:when>
                            <c:otherwise>
                                Hiện tại chưa có sản phẩm nào trong cửa hàng
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                        <i class="fas fa-home"></i> Về trang chủ
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
// ===== THÊM VÀO GIỎ HÀNG (KHÔNG DÙNG TEMPLATE LITERALS) =====
function addToCartFromProductPage(button) {
    var productId = button.getAttribute('data-product-id');
    var contextPath = '${pageContext.request.contextPath}';
    
    button.disabled = true;
    button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang thêm...';
    
    fetch(contextPath + '/cart?action=add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'productId=' + productId + '&quantity=1'
    })
    .then(function(response) {
        return response.json();
    })
    .then(function(data) {
        if(data.success) {
            var cartCountEl = document.getElementById('cart-count');
            if (cartCountEl) {
                cartCountEl.textContent = data.cartCount;
            }
            
            button.innerHTML = '<i class="fas fa-check"></i> Đã thêm!';
            button.style.background = '#28a745';
            
            showNotification('Đã thêm vào giỏ hàng!', 'success');
            
            setTimeout(function() {
                button.innerHTML = '<i class="fas fa-cart-plus"></i> Thêm giỏ hàng';
                button.style.background = '#0066cc';
                button.disabled = false;
            }, 2000);
        } else {
            button.innerHTML = '<i class="fas fa-cart-plus"></i> Thêm giỏ hàng';
            button.disabled = false;
            showNotification('Lỗi: ' + data.message, 'error');
        }
    })
    .catch(function(error) {
        console.error('Error:', error);
        button.innerHTML = '<i class="fas fa-cart-plus"></i> Thêm giỏ hàng';
        button.disabled = false;
        showNotification('Có lỗi xảy ra khi thêm vào giỏ hàng', 'error');
    });
}

// ===== SORTING FUNCTION =====
function sortProducts(sortBy) {
    var productGrid = document.getElementById('productGrid');
    var products = Array.from(productGrid.getElementsByClassName('product-card'));
    
    products.sort(function(a, b) {
        switch(sortBy) {
            case 'price_asc':
                return parseFloat(a.dataset.price) - parseFloat(b.dataset.price);
            case 'price_desc':
                return parseFloat(b.dataset.price) - parseFloat(a.dataset.price);
            case 'name':
            default:
                return a.dataset.name.localeCompare(b.dataset.name, 'vi');
//            default:
//                return parseInt(b.dataset.date) - parseInt(a.dataset.date);
        }
    });
    
    productGrid.innerHTML = '';
    products.forEach(function(product) {
        productGrid.appendChild(product);
    });
}

// ===== WISHLIST FUNCTION =====
function toggleWishlist(button) {
    var productId = button.getAttribute('data-product-id');
    var icon = button.querySelector('i');
    
    if (icon.classList.contains('far')) {
        icon.classList.remove('far');
        icon.classList.add('fas');
        button.classList.add('active');
        showNotification('Đã thêm vào yêu thích!', 'success');
        
        var wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
        if (wishlist.indexOf(productId) === -1) {
            wishlist.push(productId);
            localStorage.setItem('wishlist', JSON.stringify(wishlist));
        }
    } else {
        icon.classList.remove('fas');
        icon.classList.add('far');
        button.classList.remove('active');
        showNotification('Đã xóa khỏi yêu thích!', 'info');
        
        var wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
        wishlist = wishlist.filter(function(id) {
            return id !== productId;
        });
        localStorage.setItem('wishlist', JSON.stringify(wishlist));
    }
}

// ===== NOTIFICATION FUNCTION (KHÔNG DÙNG TEMPLATE LITERALS) =====
function showNotification(message, type) {
    var notification = document.createElement('div');
    notification.style.cssText = 
        'position: fixed;' +
        'top: 20px;' +
        'right: 20px;' +
        'padding: 15px 20px;' +
        'border-radius: 8px;' +
        'color: white;' +
        'z-index: 10000;' +
        'font-weight: 600;' +
        'box-shadow: 0 4px 12px rgba(0,0,0,0.2);' +
        'animation: slideInRight 0.3s ease;';
    
    if (type === 'success') {
        notification.style.background = '#28a745';
    } else if (type === 'error') {
        notification.style.background = '#dc3545';
    } else {
        notification.style.background = '#17a2b8';
    }
    
    var iconClass = type === 'success' ? 'check-circle' : 
                    type === 'error' ? 'exclamation-circle' : 'info-circle';
    
    notification.innerHTML = '<i class="fas fa-' + iconClass + '"></i> ' + message;
    document.body.appendChild(notification);
    
    setTimeout(function() {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(function() {
            notification.remove();
        }, 300);
    }, 3000);
}

// ===== KHÔI PHỤC WISHLIST KHI LOAD TRANG =====
document.addEventListener('DOMContentLoaded', function() {
    var wishlist = JSON.parse(localStorage.getItem('wishlist') || '[]');
    
    wishlist.forEach(function(productId) {
        var button = document.querySelector('.btn-wishlist[data-product-id="' + productId + '"]');
        if (button) {
            var icon = button.querySelector('i');
            icon.classList.remove('far');
            icon.classList.add('fas');
            button.classList.add('active');
        }
    });
});
</script>

<jsp:include page="../common/footer.jsp" />