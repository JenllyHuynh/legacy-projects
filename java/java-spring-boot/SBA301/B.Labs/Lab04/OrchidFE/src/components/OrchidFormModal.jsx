import { useState } from 'react';

const defaultFormData = {
    name: '',
    categoryId: '',
    description: '',
    isNatural: false,
    isAttractive: false,
    url: ''
};

const categories = [
    { id: '1', name: 'Orchid Vàng' },
    { id: '2', name: 'Orchid Tím' },
    { id: '3', name: 'Orchid Trắng' }
];

export default function OrchidFormModal({ initialData, onClose, onSave }) {
    const [formData, setFormData] = useState(() => ({
        ...defaultFormData,
        ...initialData,
        name: initialData?.name ?? '',
        categoryId: initialData?.categoryId ?? '',
        description: initialData?.description ?? '',
        isNatural: Boolean(initialData?.isNatural),
        isAttractive: Boolean(initialData?.isAttractive),
        url: initialData?.url ?? ''
    }));
    const [formError, setFormError] = useState('');

    const handleChange = (event) => {
        const { name, value, type, checked } = event.target;
        setFormData((current) => ({
            ...current,
            [name]: type === 'checkbox' ? checked : value
        }));
        if (formError) setFormError('');
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!formData.name.trim()) {
            setFormError('Vui lòng nhập tên hoa lan.');
            return;
        }
        if (!categories.some((category) => category.id === formData.categoryId)) {
            setFormError('Vui lòng chọn mã danh mục hợp lệ.');
            return;
        }
        if (formData.url.trim()) {
            try {
                const imageUrl = new URL(formData.url.trim());
                if (!['http:', 'https:'].includes(imageUrl.protocol)) {
                    setFormError('Đường dẫn hình ảnh phải bắt đầu bằng http:// hoặc https://.');
                    return;
                }
            } catch {
                setFormError('Vui lòng nhập đường dẫn hình ảnh hợp lệ.');
                return;
            }
        }

        onSave({
            ...formData,
            name: formData.name.trim(),
            categoryId: formData.categoryId
        });
    };

    return (
        <div className="modal-overlay">
            <section
                className="modal-container"
                role="dialog"
                aria-modal="true"
                aria-labelledby="orchid-form-title"
            >
                <div className="modal-heading">
                    <div>
                        <p className="eyebrow">THÔNG TIN HOA LAN</p>
                        <h2 id="orchid-form-title">
                            {initialData ? 'Cập nhật hoa lan' : 'Thêm hoa lan mới'}
                        </h2>
                    </div>
                    <button className="modal-close" type="button" onClick={onClose} aria-label="Đóng">
                        ×
                    </button>
                </div>

                <form className="orchid-form" onSubmit={handleSubmit}>
                    <label>
                        Tên hoa lan <span className="required">*</span>
                        <input
                            name="name"
                            type="text"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            maxLength="120"
                            autoFocus
                        />
                    </label>

                    <label>
                        Mã danh mục <span className="required">*</span>
                        <select
                            name="categoryId"
                            value={formData.categoryId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">-- Chọn mã danh mục --</option>
                            {categories.map((category) => (
                                <option key={category.id} value={category.id}>
                                    {category.id} - {category.name}
                                </option>
                            ))}
                        </select>
                    </label>

                    <label>
                        Đường dẫn hình ảnh
                        <input
                            name="url"
                            type="text"
                            value={formData.url}
                            onChange={handleChange}
                            placeholder="https://..."
                        />
                    </label>

                    <label>
                        Mô tả
                        <textarea
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            rows="4"
                        />
                    </label>

                    {formError && <p className="form-error" role="alert">{formError}</p>}

                    <div className="checkbox-row">
                        <label>
                            <input
                                type="checkbox"
                                name="isNatural"
                                checked={Boolean(formData.isNatural)}
                                onChange={handleChange}
                            />
                            Tự nhiên
                        </label>
                        <label>
                            <input
                                type="checkbox"
                                name="isAttractive"
                                checked={Boolean(formData.isAttractive)}
                                onChange={handleChange}
                            />
                            Cuốn hút
                        </label>
                    </div>

                    <div className="modal-actions">
                        <button className="button button-quiet" type="button" onClick={onClose}>
                            Hủy
                        </button>
                        <button className="button button-primary" type="submit">
                            {initialData ? 'Lưu thay đổi' : 'Thêm hoa lan'}
                        </button>
                    </div>
                </form>
            </section>
        </div>
    );
}