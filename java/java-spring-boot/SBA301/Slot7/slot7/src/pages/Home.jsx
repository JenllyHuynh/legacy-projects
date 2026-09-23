import { Button, Container } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

function Home() {
    const navigate = useNavigate();

    return (
        <Container className="py-5">
            <div className="text-center">
                <h1>Campus Event Navigator</h1>
                <p className="lead">
                    Explore campus events and find useful activities for students.
                </p>

                <Button onClick={() => navigate("/events")}>
                    Explore Events
                </Button>
            </div>
        </Container>
    );
}

export default Home;