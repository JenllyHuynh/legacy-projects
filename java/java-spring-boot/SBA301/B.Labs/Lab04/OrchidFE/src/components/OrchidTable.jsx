export default function OrchidTable({ orchids, onView, onEdit, onDelete }) {
    if (orchids.length === 0) {
        return <p className="table-message">Không tìm thấy hoa lan nào.</p>;
    }

    return (
        <div className="table-scroll">
            <table className="orchid-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Hình ảnh</th>
                        <th>Tên hoa lan</th>
                        <th>Danh mục</th>
                        <th>Mô tả</th>
                        <th>Tự nhiên</th>
                        <th>Cuốn hút</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    {orchids.map((orchid) => (
                        <tr key={orchid.id}>
                            <td>{orchid.id}</td>
                            <td>
                                {orchid.url ? (
                                    <img className="orchid-thumbnail" src={orchid.url} alt={orchid.name} />
                                ) : (
                                    <span className="image-placeholder">Chưa có ảnh</span>
                                )}
                            </td>
                            <td className="orchid-name">{orchid.name}</td>
                            <td>{orchid.categoryId || '—'}</td>
                            <td className="description-cell">{orchid.description || '—'}</td>
                            <td>{orchid.isNatural ? 'Có' : 'Không'}</td>
                            <td>{orchid.isAttractive ? 'Có' : 'Không'}</td>
                            <td>
                                <div className="row-actions">
                                    <button className="button button-small button-quiet" onClick={() => onView(orchid)}>
                                        Chi tiết
                                    </button>
                                    <button className="button button-small button-edit" onClick={() => onEdit(orchid)}>
                                        Sửa
                                    </button>
                                    <button className="button button-small button-danger" onClick={() => onDelete(orchid)}>
                                        Xóa
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}