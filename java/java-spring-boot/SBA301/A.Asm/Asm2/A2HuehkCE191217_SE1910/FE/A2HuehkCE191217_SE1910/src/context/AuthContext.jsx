import { createContext, useContext, useState } from 'react'
import http from '../api/http'

const AuthContext = createContext(null)

export const ROLE_ADMIN = 1
export const ROLE_STAFF = 2

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('user'))
    } catch {
      return null
    }
  })

  const login = async (email, password) => {
    const { data } = await http.post('/auth/login', { email, password })
    localStorage.setItem('token', data.token)
    const u = {
      accountId: data.accountId,
      accountName: data.accountName,
      accountEmail: data.accountEmail,
      accountRole: data.accountRole,
    }
    localStorage.setItem('user', JSON.stringify(u))
    setUser(u)
    return u
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  const updateUser = (patch) => {
    const u = { ...user, ...patch }
    localStorage.setItem('user', JSON.stringify(u))
    setUser(u)
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
