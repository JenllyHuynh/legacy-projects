// Huỳnh Kim Huệ - CE191217

import { useState } from 'react'
import { useData } from '../context/DataContext'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import SearchBar from '../components/SearchBar'
import StatusBadge from '../components/StatusBadge'

// Form nằm trong dialog. Vì dialog được mount mới mỗi lần mở nên form luôn sạch (create)
// hoặc được điền sẵn (update) từ prop `initial`.
function CategoryForm({ initial, categories, onSubmit, onCancel }) {
  const [name, setName] = useState(initial?.name ?? '')
  const [status, setStatus] = useState(initial?.status ?? 1)
  const [error, setError] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()
    const trimmed = name.trim()
    if (!trimmed) return setError('Tên danh mục không được để trống.')
    const duplicated = categories.some(
      (c) => c.id !== initial?.id && c.name.toLowerCase() === trimmed.toLowerCase(),
    )
    if (duplicated) return setError('Tên danh mục đã tồn tại.')
    onSubmit({ name: trimmed, status })
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <label>
        Tên danh mục
        <input value={name} onChange={(e) => setName(e.target.value)} autoFocus />
      </label>
      <label>
        Trạng thái
        <select value={status} onChange={(e) => setStatus(Number(e.target.value))}>
          <option value={1}>Active</option>
          <option value={0}>Inactive</option>
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

export default function CategoryManagement() {
  const { categories, news, categoryCrud } = useData()
  const [keyword, setKeyword] = useState('')
  const [dialog, setDialog] = useState(null) // { mode: 'create' | 'update', item }
  const [deleting, setDeleting] = useState(null)
  const [notice, setNotice] = useState('')

  // Search: lọc từ danh sách gốc, không ghi đè `categories`
  const kw = keyword.trim().toLowerCase()
  const displayed = categories.filter((c) => c.name.toLowerCase().includes(kw))

  const handleSubmit = (values) => {
    if (dialog.mode === 'create') categoryCrud.add(values)
    else categoryCrud.update(dialog.item.id, values)
    setNotice(dialog.mode === 'create' ? 'Đã thêm danh mục.' : 'Đã cập nhật danh mục.')
    setDialog(null)
  }

  const handleDelete = () => {
    const used = news.some((n) => n.categoryId === deleting.id)
    if (used) {
      setNotice(`Không thể xóa "${deleting.name}" vì đang có tin thuộc danh mục này.`)
    } else {
      categoryCrud.remove(deleting.id)
      setNotice('Đã xóa danh mục.')
    }
    setDeleting(null)
  }

  return (
    <div>
      <div className="page-head">
        <h2 className="page-title">Category Management</h2>
        <button className="btn btn-primary" onClick={() => setDialog({ mode: 'create', item: null })}>
          + Thêm danh mục
        </button>
      </div>

      <SearchBar value={keyword} onChange={setKeyword} placeholder="Tìm theo tên danh mục..." />
      {notice && (
        <div className="alert-info" onClick={() => setNotice('')}>
          {notice}
        </div>
      )}

      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên danh mục</th>
            <th>Trạng thái</th>
            <th className="col-actions">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {displayed.length === 0 ? (
            <tr>
              <td colSpan={4} className="empty">
                {categories.length === 0 ? 'Chưa có danh mục nào.' : 'Không tìm thấy kết quả phù hợp.'}
              </td>
            </tr>
          ) : (
            displayed.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>{c.name}</td>
                <td>
                  <StatusBadge status={c.status} />
                </td>
                <td className="col-actions">
                  <button className="btn btn-sm" onClick={() => setDialog({ mode: 'update', item: c })}>
                    Sửa
                  </button>
                  <button className="btn btn-sm btn-danger-outline" onClick={() => setDeleting(c)}>
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
          title={dialog.mode === 'create' ? 'Thêm danh mục' : 'Sửa danh mục'}
          onClose={() => setDialog(null)}
        >
          <CategoryForm
            initial={dialog.item}
            categories={categories}
            onSubmit={handleSubmit}
            onCancel={() => setDialog(null)}
          />
        </Modal>
      )}

      {deleting && (
        <ConfirmDialog
          message={`Bạn có chắc muốn xóa danh mục "${deleting.name}"?`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  )
}
