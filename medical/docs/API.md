# MedApp API Overview

Base URL: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Authentication Flow

1. `POST /api/v1/auth/otp/request`
2. `POST /api/v1/auth/otp/login`
3. Use `Authorization: Bearer <access-token>`
4. Refresh with `POST /api/v1/auth/refresh`

## Core Endpoints

### Auth
- `POST /api/v1/auth/otp/request`
- `POST /api/v1/auth/otp/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`

### User
- `GET /api/v1/users/me`
- `PATCH /api/v1/users/me`
- `POST /api/v1/users/me/addresses`
- `GET /api/v1/users/me/addresses`
- `GET /api/v1/users/me/orders`

### Pharmacy
- `POST /api/v1/pharmacies`
- `GET /api/v1/pharmacies/me`
- `PATCH /api/v1/pharmacies/me`
- `POST /api/v1/pharmacies/admin/{pharmacyId}/review`

### Medicine
- `GET /api/v1/medicines`
- `GET /api/v1/medicines/{medicineId}`
- `POST /api/v1/medicines` (admin)
- `PUT /api/v1/medicines/{medicineId}` (admin)

### Inventory
- `POST /api/v1/inventory/{pharmacyId}`
- `PUT /api/v1/inventory/{pharmacyId}`
- `GET /api/v1/inventory/{pharmacyId}`
- `GET /api/v1/inventory/validate`

### Prescription
- `POST /api/v1/prescriptions`
- `POST /api/v1/prescriptions/{prescriptionId}/review`
- `GET /api/v1/prescriptions/{prescriptionId}`
- `GET /api/v1/prescriptions/me`

### Cart
- `POST /api/v1/cart/items`
- `PATCH /api/v1/cart/items/{pharmacyId}/{medicineId}`
- `DELETE /api/v1/cart/items/{pharmacyId}/{medicineId}`
- `GET /api/v1/cart`

### Order
- `POST /api/v1/orders`
- `POST /api/v1/orders/{orderId}/transition`
- `GET /api/v1/orders/{orderId}`
- `GET /api/v1/orders/me`

### Payment
- `POST /api/v1/payments/initiate`
- `POST /api/v1/payments/webhook`
- `POST /api/v1/payments/{orderId}/refund`
- `GET /api/v1/payments/{orderId}`

### Delivery
- `POST /api/v1/delivery/riders/register`
- `POST /api/v1/delivery/{orderId}/assign`
- `POST /api/v1/delivery/{orderId}/out-for-delivery`
- `POST /api/v1/delivery/{orderId}/verify-otp`

### Settlement
- `GET /api/v1/settlements`
- `GET /api/v1/settlements/pharmacy/{pharmacyId}`
- `GET /api/v1/settlements/rider/{riderId}`
- `POST /api/v1/settlements/{orderId}/mark-paid`

### Audit
- `GET /api/v1/audit`
- `GET /api/v1/audit/by-actor`

## Seed Users

- Admin: `9999990001`
- User: `9999990002`
- Pharmacy: `9999990003`
- Rider: `9999990004`

Use OTP flow to obtain JWTs.
