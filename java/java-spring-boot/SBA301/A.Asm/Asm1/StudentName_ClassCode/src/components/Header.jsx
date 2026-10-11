export default function Header({ user, onLogout }) {
  return (
    <header className="header">
      <div className="brand">
        <img src="/logo.svg" alt="FUNews logo" />
        <span>FUNewsManagementSystem</span>
      </div>
      <div className="header-right">
        <span className="muted-light">
          Xin chào, <strong>{user.username}</strong>
        </span>
        <button className="btn btn-ghost" onClick={onLogout}>
          Đăng xuất
        </button>
      </div>
    </header>
  )
}
