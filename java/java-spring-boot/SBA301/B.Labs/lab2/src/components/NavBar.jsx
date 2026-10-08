import { Container, Nav, Navbar } from 'react-bootstrap';
export default function NavBar() {
    return (
        <Navbar bg="primary" data-bs-theme="dark" expand="lg">
            <Container>
                <Navbar.Brand href="#">Orchid Gallery</Navbar.Brand>
                <Navbar.Toggle aria-controls="orchid-navbar" />
                <Navbar.Collapse id="orchid-navbar">
                    <Nav className="me-auto">
                        <Nav.Link href="#home">Home</Nav.Link>
                        <Nav.Link href="#orchids">Orchids</Nav.Link>
                        <Nav.Link href="#about">About</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
