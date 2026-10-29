import { useState } from 'react'
import { Alert, Button, Card, Form } from 'react-bootstrap'
import { Navigate, useNavigate } from 'react-router-dom'
import { errorMessage } from '../api/http'
import { useAuth } from '../context/AuthContext'

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export default function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState({})
  const [serverError, setServerError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) return <Navigate to="/" replace />

  const validate = () => {
    const e = {}
    if (!email.trim()) e.email = 'Email is required'
    else if (!EMAIL_RE.test(email.trim())) e.email = 'Email is invalid'
    if (!password) e.password = 'Password is required'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const submit = async (ev) => {
    ev.preventDefault()
    setServerError('')
    if (!validate()) return
    setBusy(true)
    try {
      await login(email.trim(), password)
      navigate('/')
    } catch (err) {
      setServerError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="d-flex justify-content-center">
      <Card style={{ width: 420 }} className="shadow-sm">
        <Card.Body>
          <Card.Title className="mb-3">Sign in</Card.Title>
          {serverError && <Alert variant="danger">{serverError}</Alert>}
          <Form onSubmit={submit} noValidate>
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={email}
                isInvalid={!!errors.email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <Form.Control.Feedback type="invalid">{errors.email}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                value={password}
                isInvalid={!!errors.password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <Form.Control.Feedback type="invalid">{errors.password}</Form.Control.Feedback>
            </Form.Group>
            <Button type="submit" className="w-100" disabled={busy}>
              {busy ? 'Signing in...' : 'Login'}
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </div>
  )
}
