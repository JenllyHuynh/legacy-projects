import { useState } from "react";
import { Col, Container, Form, Row } from "react-bootstrap";
import EventCard from "../components/EventCard";
import { events } from "../data/events";

function EventsPage() {
    const [search, setSearch] = useState("");
    const [category, setCategory] = useState("All");

    const categories = ["All", ...new Set(events.map((event) => event.category))];

    const filteredEvents = events.filter((event) => {
        const keyword = search.trim().toLowerCase();

        const matchesText =
            event.title.toLowerCase().includes(keyword) ||
            event.location.toLowerCase().includes(keyword);

        const matchesCategory =
            category === "All" || event.category === category;

        return matchesText && matchesCategory;
    });

    return (
        <Container className="py-4">
            <h2>Campus Events</h2>

            <p className="text-muted">
                {filteredEvents.length} event(s) found.
            </p>

            <Row className="g-3 mb-4">
                <Col md={8}>
                    <Form.Control
                        value={search}
                        onChange={(event) => setSearch(event.target.value)}
                        placeholder="Search title or location..."
                    />
                </Col>

                <Col md={4}>
                    <Form.Select
                        value={category}
                        onChange={(event) => setCategory(event.target.value)}
                    >
                        {categories.map((item) => (
                            <option key={item} value={item}>
                                {item}
                            </option>
                        ))}
                    </Form.Select>
                </Col>
            </Row>

            <Row className="g-4">
                {filteredEvents.map((event) => (
                    <Col key={event.id} sm={12} md={6} lg={4}>
                        <EventCard event={event} />
                    </Col>
                ))}
            </Row>

            {filteredEvents.length === 0 && (
                <p className="text-center text-muted mt-4">
                    No events found.
                </p>
            )}
        </Container>
    );
}

export default EventsPage;