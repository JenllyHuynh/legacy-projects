import { useCallback, useEffect, useState } from 'react'
import { Alert, Badge, Button, Form, Modal, Spinner, Table } from 'react-bootstrap'
import http, { errorMessage, fieldErrors } from '../api/http'
import ConfirmDialog from '../components/ConfirmDialog'
import SearchBar from '../components/SearchBar'

const EMPTY = { categoryName: '', categoryDescription: '', parentCategoryId: '', isActive: true }

/** Staff: quản lý category (CRUD + search, popup, confirm delete). */
export default function CategoriesPage() {
  const [categories, setCategories] = useState([])
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [errors, setErrors] = useState({})
  const [formError, setFormError] = useState('')
  const [saving, setSaving] = useState(false)

  const [toDelete, setToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const { data } = await http.get('/categories', { params: keyword ? { keyword } : {} })
      setCategories(data)
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setLoading(false)
    }
  }, [keyword])

  useEffect(() => {
    load()
  }, [load])

  const openCreate = () => {
    setEditing(null)
    setForm(EMPTY)
    setErrors({})
    setFormError('')
    setShowForm(true)
  }

  const openEdit = (c) => {
    setEditing(c)
    setForm({
      categoryName: c.categoryName,
      categoryDescription: c.categoryDescription || '',
      parentCategoryId: c.parentCategoryId ?? '',
      isActive: c.isActive,
    })
    setErrors({})
    setFormError('')
    setShowForm(true)
  }

  const validate = () => {
    const e = {}
    if (!form.categoryName.trim()) e.categoryName = 'Category name is required'
    else if (form.categoryName.length > 100) e.categoryName = 'Category name must be at most 100 characters'
    if (!form.categoryDescription.trim()) e.categoryDescription = 'Description is required'
    else if (form.categoryDescription.length > 250) e.categoryDescription = 'Description must be at most 250 characters'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const save = async (ev) => {
    ev.preventDefault()
    setFormError('')
    if (!validate()) return
    setSaving(true)
    const payload = {
      categoryName: form.categoryName.trim(),
      categoryDescription: form.categoryDescription.trim(),
      parentCategoryId: form.parentCategoryId === '' ? null : Number(form.parentCategoryId),
      isActive: form.isActive,
    }
    try {
      if (editing) await http.put(`/categories/${editing.categoryId}`, payload)
      else await http.post('/categories', payload)
      setShowForm(false)
      setNotice(editing ? 'Category updated.' : 'Category created.')
      load()
    } catch (err) {
      setErrors(fieldErrors(err))
      setFormError(errorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const confirmDelete = async () => {
    setDeleting(true)
    try {
      await http.delete(`/categories/${toDelete.categoryId}`)
      setNotice('Category deleted.')
      setToDelete(null)
      load()
    } catch (err) {
      setError(errorMessage(err))
      setToDelete(null)
    } finally {
      setDeleting(false)
    }
  }

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  return (
    <>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="mb-0">Category management</h3>
        <Button onClick={openCreate}>+ New category</Button>
      </div>
      <div className="mb-3">
        <SearchBar placeholder="Search name or description" onSearch={setKeyword} />
      </div>

      {notice && <Alert variant="success" dismissible onClose={() => setNotice('')}>{notice}</Alert>}
      {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}

      {loading ? (
        <Spinner />
      ) : (
        <Table striped hover responsive>
          <thead>
            <tr>
              <th>ID</th><th>Name</th><th>Description</th><th>Parent</th><th>Status</th>
              <th style={{ width: 160 }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {categories.length === 0 && (
              <tr><td colSpan={6} className="text-center text-muted">No categories found</td></tr>
            )}
            {categories.map((c) => (
              <tr key={c.categoryId}>
                <td>{c.categoryId}</td>
                <td>{c.categoryName}</td>
                <td>{c.categoryDescription}</td>
                <td>{c.parentCategoryName || '—'}</td>
                <td><Badge bg={c.isActive ? 'success' : 'secondary'}>{c.isActive ? 'Active' : 'Inactive'}</Badge></td>
                <td>
                  <Button size="sm" variant="outline-primary" className="me-2" onClick={() => openEdit(c)}>Edit</Button>
                  <Button size="sm" variant="outline-danger" onClick={() => setToDelete(c)}>Delete</Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      <Modal show={showForm} onHide={() => setShowForm(false)} centered>
        <Form onSubmit={save} noValidate>
          <Modal.Header closeButton>
            <Modal.Title>{editing ? 'Edit category' : 'New category'}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {formError && <Alert variant="danger">{formError}</Alert>}
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control value={form.categoryName} onChange={set('categoryName')} isInvalid={!!errors.categoryName} />
              <Form.Control.Feedback type="invalid">{errors.categoryName}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control as="textarea" rows={3} value={form.categoryDescription} onChange={set('categoryDescription')} isInvalid={!!errors.categoryDescription} />
              <Form.Control.Feedback type="invalid">{errors.categoryDescription}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Parent category</Form.Label>
              <Form.Select value={form.parentCategoryId} onChange={set('parentCategoryId')}>
                <option value="">— None —</option>
                {categories
                  .filter((c) => !editing || c.categoryId !== editing.categoryId)
                  .map((c) => (
                    <option key={c.categoryId} value={c.categoryId}>{c.categoryName}</option>
                  ))}
              </Form.Select>
            </Form.Group>
            <Form.Check
              type="switch"
              label="Active"
              checked={form.isActive}
              onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
            />
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowForm(false)}>Cancel</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save'}</Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <ConfirmDialog
        show={!!toDelete}
        busy={deleting}
        message={`Delete category "${toDelete?.categoryName}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  )
}
