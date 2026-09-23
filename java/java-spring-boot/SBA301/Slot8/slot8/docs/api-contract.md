# Product API Contract

Base URL: `http://localhost:3001`

## Endpoints

| Method | Endpoint | Mô tả | Request body | Response thành công | Status kỳ vọng |
|---|---|---|---|---|---|
| GET | `/products` | Lấy danh sách | Không | Product[] | 200 |
| GET | `/products/{id}` | Lấy theo id | Không | Product | 200 / 404 |
| POST | `/products` | Tạo mới | ProductCreate JSON | Product mới | 201 |
| PUT | `/products/{id}` | Cập nhật toàn bộ | Product JSON | Product cập nhật | 200 |
| PATCH | `/products/{id}` | Cập nhật một phần | Partial Product JSON | Product cập nhật | 200 |
| DELETE | `/products/{id}` | Xóa product | Không | Phụ thuộc mock server | 200/204 tùy phiên bản |

## Product schema used in this lab

- `id`: number
- `name`: string
- `category`: string
- `price`: number > 0
- `quantity`: integer >= 0
- `active`: boolean

## Example Product

```json
{
  "id": 1,
  "name": "iPhone 15",
  "category": "Phone",
  "price": 25000000,
  "quantity": 10,
  "active": true
}