import { useState } from 'react'

export default function LoginPage({ onLogin }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault() // chặn form reload trang

    // 1. Validate rỗng trước
    if (!username.trim() || !password.trim()) {
      setError('Vui lòng nhập đầy đủ username và password.')
      return
    }

    // 2. Kiểm tra credential mock: Admin / Admin
    if (username.trim() === 'Admin' && password === 'Admin') {
      setError('')
      onLogin({ username: 'Admin', role: 1 })
    } else {
      setError('Sai username hoặc password.')
    }
  }

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleSubmit} noValidate>
        <img src="/logo.png" alt="FUNews logo" className="login-logo" />
        <h1>FUNewsManagementSystem</h1>
        <p className="muted">Đăng nhập để quản lý tin tức</p>

        <label>
          Username
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Admin"
            autoFocus
          />
        </label>

        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Admin"
          />
        </label>

        {error && <div className="alert-error">{error}</div>}

        <button type="submit" className="btn btn-primary btn-block">
          Đăng nhập
        </button>
      </form>
    </div>
  )
}
