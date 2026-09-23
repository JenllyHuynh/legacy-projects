import { Button, Card, Container } from "react-bootstrap";
import { Link, useNavigate, useParams } from "react-router-dom";
import { events } from "../data/events";

function EventDetail() {
    const { id } = useParams();
    const navigate = useNavigate();

    const event = events.find((item) => item.id === id);

    if (!event) {
        return (
            <Container className="py-5">
                <Card>
                    <Card.Body>
                        <Card.Title>Event not found</Card.Title>

                        <Card.Text>
                            The requested event does not exist.
                        </Card.Text>

                        <Button as={Link} to="/events">
                            Back to Events
                        </Button>
                    </Card.Body>
                </Card>
            </Container>
        );
    }

    return (
        <Container className="py-5">
            <Card>
                <Card.Body>
                    <Card.Title>{event.title}</Card.Title>

                    <Card.Text>
                        <strong>Category:</strong> {event.category}
                        <br />
                        <strong>Date:</strong> {event.date}
                        <br />
                        <strong>Location:</strong> {event.location}
                        <br />
                        <strong>Organizer:</strong> {event.organizer}
                        <br />
                        <strong>Seats:</strong> {event.seats}
                    </Card.Text>

                    <Card.Text>{event.description}</Card.Text>

                    <Button
                        as={Link}
                        to="/events"
                        variant="primary"
                        className="me-2"
                    >
                        Back to Events
                    </Button>

                    <Button
                        variant="secondary"
                        onClick={() => navigate(-1)}
                    >
                        Go Back
                    </Button>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default EventDetail;