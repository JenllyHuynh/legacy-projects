<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Giỏ hàng - FishStore" />
</jsp:include>
<head>            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cart.css">
</head>
<div class="container" style="padding: 40px 0;">
    <div class="page-header">
        <h1>Giỏ Hàng Của Bạn</h1>
        <p>Kiểm tra và chỉnh sửa giỏ hàng trước khi thanh toán</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty cartItems && !cartItems.isEmpty()}">
            <div class="cart-container">
                <div class="cart-items">
                    <c:forEach var="item" items="${cartItems}">
                        <div class="cart-item" id="cart-item-${item.maGioHang}">
                            <div class="item-image">
                                <img src="${pageContext.request.contextPath}/assets/images/${item.loaiCa.hinhAnh}" 
                                     alt="${item.loaiCa.tenLoaiCa}"
                                     onerror="this.src='${pageContext.request.contextPath}/assets/images/default.jpg'">
                            </div>
                            <div class="item-details">
                                <h4>${item.loaiCa.tenLoaiCa}</h4>
                                <p class="item-category">${item.loaiCa.danhMuc.tenDanhMuc}</p>
                                <p class="item-price">
                                    <fmt:formatNumber value="${item.loaiCa.giaBan}" pattern="#,###"/>₫/${item.loaiCa.donViTinh}
                                </p>
                            </div>
                            <div class="item-quantity">
                                <button class="qty-btn" 
                                        data-cart-id="${item.maGioHang}" 
                                        data-current-qty="${item.soLuong}"
                                        onclick="updateQuantity(this, -0.5)">-</button>
                                <span class="qty-value" id="qty-${item.maGioHang}">
                                    <fmt:formatNumber value="${item.soLuong}" pattern="#.#"/> ${item.loaiCa.donViTinh}
                                </span>
                                <button class="qty-btn" 
                                        data-cart-id="${item.maGioHang}" 
                                        data-current-qty="${item.soLuong}"
                                        onclick="updateQuantity(this, 0.5)">+</button>
                            </div>
                            <div class="item-total" id="total-${item.maGioHang}">
                                <strong><fmt:formatNumber value="${item.giaTamTinh}" pattern="#,###"/>₫</strong>
                            </div>
                            <div class="item-actions">
                                <button class="btn-remove" 
                                        data-cart-id="${item.maGioHang}"
                                        onclick="removeFromCart(this)">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="cart-summary">
                    <div class="summary-card">
                        <h3>Tổng Thanh Toán</h3>
                        <div class="summary-row">
                            <span>Tổng tiền hàng:</span>
                            <span id="subtotal"><fmt:formatNumber value="${tongTien}" pattern="#,###"/>₫</span>
                        </div>
                        <div class="summary-row">
                            <span>Phí vận chuyển:</span>
                            <span id="shipping">
                                <c:choose>
                                    <c:when test="${tongTien >= 500000}">Miễn phí</c:when>
                                    <c:otherwise>30.000₫</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="summary-row total">
                            <strong>Tổng cộng:</strong>
                            <strong id="grandtotal">
                                <fmt:formatNumber value="${tongTien >= 500000 ? tongTien : tongTien + 30000}" pattern="#,###"/>₫
                            </strong>
                        </div>

                        <a href="${pageContext.request.contextPath}/cart?action=checkout" 
                           class="btn-checkout">
                            <i class="fas fa-credit-card"></i> Tiến Hành Thanh Toán
                        </a>

                        <a href="${pageContext.request.contextPath}/product" 
                           class="btn-continue">
                            <i class="fas fa-shopping-bag"></i> Tiếp Tục Mua Hàng
                        </a>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-cart">
                <i class="fas fa-shopping-cart" style="font-size: 4rem; color: #6c757d; margin-bottom: 1rem;"></i>
                <h3>Giỏ hàng trống</h3>
                <p>Hãy thêm một vài sản phẩm ngon lành vào giỏ hàng nào!</p>
                <a href="${pageContext.request.contextPath}/product" class="btn btn-primary">
                    <i class="fas fa-fish"></i> Mua Sắm Ngay
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
// Cập nhật số lượng
    function updateQuantity(button, change) {
        const cartId = button.getAttribute('data-cart-id');
        const currentQty = parseFloat(button.getAttribute('data-current-qty'));
        const newQty = currentQty + change;

        if (newQty <= 0) {
            alert('Số lượng phải lớn hơn 0!');
            return;
        }

        fetch('${pageContext.request.contextPath}/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=update&cartId=' + cartId + '&quantity=' + newQty
        })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi xảy ra khi cập nhật số lượng');
                });
    }

// Xóa sản phẩm khỏi giỏ hàng
    function removeFromCart(button) {
        if (!confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')) {
            return;
        }

        const cartId = button.getAttribute('data-cart-id');

        fetch('${pageContext.request.contextPath}/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=remove&cartId=' + cartId
        })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        const cartCountEl = document.getElementById('cart-count');
                        if (cartCountEl) {
                            cartCountEl.textContent = data.cartCount;
                        }
                        location.reload();
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi xảy ra khi xóa sản phẩm');
                });
    }
</script>

<jsp:include page="../common/footer.jsp" />