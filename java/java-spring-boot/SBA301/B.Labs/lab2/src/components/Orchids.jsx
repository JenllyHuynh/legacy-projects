import { useState } from 'react';
import { Button, Col, Container, Form, Row } from 'react-bootstrap';
import useOrchids from '../hooks/useOrchids';
import ErrorMessage from './ErrorMessage';
import LoadingSpinner from './LoadingSpinner';
import OrchidCard from './OrchidCard';
import OrchidDetailModal from './OrchidDetailModal';
export default function Orchids() {
    const { orchids, loading, error, reload } = useOrchids();
    const [show, setShow] = useState(false);
    const [selectedOrchid, setSelectedOrchid] = useState(null);
    const handleShow = (orchid) => { setSelectedOrchid(orchid); setShow(true); };
    const handleClose = () => { setShow(false); setSelectedOrchid(null); };
    const [keyword, setKeyword] = useState('');
    const [category, setCategory] = useState('ALL');
    const [specialOnly, setSpecialOnly] = useState(false);
    const visibleOrchids = orchids.filter((o) => {
        const matchName = o.orchidName.toLowerCase().includes(keyword.trim().toLowerCase());
        const matchCategory = category === 'ALL' || o.category === category;
        const matchSpecial = !specialOnly || o.isSpecial;
        return matchName && matchCategory && matchSpecial;
    });

    return (
        <Container id="orchids" className="py-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2 className="mb-0">Orchids List</h2>
                <Button variant="outline-primary" onClick={reload} disabled={loading}>Reload</Button>
            </div>
            <Form.Control
                type="text"
                placeholder="Search by name..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                className="mb-3"
            />
            {loading && <LoadingSpinner />}
            {error && <ErrorMessage message={error} onRetry={reload} />}
            {!loading && !error && orchids.length === 0 && <p>Không có Orchid nào.</p>}
            {!loading && !error && visibleOrchids.length > 0 && (
                <Row>
                    {visibleOrchids.map((orchid) => <Col xs={12} sm={6} lg={3} key={orchid.id} className="mb-4"><OrchidCard orchid={orchid} onDetail={handleShow} /></Col>)}
                </Row>
            )}
            <OrchidDetailModal show={show} orchid={selectedOrchid} onClose={handleClose} />
        </Container>
    );
}