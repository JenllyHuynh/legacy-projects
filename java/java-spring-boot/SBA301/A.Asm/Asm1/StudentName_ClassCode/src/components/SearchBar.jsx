export default function SearchBar({ value, onChange, placeholder }) {
  return (
    <div className="searchbar">
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
      />
      {value && (
        <button className="btn" onClick={() => onChange('')}>
          Xóa tìm kiếm
        </button>
      )}
    </div>
  )
}
