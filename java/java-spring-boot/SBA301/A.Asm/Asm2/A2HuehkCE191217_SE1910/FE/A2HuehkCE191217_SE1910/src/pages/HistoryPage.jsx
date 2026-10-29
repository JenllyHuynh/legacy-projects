import { useCallback, useEffect, useState } from 'react'
import { Alert, Badge, Spinner, Table } from 'react-bootstrap'
import http, { errorMessage } from '../api/http'
import SearchBar from '../components/SearchBar'

/** Staff: xem lịch sử tin do chính mình tạo. */
export default function HistoryPage() {
  const [news, setNews] = useState([])
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const { data } = await http.get('/news/history', { params: keyword ? { keyword } : {} })
      setNews(data)
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setLoading(false)
    }
  }, [keyword])

  useEffect(() => {
    load()
  }, [load])

  return (
    <>
      <h3 className="mb-3">News history (created by me)</h3>
      <div className="mb-3">
        <SearchBar placeholder="Search title, headline, content" onSearch={setKeyword} />
      </div>
      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <Spinner />
      ) : (
        <Table striped hover responsive>
          <thead>
            <tr>
              <th>ID</th><th>Title</th><th>Category</th><th>Status</th><th>Created</th><th>Last modified</th>
            </tr>
          </thead>
          <tbody>
            {news.length === 0 && (
              <tr><td colSpan={6} className="text-center text-muted">You have not created any news yet</td></tr>
            )}
            {news.map((n) => (
              <tr key={n.newsArticleId}>
                <td>{n.newsArticleId}</td>
                <td>
                  <div className="fw-semibold">{n.newsTitle}</div>
                  <small className="text-muted">{n.headline}</small>
                </td>
                <td>{n.categoryName}</td>
                <td><Badge bg={n.newsStatus ? 'success' : 'secondary'}>{n.newsStatus ? 'Active' : 'Inactive'}</Badge></td>
                <td>{new Date(n.createdDate).toLocaleString()}</td>
                <td>
                  {n.modifiedDate
                    ? `${new Date(n.modifiedDate).toLocaleString()}${n.updatedByName ? ` by ${n.updatedByName}` : ''}`
                    : '—'}
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </>
  )
}
