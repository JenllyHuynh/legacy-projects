import { useState } from 'react'
import LoginPage from './components/LoginPage.jsx'
import AdminLayout from './components/AdminLayout.jsx'
import { DataProvider } from './context/DataContext.jsx'
import { loadData, saveData, removeData } from './services/storage'

const AUTH_KEY = 'fun_auth'

export default function App() {
  // Auth state: lưu cả localStorage để reload vẫn giữ đăng nhập
  const [currentUser, setCurrentUser] = useState(() => loadData(AUTH_KEY, null))

  const handleLogin = (user) => {
    saveData(AUTH_KEY, user)
    setCurrentUser(user)
  }

  const handleLogout = () => {
    removeData(AUTH_KEY)
    setCurrentUser(null)
  }

  // Conditional render: chưa login -> LoginPage; đã login -> AdminLayout
  if (!currentUser) {
    return <LoginPage onLogin={handleLogin} />
  }

  return (
    <DataProvider>
      <AdminLayout user={currentUser} onLogout={handleLogout} />
    </DataProvider>
  )
}
