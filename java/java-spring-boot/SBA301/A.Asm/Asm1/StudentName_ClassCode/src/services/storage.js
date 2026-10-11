// Tầng lưu trữ mock: đọc/ghi localStorage.
// Sau này nối Spring Boot thì thay file này (hoặc thêm service gọi API) là đủ.

export function loadData(key, fallback) {
  try {
    const raw = localStorage.getItem(key)
    if (raw === null) return fallback // lần chạy đầu: chưa có dữ liệu
    return JSON.parse(raw)
  } catch {
    return fallback // dữ liệu hỏng -> dùng dữ liệu mặc định
  }
}

export function saveData(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value))
  } catch {
    /* bỏ qua lỗi (vd: storage đầy) */
  }
}

export function removeData(key) {
  try {
    localStorage.removeItem(key)
  } catch {
    /* bỏ qua */
  }
}
