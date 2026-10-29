import { useCallback, useEffect, useState } from 'react'
import { Alert, Badge, Button, Form, Modal, Spinner, Table } from 'react-bootstrap'
import http, { errorMessage, fieldErrors } from '../api/http'
import ConfirmDialog from '../components/ConfirmDialog'
import SearchBar from '../components/SearchBar'
import TagsModal from '../components/TagsModal'

const EMPTY = {
  newsTitle: '',
  headline: '',
  newsContent: '',
  newsSource: '',
  categoryId: '',
  newsStatus: true,
  tagIds: [],
}

/** Staff: quản lý tin tức (CRUD + search, popup, confirm delete, gán tag). */
export default function NewsPage() {
  const [news, setNews] = useState([])
  const [categories, setCategories] = useState([])
  const [tags, setTags] = useState([])
  const [keyword, setKeyword] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [errors, setErrors] = useState({})
  const [formError, setFormError] = useState('')
  const [saving, setSaving] = useState(false)

  const [showTags, setShowTags] = useState(false)
  const [toDelete, setToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const params = {}
      if (keyword) params.keyword = keyword
      if (statusFilter !== '') params.status = statusFilter
      const { data } = await http.get('/news', { params })
      setNews(data)
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setLoading(false)
    }
  }, [keyword, statusFilter])

  const loadLookups = useCallback(async () => {
    try {
      const [c, t] = await Promise.all([http.get('/categories'), http.get('/tags')])
      setCategories(c.data)
      setTags(t.data)
    } catch (err) {
      setError(errorMessage(err))
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    loadLookups()
  }, [loadLookups])

  const openCreate = () => {
    setEditing(null)
    setForm(EMPTY)
    setErrors({})
    setFormError('')
    setShowForm(true)
  }

  const openEdit = (n) => {
    setEditing(n)
    setForm({
      newsTitle: n.newsTitle,
      headline: n.headline,
      newsContent: n.newsContent,
      newsSource: n.newsSource || '',
      categoryId: n.categoryId,
      newsStatus: n.newsStatus,
      tagIds: n.tags.map((t) => t.tagId),
    })
    setErrors({})
    setFormError('')
    setShowForm(true)
  }

  const validate = () => {
    const e = {}
    if (!form.newsTitle.trim()) e.newsTitle = 'Title is required'
    else if (form.newsTitle.length > 400) e.newsTitle = 'Title must be at most 400 characters'
    if (!form.headline.trim()) e.headline = 'Headline is required'
    else if (form.headline.length > 150) e.headline = 'Headline must be at most 150 characters'
    if (!form.newsContent.trim()) e.newsContent = 'Content is required'
    else if (form.newsContent.length > 4000) e.newsContent = 'Content must be at most 4000 characters'
    if (form.newsSource.length > 400) e.newsSource = 'Source must be at most 400 characters'
    if (form.categoryId === '') e.categoryId = 'Category is required'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const save = async (ev) => {
    ev.preventDefault()
    setFormError('')
    if (!validate()) return
    setSaving(true)
    const payload = {
      newsTitle: form.newsTitle.trim(),
      headline: form.headline.trim(),
      newsContent: form.newsContent.trim(),
      newsSource: form.newsSource.trim() || null,
      categoryId: Number(form.categoryId),
      newsStatus: form.newsStatus,
      tagIds: form.tagIds,
    }
    try {
      if (editing) await http.put(`/news/${editing.newsArticleId}`, payload)
      else await http.post('/news', payload)
      setShowForm(false)
      setNotice(editing ? 'News article updated.' : 'News article created.')
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
      await http.delete(`/news/${toDelete.newsArticleId}`)
      setNotice('News article deleted.')
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

  const toggleTag = (id) =>
    setForm({
      ...form,
      tagIds: form.tagIds.includes(id) ? form.tagIds.filter((x) => x !== id) : [...form.tagIds, id],
    })

  return (
    <>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="mb-0">News management</h3>
        <div className="d-flex gap-2">
          <Button variant="outline-secondary" onClick={() => setShowTags(true)}>Manage tags</Button>
          <Button onClick={openCreate}>+ New article</Button>
        </div>
      </div>
      <div className="mb-3">
        <SearchBar placeholder="Search title, headline, content" onSearch={setKeyword}>
          <Form.Select style={{ maxWidth: 160 }} value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All status</option>
            <option value="true">Active</option>
            <option value="false">Inactive</option>
          </Form.Select>
        </SearchBar>
      </div>

      {notice && <Alert variant="success" dismissible onClose={() => setNotice('')}>{notice}</Alert>}
      {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}

      {loading ? (
        <Spinner />
      ) : (
        <Table striped hover responsive>
          <thead>
            <tr>
              <th>ID</th><th>Title</th><th>Category</th><th>Tags</th><th>Status</th>
              <th>Created</th><th>By</th><th style={{ width: 160 }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {news.length === 0 && (
              <tr><td colSpan={8} className="text-center text-muted">No news found</td></tr>
            )}
            {news.map((n) => (
              <tr key={n.newsArticleId}>
                <td>{n.newsArticleId}</td>
                <td>
                  <div className="fw-semibold">{n.newsTitle}</div>
                  <small className="text-muted">{n.headline}</small>
                </td>
                <td>{n.categoryName}</td>
                <td>{n.tags.map((t) => <Badge key={t.tagId} bg="info" className="me-1">{t.tagName}</Badge>)}</td>
                <td><Badge bg={n.newsStatus ? 'success' : 'secondary'}>{n.newsStatus ? 'Active' : 'Inactive'}</Badge></td>
                <td>{new Date(n.createdDate).toLocaleString()}</td>
                <td>{n.createdByName}</td>
                <td>
                  <Button size="sm" variant="outline-primary" className="me-2" onClick={() => openEdit(n)}>Edit</Button>
                  <Button size="sm" variant="outline-danger" onClick={() => setToDelete(n)}>Delete</Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      <Modal show={showForm} onHide={() => setShowForm(false)} size="lg" centered scrollable>
        <Form onSubmit={save} noValidate>
          <Modal.Header closeButton>
            <Modal.Title>{editing ? 'Edit news article' : 'New news article'}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {formError && <Alert variant="danger">{formError}</Alert>}
            <Form.Group className="mb-3">
              <Form.Label>Title</Form.Label>
              <Form.Control value={form.newsTitle} onChange={set('newsTitle')} isInvalid={!!errors.newsTitle} />
              <Form.Control.Feedback type="invalid">{errors.newsTitle}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Headline</Form.Label>
              <Form.Control value={form.headline} onChange={set('headline')} isInvalid={!!errors.headline} />
              <Form.Control.Feedback type="invalid">{errors.headline}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Content</Form.Label>
              <Form.Control as="textarea" rows={6} value={form.newsContent} onChange={set('newsContent')} isInvalid={!!errors.newsContent} />
              <Form.Control.Feedback type="invalid">{errors.newsContent}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Source</Form.Label>
              <Form.Control value={form.newsSource} onChange={set('newsSource')} isInvalid={!!errors.newsSource} />
              <Form.Control.Feedback type="invalid">{errors.newsSource}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Category</Form.Label>
              <Form.Select value={form.categoryId} onChange={set('categoryId')} isInvalid={!!errors.categoryId}>
                <option value="">— Select category —</option>
                {categories
                  .filter((c) => c.isActive || c.categoryId === Number(form.categoryId))
                  .map((c) => (
                    <option key={c.categoryId} value={c.categoryId}>
                      {c.categoryName}{c.isActive ? '' : ' (inactive)'}
                    </option>
                  ))}
              </Form.Select>
              <Form.Control.Feedback type="invalid">{errors.categoryId}</Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Tags</Form.Label>
              <div>
                {tags.length === 0 && <small className="text-muted">No tags yet — use "Manage tags" to create some.</small>}
                {tags.map((t) => (
                  <Form.Check
                    inline
                    key={t.tagId}
                    type="checkbox"
                    id={`tag-${t.tagId}`}
                    label={t.tagName}
                    checked={form.tagIds.includes(t.tagId)}
                    onChange={() => toggleTag(t.tagId)}
                  />
                ))}
              </div>
            </Form.Group>
            <Form.Check
              type="switch"
              label="Active (visible to the public)"
              checked={form.newsStatus}
              onChange={(e) => setForm({ ...form, newsStatus: e.target.checked })}
            />
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowForm(false)}>Cancel</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save'}</Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <TagsModal show={showTags} onHide={() => setShowTags(false)} onChanged={loadLookups} />

      <ConfirmDialog
        show={!!toDelete}
        busy={deleting}
        message={`Delete news article "${toDelete?.newsTitle}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  )
}
