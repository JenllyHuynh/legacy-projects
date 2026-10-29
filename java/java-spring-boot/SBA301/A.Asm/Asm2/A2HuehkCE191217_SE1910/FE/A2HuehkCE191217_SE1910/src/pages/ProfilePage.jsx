import { useEffect, useState } from 'react'
import { Alert, Button, Card, Form, Spinner } from 'react-bootstrap'
import http, { errorMessage, fieldErrors } from '../api/http'
import { useAuth } from '../context/AuthContext'

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

/** Staff: quản lý hồ sơ của chính mình. */
export default function ProfilePage() {
  const { updateUser } = useAuth()
  const [form, setForm] = useState({ accountName: '', accountEmail: '', accountPassword: '' })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  useEffect(() => {
    http
      .get('/profile')
      .then(({ data }) =>
        setForm({ accountName: data.accountName, accountEmail: data.accountEmail, accountPassword: '' }),
      )
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  const validate = () => {
    const e = {}
    if (!form.accountName.trim()) e.accountName = 'Name is required'
    else if (form.accountName.length > 100) e.accountName = 'Name must be at most 100 characters'
    if (!form.accountEmail.trim()) e.accountEmail = 'Email is required'
    else if (!EMAIL_RE.test(form.accountEmail.trim())) e.accountEmail = 'Email is invalid'
    if (form.accountPassword && (form.accountPassword.length < 6 || form.accountPassword.length > 50))
      e.accountPassword = 'Password must be 6-50 characters'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const save = async (ev) => {
    ev.preventDefault()
    setError('')
    setNotice('')
    if (!validate()) return
    setSaving(true)
    try {
      const { data } = await http.put('/profile', {
        accountName: form.accountName.trim(),
        accountEmail: form.accountEmail.trim(),
        accountPassword: form.accountPassword || null,
      })
      updateUser({ accountName: data.accountName, accountEmail: data.accountEmail })
      setForm({ ...form, accountPassword: '' })
      setNotice('Profile updated.')
    } catch (err) {
      setErrors(fieldErrors(err))
      setError(errorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  if (loading) return <Spinner />

  return (
    <Card style={{ maxWidth: 520 }} className="shadow-sm">
      <Card.Body>
        <Card.Title className="mb-3">My profile</Card.Title>
        {notice && <Alert variant="success">{notice}</Alert>}
        {error && <Alert variant="danger">{error}</Alert>}
        <Form onSubmit={save} noValidate>
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
            <Form.Label>New password <small className="text-muted">(leave blank to keep current)</small></Form.Label>
            <Form.Control type="password" value={form.accountPassword} onChange={set('accountPassword')} isInvalid={!!errors.accountPassword} />
            <Form.Control.Feedback type="invalid">{errors.accountPassword}</Form.Control.Feedback>
          </Form.Group>
          <Button type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save changes'}</Button>
        </Form>
      </Card.Body>
    </Card>
  )
}
