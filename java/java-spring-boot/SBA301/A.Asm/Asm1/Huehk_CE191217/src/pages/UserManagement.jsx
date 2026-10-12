import { useState } from 'react'
import { useData } from '../context/DataContext'
import { ROLES } from '../data/initialData'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import SearchBar from '../components/SearchBar'

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function UserForm({ initial, users, onSubmit, onCancel }) {
  const [username, setUsername] = useState(initial?.username ?? '')
  const [password, setPassword] = useState(initial?.password ?? '')
  const [email, setEmail] = useState(initial?.email ?? '')
  const [role, setRole] = useState(initial?.role ?? 2)
  const [error, setError] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()
    const name = username.trim()
    if (!name) return setError('Username không được để trống.')
    if (users.some((u) => u.id !== initial?.id && u.username.toLowerCase() === name.toLowerCase()))
      return setError('Username đã tồn tại.')
    if (!password.trim()) return setError('Password không được để trống.')
    if (!EMAIL_REGEX.test(email.trim())) return setError('Email không hợp lệ.')
    onSubmit({ username: name, password, email: email.trim(), role })
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <label>
        Username
        <input value={username} onChange={(e) => setUsername(e.target.value)} autoFocus />
      </label>
      <label>
        Password
        <input value={password} onChange={(e) => setPassword(e.target.value)} />
      </label>
      <label>
        Email
        <input value={email} onChange={(e) => setEmail(e.target.value)} />
      </label>
      <label>
        Role
        <select value={role} onChange={(e) => setRole(Number(e.target.value))}>
          <option value={1}>Admin</option>
          <option value={2}>Staff</option>
        </select>
      </label>
      {error && <div className="alert-error">{error}</div>}
      <div className="modal-actions">
        <button type="button" className="btn" onClick={onCancel}>
          Hủy
        </button>
        <button type="submit" className="btn btn-primary">
          Lưu
        </button>
      </div>
    </form>
  )
}

export default function UserManagement({ user }) {
  const { users, userCrud } = useData()
  const [keyword, setKeyword] = useState('')
  const [dialog, setDialog] = useState(null)
  const [deleting, setDeleting] = useState(null)
  const [notice, setNotice] = useState('')

  // Search theo username hoặc email
  const kw = keyword.trim().toLowerCase()
  const displayed = users.filter(
    (u) => u.username.toLowerCase().includes(kw) || u.email.toLowerCase().includes(kw),
  )

  const handleSubmit = (values) => {
    if (dialog.mode === 'create') {
      userCrud.add(values)
      setNotice('Đã thêm tài khoản.')
    } else {
      userCrud.update(dialog.item.id, values)
      setNotice('Đã cập nhật tài khoản.')
    }
    setDialog(null)
  }

  const handleDelete = () => {
    if (deleting.username === user.username) {
      setNotice('Không thể xóa tài khoản đang đăng nhập.')
    } else {
      userCrud.remove(deleting.id)
      setNotice('Đã xóa tài khoản.')
    }
    setDeleting(null)
  }

  return (
    <div>
      <div className="page-head">
        <h2 className="page-title">User Management</h2>
        <button className="btn btn-primary" onClick={() => setDialog({ mode: 'create', item: null })}>
          + Thêm tài khoản
        </button>
      </div>

      <SearchBar value={keyword} onChange={setKeyword} placeholder="Tìm theo username hoặc email..." />
      {notice && (
        <div className="alert-info" onClick={() => setNotice('')}>
          {notice}
        </div>
      )}

      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th className="col-actions">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {displayed.length === 0 ? (
            <tr>
              <td colSpan={5} className="empty">
                {users.length === 0 ? 'Chưa có tài khoản nào.' : 'Không tìm thấy kết quả phù hợp.'}
              </td>
            </tr>
          ) : (
            displayed.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.username}</td>
                <td>{u.email}</td>
                <td>
                  <span className={`badge ${u.role === 1 ? 'badge-admin' : 'badge-staff'}`}>
                    {ROLES[u.role]}
                  </span>
                </td>
                <td className="col-actions">
                  <button className="btn btn-sm" onClick={() => setDialog({ mode: 'update', item: u })}>
                    Sửa
                  </button>
                  <button className="btn btn-sm btn-danger-outline" onClick={() => setDeleting(u)}>
                    Xóa
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {dialog && (
        <Modal
          title={dialog.mode === 'create' ? 'Thêm tài khoản' : 'Sửa tài khoản'}
          onClose={() => setDialog(null)}
        >
          <UserForm
            initial={dialog.item}
            users={users}
            onSubmit={handleSubmit}
            onCancel={() => setDialog(null)}
          />
        </Modal>
      )}

      {deleting && (
        <ConfirmDialog
          message={`Bạn có chắc muốn xóa tài khoản "${deleting.username}"?`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  )
}
