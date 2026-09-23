# SBA301 Slot 8 - Product REST API Design and Inspection Kit

## Scope

Practice Client-Server, HTTP, JSON, REST, Postman and DevTools.

This lab does NOT implement a real Spring Boot backend and does NOT teach Fetch/Axios in depth.

## Requirements

- Node.js 21 or equivalent course environment
- npm
- Postman
- Chrome/Edge DevTools

## Install

```bash
npm install
Run mock API
npm run api

Base URL:

http://localhost:3001

Products endpoint:

http://localhost:3001/products
Run JSON demo
npm run json-demo

This demonstrates:

JSON text
    ↓
JSON.parse()
    ↓
JavaScript object
    ↓
JSON.stringify()
    ↓
JSON text
Reset data

Stop json-server first, then run:

npm run reset-db
npm run api

The database will be restored from db.seed.json.

Required API tests
GET
GET /products
GET /products/1
GET /products/999999
POST
POST /products

Creates a new product.

PUT
PUT /products/1

Updates the whole product.

PATCH
PATCH /products/1

Updates selected product fields.

DELETE
DELETE /products/4

Deletes a product.

Evidence

Postman evidence:

evidence/postman/

DevTools evidence:

evidence/devtools/

JSON demo evidence:

evidence/terminal-json-demo.png

Reset evidence:

evidence/terminal-reset.png
API Contract

See:

docs/api-contract.md

The API contract documents the Product resource, endpoints, HTTP methods, request bodies, response expectations and error observations.

Project Structure
slot8/
├── docs/
│   └── api-contract.md
├── evidence/
│   ├── devtools/
│   │   └── 01-get-products.png
│   ├── postman/
│   │   ├── 01-get-list.png
│   │   ├── 02-get-id.png
│   │   ├── 03-get-missing.png
│   │   ├── 04-post.png
│   │   ├── 05-put.png
│   │   ├── 06-patch.png
│   │   └── 07-delete.png
│   ├── terminal-json-demo.png
│   └── terminal-reset.png
├── scripts/
│   ├── parse-json-demo.js
│   └── reset-db.js
├── db.json
├── db.seed.json
├── package.json
├── package-lock.json
└── README.md
Git

Do not commit node_modules.

The important project files are:

package.json
package-lock.json
db.json
db.seed.json
scripts/
docs/
evidence/
README.md