import { Alert, Button } from 'react-bootstrap';
export default function ErrorMessage({ message, onRetry }) {
    return <Alert variant="danger"><Alert.Heading>Không thể tải dữ liệu</Alert.Heading>
        <p>{message}</p>
        {onRetry && <Button variant="outline-danger" onClick={onRetry}>Try Again</Button>}</Alert>;
}