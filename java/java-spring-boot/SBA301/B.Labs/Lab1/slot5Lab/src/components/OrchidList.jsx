import { Container, Row, Col } from "react-bootstrap";
import { OrchidsData } from "../shared/OrchidsData";
import OrchidCard from "./OrchidCard";

function OrchidList() {
    return (
        <Container id="orchids" className="py-4">
            <h2 className="mb-4">Orchid Collection</h2>

            <Row className="g-4">
                {OrchidsData.map((orchid) => (
                    <Col key={orchid.id} xs={12} sm={6} lg={4}>
                        <OrchidCard orchid={orchid} />
                    </Col>
                ))}
            </Row>
        </Container>
    );
}

export default OrchidList;