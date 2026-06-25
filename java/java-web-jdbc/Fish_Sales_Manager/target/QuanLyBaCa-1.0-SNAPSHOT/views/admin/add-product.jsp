<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Thêm sản phẩm mới - Admin" />
</jsp:include>


<div class="container" style="padding: 40px 0;">
    <div class="admin-header">
        <h1><i class="fas fa-plus-circle"></i> Thêm sản phẩm mới</h1>
        <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline">
            <i class="fas fa-arrow-left"></i> Quay lại danh sách
        </a>
    </div>

    <!-- Thông báo -->
    <c:if test="${not empty message}">
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> ${message}
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <i class="fas fa-exclamation-circle"></i> ${error}
        </div>
    </c:if>

    <div class="product-form">
        <form action="${pageContext.request.contextPath}/admin" method="POST" id="addProductForm">
            <input type="hidden" name="action" value="add">

            <div class="form-group">
                <label for="tenLoaiCa" class="required">Tên sản phẩm</label>
                <input type="text" id="tenLoaiCa" name="tenLoaiCa" required 
                       placeholder="Nhập tên sản phẩm" class="form-control"
                       value="${param.tenLoaiCa}">
                <div class="error-message" id="tenLoaiCaError"></div>
            </div>

            <div class="form-group">
                <label for="maDanhMuc" class="required">Danh mục</label>
                <select id="maDanhMuc" name="maDanhMuc" required class="form-control">
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.maDanhMuc}" 
                                ${param.maDanhMuc == category.maDanhMuc ? 'selected' : ''}>
                            ${category.tenDanhMuc}
                        </option>
                    </c:forEach>
                </select>
                <div class="error-message" id="maDanhMucError"></div>
            </div>

            <div class="form-group">
                <label for="moTa">Mô tả sản phẩm</label>
                <textarea id="moTa" name="moTa" rows="4" 
                          placeholder="Nhập mô tả chi tiết về sản phẩm" 
                          class="form-control">${param.moTa}</textarea>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="donViTinh" class="required">Đơn vị tính</label>
                    <input type="text" id="donViTinh" name="donViTinh" required 
                           placeholder="Ví dụ: Kg, Con" value="${empty param.donViTinh ? 'Kg' : param.donViTinh}" 
                           class="form-control">
                    <div class="error-message" id="donViTinhError"></div>
                </div>

                <div class="form-group">
                    <label for="giaBan" class="required">Giá bán (VNĐ)</label>
                    <input type="number" id="giaBan" name="giaBan" required 
                           placeholder="Nhập giá bán" min="1000" step="1000" 
                           value="${param.giaBan}" class="form-control">
                    <div class="error-message" id="giaBanError"></div>
                </div>
            </div>

            <div class="form-group">
                <label for="hinhAnh">Tên file hình ảnh</label>
                <input type="text" id="hinhAnh" name="hinhAnh" 
                       placeholder="Ví dụ: ca_tra.jpg, ca_loc.jpg, ca_thu.jpg" 
                       value="${param.hinhAnh}" class="form-control">
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary" id="submitBtn">
                    <i class="fas fa-save"></i> Thêm sản phẩm
                </button>
                <button type="reset" class="btn btn-secondary">
                    <i class="fas fa-undo"></i> Nhập lại
                </button>
                <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline">
                    <i class="fas fa-times"></i> Hủy bỏ
                </a>
            </div>
        </form>
    </div>
</div>

<style>
    .admin-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 30px;
        padding-bottom: 20px;
        border-bottom: 2px solid #dee2e6;
    }

    .admin-header h1 {
        color: #2c3e50;
        margin: 0;
    }

    .product-form {
        max-width: 800px;
        margin: 0 auto;
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 2px 20px rgba(0,0,0,0.1);
    }

    .form-group {
        margin-bottom: 25px;
    }

    .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
    }

    label {
        display: block;
        margin-bottom: 8px;
        font-weight: 600;
        color: #333;
    }

    label.required::after {
        content: " *";
        color: #dc3545;
    }

    .form-control {
        width: 100%;
        padding: 12px 15px;
        border: 1px solid #ddd;
        border-radius: 6px;
        font-size: 14px;
        transition: all 0.3s;
        font-family: inherit;
    }

    .form-control:focus {
        outline: none;
        border-color: #007bff;
        box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
    }

    .form-control:invalid {
        border-color: #dc3545;
    }

    textarea.form-control {
        resize: vertical;
        min-height: 100px;
    }

    .form-text {
        display: block;
        margin-top: 5px;
        color: #6c757d;
        font-size: 12px;
    }

    .error-message {
        color: #dc3545;
        font-size: 12px;
        margin-top: 5px;
        display: none;
    }

    .form-actions {
        display: flex;
        gap: 15px;
        margin-top: 30px;
        padding-top: 20px;
        border-top: 1px solid #dee2e6;
    }

    .btn {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        padding: 12px 24px;
        border: none;
        border-radius: 6px;
        font-weight: 600;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 14px;
        font-family: inherit;
    }

    .btn-primary {
        background: #007bff;
        color: white;
    }

    .btn-primary:hover {
        background: #0056b3;
        transform: translateY(-1px);
    }

    .btn-secondary {
        background: #6c757d;
        color: white;
    }

    .btn-secondary:hover {
        background: #545b62;
    }

    .btn-outline {
        background: transparent;
        border: 2px solid #6c757d;
        color: #6c757d;
    }

    .btn-outline:hover {
        background: #6c757d;
        color: white;
    }

    .alert {
        padding: 15px 20px;
        border-radius: 6px;
        margin-bottom: 25px;
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .alert-success {
        background: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
    }

    .alert-danger {
        background: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }

    @media (max-width: 768px) {
        .form-row {
            grid-template-columns: 1fr;
        }

        .admin-header {
            flex-direction: column;
            gap: 15px;
            align-items: flex-start;
        }

        .form-actions {
            flex-direction: column;
        }

        .btn {
            justify-content: center;
        }
    }
</style>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById('addProductForm');
        const submitBtn = document.getElementById('submitBtn');

        form.addEventListener('submit', function (e) {
            if (!validateForm()) {
                e.preventDefault();
            } else {
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
                submitBtn.disabled = true;
            }
        });

        function validateForm() {
            let isValid = true;

            // Validate tên sản phẩm
            const tenLoaiCa = document.getElementById('tenLoaiCa');
            const tenLoaiCaError = document.getElementById('tenLoaiCaError');
            if (!tenLoaiCa.value.trim()) {
                tenLoaiCaError.textContent = 'Vui lòng nhập tên sản phẩm';
                tenLoaiCaError.style.display = 'block';
                isValid = false;
            } else {
                tenLoaiCaError.style.display = 'none';
            }

            // Validate danh mục
            const maDanhMuc = document.getElementById('maDanhMuc');
            const maDanhMucError = document.getElementById('maDanhMucError');
            if (!maDanhMuc.value) {
                maDanhMucError.textContent = 'Vui lòng chọn danh mục';
                maDanhMucError.style.display = 'block';
                isValid = false;
            } else {
                maDanhMucError.style.display = 'none';
            }

            // Validate đơn vị tính
            const donViTinh = document.getElementById('donViTinh');
            const donViTinhError = document.getElementById('donViTinhError');
            if (!donViTinh.value.trim()) {
                donViTinhError.textContent = 'Vui lòng nhập đơn vị tính';
                donViTinhError.style.display = 'block';
                isValid = false;
            } else {
                donViTinhError.style.display = 'none';
            }

            // Validate giá bán
            const giaBan = document.getElementById('giaBan');
            const giaBanError = document.getElementById('giaBanError');
            if (!giaBan.value || giaBan.value < 1000) {
                giaBanError.textContent = 'Giá bán phải từ 1,000 VNĐ trở lên';
                giaBanError.style.display = 'block';
                isValid = false;
            } else {
                giaBanError.style.display = 'none';
            }

            return isValid;
        }

        // Real-time validation
        const inputs = form.querySelectorAll('input[required], select[required]');
        inputs.forEach(input => {
            input.addEventListener('blur', validateForm);
            input.addEventListener('input', function () {
                const errorElement = document.getElementById(this.id + 'Error');
                if (errorElement) {
                    errorElement.style.display = 'none';
                }
            });
        });
    });
</script>

<jsp:include page="../common/footer.jsp" />