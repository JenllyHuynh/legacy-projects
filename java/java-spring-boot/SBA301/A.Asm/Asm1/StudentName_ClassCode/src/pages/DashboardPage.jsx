import { useData } from '../context/DataContext'

export default function DashboardPage() {
  const { categories, news, users } = useData()

  const activeNews = news.filter((n) => n.status === 1).length
  const activeCategories = categories.filter((c) => c.status === 1).length
  const admins = users.filter((u) => u.role === 1).length

  const stats = [
    { label: 'Tổng số tin', value: news.length, note: `${activeNews} đang hiển thị` },
    { label: 'Danh mục', value: categories.length, note: `${activeCategories} đang hoạt động` },
    { label: 'Tài khoản', value: users.length, note: `${admins} Admin, ${users.length - admins} Staff` },
  ]

  const recent = [...news].sort((a, b) => b.createdAt.localeCompare(a.createdAt)).slice(0, 5)

  return (
    <div>
      <h2 className="page-title">Dashboard</h2>
      <div className="stats">
        {stats.map((s) => (
          <div className="stat" key={s.label}>
            <div className="stat-value">{s.value}</div>
            <div className="stat-label">{s.label}</div>
            <div className="muted">{s.note}</div>
          </div>
        ))}
      </div>

      <h3 className="section-title">Tin mới nhất</h3>
      {recent.length === 0 ? (
        <div className="empty">Chưa có tin nào.</div>
      ) : (
        <ul className="recent">
          {recent.map((n) => (
            <li key={n.id}>
              <span>{n.title}</span>
              <span className="muted">{n.createdAt}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
