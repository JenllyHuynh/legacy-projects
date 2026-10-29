import { useCallback, useEffect, useState } from 'react'
import { Alert, Badge, Button, Card, Col, Form, Modal, Row, Spinner } from 'react-bootstrap'
import http, { errorMessage } from '../api/http'
import SearchBar from '../components/SearchBar'

/** Trang công khai: xem tin active, không cần đăng nhập. */
export default function HomePage() {
  const [news, setNews] = useState([])
  const [categories, setCategories] = useState([])
  const [keyword, setKeyword] = useState('')
  const [categoryId, setCategoryId] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selected, setSelected] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const params = {}
      if (keyword) params.keyword = keyword
      if (categoryId) params.categoryId = categoryId
      const { data } = await http.get('/public/news', { params })
      setNews(data)
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setLoading(false)
    }
  }, [keyword, categoryId])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    http.get('/public/categories').then((res) => setCategories(res.data)).catch(() => {})
  }, [])

  return (
    <>
      <h3 className="mb-3">Latest news</h3>
      <div className="mb-4">
        <SearchBar placeholder="Search title, headline, content" onSearch={setKeyword}>
          <Form.Select
            style={{ maxWidth: 220 }}
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
          >
            <option value="">All categories</option>
            {categories.map((c) => (
              <option key={c.categoryId} value={c.categoryId}>{c.categoryName}</option>
            ))}
          </Form.Select>
        </SearchBar>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <Spinner />
      ) : news.length === 0 ? (
        <Alert variant="info">No news found.</Alert>
      ) : (
        <Row xs={1} md={2} lg={3} className="g-3">
          {news.map((n) => (
            <Col key={n.newsArticleId}>
              <Card className="h-100 shadow-sm">
                <Card.Body className="d-flex flex-column">
                  <Badge bg="secondary" className="align-self-start mb-2">{n.categoryName}</Badge>
                  <Card.Title>{n.newsTitle}</Card.Title>
                  <Card.Subtitle className="mb-2 text-muted">{n.headline}</Card.Subtitle>
                  <Card.Text className="flex-grow-1">
                    {n.newsContent.length > 120 ? `${n.newsContent.slice(0, 120)}...` : n.newsContent}
                  </Card.Text>
                  <div className="small text-muted mb-2">
                    {new Date(n.createdDate).toLocaleString()} · {n.createdByName}
                  </div>
                  <div className="mb-2">
                    {n.tags.map((t) => (
                      <Badge key={t.tagId} bg="info" className="me-1">#{t.tagName}</Badge>
                    ))}
                  </div>
                  <Button variant="outline-primary" size="sm" onClick={() => setSelected(n)}>Read more</Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      <Modal show={!!selected} onHide={() => setSelected(null)} size="lg" centered scrollable>
        {selected && (
          <>
            <Modal.Header closeButton>
              <Modal.Title>{selected.newsTitle}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <p className="fw-semibold">{selected.headline}</p>
              <p style={{ whiteSpace: 'pre-wrap' }}>{selected.newsContent}</p>
              <hr />
              <div className="small text-muted">
                Category: {selected.categoryName}<br />
                Source: {selected.newsSource || '—'}<br />
                By {selected.createdByName} on {new Date(selected.createdDate).toLocaleString()}
              </div>
              <div className="mt-2">
                {selected.tags.map((t) => (
                  <Badge key={t.tagId} bg="info" className="me-1">#{t.tagName}</Badge>
                ))}
              </div>
            </Modal.Body>
          </>
        )}
      </Modal>
    </>
  )
}
