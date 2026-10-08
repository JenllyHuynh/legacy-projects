import { Spinner } from 'react-bootstrap';
export default function LoadingSpinner() {
    return <div className="text-center py-5"><Spinner animation="border" role="status" />
        <div className="mt-2">Loading Orchids...</div>
    </div>;
}