const slides = document.querySelectorAll('.slide');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const slideCounter = document.getElementById('slideCounter');

let currentSlide = 0;
const totalSlides = slides.length;

// Biến để lưu trữ biểu đồ
let myPieChart = null;

// Hardware Modal Variables
const hardwareModal = document.getElementById('hardwareModal');
const modalImage = document.getElementById('modalImage');
const modalTitle = document.getElementById('modalTitle');
const modalDescription = document.getElementById('modalDescription');
const closeBtn = document.querySelector('.close-btn');

// Dữ liệu mô tả cho từng hardware item
const hardwareData = {
    'Board': {
        description: 'Main microcontroller: Responsible for controlling the entire system. This development board acts as the brain of the automatic irrigation system.'
    },
    'ESP32 Module': {
        description: 'WiFi module: Enables Internet connectivity, allowing data transmission and remote control commands to be received via a web server. The ESP32 provides robust wireless connectivity for the IoT system.'
    },
    'Relay Module': {
        description: 'Environmental sensors: Include temperature and air humidity sensors to monitor the surrounding conditions of the crops. These sensors provide real-time data about the cultivation environment.'
    },
    'Soil Moisture Sensor': {
        description: 'Soil moisture sensor: The most critical component, measuring the water content in the soil to determine when to activate automatic irrigation. This sensor helps prevent overwatering or underwatering of the plants.'
    },
    'Water Pump': {
        description: 'Water pump: Automatically controlled to supply water to the crops when the system detects that the soil is too dry. The pump is operated via a relay and can be adjusted for water flow rate.'
    }
};

function showSlide(slideIndex) {
    // Ẩn slide hiện tại
    slides.forEach(slide => {
        slide.classList.remove('active');
    });

    // Hiển thị slide mới
    slides[slideIndex].classList.add('active');

    // Cập nhật số trang
    slideCounter.textContent = `${slideIndex + 1} / ${totalSlides}`;

    // Vô hiệu hóa nút nếu ở slide đầu hoặc cuối
    prevBtn.disabled = slideIndex === 0;
    nextBtn.disabled = slideIndex === totalSlides - 1;

    // Tạo biểu đồ nếu slide hiện tại có canvas
    initializeChart();
}

// Hàm tạo biểu đồ
function initializeChart() {
    const chartCanvas = document.getElementById('myPieChart');
    if (!chartCanvas) return; // Nếu không có canvas thì bỏ qua

    // Kiểm tra xem slide hiện tại có chứa canvas không
    const currentSlideElement = slides[currentSlide];
    const hasChart = currentSlideElement.querySelector('#myPieChart');

    if (!hasChart) return; // Nếu slide hiện tại không có canvas thì bỏ qua

    // Dữ liệu biểu đồ
    const dataPoints = [
        { label: 'Labor force', value: 40, color: '#6b8e23' },
        { label: 'GDP', value: 18, color: '#a2b96c' },
        { label: 'GDP growth rate', value: 13.5, color: '#c9e265' }
    ];

    const labels = dataPoints.map(item => item.label);
    const values = dataPoints.map(item => item.value);
    const backgroundColors = dataPoints.map(item => item.color);
    const formattedLabels = dataPoints.map(item => `${item.label} ${item.value}`);

    const ctx = chartCanvas.getContext('2d');

    // Hủy biểu đồ cũ nếu có
    if (myPieChart) {
        myPieChart.destroy();
    }

    // Tạo biểu đồ mới
    myPieChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: formattedLabels,
            datasets: [{
                data: values,
                backgroundColor: backgroundColors,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((context.parsed * 100) / total).toFixed(1);
                            return `${context.label}: ${percentage}%`;
                        }
                    }
                }
            }
        }
    });
}

function openDBDiagram() {
    window.open('https://dbdiagram.io/d/SE1908_Group2_Auto_Watering_System_ProjectProposal-68dc8f65d2b621e422b18755', '_blank');
}

// Hardware Modal Functions - FIXED
function initializeHardwareModal() {
    console.log('Initializing hardware modal...');

    // Đảm bảo modal bắt đầu ở trạng thái ẩn
    hardwareModal.style.display = 'none';

    const hardwareCards = document.querySelectorAll('.hardware-card');
    console.log('Found hardware cards:', hardwareCards.length);

    hardwareCards.forEach((card, index) => {
        card.style.cursor = 'pointer';

        card.addEventListener('click', function (event) {
            console.log('Card clicked!');
            event.stopPropagation();

            const title = this.querySelector('h4').textContent;
            console.log('Card title:', title);

            const imgElement = this.querySelector('img');
            const imgSrc = imgElement.src;
            const imgAlt = imgElement.alt;

            console.log('Opening modal for:', title);

            // Cập nhật nội dung modal
            modalImage.src = imgSrc;
            modalImage.alt = imgAlt;
            modalTitle.textContent = title;
            modalDescription.textContent = hardwareData[title]?.description || 'Mô tả đang được cập nhật...';

            // Hiển thị modal
            hardwareModal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        });

        // Hiệu ứng hover
        card.addEventListener('mouseenter', function () {
            this.style.transform = 'translateY(-5px)';
        });

        card.addEventListener('mouseleave', function () {
            this.style.transform = 'translateY(0)';
        });
    });

    // Đóng modal khi click nút close
    closeBtn.addEventListener('click', function (event) {
        event.stopPropagation();
        closeModal();
    });

    // Đóng modal khi click outside content
    hardwareModal.addEventListener('click', function (event) {
        if (event.target === hardwareModal) {
            closeModal();
        }
    });

    // Đóng modal khi nhấn phím ESC
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && hardwareModal.style.display === 'block') {
            closeModal();
        }
    });
}

function closeModal() {
    hardwareModal.style.display = 'none';
    document.body.style.overflow = 'auto';
    console.log('Modal closed');
}
// Video Demo Functions
function initializeVideoDemo() {
    const video = document.querySelector('.demo-video');
    const videoWrapper = document.querySelector('.video-wrapper');
    const overlay = document.querySelector('.video-overlay');
    
    if (video && overlay) {
        // Play video when overlay is clicked
        overlay.addEventListener('click', function() {
            video.play();
            videoWrapper.classList.add('video-playing');
        });
        
        // Show overlay when video ends
        video.addEventListener('ended', function() {
            videoWrapper.classList.remove('video-playing');
        });
        
        // Show overlay when video is paused
        video.addEventListener('pause', function() {
            if (video.currentTime > 0 && !video.ended) {
                videoWrapper.classList.remove('video-playing');
            }
        });
    }
}
// Hàm cho video
function playVideo() {
    const video = document.querySelector('.demo-video');
    const videoWrapper = document.querySelector('.video-wrapper');
    
    if (video) {
        video.play();
        videoWrapper.classList.add('video-playing');
    }
}

function pauseVideo() {
    const video = document.querySelector('.demo-video');
    const videoWrapper = document.querySelector('.video-wrapper');
    
    if (video) {
        video.pause();
        videoWrapper.classList.remove('video-playing');
    }
}
// Sự kiện cho nút "Sau"
nextBtn.addEventListener('click', () => {
    if (currentSlide < totalSlides - 1) {
        currentSlide++;
        showSlide(currentSlide);
    }
});

// Sự kiện cho nút "Trước"
prevBtn.addEventListener('click', () => {
    if (currentSlide > 0) {
        currentSlide--;
        showSlide(currentSlide);
    }
});

// Bắt sự kiện dùng phím mũi tên trái/phải để chuyển slide
document.addEventListener('keydown', (event) => {
    if (event.key === 'ArrowRight') {
        nextBtn.click();
    } else if (event.key === 'ArrowLeft') {
        prevBtn.click();
    }
});

// Hiển thị slide đầu tiên khi tải trang và khởi tạo modal
document.addEventListener('DOMContentLoaded', function () {
    showSlide(currentSlide);
    initializeHardwareModal();
    initializeVideoDemo();
});
