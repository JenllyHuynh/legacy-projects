import { Button, Container } from "react-bootstrap";
import { Link } from "react-router-dom";

function NotFound() {
    return (
        <Container className="py-5 text-center">
            <h1>404 - Page Not Found</h1>

            <p>
                The page you are looking for does not exist.
            </p>

            <Button as={Link} to="/" variant="primary">
                Go Home
            </Button>
        </Container>
    );
}

export default NotFound;