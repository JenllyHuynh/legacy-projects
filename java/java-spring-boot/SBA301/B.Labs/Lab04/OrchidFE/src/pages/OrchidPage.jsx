import { useCallback, useEffect, useState } from 'react';
import OrchidDetailModal from '../components/OrchidDetailModal';
import OrchidFormModal from '../components/OrchidFormModal';
import OrchidTable from '../components/OrchidTable';
import {
    addOrchid,
    deleteOrchid,
    getAllList,
    searchOrchidByName,
    updateOrchid
} from '../services/orchidService';
import '../App.css';

const getErrorMessage = (error) =>
    error.response?.data?.message || error.message || 'Đã xảy ra lỗi. Vui lòng thử lại.';

export default function OrchidPage() {
    const [orchids, setOrchids] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingOrchid, setEditingOrchid] = useState(null);
    const [selectedOrchid, setSelectedOrchid] = useState(null);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState('');

    const loadOrchids = useCallback(async (name = '') => {
        setLoading(true);
        setErrorMessage('');
        try {
            const data = name.trim()
                ? await searchOrchidByName(name.trim())
                : await getAllList();
            setOrchids(data);
        } catch (error) {
            setErrorMessage(`Không thể tải danh sách hoa lan: ${getErrorMessage(error)}`);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        let isActive = true;

        const loadInitialOrchids = async () => {
            try {
                const data = await getAllList();
                if (isActive) setOrchids(data);
            } catch (error) {
                if (isActive) {
                    setErrorMessage(`Không thể tải danh sách hoa lan: ${getErrorMessage(error)}`);
                }
            } finally {
                if (isActive) setLoading(false);
            }
        };

        loadInitialOrchids();
        return () => {
            isActive = false;
        };
    }, []);

    const handleSearch = (event) => {
        event.preventDefault();
        loadOrchids(searchTerm);
    };

    const handleSave = async (orchid) => {
        setErrorMessage('');
        try {
            if (editingOrchid) {
                await updateOrchid(editingOrchid.id, orchid);
            } else {
                await addOrchid(orchid);
            }

            const search = editingOrchid ? searchTerm : '';
            if (!editingOrchid) setSearchTerm('');
            setIsFormOpen(false);
            setEditingOrchid(null);
            await loadOrchids(search);
        } catch (error) {
            setErrorMessage(`Không thể lưu hoa lan: ${getErrorMessage(error)}`);
        }
    };

    const handleDelete = async (orchid) => {
        if (!window.confirm(`Bạn có chắc chắn muốn xóa "${orchid.name}"?`)) return;

        setErrorMessage('');
        try {
            await deleteOrchid(orchid.id);
            await loadOrchids(searchTerm);
        } catch (error) {
            setErrorMessage(`Không thể xóa hoa lan: ${getErrorMessage(error)}`);
        }
    };

    const openCreateForm = () => {
        setEditingOrchid(null);
        setIsFormOpen(true);
    };

    const openEditForm = (orchid) => {
        setEditingOrchid(orchid);
        setIsFormOpen(true);
    };

    const closeForm = () => {
        setIsFormOpen(false);
        setEditingOrchid(null);
    };

    return (
        <main className="orchid-page">
            <header className="page-header">
                <div>
                    <p className="eyebrow">QUẢN LÝ BỘ SƯU TẬP</p>
                    <h1>Danh sách hoa lan</h1>
                    <p className="page-description">
                        Tìm kiếm, xem chi tiết và quản lý thông tin hoa lan.
                    </p>
                </div>
                <button className="button button-primary" onClick={openCreateForm}>
                    + Thêm hoa lan
                </button>
            </header>

            <form className="search-bar" onSubmit={handleSearch}>
                <label className="visually-hidden" htmlFor="orchid-search">
                    Tìm hoa lan theo tên
                </label>
                <input
                    id="orchid-search"
                    type="search"
                    placeholder="Nhập tên hoa lan..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                />
                <button className="button button-secondary" type="submit">
                    Tìm kiếm
                </button>
                {searchTerm && (
                    <button
                        className="button button-quiet"
                        type="button"
                        onClick={() => {
                            setSearchTerm('');
                            loadOrchids();
                        }}
                    >
                        Xóa tìm kiếm
                    </button>
                )}
            </form>

            {errorMessage && (
                <p className="error-message" role="alert">
                    {errorMessage}
                </p>
            )}

            <section className="table-card" aria-label="Danh sách hoa lan">
                {loading ? (
                    <p className="table-message" role="status">Đang tải danh sách...</p>
                ) : (
                    <OrchidTable
                        orchids={orchids}
                        onView={setSelectedOrchid}
                        onEdit={openEditForm}
                        onDelete={handleDelete}
                    />
                )}
            </section>

            {isFormOpen && (
                <OrchidFormModal
                    key={editingOrchid?.id ?? 'new-orchid'}
                    initialData={editingOrchid}
                    onClose={closeForm}
                    onSave={handleSave}
                />
            )}
            {selectedOrchid && (
                <OrchidDetailModal
                    orchid={selectedOrchid}
                    onClose={() => setSelectedOrchid(null)}
                />
            )}
        </main>
    );
}
