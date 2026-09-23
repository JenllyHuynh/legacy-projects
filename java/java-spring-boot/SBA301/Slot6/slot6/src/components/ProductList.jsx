import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import ProductItem from "./ProductItem";

export default function ProductList({
                                        products,
                                        onIncrease,
                                        onDecrease,
                                        onEdit,
                                        onDelete,
                                    }) {
    return (
        <Row className="g-3">
            {products.map((product) => (
                <Col key={product.id} xs={12} md={6} lg={4}>
                    <ProductItem
                        product={product}
                        onIncrease={onIncrease}
                        onDecrease={onDecrease}
                        onEdit={onEdit}
                        onDelete={onDelete}
                    />
                </Col>
            ))}
        </Row>
    );
}