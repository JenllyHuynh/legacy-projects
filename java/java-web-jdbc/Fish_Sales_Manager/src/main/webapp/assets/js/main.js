// LẤY CONTEXT PATH
function getContextPath() {
    const metaTag = document.querySelector('meta[name="context-path"]');
    return metaTag ? metaTag.content : '';
}
// THÊM VÀO GIỎ HÀNG
function addToCart(productId) {
    const contextPath = getContextPath();
    
    fetch(`${contextPath}/cart?action=add`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `productId=${productId}&quantity=1`
    })
    .then(response => response.json())
    .then(data => {
        if(data.success) {
            // Cập nhật số lượng giỏ hàng
            const cartCountEl = document.getElementById('cart-count');
            if (cartCountEl) {
                cartCountEl.textContent = data.cartCount;
            }
            alert('Đã thêm vào giỏ hàng!');
        } else {
            alert('Có lỗi xảy ra: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi thêm vào giỏ hàng');
    });
}

// XỬ LÝ KHI DOM ĐÃ LOAD
document.addEventListener("DOMContentLoaded", function () {
    
    // WISHLIST
    const wishBtns = document.querySelectorAll(".btn-wishlist i");
    wishBtns.forEach(icon => {
        icon.addEventListener("click", function () {
            this.classList.toggle("fas");
            this.classList.toggle("far");
            this.classList.toggle("active");
        });
    });

    // AI CHAT
    const aiInput = document.getElementById("testInput");
    const aiButton = document.getElementById("testButton");
    const aiResult = document.getElementById("testResult");

    if (aiInput && aiButton && aiResult) {
        // Hàm gửi tin nhắn AI
        function sendAIMessage() {
            const message = aiInput.value.trim();
            const contextPath = document.querySelector('meta[name="context-path"]')?.content || '';
            
            if (!message) {
                aiResult.innerHTML = '<div style="color: #dc3545; margin: 5px 0;">Vui lòng nhập câu hỏi!</div>';
                return;
            }

            // Hiển thị tin nhắn người dùng
            aiResult.innerHTML += `<div style="margin: 8px 0; padding: 8px; background: #e3f2fd; border-radius: 8px;"><b>Bạn:</b> ${message}</div>`;
            
            // Hiển thị trạng thái đang xử lý
            const loadingDiv = document.createElement('div');
            loadingDiv.id = 'ai-loading';
            loadingDiv.style.cssText = 'margin: 8px 0; padding: 8px; background: #f5f5f5; border-radius: 8px;';
            loadingDiv.innerHTML = '<b>AI:</b> <i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
            aiResult.appendChild(loadingDiv);

            // Gọi API AI
            fetch(`${contextPath}/ai-chat`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'message=' + encodeURIComponent(message)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                // Xóa loading
                const loading = document.getElementById('ai-loading');
                if (loading) {
                    loading.remove();
                }
                
                // Hiển thị phản hồi từ AI
                aiResult.innerHTML += `<div style="margin: 8px 0; padding: 8px; background: #e8f5e9; border-radius: 8px;"><b>AI:</b> ${data.response}</div>`;
            })
            .catch(error => {
                console.error('Error:', error);
                
                // Xóa loading
                const loading = document.getElementById('ai-loading');
                if (loading) {
                    loading.remove();
                }
                
                // Hiển thị lỗi
                aiResult.innerHTML += `<div style="margin: 8px 0; padding: 8px; background: #ffebee; border-radius: 8px; color: #c62828;"><b>AI:</b> Xin lỗi, có lỗi xảy ra. Vui lòng thử lại!</div>`;
            })
            .finally(() => {
                // Reset input và scroll xuống cuối
                aiInput.value = "";
                aiResult.scrollTop = aiResult.scrollHeight;
            });
        }

        // Bấm Enter để gửi
        aiInput.addEventListener("keydown", function(event) {
            if (event.key === "Enter") {
                event.preventDefault();
                sendAIMessage();
            }
        });

        // Bấm nút để gửi
        aiButton.addEventListener("click", sendAIMessage);
    }

    // MOBILE MENU
    window.toggleMobileMenu = function() {
        const navMenu = document.querySelector('.nav-menu');
        if (navMenu) {
            navMenu.classList.toggle('active');
        }
    };
});
