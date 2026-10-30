// Huỳnh Kim Huệ - CE191217

import { useState } from 'react'
import { useData } from '../context/DataContext'
import { ROLES } from '../data/initialData'
import ConfirmDialog from '../components/ConfirmDialog'

export default function SettingsPage({ user }) {
  const { resetData } = useData()
  const [confirming, setConfirming] = useState(false)
  const [done, setDone] = useState(false)

  return (
    <div>
      <h2 className="page-title">Settings</h2>

      <div className="panel">
        <h3 className="section-title">Tài khoản đang đăng nhập</h3>
        <dl className="info">
          <dt>Username</dt>
          <dd>{user.username}</dd>
          <dt>Role</dt>
          <dd>{ROLES[user.role]}</dd>
        </dl>
      </div>

      <div className="panel">
        <h3 className="section-title">Dữ liệu mẫu</h3>
        <p className="muted">
          Dữ liệu được lưu trong trình duyệt (localStorage). Đặt lại để quay về dữ liệu mẫu ban đầu.
        </p>
        <button className="btn btn-danger-outline" onClick={() => setConfirming(true)}>
          Đặt lại dữ liệu mẫu
        </button>
        {done && <div className="alert-info">Đã đặt lại dữ liệu mẫu.</div>}
      </div>

      {confirming && (
        <ConfirmDialog
          message="Toàn bộ thay đổi trên Category, News, Users sẽ bị xóa. Tiếp tục?"
          onConfirm={() => {
            resetData()
            setConfirming(false)
            setDone(true)
          }}
          onCancel={() => setConfirming(false)}
        />
      )}
    </div>
  )
}
