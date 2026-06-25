<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Thanh toán - FishStore" />
</jsp:include>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cart.css">
</head>

<div class="container" style="padding: 40px 0;">
    <div class="page-header">
        <h1>Thanh Toán</h1>
        <p>Hoàn tất thông tin để đặt hàng</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="checkout-container">
        <!-- Form thông tin giao hàng -->
        <div class="checkout-form">
            <form action="${pageContext.request.contextPath}/cart" method="POST" id="checkoutForm">
                <input type="hidden" name="action" value="checkout">
                
                <div class="form-section">
                    <h3><i class="fas fa-user"></i> Thông tin nhận hàng</h3>
                    
                    <div class="form-group">
                        <label for="tenNguoiNhan">Họ tên người nhận *</label>
                        <input type="text" id="tenNguoiNhan" name="tenNguoiNhan" required placeholder="Nhập họ tên đầy đủ">
                    </div>

                    <div class="form-group">
                        <label for="soDienThoaiNhan">Số điện thoại *</label>
                        <input type="tel" id="soDienThoaiNhan" name="soDienThoaiNhan" required placeholder="Nhập số điện thoại">
                    </div>

                    <div class="form-group">
                        <label for="diaChiGiaoHang">Địa chỉ giao hàng *</label>
                        <textarea id="diaChiGiaoHang" name="diaChiGiaoHang" required placeholder="Nhập địa chỉ chi tiết"></textarea>
                    </div>
                </div>

                <div class="form-section">
                    <h3><i class="fas fa-credit-card"></i> Phương thức thanh toán</h3>
                    
                    <div class="payment-methods">
                        <div class="payment-option">
                            <input type="radio" id="cod" name="phuongThucThanhToan" value="COD" checked>
                            <label for="cod">
                                <i class="fas fa-money-bill-wave"></i>
                                <div>
                                    <strong>Thanh toán khi nhận hàng (COD)</strong>
                                    <small>Trả tiền mặt khi nhận được hàng</small>
                                </div>
                            </label>
                        </div>
                        
                        <div class="payment-option">
                            <input type="radio" id="banking" name="phuongThucThanhToan" value="Chuyển khoản">
                            <label for="banking">
                                <i class="fas fa-university"></i>
                                <div>
                                    <strong>Chuyển khoản ngân hàng</strong>
                                    <small>Chuyển khoản trước qua ngân hàng</small>
                                </div>
                            </label>
                        </div>
                    </div>
                    
                    <!-- Thông tin chuyển khoản (hiển thị khi chọn phương thức này) -->
                    <div id="bankingInfo" class="banking-info" style="display: none;">
                        <div class="banking-details">
                            <h4>Thông tin chuyển khoản</h4>
                            <div class="bank-account">
                                <p><strong>Ngân hàng:</strong> ABC Bank</p>
                                <p><strong>Số tài khoản:</strong> 123456789</p>
                                <p><strong>Chủ tài khoản:</strong> FISH STORE COMPANY</p>
                                <p><strong>Nội dung chuyển khoản:</strong> <span id="transferContent">Mã đơn hàng sẽ được cập nhật</span></p>
                            </div>
                            <div class="note">
                                <small>Lưu ý: Vui lòng chuyển khoản trong vòng 24 giờ để đảm bảo đơn hàng được xử lý.</small>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h3><i class="fas fa-edit"></i> Ghi chú đơn hàng</h3>
                    <div class="form-group">
                        <textarea id="ghiChu" name="ghiChu" rows="3" placeholder="Ghi chú cho đơn hàng (không bắt buộc)"></textarea>
                    </div>
                </div>

                <div class="checkout-actions">
                    <a href="${pageContext.request.contextPath}/cart?action=view" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Quay lại giỏ hàng
                    </a>
                    <button type="submit" class="btn-submit" id="submitBtn">
                        <i class="fas fa-check"></i> Hoàn Tất Đặt Hàng
                    </button>
                </div>
            </form>
        </div>

        <!-- Tóm tắt đơn hàng -->
        <div class="order-summary">
            <div class="summary-card">
                <h3>Đơn Hàng Của Bạn</h3>
                
                <div class="order-items">
                    <c:forEach var="item" items="${cartItems}">
                        <div class="order-item">
                            <div class="item-info">
                                <span class="item-name">${item.loaiCa.tenLoaiCa}</span>
                                <span class="item-quantity">${item.soLuong} ${item.loaiCa.donViTinh}</span>
                            </div>
                            <span class="item-price">${item.giaTamTinh}₫</span>
                        </div>
                    </c:forEach>
                </div>

                <div class="summary-totals">
                    <div class="total-row">
                        <span>Tổng tiền hàng:</span>
                        <span>${tongTienHang}₫</span>
                    </div>
                    <div class="total-row">
                        <span>Phí vận chuyển:</span>
                        <span>
                            <c:choose>
                                <c:when test="${tongTienHang >= 500000}">Miễn phí</c:when>
                                <c:otherwise>30.000₫</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="total-row grand-total">
                        <strong>Tổng thanh toán:</strong>
                        <strong>${tongThanhToan}₫</strong>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Lấy các phần tử DOM cần thiết
    const codRadio = document.getElementById('cod');
    const bankingRadio = document.getElementById('banking');
    const bankingInfo = document.getElementById('bankingInfo');
    const checkoutForm = document.getElementById('checkoutForm');
    const submitBtn = document.getElementById('submitBtn');
    const transferContent = document.getElementById('transferContent');
    
    // Xử lý sự kiện khi thay đổi phương thức thanh toán
    function handlePaymentMethodChange() {
        if (bankingRadio.checked) {
            // Hiển thị thông tin chuyển khoản
            bankingInfo.style.display = 'block';
            
            // Cập nhật nội dung chuyển khoản với tên người nhận
            const customerName = document.getElementById('tenNguoiNhan').value || 'KHACHHANG';
            transferContent.textContent = `CHUYENKHOAN \${customerName.toUpperCase().replace(/\s+/g, '')}`;
        } else {
            // Ẩn thông tin chuyển khoản
            bankingInfo.style.display = 'none';
        }
    }
    
    // Gán sự kiện cho các radio button
    codRadio.addEventListener('change', handlePaymentMethodChange);
    bankingRadio.addEventListener('change', handlePaymentMethodChange);
    
    // Cập nhật nội dung chuyển khoản khi tên người nhận thay đổi
    document.getElementById('tenNguoiNhan').addEventListener('input', function() {
        if (bankingRadio.checked) {
            const customerName = this.value || 'KHACHHANG';
            transferContent.textContent = `CHUYENKHOAN \${customerName.toUpperCase().replace(/\s+/g, '')}`;
        }
    });
    
    // Xác thực form trước khi gửi
    checkoutForm.addEventListener('submit', function(e) {
        // Kiểm tra các trường bắt buộc
        const requiredFields = [
            { id: 'tenNguoiNhan', name: 'Họ tên người nhận' },
            { id: 'soDienThoaiNhan', name: 'Số điện thoại' },
            { id: 'diaChiGiaoHang', name: 'Địa chỉ giao hàng' }
        ];
        
        let isValid = true;
        let firstInvalidField = null;
        
        for (const field of requiredFields) {
            const input = document.getElementById(field.id);
            if (!input.value.trim()) {
                isValid = false;
                if (!firstInvalidField) {
                    firstInvalidField = input;
                }
                
                // Thêm lớp lỗi
                input.classList.add('error');
            } else {
                input.classList.remove('error');
            }
        }
        
        // Kiểm tra số điện thoại hợp lệ
        const phoneInput = document.getElementById('soDienThoaiNhan');
        const phoneRegex = /^(0|\+84)(\d{9,10})$/;
        if (phoneInput.value.trim() && !phoneRegex.test(phoneInput.value.trim())) {
            isValid = false;
            phoneInput.classList.add('error');
            alert('Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 chữ số, bắt đầu bằng 0 hoặc +84).');
            if (!firstInvalidField) {
                firstInvalidField = phoneInput;
            }
        }
        
        if (!isValid) {
            e.preventDefault();
            if (firstInvalidField) {
                firstInvalidField.focus();
            }
            alert('Vui lòng điền đầy đủ thông tin bắt buộc.');
            return;
        }
        
        // Xác nhận đặt hàng
        if (bankingRadio.checked) {
            const isConfirmed = confirm('Bạn đã chọn phương thức chuyển khoản ngân hàng. Vui lòng chuyển khoản theo thông tin được cung cấp. Bạn có chắc chắn muốn đặt hàng?');
            if (!isConfirmed) {
                e.preventDefault();
                return;
            }
        } else {
            const isConfirmed = confirm('Xác nhận đặt hàng?');
            if (!isConfirmed) {
                e.preventDefault();
                return;
            }
        }
        
        // Vô hiệu hóa nút submit để tránh gửi nhiều lần
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
    });
    
    // Hiệu ứng khi chọn phương thức thanh toán
    const paymentOptions = document.querySelectorAll('.payment-option');
    paymentOptions.forEach(option => {
        option.addEventListener('click', function() {
            // Xóa lớp active từ tất cả các option
            paymentOptions.forEach(opt => {
                opt.classList.remove('active');
            });
            
            // Thêm lớp active cho option được chọn
            this.classList.add('active');
            
            // Đánh dấu radio button tương ứng
            const radio = this.querySelector('input[type="radio"]');
            radio.checked = true;
            
            // Kích hoạt sự kiện change
            const event = new Event('change');
            radio.dispatchEvent(event);
        });
    });
    
    // Khởi tạo trạng thái ban đầu
    document.querySelector('.payment-option:first-child').classList.add('active');
    handlePaymentMethodChange();
});
</script>



<jsp:include page="../common/footer.jsp" />