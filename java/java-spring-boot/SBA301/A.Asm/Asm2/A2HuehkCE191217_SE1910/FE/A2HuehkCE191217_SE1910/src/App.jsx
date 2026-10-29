import { Navigate, Route, Routes } from 'react-router-dom'
import { Container } from 'react-bootstrap'
import AppNavbar from './components/AppNavbar'
import ProtectedRoute from './components/ProtectedRoute'
import { ROLE_ADMIN, ROLE_STAFF, useAuth } from './context/AuthContext'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import AccountsPage from './pages/AccountsPage'
import CategoriesPage from './pages/CategoriesPage'
import NewsPage from './pages/NewsPage'
import HistoryPage from './pages/HistoryPage'
import ProfilePage from './pages/ProfilePage'

function HomeRedirect() {
  const { user } = useAuth()
  if (user?.accountRole === ROLE_ADMIN) return <Navigate to="/admin/accounts" replace />
  if (user?.accountRole === ROLE_STAFF) return <Navigate to="/staff/news" replace />
  return <HomePage />
}

export default function App() {
  return (
    <>
      <AppNavbar />
      <Container className="py-4">
        <Routes>
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/news" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />

          <Route element={<ProtectedRoute role={ROLE_ADMIN} />}>
            <Route path="/admin/accounts" element={<AccountsPage />} />
          </Route>

          <Route element={<ProtectedRoute role={ROLE_STAFF} />}>
            <Route path="/staff/news" element={<NewsPage />} />
            <Route path="/staff/categories" element={<CategoriesPage />} />
            <Route path="/staff/history" element={<HistoryPage />} />
            <Route path="/staff/profile" element={<ProfilePage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Container>
    </>
  )
}
