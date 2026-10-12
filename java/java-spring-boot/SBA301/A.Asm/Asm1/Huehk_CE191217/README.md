# FUNewsManagementSystem – Assignment 01 (ReactJS + Vite)

Frontend quản trị tin tức bằng ReactJS. Dữ liệu là mock, lưu trong `localStorage` của trình duyệt (không cần backend hay database).

---

## 1. Chuẩn bị (chỉ làm một lần)

Cài **Node.js bản LTS** (từ 18 trở lên) tại https://nodejs.org rồi kiểm tra trong terminal:

```bash
node -v
npm -v
```

Thấy hiện số phiên bản là được.

## 2. Chạy project

Giải nén file zip, mở terminal (hoặc mở thư mục trong VS Code → Terminal → New Terminal), rồi:

```bash
cd StudentName_ClassCode
npm install
npm run dev
```

- `npm install`: đọc `package.json` và cài thư viện vào thư mục `node_modules` (chỉ cần chạy lần đầu).
- `npm run dev`: bật server phát triển. Terminal sẽ in ra địa chỉ, thường là **http://localhost:5173** → mở địa chỉ đó trong trình duyệt.
- Dừng server: bấm `Ctrl + C` trong terminal.

## 3. Tài khoản đăng nhập

| Username | Password |
|----------|----------|
| Admin    | Admin    |

(Phân biệt hoa/thường. Đăng nhập sai hoặc để trống sẽ hiện thông báo lỗi.)

## 4. Đổi tên project cho đúng quy ước

Đề yêu cầu tên project là `StudentName_ClassCode`. Hãy đổi:

1. Tên thư mục: ví dụ `NguyenVanA_SE1801`.
2. Trong `package.json`, dòng `"name"` (viết thường, ví dụ `"nguyenvana_se1801"`).

## 5. Thay logo bằng logo do AI tạo

Đề yêu cầu logo tạo bằng AI. Logo hiện tại (`public/logo.svg`) chỉ là logo tạm. Cách thay:

1. Dùng công cụ AI tạo ảnh (ví dụ prompt: *"Minimal flat logo for a news management system called FUNews, newspaper icon, teal color, white background"*).
2. Lưu ảnh vào thư mục `public/` với tên `logo.png`.
3. Sửa 3 chỗ đang dùng `/logo.svg` thành `/logo.png`: `src/components/Header.jsx`, `src/components/LoginPage.jsx`, `index.html`.

## 6. Chức năng

- **Login**: validate rỗng, kiểm tra Admin/Admin, báo lỗi khi sai; giữ đăng nhập sau khi reload; có nút Đăng xuất.
- **Layout**: Header (logo, tên hệ thống, user, đăng xuất) + Sidebar 5 mục: Dashboard, Category, News, Users, Settings.
- **Dashboard**: thống kê số tin / danh mục / tài khoản và danh sách tin mới nhất.
- **Category, News, Users**: Xem danh sách, Thêm và Sửa bằng popup dialog, Xóa có hộp thoại xác nhận, Tìm kiếm, hiển thị khi danh sách rỗng hoặc không có kết quả.
- **Settings**: xem thông tin tài khoản, nút đặt lại dữ liệu mẫu.
- Ràng buộc dữ liệu:
  - Tên Category và username User không được trùng.
  - News phải chọn Category tồn tại.
  - Không xóa được Category đang có News.
  - Không xóa được tài khoản đang đăng nhập.
  - Email User phải đúng định dạng.
  - Role: 1 = Admin, 2 = Staff. Status: 1 = Active, 0 = Inactive.

## 7. Cấu trúc thư mục

```
StudentName_ClassCode/
├─ index.html
├─ package.json
├─ vite.config.js
├─ public/logo.svg               # logo
└─ src/
   ├─ main.jsx                   # điểm khởi động React
   ├─ App.jsx                    # quyết định hiện Login hay Admin (auth state)
   ├─ styles.css                 # toàn bộ CSS
   ├─ components/                # LoginPage, Header, Sidebar, AdminLayout, Modal, ConfirmDialog, SearchBar, StatusBadge
   ├─ pages/                     # Dashboard, CategoryManagement, NewsManagement, UserManagement, SettingsPage
   ├─ context/DataContext.jsx    # state dùng chung + hàm CRUD (add/update/remove)
   ├─ services/storage.js        # đọc/ghi localStorage
   └─ data/initialData.js        # dữ liệu mẫu + nhãn Role/Status
```

## 8. Lỗi thường gặp

| Vấn đề | Cách xử lý |
|--------|-----------|
| `npm: command not found` | Chưa cài Node.js → cài lại rồi mở terminal mới. |
| Chạy `npm install` báo không tìm thấy `package.json` | Đang đứng sai thư mục → `cd` vào đúng thư mục project. |
| Port 5173 đang bận | Vite tự chuyển sang port khác (xem địa chỉ trong terminal), hoặc tắt server cũ bằng `Ctrl + C`. |
| Muốn xóa hết dữ liệu đã thêm | Vào **Settings → Đặt lại dữ liệu mẫu**. |
| Build bản production | `npm run build` (kết quả trong thư mục `dist`). |

## 9. Giới hạn

- Dữ liệu chỉ lưu trong trình duyệt hiện tại (localStorage), không dùng backend/database thật.
- Chỉ tài khoản Admin/Admin đăng nhập được (mock authentication).
- Điều hướng bằng state nên đường dẫn URL không đổi khi chuyển trang.
