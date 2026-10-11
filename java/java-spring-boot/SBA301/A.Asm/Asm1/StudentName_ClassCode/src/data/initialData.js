// Quy ước: Role Admin = 1, Staff = 2 | Status active = 1, inactive = 0

export const ROLES = { 1: 'Admin', 2: 'Staff' }
export const STATUS = { 1: 'Active', 0: 'Inactive' }

export const initialCategories = [
  { id: 1, name: 'Technology', status: 1 },
  { id: 2, name: 'Sports', status: 1 },
  { id: 3, name: 'Education', status: 1 },
  { id: 4, name: 'Entertainment', status: 0 },
]

export const initialUsers = [
  { id: 1, username: 'Admin', password: 'Admin', email: 'admin@funews.edu.vn', role: 1 },
  { id: 2, username: 'staff01', password: '123456', email: 'staff01@funews.edu.vn', role: 2 },
  { id: 3, username: 'staff02', password: '123456', email: 'staff02@funews.edu.vn', role: 2 },
]

export const initialNews = [
  {
    id: 1,
    title: 'FPT University mở đăng ký hội thảo AI 2026',
    content: 'Hội thảo quy tụ nhiều chuyên gia trong và ngoài nước chia sẻ về xu hướng trí tuệ nhân tạo.',
    categoryId: 1,
    createdBy: 'Admin',
    status: 1,
    tags: 'ai, hội thảo',
    createdAt: '2026-09-15',
  },
  {
    id: 2,
    title: 'Giải bóng đá sinh viên khởi tranh cuối tuần này',
    content: 'Hơn 20 đội bóng đến từ các khoa sẽ tranh tài trong giải đấu thường niên.',
    categoryId: 2,
    createdBy: 'staff01',
    status: 1,
    tags: 'thể thao, sinh viên',
    createdAt: '2026-09-20',
  },
  {
    id: 3,
    title: 'Thông báo lịch thi học kỳ mới',
    content: 'Sinh viên xem lịch thi chi tiết trên cổng thông tin trước ngày 30 tháng này.',
    categoryId: 3,
    createdBy: 'staff02',
    status: 1,
    tags: 'lịch thi',
    createdAt: '2026-09-28',
  },
  {
    id: 4,
    title: 'Đêm nhạc chào tân sinh viên',
    content: 'Chương trình văn nghệ diễn ra tại hội trường lớn, mở cửa tự do.',
    categoryId: 4,
    createdBy: 'Admin',
    status: 0,
    tags: 'âm nhạc',
    createdAt: '2026-10-02',
  },
]
