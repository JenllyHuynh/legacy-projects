import { useCallback, useEffect, useState } from 'react'
import { Alert, Badge, Button, Form, Modal, Spinner, Table } from 'react-bootstrap'
import http, { errorMessage, fieldErrors } from '../api/http'
import ConfirmDialog from '../components/ConfirmDialog'
import SearchBar from '../components/SearchBar'
import { ROLE_ADMIN, useAuth } from '../context/AuthContext'

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const EMPTY = { accountName: '', accountEmail: '', accountRole: 2, accountPassword: '' }

/** Admin: quản lý tài khoản (CRUD + search, popup, confirm delete). */
export default function AccountsPage() {
  const { user } = useAuth()
  const [accounts, setAccounts] = useState([])
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
      const { data } = await http.get('/accounts', { params: keyword ? { keyword } : {} })
      setAccounts(data)
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

  const openEdit = (a) => {
    setEditing(a)
    setForm({ ...a, accountPassword: '' })
    setErrors({})
    setFormError('')
    setShowForm(true)
  }

  const validate = () => {
    const e = {}
    if (!form.accountName.trim()) e.accountName = 'Name is required'
    else if (form.accountName.length > 100) e.accountName = 'Name must be at most 100 characters'
    if (!form.accountEmail.trim()) e.accountEmail = 'Email is required'
    else if (!EMAIL_RE.test(form.accountEmail.trim())) e.accountEmail = 'Email is invalid'
    if (![1, 2].includes(Number(form.accountRole))) e.accountRole = 'Role must be Admin or Staff'
    if (!editing && !form.accountPassword) e.accountPassword = 'Password is required'
    else if (form.accountPassword && (form.accountPassword.length < 6 || form.accountPassword.length > 50))
      e.accountPassword = 'Password must be 6-50 characters'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const save = async (ev) => {
    ev.preventDefault()
    setFormError('')
    if (!validate()) return
    setSaving(true)
    const payload = {
      accountName: form.accountName.trim(),
      accountEmail: form.accountEmail.trim(),
      accountRole: Number(form.accountRole),
      accountPassword: form.accountPassword || null,
    }
    try {
      if (editing) await http.put(`/accounts/${editing.accountId}`, payload)
      else await http.post('/accounts', payload)
      setShowForm(false)
      setNotice(editing ? 'Account updated.' : 'Account created.')
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
      await http.delete(`/accounts/${toDelete.accountId}`)
      setNotice('Account deleted.')
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
        <h3 className="mb-0">Account management</h3>
        <Button onClick={openCreate}>+ New account</Button>
      </div>
      <div className="mb-3">
        <SearchBar placeholder="Search name or email" onSearch={setKeyword} />
      </div>

      {notice && <Alert variant="success" dismissible onClose={() => setNotice('')}>{notice}</Alert>}
      {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}

      {loading ? (
        <Spinner />
      ) : (
        <Table striped hover responsive>
          <thead>
            <tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th style={{ width: 160 }}>Actions</th></tr>
          </thead>
          <tbody>
            {accounts.length === 0 && (
              <tr><td colSpan={5} className="text-center text-muted">No accounts found</td></tr>
            )}
            {accounts.map((a) => (
              <tr key={a.accountId}>
                <td>{a.accountId}</td>
                <td>{a.accountName}</td>
                <td>{a.accountEmail}</td>
                <td>
                  <Badge bg={a.accountRole === ROLE_ADMIN ? 'danger' : 'primary'}>
                    {a.accountRole === ROLE_ADMIN ? 'Admin' : 'Staff'}
                  </Badge>
                </td>
                <td>
                  <Button size="sm" variant="outline-primary" className="me-2" onClick={() => openEdit(a)}>Edit</Button>
                  <Button
                    size="sm"
                    variant="outline-danger"
                    disabled={a.accountId === user.accountId}
                    onClick={() => setToDelete(a)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      <Modal show={showForm} onHide={() => setShowForm(false)} centered>
        <Form onSubmit={save} noValidate>
          <Modal.Header closeButton>
            <Modal.Title>{editing ? 'Edit account' : 'New account'}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {formError && <Alert variant="danger">{formError}</Alert>}
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control value={form.accountName} onChange={set('accountName')} isInvalid={!!errors.accountName} />
              <Form.Control.Feedback type="invalid">{errors.accountName}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control type="email" value={form.accountEmail} onChange={set('accountEmail')} isInvalid={!!errors.accountEmail} />
              <Form.Control.Feedback type="invalid">{errors.accountEmail}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Role</Form.Label>
              <Form.Select value={form.accountRole} onChange={set('accountRole')} isInvalid={!!errors.accountRole}>
                <option value={1}>Admin</option>
                <option value={2}>Staff</option>
              </Form.Select>
              <Form.Control.Feedback type="invalid">{errors.accountRole}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group>
              <Form.Label>Password {editing && <small className="text-muted">(leave blank to keep current)</small>}</Form.Label>
              <Form.Control type="password" value={form.accountPassword} onChange={set('accountPassword')} isInvalid={!!errors.accountPassword} />
              <Form.Control.Feedback type="invalid">{errors.accountPassword}</Form.Control.Feedback>
            </Form.Group>
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
        message={`Delete account "${toDelete?.accountName}" (${toDelete?.accountEmail})?`}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  )
}
