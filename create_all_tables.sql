-- ================================================================
-- CREATE ALL TABLES FOR UTEEXPRESS LOGISTICS SYSTEM
-- Database: MySQL 8.0+
-- Charset: UTF-8
-- ================================================================

-- Drop database if exists and create new
-- DROP DATABASE IF EXISTS uteexpress_db;
-- CREATE DATABASE uteexpress_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE uteexpress_db;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables if they exist
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS chat_messages;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS financial_reports;
DROP TABLE IF EXISTS bank_reconciliations;
DROP TABLE IF EXISTS payrolls;
DROP TABLE IF EXISTS commissions;
DROP TABLE IF EXISTS receipts;
DROP TABLE IF EXISTS debts;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS partners;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

-- Enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================================
-- 1. USERS TABLE (Người dùng)
-- ================================================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT 'Tên đăng nhập',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT 'Email',
    password VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã mã hóa',
    full_name VARCHAR(100) COMMENT 'Họ và tên',
    phone VARCHAR(20) COMMENT 'Số điện thoại',
    avatar_url VARCHAR(500) COMMENT 'URL ảnh đại diện',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Trạng thái hoạt động',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Ngày cập nhật',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng người dùng';

-- ================================================================
-- 2. USER_ROLES TABLE (Vai trò người dùng)
-- ================================================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL COMMENT 'ID người dùng',
    role VARCHAR(50) NOT NULL COMMENT 'Vai trò: ROLE_ADMIN, ROLE_CUSTOMER, ROLE_SHIPPER, ROLE_WAREHOUSE_STAFF, ROLE_ACCOUNTANT',
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng vai trò người dùng';

-- ================================================================
-- 3. PARTNERS TABLE (Đối tác)
-- ================================================================
CREATE TABLE partners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT 'Tên đối tác',
    code VARCHAR(100) UNIQUE COMMENT 'Mã đối tác',
    contact_person VARCHAR(255) COMMENT 'Người liên hệ',
    phone VARCHAR(50) COMMENT 'Số điện thoại',
    email VARCHAR(255) COMMENT 'Email',
    address TEXT COMMENT 'Địa chỉ',
    partner_type VARCHAR(50) COMMENT 'Loại đối tác: SHIPPING, WAREHOUSE, PAYMENT, SUPPLIER',
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT 'Trạng thái: ACTIVE, INACTIVE, PENDING',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code),
    INDEX idx_type (partner_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng đối tác';

-- ================================================================
-- 4. ADDRESSES TABLE (Địa chỉ)
-- ================================================================
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT 'Tên người nhận',
    phone VARCHAR(50) NOT NULL COMMENT 'Số điện thoại',
    address TEXT NOT NULL COMMENT 'Địa chỉ chi tiết',
    city VARCHAR(100) COMMENT 'Thành phố',
    district VARCHAR(100) COMMENT 'Quận/Huyện',
    ward VARCHAR(100) COMMENT 'Phường/Xã',
    postal_code VARCHAR(20) COMMENT 'Mã bưu điện',
    is_default BOOLEAN DEFAULT FALSE COMMENT 'Địa chỉ mặc định',
    user_id BIGINT NOT NULL COMMENT 'ID người dùng',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng địa chỉ';

-- ================================================================
-- 5. ORDERS TABLE (Đơn hàng)
-- ================================================================
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(255) UNIQUE NOT NULL COMMENT 'Mã đơn hàng',
    sender_name VARCHAR(255) NOT NULL COMMENT 'Tên người gửi',
    sender_phone VARCHAR(50) NOT NULL COMMENT 'SĐT người gửi',
    sender_address TEXT NOT NULL COMMENT 'Địa chỉ người gửi',
    recipient_name VARCHAR(255) NOT NULL COMMENT 'Tên người nhận',
    recipient_phone VARCHAR(50) NOT NULL COMMENT 'SĐT người nhận',
    recipient_address TEXT NOT NULL COMMENT 'Địa chỉ người nhận',
    shipment_fee DECIMAL(19,2) NOT NULL DEFAULT 0.00 COMMENT 'Phí vận chuyển',
    cod_amount DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Tiền thu hộ (COD)',
    total_amount DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Tổng tiền',
    weight DECIMAL(10,2) COMMENT 'Khối lượng (kg)',
    notes TEXT COMMENT 'Ghi chú',
    image_url VARCHAR(500) COMMENT 'URL hình ảnh hàng hóa',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, CONFIRMED, PICKING_UP, IN_TRANSIT, DELIVERING, DELIVERED, CANCELLED, RETURNED',
    service_type VARCHAR(50) COMMENT 'Loại dịch vụ: CHUAN, NHANH, TIET_KIEM',
    customer_id BIGINT COMMENT 'ID khách hàng',
    shipper_id BIGINT COMMENT 'ID shipper',
    pickup_date TIMESTAMP NULL COMMENT 'Ngày lấy hàng',
    delivery_date TIMESTAMP NULL COMMENT 'Ngày giao hàng',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_orders_shipper FOREIGN KEY (shipper_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_order_code (order_code),
    INDEX idx_status (status),
    INDEX idx_customer_id (customer_id),
    INDEX idx_shipper_id (shipper_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng đơn hàng';

-- ================================================================
-- 6. INVOICES TABLE (Hóa đơn)
-- ================================================================
CREATE TABLE invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_number VARCHAR(100) UNIQUE NOT NULL COMMENT 'Số hóa đơn',
    order_id BIGINT NOT NULL COMMENT 'ID đơn hàng',
    total_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng tiền trước thuế',
    tax_amount DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Tiền thuế (VAT)',
    discount_amount DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Tiền giảm giá',
    final_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng tiền sau thuế',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, PAID, CANCELLED, OVERDUE',
    issue_date DATE NOT NULL COMMENT 'Ngày phát hành',
    due_date DATE COMMENT 'Ngày đến hạn',
    payment_date DATE COMMENT 'Ngày thanh toán',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoices_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_issue_date (issue_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng hóa đơn';

-- ================================================================
-- 7. PAYMENTS TABLE (Thanh toán)
-- ================================================================
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT 'ID đơn hàng',
    amount DECIMAL(19,2) NOT NULL COMMENT 'Số tiền thanh toán',
    payment_method VARCHAR(50) NOT NULL COMMENT 'Phương thức: CASH, BANK_TRANSFER, E_WALLET, CREDIT_CARD',
    transaction_id VARCHAR(255) COMMENT 'Mã giao dịch',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, COMPLETED, FAILED, REFUNDED',
    payment_date TIMESTAMP NULL COMMENT 'Ngày thanh toán',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng thanh toán';

-- ================================================================
-- 8. DEBTS TABLE (Công nợ)
-- ================================================================
CREATE TABLE debts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    debtor_id BIGINT COMMENT 'ID người nợ',
    creditor_id BIGINT COMMENT 'ID người cho vay',
    amount DECIMAL(19,2) NOT NULL COMMENT 'Số tiền nợ',
    paid_amount DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Số tiền đã trả',
    remaining_amount DECIMAL(19,2) COMMENT 'Số tiền còn lại',
    debt_type VARCHAR(50) NOT NULL COMMENT 'Loại nợ: CUSTOMER_DEBT, SHIPPER_DEBT, PARTNER_DEBT, SUPPLIER_DEBT',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, PARTIAL, PAID, OVERDUE, CANCELLED',
    description TEXT COMMENT 'Mô tả',
    due_date DATETIME COMMENT 'Ngày đến hạn',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_debts_debtor FOREIGN KEY (debtor_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_debts_creditor FOREIGN KEY (creditor_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_debtor_id (debtor_id),
    INDEX idx_creditor_id (creditor_id),
    INDEX idx_debt_type (debt_type),
    INDEX idx_status (status),
    INDEX idx_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng công nợ';

-- ================================================================
-- 9. RECEIPTS TABLE (Biên lai thu chi)
-- ================================================================
CREATE TABLE receipts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receipt_number VARCHAR(100) UNIQUE NOT NULL COMMENT 'Số biên lai',
    amount DECIMAL(19,2) NOT NULL COMMENT 'Số tiền',
    receipt_type VARCHAR(50) NOT NULL COMMENT 'Loại: INCOME (thu), EXPENSE (chi)',
    payer_name VARCHAR(255) COMMENT 'Tên người nộp/nhận',
    description TEXT COMMENT 'Mô tả',
    receipt_date TIMESTAMP NOT NULL COMMENT 'Ngày lập biên lai',
    created_by BIGINT COMMENT 'Người lập',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_receipts_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_receipt_number (receipt_number),
    INDEX idx_receipt_type (receipt_type),
    INDEX idx_receipt_date (receipt_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng biên lai thu chi';

-- ================================================================
-- 10. COMMISSIONS TABLE (Hoa hồng)
-- ================================================================
CREATE TABLE commissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipper_id BIGINT NOT NULL COMMENT 'ID shipper',
    order_id BIGINT COMMENT 'ID đơn hàng',
    base_amount DECIMAL(19,2) NOT NULL COMMENT 'Số tiền cơ sở',
    commission_rate DECIMAL(5,2) NOT NULL COMMENT 'Tỷ lệ hoa hồng (%)',
    commission_amount DECIMAL(19,2) NOT NULL COMMENT 'Số tiền hoa hồng',
    commission_type VARCHAR(50) COMMENT 'Loại: DELIVERY, PICKUP, BONUS',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, APPROVED, PAID, REJECTED',
    calculated_date DATE COMMENT 'Ngày tính',
    paid_date DATE COMMENT 'Ngày trả',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_commissions_shipper FOREIGN KEY (shipper_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_commissions_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    INDEX idx_shipper_id (shipper_id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_calculated_date (calculated_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng hoa hồng';

-- ================================================================
-- 11. PAYROLLS TABLE (Bảng lương)
-- ================================================================
CREATE TABLE payrolls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT 'ID nhân viên',
    month INT NOT NULL COMMENT 'Tháng',
    year INT NOT NULL COMMENT 'Năm',
    base_salary DECIMAL(19,2) NOT NULL COMMENT 'Lương cơ bản',
    bonus DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Thưởng',
    deduction DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Khấu trừ',
    total_salary DECIMAL(19,2) NOT NULL COMMENT 'Tổng lương',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, APPROVED, PAID',
    payment_date DATE COMMENT 'Ngày trả lương',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payrolls_employee FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_employee_month_year (employee_id, month, year),
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_month_year (month, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng lương nhân viên';

-- ================================================================
-- 12. BANK_RECONCILIATIONS TABLE (Đối soát ngân hàng)
-- ================================================================
CREATE TABLE bank_reconciliations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bank_name VARCHAR(255) NOT NULL COMMENT 'Tên ngân hàng',
    account_number VARCHAR(100) NOT NULL COMMENT 'Số tài khoản',
    reconciliation_date DATE NOT NULL COMMENT 'Ngày đối soát',
    statement_balance DECIMAL(19,2) NOT NULL COMMENT 'Số dư sao kê',
    book_balance DECIMAL(19,2) NOT NULL COMMENT 'Số dư sổ sách',
    difference_amount DECIMAL(19,2) COMMENT 'Chênh lệch',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, MATCHED, UNMATCHED',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_bank_name (bank_name),
    INDEX idx_reconciliation_date (reconciliation_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng đối soát ngân hàng';

-- ================================================================
-- 13. FINANCIAL_REPORTS TABLE (Báo cáo tài chính)
-- ================================================================
CREATE TABLE financial_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_type VARCHAR(50) NOT NULL COMMENT 'Loại báo cáo: DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY',
    report_period VARCHAR(100) NOT NULL COMMENT 'Kỳ báo cáo',
    start_date DATE NOT NULL COMMENT 'Ngày bắt đầu',
    end_date DATE NOT NULL COMMENT 'Ngày kết thúc',
    total_revenue DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Tổng doanh thu',
    total_expense DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Tổng chi phí',
    net_profit DECIMAL(19,2) DEFAULT 0.00 COMMENT 'Lợi nhuận ròng',
    total_orders INT DEFAULT 0 COMMENT 'Tổng số đơn hàng',
    notes TEXT COMMENT 'Ghi chú',
    created_by BIGINT COMMENT 'Người tạo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reports_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_report_type (report_type),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng báo cáo tài chính';

-- ================================================================
-- 14. NOTIFICATIONS TABLE (Thông báo)
-- ================================================================
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipient_type VARCHAR(50) NOT NULL COMMENT 'Loại người nhận: CUSTOMER, SHIPPER, ADMIN, ALL',
    recipient_id BIGINT COMMENT 'ID người nhận (NULL nếu ALL)',
    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề',
    message TEXT NOT NULL COMMENT 'Nội dung',
    type VARCHAR(50) DEFAULT 'INFO' COMMENT 'Loại: INFO, WARNING, ERROR, SUCCESS',
    order_id BIGINT COMMENT 'ID đơn hàng liên quan',
    is_read BOOLEAN DEFAULT FALSE COMMENT 'Đã đọc',
    read_at TIMESTAMP NULL COMMENT 'Thời gian đọc',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    INDEX idx_recipient (recipient_type, recipient_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng thông báo';

-- ================================================================
-- 15. CHAT_MESSAGES TABLE (Tin nhắn chat)
-- ================================================================
CREATE TABLE chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL COMMENT 'Nội dung tin nhắn',
    sender_id BIGINT NOT NULL COMMENT 'ID người gửi',
    receiver_id BIGINT NOT NULL COMMENT 'ID người nhận',
    is_from_customer BOOLEAN DEFAULT TRUE COMMENT 'Tin nhắn từ khách hàng',
    is_read BOOLEAN DEFAULT FALSE COMMENT 'Đã đọc',
    read_at TIMESTAMP NULL COMMENT 'Thời gian đọc',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng tin nhắn chat';

-- ================================================================
-- 16. REVIEWS TABLE (Đánh giá)
-- ================================================================
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT 'ID đơn hàng',
    customer_id BIGINT NOT NULL COMMENT 'ID khách hàng',
    rating INT NOT NULL COMMENT 'Điểm đánh giá (1-5)',
    comment TEXT COMMENT 'Nhận xét',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5),
    INDEX idx_order_id (order_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng đánh giá dịch vụ';

-- ================================================================
-- SUMMARY - TỔNG KẾT CÁC BẢNG
-- ================================================================
-- 1. users - Người dùng (admin, customer, shipper, warehouse staff, accountant)
-- 2. user_roles - Vai trò người dùng
-- 3. partners - Đối tác (shipping, warehouse, payment, supplier)
-- 4. addresses - Địa chỉ giao/nhận hàng
-- 5. orders - Đơn hàng
-- 6. invoices - Hóa đơn
-- 7. payments - Thanh toán
-- 8. debts - Công nợ
-- 9. receipts - Biên lai thu chi
-- 10. commissions - Hoa hồng
-- 11. payrolls - Bảng lương
-- 12. bank_reconciliations - Đối soát ngân hàng
-- 13. financial_reports - Báo cáo tài chính
-- 14. notifications - Thông báo
-- 15. chat_messages - Tin nhắn chat
-- 16. reviews - Đánh giá dịch vụ
-- ================================================================
-- TOTAL: 16 tables created successfully!
-- ================================================================

SELECT 'All tables created successfully!' AS status;

-- ================================================================
-- INSERT SAMPLE DATA
-- ================================================================

-- ================================================================
-- Insert Users (Admin, Customer, Shipper, Accountant)
-- ================================================================
INSERT INTO users (username, email, password, full_name, phone, is_active) VALUES
('admin', 'admin@uteexpress.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', 'Admin System', '0900000000', TRUE),
('customer1', 'customer1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', 'Nguyễn Văn A', '0901234567', TRUE),
('shipper1', 'shipper1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', 'Trần Văn B', '0912345678', TRUE),
('accountant1', 'accountant1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', 'Lê Thị C', '0923456789', TRUE);

-- ================================================================
-- Insert User Roles
-- ================================================================
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_CUSTOMER'),
(3, 'ROLE_SHIPPER'),
(4, 'ROLE_ACCOUNTANT');

-- ================================================================
-- Insert Sample Orders (10 orders with different statuses)
-- ================================================================
INSERT INTO orders (order_code, sender_name, sender_phone, sender_address, recipient_name, recipient_phone, recipient_address, shipment_fee, notes, status, service_type, customer_id, created_at, updated_at) VALUES
-- Completed orders (5)
('ORD-2024-001', 'Nguyễn Văn A', '0901234567', '123 Đường ABC, Q.1, TP.HCM', 'Trần Thị B', '0912345678', '456 Đường XYZ, Q.2, TP.HCM', 50000.00, 'Giao hàng nhanh', 'HOAN_THANH', 'NHANH', 2, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
('ORD-2024-002', 'Lê Văn C', '0923456789', '789 Đường DEF, Q.3, TP.HCM', 'Phạm Thị D', '0934567890', '321 Đường GHI, Q.4, TP.HCM', 75000.00, 'Hàng dễ vỡ', 'HOAN_THANH', 'CHUAN', 2, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('ORD-2024-003', 'Hoàng Văn E', '0945678901', '654 Đường JKL, Q.5, TP.HCM', 'Võ Thị F', '0956789012', '987 Đường MNO, Q.6, TP.HCM', 120000.00, NULL, 'HOAN_THANH', 'NHANH', 2, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
('ORD-2024-004', 'Đỗ Văn G', '0967890123', '147 Đường PQR, Q.7, TP.HCM', 'Bùi Thị H', '0978901234', '258 Đường STU, Q.8, TP.HCM', 95000.00, 'Giao giờ hành chính', 'HOAN_THANH', 'CHUAN', 2, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
('ORD-2024-005', 'Phan Văn I', '0989012345', '369 Đường VWX, Q.9, TP.HCM', 'Lý Thị K', '0990123456', '741 Đường YZ, Q.10, TP.HCM', 60000.00, NULL, 'HOAN_THANH', 'TIET_KIEM', 2, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
-- Pending orders (2)
('ORD-2024-006', 'Trịnh Văn L', '0901111111', '852 Đường AB, Q.11, TP.HCM', 'Mai Thị M', '0912222222', '963 Đường CD, Q.12, TP.HCM', 45000.00, 'Gọi trước khi giao', 'CHO_GIAO', 'CHUAN', 2, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
('ORD-2024-007', 'Ngô Văn N', '0923333333', '159 Đường EF, Bình Thạnh, TP.HCM', 'Dương Thị O', '0934444444', '357 Đường GH, Phú Nhuận, TP.HCM', 55000.00, NULL, 'CHO_GIAO', 'NHANH', 2, NOW(), NOW()),
-- In-transit orders (2)
('ORD-2024-008', 'Vũ Văn P', '0945555555', '753 Đường IJ, Tân Bình, TP.HCM', 'Lâm Thị Q', '0956666666', '951 Đường KL, Gò Vấp, TP.HCM', 85000.00, 'Hàng cồng kềnh', 'DANG_GIAO', 'NHANH', 2, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
('ORD-2024-009', 'Tô Văn R', '0967777777', '456 Đường MN, Thủ Đức, TP.HCM', 'Hồ Thị S', '0978888888', '789 Đường OP, Bình Tân, TP.HCM', 70000.00, NULL, 'DANG_GIAO', 'CHUAN', 2, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
-- Failed order (1)
('ORD-2024-010', 'Đinh Văn T', '0989999999', '123 Đường QR, Quận 1, TP.HCM', 'Chu Thị U', '0990000000', '321 Đường ST, Quận 2, TP.HCM', 40000.00, 'Không liên lạc được người nhận', 'THAT_BAI', 'TIET_KIEM', 2, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW());

-- ================================================================
-- Insert Invoices for completed orders
-- ================================================================
INSERT INTO invoices (invoice_number, order_id, total_amount, tax_amount, discount_amount, final_amount, notes, status, issue_date, due_date) VALUES
('INV-2024-001', (SELECT id FROM orders WHERE order_code = 'ORD-2024-001'), 50000.00, 5000.00, 0.00, 55000.00, 'Hóa đơn đơn hàng ORD-2024-001', 'PAID', DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 28 DAY)),
('INV-2024-002', (SELECT id FROM orders WHERE order_code = 'ORD-2024-002'), 75000.00, 7500.00, 0.00, 82500.00, 'Hóa đơn đơn hàng ORD-2024-002', 'PAID', DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY)),
('INV-2024-003', (SELECT id FROM orders WHERE order_code = 'ORD-2024-003'), 120000.00, 12000.00, 0.00, 132000.00, 'Hóa đơn đơn hàng ORD-2024-003', 'PENDING', DATE_SUB(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 23 DAY)),
('INV-2024-004', (SELECT id FROM orders WHERE order_code = 'ORD-2024-004'), 95000.00, 9500.00, 0.00, 104500.00, 'Hóa đơn đơn hàng ORD-2024-004', 'PAID', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY)),
('INV-2024-005', (SELECT id FROM orders WHERE order_code = 'ORD-2024-005'), 60000.00, 6000.00, 0.00, 66000.00, 'Hóa đơn đơn hàng ORD-2024-005', 'PENDING', DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY));

-- ================================================================
-- Insert Payments for completed orders
-- ================================================================
INSERT INTO payments (order_id, amount, payment_method, status, transaction_id, created_at) VALUES
((SELECT id FROM orders WHERE order_code = 'ORD-2024-001'), 50000.00, 'CASH', 'COMPLETED', 'TXN-001', DATE_SUB(NOW(), INTERVAL 2 DAY)),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-002'), 75000.00, 'BANK_TRANSFER', 'COMPLETED', 'TXN-002', DATE_SUB(NOW(), INTERVAL 5 DAY)),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-003'), 120000.00, 'VNPAY', 'PENDING', 'TXN-003', DATE_SUB(NOW(), INTERVAL 7 DAY)),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-004'), 95000.00, 'MOMO', 'COMPLETED', 'TXN-004', DATE_SUB(NOW(), INTERVAL 10 DAY)),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-005'), 60000.00, 'COD', 'PENDING', 'TXN-005', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- ================================================================
-- Insert Sample Debts
-- ================================================================
INSERT INTO debts (debtor_id, creditor_id, amount, paid_amount, remaining_amount, debt_type, status, description, due_date) VALUES
(2, 1, 5000000.00, 0.00, 5000000.00, 'CUSTOMER_DEBT', 'PENDING', 'Công nợ khách hàng chưa thanh toán', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(3, 1, 3000000.00, 1000000.00, 2000000.00, 'SHIPPER_DEBT', 'PARTIAL', 'Công nợ shipper đã thanh toán một phần', DATE_ADD(NOW(), INTERVAL 15 DAY)),
(2, 1, 2000000.00, 0.00, 2000000.00, 'CUSTOMER_DEBT', 'OVERDUE', 'Công nợ quá hạn', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 1, 10000000.00, 10000000.00, 0.00, 'PARTNER_DEBT', 'PAID', 'Công nợ đối tác đã thanh toán', NOW());

-- ================================================================
-- Insert Partners (Đối tác)
-- ================================================================
INSERT INTO partners (name, code, contact_person, phone, email, address, partner_type, status, notes) VALUES
('Viettel Post', 'VTEL-001', 'Nguyễn Văn X', '0901111111', 'contact@viettelpost.vn', '123 Đường Lê Lợi, Q.1, TP.HCM', 'SHIPPING', 'ACTIVE', 'Đối tác vận chuyển chính'),
('Vietnam Post', 'VNP-001', 'Trần Thị Y', '0902222222', 'contact@vnpost.vn', '456 Đường Điện Biên Phủ, Q.3, TP.HCM', 'SHIPPING', 'ACTIVE', 'Đối tác bưu điện'),
('Kho Sài Gòn', 'WH-SG-001', 'Lê Văn Z', '0903333333', 'warehouse@sg.com', '789 Đường Xa Lộ Hà Nội, Q.9, TP.HCM', 'WAREHOUSE', 'ACTIVE', 'Kho bãi Sài Gòn'),
('VNPay Gateway', 'VNPAY-001', 'Phạm Thị T', '0904444444', 'support@vnpay.vn', '321 Đường Pasteur, Q.1, TP.HCM', 'PAYMENT', 'ACTIVE', 'Cổng thanh toán VNPay'),
('Nhà cung cấp A', 'SUP-A-001', 'Hoàng Văn K', '0905555555', 'sales@supplier-a.com', '654 Đường CMT8, Q.10, TP.HCM', 'SUPPLIER', 'ACTIVE', 'Nhà cung cấp vật tư');

-- ================================================================
-- Insert Addresses (Địa chỉ)
-- ================================================================
INSERT INTO addresses (name, phone, address, city, district, ward, postal_code, is_default, user_id) VALUES
('Nguyễn Văn A', '0901234567', '123 Đường ABC, Phường 1', 'TP. Hồ Chí Minh', 'Quận 1', 'Phường Bến Nghé', '700000', TRUE, 2),
('Nguyễn Văn A', '0901234567', '456 Đường XYZ, Phường 5', 'TP. Hồ Chí Minh', 'Quận 3', 'Phường 5', '700000', FALSE, 2),
('Trần Văn B', '0912345678', '789 Đường DEF, Phường 10', 'TP. Hồ Chí Minh', 'Quận 10', 'Phường 10', '700000', TRUE, 3);

-- ================================================================
-- Insert Receipts (Biên lai thu chi)
-- ================================================================
INSERT INTO receipts (receipt_number, amount, receipt_type, payer_name, description, receipt_date, created_by, notes) VALUES
('RCPT-IN-001', 5000000.00, 'INCOME', 'Nguyễn Văn A', 'Thu tiền vận chuyển tháng 10', DATE_SUB(NOW(), INTERVAL 3 DAY), 4, 'Thu từ khách hàng'),
('RCPT-OUT-001', 2000000.00, 'EXPENSE', 'Viettel Post', 'Chi phí vận chuyển', DATE_SUB(NOW(), INTERVAL 2 DAY), 4, 'Thanh toán đối tác'),
('RCPT-IN-002', 3000000.00, 'INCOME', 'Lê Văn C', 'Thu tiền COD', DATE_SUB(NOW(), INTERVAL 1 DAY), 4, 'Thu tiền hộ'),
('RCPT-OUT-002', 1500000.00, 'EXPENSE', 'Kho Sài Gòn', 'Thuê kho bãi tháng 10', NOW(), 4, 'Chi phí kho');

-- ================================================================
-- Insert Commissions (Hoa hồng)
-- ================================================================
INSERT INTO commissions (shipper_id, order_id, base_amount, commission_rate, commission_amount, commission_type, status, calculated_date, notes) VALUES
(3, (SELECT id FROM orders WHERE order_code = 'ORD-2024-001'), 50000.00, 10.00, 5000.00, 'DELIVERY', 'PAID', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Hoa hồng giao hàng'),
(3, (SELECT id FROM orders WHERE order_code = 'ORD-2024-002'), 75000.00, 10.00, 7500.00, 'DELIVERY', 'APPROVED', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Hoa hồng giao hàng'),
(3, (SELECT id FROM orders WHERE order_code = 'ORD-2024-003'), 120000.00, 10.00, 12000.00, 'DELIVERY', 'PENDING', CURDATE(), 'Hoa hồng giao hàng'),
(3, NULL, 500000.00, 100.00, 500000.00, 'BONUS', 'PAID', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'Thưởng hoàn thành KPI tháng 9');

-- ================================================================
-- Insert Payrolls (Bảng lương)
-- ================================================================
INSERT INTO payrolls (employee_id, month, year, base_salary, bonus, deduction, total_salary, status, payment_date, notes) VALUES
(3, 9, 2024, 8000000.00, 500000.00, 200000.00, 8300000.00, 'PAID', DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'Lương tháng 9 - Shipper'),
(4, 9, 2024, 12000000.00, 1000000.00, 300000.00, 12700000.00, 'PAID', DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'Lương tháng 9 - Kế toán'),
(3, 10, 2024, 8000000.00, 600000.00, 200000.00, 8400000.00, 'APPROVED', NULL, 'Lương tháng 10 - Shipper'),
(4, 10, 2024, 12000000.00, 1200000.00, 300000.00, 12900000.00, 'PENDING', NULL, 'Lương tháng 10 - Kế toán');

-- ================================================================
-- Insert Bank Reconciliations (Đối soát ngân hàng)
-- ================================================================
INSERT INTO bank_reconciliations (bank_name, account_number, reconciliation_date, statement_balance, book_balance, difference_amount, status, notes) VALUES
('Vietcombank', '0123456789', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 50000000.00, 50000000.00, 0.00, 'MATCHED', 'Đối soát khớp hoàn toàn'),
('Techcombank', '9876543210', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 30000000.00, 29500000.00, 500000.00, 'UNMATCHED', 'Chênh lệch 500k - cần kiểm tra'),
('BIDV', '1122334455', CURDATE(), 75000000.00, 75000000.00, 0.00, 'PENDING', 'Chưa đối soát');

-- ================================================================
-- Insert Financial Reports (Báo cáo tài chính)
-- ================================================================
INSERT INTO financial_reports (report_type, report_period, start_date, end_date, total_revenue, total_expense, net_profit, total_orders, notes, created_by) VALUES
('MONTHLY', 'Tháng 9/2024', '2024-09-01', '2024-09-30', 50000000.00, 30000000.00, 20000000.00, 150, 'Báo cáo tháng 9', 4),
('WEEKLY', 'Tuần 40/2024', '2024-10-01', '2024-10-07', 8000000.00, 5000000.00, 3000000.00, 25, 'Báo cáo tuần 40', 4),
('QUARTERLY', 'Quý 3/2024', '2024-07-01', '2024-09-30', 140000000.00, 85000000.00, 55000000.00, 420, 'Báo cáo quý 3', 4);

-- ================================================================
-- Insert Notifications (Thông báo)
-- ================================================================
INSERT INTO notifications (recipient_type, recipient_id, title, message, type, order_id, is_read, read_at) VALUES
('CUSTOMER', 2, 'Đơn hàng đã được giao', 'Đơn hàng ORD-2024-001 đã được giao thành công', 'SUCCESS', (SELECT id FROM orders WHERE order_code = 'ORD-2024-001'), TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('CUSTOMER', 2, 'Đơn hàng đang vận chuyển', 'Đơn hàng ORD-2024-008 đang được vận chuyển', 'INFO', (SELECT id FROM orders WHERE order_code = 'ORD-2024-008'), FALSE, NULL),
('SHIPPER', 3, 'Có đơn hàng mới', 'Bạn có đơn hàng mới cần nhận: ORD-2024-006', 'INFO', (SELECT id FROM orders WHERE order_code = 'ORD-2024-006'), TRUE, NOW()),
('ALL', NULL, 'Thông báo bảo trì hệ thống', 'Hệ thống sẽ bảo trì từ 2h-4h sáng ngày mai', 'WARNING', NULL, FALSE, NULL),
('CUSTOMER', 2, 'Đơn hàng giao thất bại', 'Đơn hàng ORD-2024-010 giao không thành công', 'ERROR', (SELECT id FROM orders WHERE order_code = 'ORD-2024-010'), TRUE, DATE_SUB(NOW(), INTERVAL 19 DAY));

-- ================================================================
-- Insert Chat Messages (Tin nhắn chat)
-- ================================================================
INSERT INTO chat_messages (content, sender_id, receiver_id, is_from_customer, is_read, read_at) VALUES
('Xin chào, tôi muốn hỏi về đơn hàng ORD-2024-006', 2, 1, TRUE, TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('Chào bạn! Đơn hàng của bạn đang được xử lý. Dự kiến giao trong hôm nay.', 1, 2, FALSE, TRUE, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('Cảm ơn bạn! Vậy khoảng mấy giờ ship đến ạ?', 2, 1, TRUE, TRUE, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('Dự kiến từ 14h-16h chiều nay. Shipper sẽ gọi điện trước khi đến.', 1, 2, FALSE, FALSE, NULL),
('Shipper có thể giao vào buổi tối được không?', 2, 3, TRUE, TRUE, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('Được ạ, tôi sẽ giao vào khoảng 18h-19h tối nay.', 3, 2, FALSE, TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- ================================================================
-- Insert Reviews (Đánh giá)
-- ================================================================
INSERT INTO reviews (order_id, customer_id, rating, comment) VALUES
((SELECT id FROM orders WHERE order_code = 'ORD-2024-001'), 2, 5, 'Dịch vụ tuyệt vời! Giao hàng nhanh, shipper thân thiện.'),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-002'), 2, 4, 'Tốt, nhưng giao hơi chậm so với dự kiến.'),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-003'), 2, 5, 'Rất hài lòng, sẽ sử dụng lại dịch vụ.'),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-004'), 2, 3, 'Bình thường, cần cải thiện thời gian giao hàng.'),
((SELECT id FROM orders WHERE order_code = 'ORD-2024-010'), 2, 1, 'Giao hàng thất bại, không thể liên lạc được shipper.');

-- ================================================================
-- FINAL SUMMARY - TỔNG KẾT DỮ LIỆU
-- ================================================================
SELECT 
    'Sample data inserted successfully!' AS status,
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM user_roles) AS total_user_roles,
    (SELECT COUNT(*) FROM partners) AS total_partners,
    (SELECT COUNT(*) FROM addresses) AS total_addresses,
    (SELECT COUNT(*) FROM orders) AS total_orders,
    (SELECT COUNT(*) FROM orders WHERE status = 'HOAN_THANH') AS completed_orders,
    (SELECT COUNT(*) FROM invoices) AS total_invoices,
    (SELECT COUNT(*) FROM payments) AS total_payments,
    (SELECT COUNT(*) FROM debts) AS total_debts,
    (SELECT COUNT(*) FROM receipts) AS total_receipts,
    (SELECT COUNT(*) FROM commissions) AS total_commissions,
    (SELECT COUNT(*) FROM payrolls) AS total_payrolls,
    (SELECT COUNT(*) FROM bank_reconciliations) AS total_reconciliations,
    (SELECT COUNT(*) FROM financial_reports) AS total_reports,
    (SELECT COUNT(*) FROM notifications) AS total_notifications,
    (SELECT COUNT(*) FROM chat_messages) AS total_messages,
    (SELECT COUNT(*) FROM reviews) AS total_reviews;

