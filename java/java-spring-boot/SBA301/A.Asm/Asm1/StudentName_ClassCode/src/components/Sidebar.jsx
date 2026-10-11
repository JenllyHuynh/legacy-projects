// Cấu hình menu tập trung: thêm page mới chỉ cần thêm 1 dòng ở đây + AdminLayout
export const MENU_ITEMS = [
  { key: 'dashboard', label: 'Dashboard' },
  { key: 'category', label: 'Category' },
  { key: 'news', label: 'News' },
  { key: 'users', label: 'Users' },
  { key: 'settings', label: 'Settings' },
]

export default function Sidebar({ activePage, onNavigate }) {
  return (
    <nav className="sidebar">
      {MENU_ITEMS.map((item) => (
        <button
          key={item.key}
          className={`menu-item ${activePage === item.key ? 'active' : ''}`}
          onClick={() => onNavigate(item.key)}
        >
          {item.label}
        </button>
      ))}
    </nav>
  )
}
