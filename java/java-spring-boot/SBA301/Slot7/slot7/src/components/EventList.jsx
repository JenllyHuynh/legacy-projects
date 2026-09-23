import { Col, Container, Form, Row, Modal, Button } from "react-bootstrap";
import {useState} from "react";
import EventCard from "./EventCard";

function EventList({events}) {
    const [searchText, setSearchText] = useState("");
    const [category, setCategory] = useState("All");
    const [featuredOnly, setFeaturedOnly] = useState(false);

    const categories = [
        "All",
        ...new Set(events.map((event) => event.category)),
    ];

    const filteredEvents = events.filter((event) => {
        const matchesText = event.title
            .toLowerCase()
            .includes(searchText.trim().toLowerCase());

        const matchesCategory =
            category === "All" || event.category === category;

        const matchesFeatured = !featuredOnly || event.featured;

        return matchesText && matchesCategory && matchesFeatured;
    });

    const [selectedEvent, setSelectedEvent] = useState(null);
    const [showModal, setShowModal] = useState(false);
    return (
        <section id="events" className="py-5">
            <Modal
                show={showModal}
                onHide={() => setShowModal(false)}
            >
                {selectedEvent && (
                    <>
                        <Modal.Header closeButton>
                            <Modal.Title>{selectedEvent.title}</Modal.Title>
                        </Modal.Header>

                        <Modal.Body>
                            <p>
                                <strong>Category:</strong>{" "}
                                {selectedEvent.category}
                            </p>

                            <p>
                                <strong>Date:</strong>{" "}
                                {selectedEvent.date}
                            </p>

                            <p>
                                <strong>Location:</strong>{" "}
                                {selectedEvent.location}
                            </p>

                            <p>
                                {selectedEvent.description}
                            </p>
                        </Modal.Body>

                        <Modal.Footer>
                            <Button
                                variant="secondary"
                                onClick={() => setShowModal(false)}
                            >
                                Close
                            </Button>
                        </Modal.Footer>
                    </>
                )}
            </Modal>

            <Container>
                <h2 className="mb-4">Upcoming Events</h2>

                <Form.Control
                    type="search"
                    placeholder="Search events..."
                    value={searchText}
                    onChange={(e) => setSearchText(e.target.value)}
                />

                <Form.Select
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    className="mt-3"
                >
                    {categories.map((item) => (
                        <option key={item} value={item}>
                            {item}
                        </option>
                    ))}
                </Form.Select>

                <Form.Check
                    type="switch"
                    id="featured-only"
                    label="Featured only"
                    checked={featuredOnly}
                    onChange={(e) => setFeaturedOnly(e.target.checked)}
                />

                <p className="text-muted mt-3 mb-0">
                    Showing {filteredEvents.length} of {events.length} events
                </p>

                {filteredEvents.length === 0 ? (
                    <div className="text-center py-5">
                        <h4>No events found</h4>
                        <p className="text-muted">
                            Try changing your search or filters.
                        </p>
                    </div>
                ) : (
                    <Row className="g-4">
                        {filteredEvents.map((event) => (
                            <Col key={event.id} xs={12} md={6} lg={4}>
                                <EventCard
                                    event={event}
                                    onSelect={(selected) => {
                                        setSelectedEvent(selected);
                                        setShowModal(true);
                                    }}
                                />
                            </Col>
                        ))}
                    </Row>
                )}
            </Container>
        </section>
    );
}

export default EventList;