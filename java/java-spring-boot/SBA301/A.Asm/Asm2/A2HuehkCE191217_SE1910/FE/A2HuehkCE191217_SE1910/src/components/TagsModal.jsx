import { useCallback, useEffect, useState } from 'react'
import { Alert, Button, Form, InputGroup, Modal, Table } from 'react-bootstrap'
import http, { errorMessage } from '../api/http'
import ConfirmDialog from './ConfirmDialog'

/** Popup quản lý tag: xem, thêm, sửa, xoá (có xác nhận). */
export default function TagsModal({ show, onHide, onChanged }) {
  const [tags, setTags] = useState([])
  const [name, setName] = useState('')
  const [note, setNote] = useState('')
  const [editing, setEditing] = useState(null)
  const [error, setError] = useState('')
  const [toDelete, setToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)

  const load = useCallback(async () => {
    try {
      const { data } = await http.get('/tags')
      setTags(data)
    } catch (err) {
      setError(errorMessage(err))
    }
  }, [])

  useEffect(() => {
    if (show) {
      setError('')
      load()
    }
  }, [show, load])

  const reset = () => {
    setName('')
    setNote('')
    setEditing(null)
  }

  const save = async (ev) => {
    ev.preventDefault()
    setError('')
    if (!name.trim()) return setError('Tag name is required')
    if (name.length > 50) return setError('Tag name must be at most 50 characters')
    if (note.length > 400) return setError('Note must be at most 400 characters')
    try {
      const payload = { tagName: name.trim(), note: note.trim() || null }
      if (editing) await http.put(`/tags/${editing.tagId}`, payload)
      else await http.post('/tags', payload)
      reset()
      await load()
      onChanged?.()
    } catch (err) {
      setError(errorMessage(err))
    }
  }

  const confirmDelete = async () => {
    setDeleting(true)
    try {
      await http.delete(`/tags/${toDelete.tagId}`)
      setToDelete(null)
      await load()
      onChanged?.()
    } catch (err) {
      setError(errorMessage(err))
      setToDelete(null)
    } finally {
      setDeleting(false)
    }
  }

  return (
    <>
      <Modal show={show} onHide={onHide} centered scrollable>
        <Modal.Header closeButton>
          <Modal.Title>Manage tags</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          <Form onSubmit={save} className="mb-3">
            <InputGroup>
              <Form.Control placeholder="Tag name" value={name} onChange={(e) => setName(e.target.value)} />
              <Form.Control placeholder="Note (optional)" value={note} onChange={(e) => setNote(e.target.value)} />
              <Button type="submit">{editing ? 'Update' : 'Add'}</Button>
              {editing && <Button variant="outline-secondary" onClick={reset}>Cancel</Button>}
            </InputGroup>
          </Form>
          <Table size="sm" hover>
            <thead><tr><th>Name</th><th>Note</th><th /></tr></thead>
            <tbody>
              {tags.length === 0 && (
                <tr><td colSpan={3} className="text-center text-muted">No tags yet</td></tr>
              )}
              {tags.map((t) => (
                <tr key={t.tagId}>
                  <td>{t.tagName}</td>
                  <td>{t.note}</td>
                  <td className="text-end">
                    <Button
                      size="sm"
                      variant="link"
                      onClick={() => {
                        setEditing(t)
                        setName(t.tagName)
                        setNote(t.note || '')
                      }}
                    >
                      Edit
                    </Button>
                    <Button size="sm" variant="link" className="text-danger" onClick={() => setToDelete(t)}>Delete</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Modal.Body>
      </Modal>

      <ConfirmDialog
        show={!!toDelete}
        busy={deleting}
        message={`Delete tag "${toDelete?.tagName}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  )
}
