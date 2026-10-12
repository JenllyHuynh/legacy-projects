// Hộp thoại xác nhận trước khi xóa
export default function ConfirmDialog({ message, onConfirm, onCancel }) {
  return (
    <div className="overlay" onMouseDown={onCancel}>
      <div className="modal modal-small" role="alertdialog" onMouseDown={(e) => e.stopPropagation()}>
        <h3>Xác nhận xóa</h3>
        <p>{message}</p>
        <div className="modal-actions">
          <button className="btn" onClick={onCancel}>
            Hủy
          </button>
          <button className="btn btn-danger" onClick={onConfirm}>
            Xóa
          </button>
        </div>
      </div>
    </div>
  )
}
