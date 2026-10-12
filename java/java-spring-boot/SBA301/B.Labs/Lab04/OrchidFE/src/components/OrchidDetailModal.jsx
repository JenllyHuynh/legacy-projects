export default function OrchidDetailModal({ orchid, onClose }) {
    if (!orchid) return null;

    return (
        <div className="modal-overlay" onMouseDown={onClose}>
            <section
                className="modal-container detail-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="orchid-detail-title"
                onMouseDown={(event) => event.stopPropagation()}
            >
                <div className="modal-heading">
                    <div>
                        <p className="eyebrow">CHI TIẾT</p>
                        <h2 id="orchid-detail-title">{orchid.name}</h2>
                    </div>
                    <button className="modal-close" type="button" onClick={onClose} aria-label="Đóng">
                        ×
                    </button>
                </div>

                {orchid.url && <img className="detail-image" src={orchid.url} alt={orchid.name} />}

                <dl className="detail-list">
                    <div><dt>ID</dt><dd>{orchid.id}</dd></div>
                    <div><dt>Danh mục</dt><dd>{orchid.categoryId || '—'}</dd></div>
                    <div><dt>Tự nhiên</dt><dd>{orchid.isNatural ? 'Có' : 'Không'}</dd></div>
                    <div><dt>Cuốn hút</dt><dd>{orchid.isAttractive ? 'Có' : 'Không'}</dd></div>
                    <div><dt>Mô tả</dt><dd>{orchid.description || 'Chưa có mô tả'}</dd></div>
                </dl>

                <div className="modal-actions">
                    <button className="button button-primary" type="button" onClick={onClose}>
                        Đóng
                    </button>
                </div>
            </section>
        </div>
    );
}