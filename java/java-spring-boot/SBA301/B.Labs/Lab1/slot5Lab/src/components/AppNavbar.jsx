import { Container, Nav, Navbar } from "react-bootstrap";

function AppNavbar() {
    return (
        <Navbar bg="dark" data-bs-theme="dark" expand="lg">
            <Container>
                <Navbar.Brand href="#top">
                    Orchid Gallery
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="orchid-nav" />

                <Navbar.Collapse id="orchid-nav">
                    <Nav className="ms-auto">
                        <Nav.Link href="#top">Home</Nav.Link>
                        <Nav.Link href="#orchids">Orchids</Nav.Link>
                        <Nav.Link href="#about">About</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default AppNavbar;