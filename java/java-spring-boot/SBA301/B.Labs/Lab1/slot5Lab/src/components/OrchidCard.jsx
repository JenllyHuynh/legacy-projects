import {useState} from "react";
import {Badge, Button, Card, Modal} from "react-bootstrap";

function OrchidCard({orchid}) {
    const [show, setShow] = useState(false);
    return (
        <>
            <Card className="h-100 shadow-sm">
                <Card.Img
                    variant="top"
                    src={orchid.image}
                    alt={orchid.orchidName}
                />

                <Card.Body className="d-flex flex-column">
                    <Card.Title>{orchid.orchidName}</Card.Title>

                    <Card.Text className="mb-2">
                        Category: {orchid.category}
                    </Card.Text>

                    <div className="mb-3">
                        {orchid.isSpecial && (
                            <Badge bg="warning">Special</Badge>
                        )}
                    </div>

                    <Button
                        className="mt-auto"
                        onClick={() => setShow(true)}
                    >
                        View Detail
                    </Button>
                </Card.Body>
            </Card>

            <Modal
                show={show}
                onHide={() => setShow(false)}
                centered
            >
                <Modal.Header closeButton>
                    <Modal.Title>{orchid.orchidName}</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    <img
                        src={orchid.image}
                        alt={orchid.orchidName}
                        className="img-fluid rounded mb-3"
                    />

                    <p>
                        <strong>Category:</strong> {orchid.category}
                    </p>

                    <p>
                        <strong>Origin:</strong> {orchid.origin}
                    </p>

                    <p>
                        <strong>Color:</strong> {orchid.color}
                    </p>

                    <p>
                        <strong>Rating:</strong> {orchid.rating}
                    </p>
                </Modal.Body>

                <Modal.Footer>
                    <Button
                        variant="secondary"
                        onClick={() => setShow(false)}
                    >
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default OrchidCard;