import Card from "react-bootstrap/Card";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";

const money = new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
});

export default function ProductStats({ products }) {
    const itemTypes = products.length;

    const totalQuantity = products.reduce(
        (sum, item) => sum + item.quantity,
        0
    );

    const inventoryValue = products.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
    );

    const stats = [
        ["Product types", itemTypes],
        ["Total quantity", totalQuantity],
        ["Inventory value", money.format(inventoryValue)],
    ];

    return (
        <Row className="g-3 mb-4">
            {stats.map(([label, value]) => (
                <Col key={label} xs={12} md={4}>
                    <Card className="text-center shadow-sm">
                        <Card.Body>
                            <div className="text-secondary small">
                                {label}
                            </div>

                            <strong className="fs-5">
                                {value}
                            </strong>
                        </Card.Body>
                    </Card>
                </Col>
            ))}
        </Row>
    );
}