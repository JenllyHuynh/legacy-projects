import Button from "react-bootstrap/Button";
import Card from "react-bootstrap/Card";

export default function ProductItem({
                                        product,
                                        onIncrease,
                                        onDecrease,
                                        onEdit,
                                        onDelete,
                                    }) {
    return (
        <Card className="h-100 shadow-sm">
            <Card.Body>
                <Card.Title>{product.name}</Card.Title>

                <Card.Text className="mb-1">
                    Category: {product.category}
                </Card.Text>

                <Card.Text className="mb-1">
                    Price: {product.price.toLocaleString("vi-VN")} VND
                </Card.Text>

                <Card.Text className="mb-3">
                    Quantity: {product.quantity}
                </Card.Text>

                <div className="d-flex gap-2 flex-wrap">
                    <Button
                        variant="outline-secondary"
                        onClick={() => onDecrease(product.id)}
                        disabled={product.quantity === 0}
                    >
                        -
                    </Button>

                    <Button
                        variant="outline-primary"
                        onClick={() => onIncrease(product.id)}
                    >
                        +
                    </Button>

                    <Button
                        variant="outline-warning"
                        onClick={() => onEdit(product)}
                    >
                        Edit
                    </Button>

                    <Button
                        variant="outline-danger"
                        onClick={() => onDelete(product.id)}
                    >
                        Delete
                    </Button>

                </div>
            </Card.Body>
        </Card>
    );
}
