import { Badge, Button, Card } from "react-bootstrap";
import { Link } from "react-router-dom";

function EventCard({ event }) {
    return (
        <Card className="h-100 shadow-sm">
            <Card.Body>
                <div className="mb-2">
                    {event.featured && (
                        <Badge bg="warning" text="dark" className="me-2">
                            Featured
                        </Badge>
                    )}

                    <Badge bg="secondary">
                        {event.category}
                    </Badge>
                </div>

                <Card.Title>{event.title}</Card.Title>

                <Card.Text>
                    <strong>Date:</strong> {event.date}
                    <br />
                    <strong>Location:</strong> {event.location}
                </Card.Text>

                <Button
                    as={Link}
                    to={`/events/${event.id}`}
                    variant="primary"
                >
                    View Details
                </Button>
            </Card.Body>
        </Card>
    );
}

export default EventCard;