import { useState } from 'react'
import Header from './Header.jsx'
import Sidebar from './Sidebar.jsx'
import DashboardPage from '../pages/DashboardPage.jsx'
import CategoryManagement from '../pages/CategoryManagement.jsx'
import NewsManagement from '../pages/NewsManagement.jsx'
import UserManagement from '../pages/UserManagement.jsx'
import SettingsPage from '../pages/SettingsPage.jsx'

export default function AdminLayout({ user, onLogout }) {
  // Navigation bằng state: page nào đang hiển thị
  const [activePage, setActivePage] = useState('dashboard')

  const renderPage = () => {
    switch (activePage) {
      case 'category':
        return <CategoryManagement />
      case 'news':
        return <NewsManagement user={user} />
      case 'users':
        return <UserManagement user={user} />
      case 'settings':
        return <SettingsPage user={user} />
      default:
        return <DashboardPage />
    }
  }

  return (
    <div className="layout">
      <Header user={user} onLogout={onLogout} />
      <div className="layout-body">
        <Sidebar activePage={activePage} onNavigate={setActivePage} />
        <main className="content">{renderPage()}</main>
      </div>
    </div>
  )
}
