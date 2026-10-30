// Huỳnh Kim Huệ - CE191217

import { useState } from 'react'
import { useData } from '../context/DataContext'
import Modal from '../components/Modal'
import ConfirmDialog from '../components/ConfirmDialog'
import SearchBar from '../components/SearchBar'
import StatusBadge from '../components/StatusBadge'

function NewsForm({ initial, categories, onSubmit, onCancel }) {
  const [title, setTitle] = useState(initial?.title ?? '')
  const [content, setContent] = useState(initial?.content ?? '')
  const [categoryId, setCategoryId] = useState(initial?.categoryId ?? '')
  const [status, setStatus] = useState(initial?.status ?? 1)
  const [tags, setTags] = useState(initial?.tags ?? '')
  const [error, setError] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!title.trim()) return setError('Tiêu đề không được để trống.')
    if (!content.trim()) return setError('Nội dung không được để trống.')
    if (!categoryId) return setError('Vui lòng chọn danh mục.')
    onSubmit({
      title: title.trim(),
      content: content.trim(),
      categoryId: Number(categoryId),
      status,
      tags: tags.trim(),
    })
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <label>
        Tiêu đề
        <input value={title} onChange={(e) => setTitle(e.target.value)} autoFocus />
      </label>
      <label>
        Nội dung
        <textarea rows={4} value={content} onChange={(e) => setContent(e.target.value)} />
      </label>
      <div className="row">
        <label>
          Danh mục
          <select value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
            <option value="">-- Chọn danh mục --</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Trạng thái
          <select value={status} onChange={(e) => setStatus(Number(e.target.value))}>
            <option value={1}>Active</option>
            <option value={0}>Inactive</option>
          </select>
        </label>
      </div>
      <label>
        Tags (phân cách bằng dấu phẩy)
        <input value={tags} onChange={(e) => setTags(e.target.value)} />
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

export default function NewsManagement({ user }) {
  const { news, categories, newsCrud } = useData()
  const [keyword, setKeyword] = useState('')
  const [dialog, setDialog] = useState(null)
  const [deleting, setDeleting] = useState(null)
  const [notice, setNotice] = useState('')

  const categoryName = (id) => categories.find((c) => c.id === id)?.name ?? '(không rõ)'

  // Search theo title và content; luôn lọc từ danh sách gốc `news`
  const kw = keyword.trim().toLowerCase()
  const displayed = news.filter(
    (n) => n.title.toLowerCase().includes(kw) || n.content.toLowerCase().includes(kw),
  )

  const handleSubmit = (values) => {
    if (dialog.mode === 'create') {
      newsCrud.add({
        ...values,
        createdBy: user.username,
        createdAt: new Date().toISOString().slice(0, 10),
      })
      setNotice('Đã thêm tin.')
    } else {
      newsCrud.update(dialog.item.id, values)
      setNotice('Đã cập nhật tin.')
    }
    setDialog(null)
  }

  const handleDelete = () => {
    newsCrud.remove(deleting.id)
    setDeleting(null)
    setNotice('Đã xóa tin.')
  }

  return (
    <div>
      <div className="page-head">
        <h2 className="page-title">News Management</h2>
        <button className="btn btn-primary" onClick={() => setDialog({ mode: 'create', item: null })}>
          + Thêm tin
        </button>
      </div>

      <SearchBar value={keyword} onChange={setKeyword} placeholder="Tìm theo tiêu đề hoặc nội dung..." />
      {notice && (
        <div className="alert-info" onClick={() => setNotice('')}>
          {notice}
        </div>
      )}

      <div className="table-wrap">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tiêu đề</th>
              <th>Danh mục</th>
              <th>Người tạo</th>
              <th>Ngày tạo</th>
              <th>Tags</th>
              <th>Trạng thái</th>
              <th className="col-actions">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {displayed.length === 0 ? (
              <tr>
                <td colSpan={8} className="empty">
                  {news.length === 0 ? 'Chưa có tin nào.' : 'Không tìm thấy kết quả phù hợp.'}
                </td>
              </tr>
            ) : (
              displayed.map((n) => (
                <tr key={n.id}>
                  <td>{n.id}</td>
                  <td>{n.title}</td>
                  <td>{categoryName(n.categoryId)}</td>
                  <td>{n.createdBy}</td>
                  <td>{n.createdAt}</td>
                  <td>{n.tags}</td>
                  <td>
                    <StatusBadge status={n.status} />
                  </td>
                  <td className="col-actions">
                    <button className="btn btn-sm" onClick={() => setDialog({ mode: 'update', item: n })}>
                      Sửa
                    </button>
                    <button className="btn btn-sm btn-danger-outline" onClick={() => setDeleting(n)}>
                      Xóa
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {dialog && (
        <Modal title={dialog.mode === 'create' ? 'Thêm tin' : 'Sửa tin'} onClose={() => setDialog(null)}>
          <NewsForm
            initial={dialog.item}
            categories={categories}
            onSubmit={handleSubmit}
            onCancel={() => setDialog(null)}
          />
        </Modal>
      )}

      {deleting && (
        <ConfirmDialog
          message={`Bạn có chắc muốn xóa tin "${deleting.title}"?`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  )
}
