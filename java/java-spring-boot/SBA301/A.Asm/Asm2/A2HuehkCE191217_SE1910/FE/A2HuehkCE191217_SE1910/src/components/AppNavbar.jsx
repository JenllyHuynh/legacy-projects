import { Button, Container, Nav, Navbar } from 'react-bootstrap'
import { NavLink, useNavigate } from 'react-router-dom'
import { ROLE_ADMIN, ROLE_STAFF, useAuth } from '../context/AuthContext'

export default function AppNavbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <Navbar bg="dark" data-bs-theme="dark" expand="md">
      <Container>
        <Navbar.Brand as={NavLink} to="/">FU News</Navbar.Brand>
        <Navbar.Toggle aria-controls="main-nav" />
        <Navbar.Collapse id="main-nav">
          <Nav className="me-auto">
            <Nav.Link as={NavLink} to="/news">Latest news</Nav.Link>
            {user?.accountRole === ROLE_ADMIN && (
              <Nav.Link as={NavLink} to="/admin/accounts">Accounts</Nav.Link>
            )}
            {user?.accountRole === ROLE_STAFF && (
              <>
                <Nav.Link as={NavLink} to="/staff/news">News</Nav.Link>
                <Nav.Link as={NavLink} to="/staff/categories">Categories</Nav.Link>
                <Nav.Link as={NavLink} to="/staff/history">My history</Nav.Link>
                <Nav.Link as={NavLink} to="/staff/profile">Profile</Nav.Link>
              </>
            )}
          </Nav>
          <Nav className="align-items-md-center gap-2">
            {user ? (
              <>
                <span className="navbar-text">
                  {user.accountName} ({user.accountRole === ROLE_ADMIN ? 'Admin' : 'Staff'})
                </span>
                <Button size="sm" variant="outline-light" onClick={handleLogout}>Logout</Button>
              </>
            ) : (
              <Button size="sm" variant="outline-light" as={NavLink} to="/login">Staff / Admin login</Button>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}
