-- ================================================================
-- CREATE ALL TABLES FOR UTEEXPRESS LOGISTICS SYSTEM
-- Database: SQL Server 2019+
-- Charset: UTF-8
-- ================================================================

-- Use database
USE uteexpress;
GO

-- Drop all tables if they exist (reverse order due to FK constraints)
IF OBJECT_ID('reviews', 'U') IS NOT NULL DROP TABLE reviews;
IF OBJECT_ID('chat_messages', 'U') IS NOT NULL DROP TABLE chat_messages;
IF OBJECT_ID('notifications', 'U') IS NOT NULL DROP TABLE notifications;
IF OBJECT_ID('financial_reports', 'U') IS NOT NULL DROP TABLE financial_reports;
IF OBJECT_ID('bank_reconciliations', 'U') IS NOT NULL DROP TABLE bank_reconciliations;
IF OBJECT_ID('payrolls', 'U') IS NOT NULL DROP TABLE payrolls;
IF OBJECT_ID('commissions', 'U') IS NOT NULL DROP TABLE commissions;
IF OBJECT_ID('receipts', 'U') IS NOT NULL DROP TABLE receipts;
IF OBJECT_ID('debts', 'U') IS NOT NULL DROP TABLE debts;
IF OBJECT_ID('payments', 'U') IS NOT NULL DROP TABLE payments;
IF OBJECT_ID('invoices', 'U') IS NOT NULL DROP TABLE invoices;
IF OBJECT_ID('order_items', 'U') IS NOT NULL DROP TABLE order_items;
IF OBJECT_ID('orders', 'U') IS NOT NULL DROP TABLE orders;
IF OBJECT_ID('customer_profiles', 'U') IS NOT NULL DROP TABLE customer_profiles;
IF OBJECT_ID('customers', 'U') IS NOT NULL DROP TABLE customers;
IF OBJECT_ID('addresses', 'U') IS NOT NULL DROP TABLE addresses;
IF OBJECT_ID('shippers', 'U') IS NOT NULL DROP TABLE shippers;
IF OBJECT_ID('warehouses', 'U') IS NOT NULL DROP TABLE warehouses;
IF OBJECT_ID('partners', 'U') IS NOT NULL DROP TABLE partners;
IF OBJECT_ID('user_roles', 'U') IS NOT NULL DROP TABLE user_roles;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;
GO

-- ================================================================
-- 1. USERS TABLE (Người dùng)
-- ================================================================
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100),
    phone NVARCHAR(20),
    avatar_url NVARCHAR(500),
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_phone ON users(phone);
GO

-- ================================================================
-- 2. USER_ROLES TABLE (Vai trò người dùng)
-- ================================================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role NVARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_role ON user_roles(role);
GO

-- ================================================================
-- 3. PARTNERS TABLE (Đối tác)
-- ================================================================
CREATE TABLE partners (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    code NVARCHAR(100) UNIQUE,
    contact_person NVARCHAR(255),
    phone NVARCHAR(50),
    email NVARCHAR(255),
    address NVARCHAR(MAX),
    partner_type NVARCHAR(50),
    status NVARCHAR(50) DEFAULT 'ACTIVE',
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
CREATE INDEX idx_code ON partners(code);
CREATE INDEX idx_type ON partners(partner_type);
CREATE INDEX idx_status ON partners(status);
GO

-- ================================================================
-- 4. SHIPPERS TABLE (Shipper - người giao hàng)
-- ================================================================
CREATE TABLE shippers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code NVARCHAR(100) UNIQUE NOT NULL,
    name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(50) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    vehicle_type NVARCHAR(100),
    vehicle_number NVARCHAR(100),
    is_active BIT DEFAULT 1,
    current_latitude FLOAT,
    current_longitude FLOAT,
    total_deliveries INT DEFAULT 0,
    successful_deliveries INT DEFAULT 0,
    failed_deliveries INT DEFAULT 0,
    user_id BIGINT,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_shippers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_shipper_code ON shippers(code);
CREATE INDEX idx_shipper_user_id ON shippers(user_id);
CREATE INDEX idx_shipper_active ON shippers(is_active);
GO

-- ================================================================
-- 5. WAREHOUSES TABLE (Kho bãi)
-- ================================================================
CREATE TABLE warehouses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code NVARCHAR(100) UNIQUE NOT NULL,
    name NVARCHAR(255) NOT NULL,
    address NVARCHAR(MAX) NOT NULL,
    phone NVARCHAR(50) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    manager NVARCHAR(255),
    total_capacity INT,
    current_stock INT DEFAULT 0,
    user_id BIGINT,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_warehouses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_warehouse_code ON warehouses(code);
CREATE INDEX idx_warehouse_user_id ON warehouses(user_id);
GO

-- ================================================================
-- 6. CUSTOMERS TABLE (Khách hàng)
-- ================================================================
CREATE TABLE customers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(255),
    email NVARCHAR(255),
    phone NVARCHAR(50),
    avatar_url NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
CREATE INDEX idx_customer_username ON customers(username);
GO

-- ================================================================
-- 7. CUSTOMER_PROFILES TABLE (Hồ sơ khách hàng)
-- ================================================================
CREATE TABLE customer_profiles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    loyalty_points INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    total_spent DECIMAL(19,2) DEFAULT 0.00,
    membership_level NVARCHAR(50) DEFAULT 'BRONZE',
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_customer_profiles_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
CREATE INDEX idx_profile_customer_id ON customer_profiles(customer_id);
CREATE INDEX idx_profile_level ON customer_profiles(membership_level);
GO

-- ================================================================
-- 8. ADDRESSES TABLE (Địa chỉ)
-- ================================================================
CREATE TABLE addresses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(50) NOT NULL,
    address NVARCHAR(MAX) NOT NULL,
    city NVARCHAR(100),
    district NVARCHAR(100),
    ward NVARCHAR(100),
    postal_code NVARCHAR(20),
    is_default BIT DEFAULT 0,
    user_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_address_user_id ON addresses(user_id);
CREATE INDEX idx_address_default ON addresses(is_default);
GO

-- ================================================================
-- 9. ORDERS TABLE (Đơn hàng)
-- ================================================================
CREATE TABLE orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_code NVARCHAR(255) UNIQUE NOT NULL,
    sender_name NVARCHAR(255) NOT NULL,
    sender_phone NVARCHAR(50) NOT NULL,
    sender_address NVARCHAR(MAX) NOT NULL,
    recipient_name NVARCHAR(255) NOT NULL,
    recipient_phone NVARCHAR(50) NOT NULL,
    recipient_address NVARCHAR(MAX) NOT NULL,
    shipment_fee DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    cod_amount DECIMAL(19,2) DEFAULT 0.00,
    total_amount DECIMAL(19,2) DEFAULT 0.00,
    weight DECIMAL(10,2),
    notes NVARCHAR(MAX),
    image_url NVARCHAR(500),
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    service_type NVARCHAR(50),
    customer_id BIGINT,
    shipper_id BIGINT,
    pickup_date DATETIME NULL,
    delivery_date DATETIME NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_orders_shipper FOREIGN KEY (shipper_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_order_code ON orders(order_code);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_customer_id ON orders(customer_id);
CREATE INDEX idx_order_shipper_id ON orders(shipper_id);
CREATE INDEX idx_order_created_at ON orders(created_at);
GO

-- ================================================================
-- 10. ORDER_ITEMS TABLE (Chi tiết đơn hàng)
-- ================================================================
CREATE TABLE order_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_name NVARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    weight DECIMAL(10,2),
    description NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
CREATE INDEX idx_item_order_id ON order_items(order_id);
GO

-- ================================================================
-- 11. INVOICES TABLE (Hóa đơn)
-- ================================================================
CREATE TABLE invoices (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    invoice_number NVARCHAR(100) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(19,2) DEFAULT 0.00,
    discount_amount DECIMAL(19,2) DEFAULT 0.00,
    final_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status NVARCHAR(50) DEFAULT 'PENDING',
    issue_date DATE NOT NULL,
    due_date DATE,
    payment_date DATE,
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_invoices_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
CREATE INDEX idx_invoice_number ON invoices(invoice_number);
CREATE INDEX idx_invoice_order_id ON invoices(order_id);
CREATE INDEX idx_invoice_status ON invoices(status);
CREATE INDEX idx_invoice_issue_date ON invoices(issue_date);
GO

-- ================================================================
-- 12. PAYMENTS TABLE (Thanh toán)
-- ================================================================
CREATE TABLE payments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    payment_method NVARCHAR(50) NOT NULL,
    transaction_id NVARCHAR(255),
    status NVARCHAR(50) DEFAULT 'PENDING',
    payment_date DATETIME NULL,
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
CREATE INDEX idx_payment_order_id ON payments(order_id);
CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payment_date ON payments(payment_date);
GO

-- ================================================================
-- 13. DEBTS TABLE (Công nợ)
-- ================================================================
CREATE TABLE debts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    debtor_id BIGINT,
    creditor_id BIGINT,
    amount DECIMAL(19,2) NOT NULL,
    paid_amount DECIMAL(19,2) DEFAULT 0.00,
    remaining_amount DECIMAL(19,2),
    debt_type NVARCHAR(50) NOT NULL,
    status NVARCHAR(50) DEFAULT 'PENDING',
    description NVARCHAR(MAX),
    due_date DATETIME,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_debts_debtor FOREIGN KEY (debtor_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_debts_creditor FOREIGN KEY (creditor_id) REFERENCES users(id) ON DELETE NO ACTION
);
CREATE INDEX idx_debt_debtor_id ON debts(debtor_id);
CREATE INDEX idx_debt_creditor_id ON debts(creditor_id);
CREATE INDEX idx_debt_type ON debts(debt_type);
CREATE INDEX idx_debt_status ON debts(status);
CREATE INDEX idx_debt_due_date ON debts(due_date);
GO

-- ================================================================
-- 14. RECEIPTS TABLE (Biên lai thu chi)
-- ================================================================
CREATE TABLE receipts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_number NVARCHAR(100) UNIQUE NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    receipt_type NVARCHAR(50) NOT NULL,
    payer_name NVARCHAR(255),
    description NVARCHAR(MAX),
    receipt_date DATETIME NOT NULL,
    created_by BIGINT,
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_receipts_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_receipt_number ON receipts(receipt_number);
CREATE INDEX idx_receipt_type ON receipts(receipt_type);
CREATE INDEX idx_receipt_date ON receipts(receipt_date);
GO

-- ================================================================
-- 15. COMMISSIONS TABLE (Hoa hồng)
-- ================================================================
CREATE TABLE commissions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    shipper_id BIGINT NOT NULL,
    order_id BIGINT,
    base_amount DECIMAL(19,2) NOT NULL,
    commission_rate DECIMAL(5,2) NOT NULL,
    commission_amount DECIMAL(19,2) NOT NULL,
    commission_type NVARCHAR(50),
    status NVARCHAR(50) DEFAULT 'PENDING',
    calculated_date DATE,
    paid_date DATE,
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_commissions_shipper FOREIGN KEY (shipper_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_commissions_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
);
CREATE INDEX idx_commission_shipper_id ON commissions(shipper_id);
CREATE INDEX idx_commission_order_id ON commissions(order_id);
CREATE INDEX idx_commission_status ON commissions(status);
CREATE INDEX idx_commission_calculated_date ON commissions(calculated_date);
GO

-- ================================================================
-- 16. PAYROLLS TABLE (Bảng lương)
-- ================================================================
CREATE TABLE payrolls (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    base_salary DECIMAL(19,2) NOT NULL,
    bonus DECIMAL(19,2) DEFAULT 0.00,
    deduction DECIMAL(19,2) DEFAULT 0.00,
    total_salary DECIMAL(19,2) NOT NULL,
    status NVARCHAR(50) DEFAULT 'PENDING',
    payment_date DATE,
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_payrolls_employee FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_employee_month_year UNIQUE (employee_id, month, year)
);
CREATE INDEX idx_payroll_employee_id ON payrolls(employee_id);
CREATE INDEX idx_payroll_status ON payrolls(status);
CREATE INDEX idx_payroll_month_year ON payrolls(month, year);
GO

-- ================================================================
-- 17. BANK_RECONCILIATIONS TABLE (Đối soát ngân hàng)
-- ================================================================
CREATE TABLE bank_reconciliations (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    bank_name NVARCHAR(255) NOT NULL,
    account_number NVARCHAR(100) NOT NULL,
    reconciliation_date DATE NOT NULL,
    statement_balance DECIMAL(19,2) NOT NULL,
    book_balance DECIMAL(19,2) NOT NULL,
    difference_amount DECIMAL(19,2),
    status NVARCHAR(50) DEFAULT 'PENDING',
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
CREATE INDEX idx_recon_bank_name ON bank_reconciliations(bank_name);
CREATE INDEX idx_recon_date ON bank_reconciliations(reconciliation_date);
CREATE INDEX idx_recon_status ON bank_reconciliations(status);
GO

-- ================================================================
-- 18. FINANCIAL_REPORTS TABLE (Báo cáo tài chính)
-- ================================================================
CREATE TABLE financial_reports (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    report_name NVARCHAR(255) NOT NULL,
    report_type NVARCHAR(50) NOT NULL,
    report_date DATE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_revenue DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_expenses DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    net_profit DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    shipping_revenue DECIMAL(19,2) DEFAULT 0.00,
    commission_expenses DECIMAL(19,2) DEFAULT 0.00,
    operational_expenses DECIMAL(19,2) DEFAULT 0.00,
    total_orders INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
);
CREATE INDEX idx_report_type ON financial_reports(report_type);
CREATE INDEX idx_report_start_date ON financial_reports(start_date);
CREATE INDEX idx_report_end_date ON financial_reports(end_date);
CREATE INDEX idx_report_date ON financial_reports(report_date);
GO

-- ================================================================
-- 19. NOTIFICATIONS TABLE (Thông báo)
-- ================================================================
CREATE TABLE notifications (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    recipient_type NVARCHAR(50) NOT NULL,
    recipient_id BIGINT,
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    type NVARCHAR(50) DEFAULT 'INFO',
    order_id BIGINT,
    is_read BIT DEFAULT 0,
    read_at DATETIME NULL,
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
);
CREATE INDEX idx_notif_recipient ON notifications(recipient_type, recipient_id);
CREATE INDEX idx_notif_is_read ON notifications(is_read);
CREATE INDEX idx_notif_created_at ON notifications(created_at);
GO

-- ================================================================
-- 20. CHAT_MESSAGES TABLE (Tin nhắn chat)
-- ================================================================
CREATE TABLE chat_messages (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    content NVARCHAR(MAX) NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    is_from_customer BIT DEFAULT 1,
    is_read BIT DEFAULT 0,
    read_at DATETIME NULL,
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_chat_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE NO ACTION
);
CREATE INDEX idx_chat_sender_id ON chat_messages(sender_id);
CREATE INDEX idx_chat_receiver_id ON chat_messages(receiver_id);
CREATE INDEX idx_chat_is_read ON chat_messages(is_read);
CREATE INDEX idx_chat_created_at ON chat_messages(created_at);
GO

-- ================================================================
-- 21. REVIEWS TABLE (Đánh giá)
-- ================================================================
CREATE TABLE reviews (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE NO ACTION
);
CREATE INDEX idx_review_order_id ON reviews(order_id);
CREATE INDEX idx_review_customer_id ON reviews(customer_id);
CREATE INDEX idx_review_rating ON reviews(rating);
GO

PRINT 'All tables created successfully!';
GO

-- ================================================================
-- INSERT SAMPLE DATA
-- ================================================================

-- ================================================================
-- Insert Users
-- ================================================================
SET IDENTITY_INSERT users ON;
INSERT INTO users (id, username, email, password, full_name, phone, is_active) VALUES
(1, 'admin', 'admin@uteexpress.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Admin System', '0900000000', 1),
(2, 'customer1', 'customer1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Nguyễn Văn A', '0901234567', 1),
(3, 'shipper1', 'shipper1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Trần Văn B', '0912345678', 1),
(4, 'accountant1', 'accountant1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Lê Thị C', '0923456789', 1),
(5, 'shipper2', 'shipper2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Nguyễn Văn C', '0913456789', 1),
(6, 'shipper3', 'shipper3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Lê Văn D', '0914567890', 1),
(7, 'customer2', 'customer2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Trần Thị E', '0915678901', 1),
(8, 'customer3', 'customer3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Phạm Văn F', '0916789012', 1),
(9, 'accountant2', 'accountant2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Hoàng Thị G', '0917890123', 1),
(10, 'warehouse1', 'warehouse1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Võ Văn H', '0918901234', 1);
SET IDENTITY_INSERT users OFF;
GO

-- ================================================================
-- Insert User Roles
-- ================================================================
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_CUSTOMER'),
(3, 'ROLE_SHIPPER'),
(4, 'ROLE_ACCOUNTANT'),
(5, 'ROLE_SHIPPER'),
(6, 'ROLE_SHIPPER'),
(7, 'ROLE_CUSTOMER'),
(8, 'ROLE_CUSTOMER'),
(9, 'ROLE_ACCOUNTANT'),
(10, 'ROLE_WAREHOUSE_STAFF');
GO

-- ================================================================
-- Insert Shippers
-- ================================================================
SET IDENTITY_INSERT shippers ON;
INSERT INTO shippers (id, code, name, phone, email, vehicle_type, vehicle_number, is_active, total_deliveries, successful_deliveries, failed_deliveries, user_id) VALUES
(1, 'SHP001', N'Trần Văn B', '0912345678', 'shipper1@example.com', N'Xe máy', '59-A1 12345', 1, 0, 0, 0, 3),
(2, 'SHP002', N'Nguyễn Văn C', '0913456789', 'shipper2@example.com', N'Xe máy', '59-B2 23456', 1, 15, 14, 1, 5),
(3, 'SHP003', N'Lê Văn D', '0914567890', 'shipper3@example.com', N'Xe tải nhỏ', '59-C3 34567', 1, 25, 23, 2, 6);
SET IDENTITY_INSERT shippers OFF;
GO

-- ================================================================
-- Insert Warehouses
-- ================================================================
SET IDENTITY_INSERT warehouses ON;
INSERT INTO warehouses (id, code, name, address, phone, email, manager, total_capacity, current_stock, user_id) VALUES
(1, 'WH001', N'Kho Trung Tâm TP.HCM', N'123 Đường Xa Lộ Hà Nội, Quận 9, TP.HCM', '0281234567', 'warehouse@uteexpress.com', N'Nguyễn Văn X', 10000, 0, NULL),
(2, 'WH002', N'Kho Hà Nội', N'456 Đường Giải Phóng, Hai Bà Trưng, Hà Nội', '0242345678', 'wh-hanoi@uteexpress.com', N'Đỗ Văn I', 8000, 150, 10),
(3, 'WH003', N'Kho Đà Nẵng', N'789 Đường 2/9, Hải Châu, Đà Nẵng', '0236345678', 'wh-danang@uteexpress.com', N'Bùi Thị K', 5000, 80, NULL);
SET IDENTITY_INSERT warehouses OFF;
GO

-- ================================================================
-- Insert Customers
-- ================================================================
SET IDENTITY_INSERT customers ON;
INSERT INTO customers (id, username, password, full_name, email, phone) VALUES
(1, 'customer1', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Nguyễn Văn A', 'customer1@example.com', '0901234567'),
(2, 'customer2', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Trần Thị E', 'customer2@example.com', '0915678901'),
(3, 'customer3', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JGO.6Q3NQvDJTLLKgVMF9s0Lhpgu', N'Phạm Văn F', 'customer3@example.com', '0916789012');
SET IDENTITY_INSERT customers OFF;
GO

-- ================================================================
-- Insert Customer Profiles
-- ================================================================
SET IDENTITY_INSERT customer_profiles ON;
INSERT INTO customer_profiles (id, customer_id, loyalty_points, total_orders, total_spent, membership_level) VALUES
(1, 1, 150, 10, 1500000.00, 'SILVER'),
(2, 2, 50, 3, 450000.00, 'BRONZE'),
(3, 3, 280, 18, 3200000.00, 'GOLD');
SET IDENTITY_INSERT customer_profiles OFF;
GO

-- ================================================================
-- Insert Partners
-- ================================================================
SET IDENTITY_INSERT partners ON;
INSERT INTO partners (id, name, code, contact_person, phone, email, address, partner_type, status, notes) VALUES
(1, 'Viettel Post', 'VTEL-001', N'Nguyễn Văn X', '0901111111', 'contact@viettelpost.vn', N'123 Đường Lê Lợi, Q.1, TP.HCM', 'SHIPPING', 'ACTIVE', N'Đối tác vận chuyển chính'),
(2, 'Vietnam Post', 'VNP-001', N'Trần Thị Y', '0902222222', 'contact@vnpost.vn', N'456 Đường Điện Biên Phủ, Q.3, TP.HCM', 'SHIPPING', 'ACTIVE', N'Đối tác bưu điện'),
(3, N'Kho Sài Gòn', 'WH-SG-001', N'Lê Văn Z', '0903333333', 'warehouse@sg.com', N'789 Đường Xa Lộ Hà Nội, Q.9, TP.HCM', 'WAREHOUSE', 'ACTIVE', N'Kho bãi Sài Gòn'),
(4, 'VNPay Gateway', 'VNPAY-001', N'Phạm Thị T', '0904444444', 'support@vnpay.vn', N'321 Đường Pasteur, Q.1, TP.HCM', 'PAYMENT', 'ACTIVE', N'Cổng thanh toán VNPay'),
(5, N'Nhà cung cấp A', 'SUP-A-001', N'Hoàng Văn K', '0905555555', 'sales@supplier-a.com', N'654 Đường CMT8, Q.10, TP.HCM', 'SUPPLIER', 'ACTIVE', N'Nhà cung cấp vật tư');
SET IDENTITY_INSERT partners OFF;
GO

-- ================================================================
-- Insert Addresses
-- ================================================================
SET IDENTITY_INSERT addresses ON;
INSERT INTO addresses (id, name, phone, address, city, district, ward, postal_code, is_default, user_id) VALUES
(1, N'Nguyễn Văn A', '0901234567', N'123 Đường ABC, Phường 1', N'TP. Hồ Chí Minh', N'Quận 1', N'Phường Bến Nghé', '700000', 1, 2),
(2, N'Nguyễn Văn A', '0901234567', N'456 Đường XYZ, Phường 5', N'TP. Hồ Chí Minh', N'Quận 3', N'Phường 5', '700000', 0, 2),
(3, N'Trần Văn B', '0912345678', N'789 Đường DEF, Phường 10', N'TP. Hồ Chí Minh', N'Quận 10', N'Phường 10', '700000', 1, 3),
(4, N'Trần Thị E', '0915678901', N'147 Đường Lê Lợi, Phường 3', N'TP. Hồ Chí Minh', N'Quận 5', N'Phường 3', '700000', 1, 7),
(5, N'Trần Thị E', '0915678901', N'258 Đường Hai Bà Trưng, Phường 6', N'TP. Hồ Chí Minh', N'Quận 3', N'Phường 6', '700000', 0, 7),
(6, N'Phạm Văn F', '0916789012', N'369 Đường Nguyễn Huệ, Phường 1', N'TP. Hồ Chí Minh', N'Quận 1', N'Phường Bến Nghé', '700000', 1, 8),
(7, N'Nguyễn Văn A', '0901234567', N'741 Đường Võ Văn Tần, Phường 5', N'TP. Hồ Chí Minh', N'Quận 3', N'Phường 5', '700000', 0, 2);
SET IDENTITY_INSERT addresses OFF;
GO

-- ================================================================
-- Insert Sample Orders (20 orders)
-- ================================================================
SET IDENTITY_INSERT orders ON;
INSERT INTO orders (id, order_code, sender_name, sender_phone, sender_address, recipient_name, recipient_phone, recipient_address, shipment_fee, notes, status, service_type, customer_id, shipper_id, created_at, updated_at) VALUES
-- Completed orders (10)
(1, 'ORD-2024-001', N'Nguyễn Văn A', '0901234567', N'123 Đường ABC, Q.1, TP.HCM', N'Trần Thị B', '0912345678', N'456 Đường XYZ, Q.2, TP.HCM', 50000.00, N'Giao hàng nhanh', 'HOAN_THANH', 'NHANH', 2, NULL, DATEADD(DAY, -2, GETDATE()), GETDATE()),
(2, 'ORD-2024-002', N'Lê Văn C', '0923456789', N'789 Đường DEF, Q.3, TP.HCM', N'Phạm Thị D', '0934567890', N'321 Đường GHI, Q.4, TP.HCM', 75000.00, N'Hàng dễ vỡ', 'HOAN_THANH', 'CHUAN', 2, NULL, DATEADD(DAY, -5, GETDATE()), GETDATE()),
(3, 'ORD-2024-003', N'Hoàng Văn E', '0945678901', N'654 Đường JKL, Q.5, TP.HCM', N'Võ Thị F', '0956789012', N'987 Đường MNO, Q.6, TP.HCM', 120000.00, NULL, 'HOAN_THANH', 'NHANH', 2, NULL, DATEADD(DAY, -7, GETDATE()), GETDATE()),
(4, 'ORD-2024-004', N'Đỗ Văn G', '0967890123', N'147 Đường PQR, Q.7, TP.HCM', N'Bùi Thị H', '0978901234', N'258 Đường STU, Q.8, TP.HCM', 95000.00, N'Giao giờ hành chính', 'HOAN_THANH', 'CHUAN', 2, NULL, DATEADD(DAY, -10, GETDATE()), GETDATE()),
(5, 'ORD-2024-005', N'Phan Văn I', '0989012345', N'369 Đường VWX, Q.9, TP.HCM', N'Lý Thị K', '0990123456', N'741 Đường YZ, Q.10, TP.HCM', 60000.00, NULL, 'HOAN_THANH', 'TIET_KIEM', 2, NULL, DATEADD(DAY, -15, GETDATE()), GETDATE()),
(11, 'ORD-2024-011', N'Nguyễn Văn A', '0901234567', N'123 Đường ABC, Q.1, TP.HCM', N'Lê Thị X', '0919999999', N'456 Đường DEF, Q.2, TP.HCM', 45000.00, NULL, 'HOAN_THANH', 'CHUAN', 2, 1, DATEADD(DAY, -3, GETDATE()), GETDATE()),
(12, 'ORD-2024-012', N'Trần Thị E', '0915678901', N'147 Đường Lê Lợi, Q.5, TP.HCM', N'Mai Văn Y', '0918888888', N'789 Đường GHI, Q.6, TP.HCM', 60000.00, N'Giao nhanh', 'HOAN_THANH', 'NHANH', 7, 2, DATEADD(DAY, -4, GETDATE()), GETDATE()),
(13, 'ORD-2024-013', N'Phạm Văn F', '0916789012', N'369 Đường Nguyễn Huệ, Q.1, TP.HCM', N'Vũ Thị Z', '0917777777', N'951 Đường JKL, Q.7, TP.HCM', 75000.00, NULL, 'HOAN_THANH', 'NHANH', 8, 3, DATEADD(DAY, -5, GETDATE()), GETDATE()),
(14, 'ORD-2024-014', N'Nguyễn Văn A', '0901234567', N'123 Đường ABC, Q.1, TP.HCM', N'Hồ Văn W', '0916666666', N'357 Đường MNO, Q.8, TP.HCM', 50000.00, NULL, 'HOAN_THANH', 'CHUAN', 2, 1, DATEADD(DAY, -6, GETDATE()), GETDATE()),
(15, 'ORD-2024-015', N'Trần Thị E', '0915678901', N'147 Đường Lê Lợi, Q.5, TP.HCM', N'Chu Thị V', '0915555555', N'159 Đường PQR, Q.9, TP.HCM', 85000.00, N'Hàng dễ vỡ', 'HOAN_THANH', 'NHANH', 7, 2, DATEADD(DAY, -8, GETDATE()), GETDATE()),
-- In-transit orders (6)
(6, 'ORD-2024-006', N'Trịnh Văn L', '0901111111', N'852 Đường AB, Q.11, TP.HCM', N'Mai Thị M', '0912222222', N'963 Đường CD, Q.12, TP.HCM', 45000.00, N'Gọi trước khi giao', 'DANG_GIAO', 'CHUAN', 2, NULL, DATEADD(DAY, -1, GETDATE()), GETDATE()),
(7, 'ORD-2024-007', N'Ngô Văn N', '0923333333', N'159 Đường EF, Bình Thạnh, TP.HCM', N'Dương Thị O', '0934444444', N'357 Đường GH, Phú Nhuận, TP.HCM', 55000.00, NULL, 'DANG_GIAO', 'NHANH', 2, NULL, GETDATE(), GETDATE()),
(8, 'ORD-2024-008', N'Vũ Văn P', '0945555555', N'753 Đường IJ, Tân Bình, TP.HCM', N'Lâm Thị Q', '0956666666', N'951 Đường KL, Gò Vấp, TP.HCM', 85000.00, N'Hàng cồng kềnh', 'DANG_GIAO', 'NHANH', 2, NULL, DATEADD(DAY, -3, GETDATE()), GETDATE()),
(9, 'ORD-2024-009', N'Tô Văn R', '0967777777', N'456 Đường MN, Thủ Đức, TP.HCM', N'Hồ Thị S', '0978888888', N'789 Đường OP, Bình Tân, TP.HCM', 70000.00, NULL, 'DANG_GIAO', 'CHUAN', 2, NULL, DATEADD(DAY, -1, GETDATE()), GETDATE()),
(16, 'ORD-2024-016', N'Phạm Văn F', '0916789012', N'369 Đường Nguyễn Huệ, Q.1, TP.HCM', N'Đinh Văn U', '0914444444', N'753 Đường STU, Q.10, TP.HCM', 55000.00, NULL, 'DANG_GIAO', 'CHUAN', 8, 3, DATEADD(DAY, -1, GETDATE()), GETDATE()),
(17, 'ORD-2024-017', N'Nguyễn Văn A', '0901234567', N'123 Đường ABC, Q.1, TP.HCM', N'Tô Thị T', '0913333333', N'951 Đường VWX, Q.11, TP.HCM', 65000.00, N'Gọi trước 30 phút', 'DANG_GIAO', 'NHANH', 2, 1, GETDATE(), GETDATE()),
-- Pending orders (3)
(18, 'ORD-2024-018', N'Trần Thị E', '0915678901', N'147 Đường Lê Lợi, Q.5, TP.HCM', N'Lâm Văn S', '0912222222', N'456 Đường YZ, Q.12, TP.HCM', 48000.00, NULL, 'CHO_GIAO', 'CHUAN', 7, NULL, GETDATE(), GETDATE()),
(19, 'ORD-2024-019', N'Phạm Văn F', '0916789012', N'369 Đường Nguyễn Huệ, Q.1, TP.HCM', N'Phan Thị R', '0911111111', N'147 Đường ABC, Tân Bình, TP.HCM', 70000.00, N'Giao buổi chiều', 'CHO_GIAO', 'NHANH', 8, NULL, GETDATE(), GETDATE()),
(20, 'ORD-2024-020', N'Nguyễn Văn A', '0901234567', N'123 Đường ABC, Q.1, TP.HCM', N'Võ Văn Q', '0910000000', N'258 Đường DEF, Bình Thạnh, TP.HCM', 40000.00, NULL, 'CHO_GIAO', 'TIET_KIEM', 2, NULL, GETDATE(), GETDATE()),
-- Failed order (1)
(10, 'ORD-2024-010', N'Đinh Văn T', '0989999999', N'123 Đường QR, Quận 1, TP.HCM', N'Chu Thị U', '0990000000', N'321 Đường ST, Quận 2, TP.HCM', 40000.00, N'Không liên lạc được người nhận', 'THAT_BAI', 'TIET_KIEM', 2, NULL, DATEADD(DAY, -20, GETDATE()), GETDATE());
SET IDENTITY_INSERT orders OFF;
GO

-- ================================================================
-- Insert Order Items
-- ================================================================
SET IDENTITY_INSERT order_items ON;
INSERT INTO order_items (id, order_id, product_name, quantity, weight, description) VALUES
(1, 11, N'Quần áo', 2, 1.5, N'Áo thun và quần jean'),
(2, 11, N'Giày thể thao', 1, 0.8, N'Giày Nike size 42'),
(3, 12, N'Điện thoại', 1, 0.5, N'iPhone 15 Pro'),
(4, 13, N'Laptop', 1, 2.0, N'Macbook Pro 14 inch'),
(5, 14, N'Sách', 5, 1.2, N'Sách văn học'),
(6, 15, N'Đồ gốm sứ', 3, 2.5, N'Bộ chén đĩa cao cấp'),
(7, 16, N'Thực phẩm', 10, 5.0, N'Bánh kẹo các loại'),
(8, 17, N'Mỹ phẩm', 4, 0.8, N'Son môi và kem dưỡng da');
SET IDENTITY_INSERT order_items OFF;
GO

-- ================================================================
-- Insert Invoices
-- ================================================================
SET IDENTITY_INSERT invoices ON;
INSERT INTO invoices (id, invoice_number, order_id, total_amount, tax_amount, discount_amount, final_amount, notes, status, issue_date, due_date) VALUES
(1, 'INV-2024-001', 1, 50000.00, 5000.00, 0.00, 55000.00, N'Hóa đơn đơn hàng ORD-2024-001', 'PAID', CAST(DATEADD(DAY, -2, GETDATE()) AS DATE), CAST(DATEADD(DAY, 28, GETDATE()) AS DATE)),
(2, 'INV-2024-002', 2, 75000.00, 7500.00, 0.00, 82500.00, N'Hóa đơn đơn hàng ORD-2024-002', 'PAID', CAST(DATEADD(DAY, -5, GETDATE()) AS DATE), CAST(DATEADD(DAY, 25, GETDATE()) AS DATE)),
(3, 'INV-2024-003', 3, 120000.00, 12000.00, 0.00, 132000.00, N'Hóa đơn đơn hàng ORD-2024-003', 'PENDING', CAST(DATEADD(DAY, -7, GETDATE()) AS DATE), CAST(DATEADD(DAY, 23, GETDATE()) AS DATE)),
(4, 'INV-2024-004', 4, 95000.00, 9500.00, 0.00, 104500.00, N'Hóa đơn đơn hàng ORD-2024-004', 'PAID', CAST(DATEADD(DAY, -10, GETDATE()) AS DATE), CAST(DATEADD(DAY, 20, GETDATE()) AS DATE)),
(5, 'INV-2024-005', 5, 60000.00, 6000.00, 0.00, 66000.00, N'Hóa đơn đơn hàng ORD-2024-005', 'PENDING', CAST(DATEADD(DAY, -15, GETDATE()) AS DATE), CAST(DATEADD(DAY, 15, GETDATE()) AS DATE)),
(6, 'INV-2024-006', 11, 45000.00, 4500.00, 0.00, 49500.00, N'Hóa đơn đơn hàng ORD-2024-011', 'PAID', CAST(DATEADD(DAY, -3, GETDATE()) AS DATE), CAST(DATEADD(DAY, 27, GETDATE()) AS DATE)),
(7, 'INV-2024-007', 12, 60000.00, 6000.00, 0.00, 66000.00, N'Hóa đơn đơn hàng ORD-2024-012', 'PAID', CAST(DATEADD(DAY, -4, GETDATE()) AS DATE), CAST(DATEADD(DAY, 26, GETDATE()) AS DATE)),
(8, 'INV-2024-008', 13, 75000.00, 7500.00, 0.00, 82500.00, N'Hóa đơn đơn hàng ORD-2024-013', 'PENDING', CAST(DATEADD(DAY, -5, GETDATE()) AS DATE), CAST(DATEADD(DAY, 25, GETDATE()) AS DATE)),
(9, 'INV-2024-009', 14, 50000.00, 5000.00, 0.00, 55000.00, N'Hóa đơn đơn hàng ORD-2024-014', 'PAID', CAST(DATEADD(DAY, -6, GETDATE()) AS DATE), CAST(DATEADD(DAY, 24, GETDATE()) AS DATE)),
(10, 'INV-2024-010', 15, 85000.00, 8500.00, 0.00, 93500.00, N'Hóa đơn đơn hàng ORD-2024-015', 'PENDING', CAST(DATEADD(DAY, -8, GETDATE()) AS DATE), CAST(DATEADD(DAY, 22, GETDATE()) AS DATE));
SET IDENTITY_INSERT invoices OFF;
GO

-- ================================================================
-- Insert Payments
-- ================================================================
SET IDENTITY_INSERT payments ON;
INSERT INTO payments (id, order_id, amount, payment_method, status, transaction_id, created_at) VALUES
(1, 1, 50000.00, 'CASH', 'COMPLETED', 'TXN-001', DATEADD(DAY, -2, GETDATE())),
(2, 2, 75000.00, 'BANK_TRANSFER', 'COMPLETED', 'TXN-002', DATEADD(DAY, -5, GETDATE())),
(3, 3, 120000.00, 'VNPAY', 'PENDING', 'TXN-003', DATEADD(DAY, -7, GETDATE())),
(4, 4, 95000.00, 'MOMO', 'COMPLETED', 'TXN-004', DATEADD(DAY, -10, GETDATE())),
(5, 5, 60000.00, 'COD', 'PENDING', 'TXN-005', DATEADD(DAY, -15, GETDATE())),
(6, 11, 45000.00, 'CASH', 'COMPLETED', 'TXN-011', DATEADD(DAY, -3, GETDATE())),
(7, 12, 60000.00, 'VNPAY', 'COMPLETED', 'TXN-012', DATEADD(DAY, -4, GETDATE())),
(8, 13, 75000.00, 'MOMO', 'PENDING', 'TXN-013', DATEADD(DAY, -5, GETDATE())),
(9, 14, 50000.00, 'BANK_TRANSFER', 'COMPLETED', 'TXN-014', DATEADD(DAY, -6, GETDATE())),
(10, 15, 85000.00, 'COD', 'PENDING', 'TXN-015', DATEADD(DAY, -8, GETDATE()));
SET IDENTITY_INSERT payments OFF;
GO

-- ================================================================
-- Insert Debts
-- ================================================================
SET IDENTITY_INSERT debts ON;
INSERT INTO debts (id, debtor_id, creditor_id, amount, paid_amount, remaining_amount, debt_type, status, description, due_date) VALUES
(1, 2, 1, 5000000.00, 0.00, 5000000.00, 'CUSTOMER_DEBT', 'PENDING', N'Công nợ khách hàng chưa thanh toán', DATEADD(DAY, 30, GETDATE())),
(2, 3, 1, 3000000.00, 1000000.00, 2000000.00, 'SHIPPER_DEBT', 'PARTIAL', N'Công nợ shipper đã thanh toán một phần', DATEADD(DAY, 15, GETDATE())),
(3, 2, 1, 2000000.00, 0.00, 2000000.00, 'CUSTOMER_DEBT', 'OVERDUE', N'Công nợ quá hạn', DATEADD(DAY, -5, GETDATE())),
(4, 4, 1, 10000000.00, 10000000.00, 0.00, 'PARTNER_DEBT', 'PAID', N'Công nợ đối tác đã thanh toán', GETDATE());
SET IDENTITY_INSERT debts OFF;
GO

-- ================================================================
-- Insert Receipts
-- ================================================================
SET IDENTITY_INSERT receipts ON;
INSERT INTO receipts (id, receipt_number, amount, receipt_type, payer_name, description, receipt_date, created_by, notes) VALUES
(1, 'RCPT-IN-001', 5000000.00, 'INCOME', N'Nguyễn Văn A', N'Thu tiền vận chuyển tháng 10', DATEADD(DAY, -3, GETDATE()), 4, N'Thu từ khách hàng'),
(2, 'RCPT-OUT-001', 2000000.00, 'EXPENSE', 'Viettel Post', N'Chi phí vận chuyển', DATEADD(DAY, -2, GETDATE()), 4, N'Thanh toán đối tác'),
(3, 'RCPT-IN-002', 3000000.00, 'INCOME', N'Lê Văn C', N'Thu tiền COD', DATEADD(DAY, -1, GETDATE()), 4, N'Thu tiền hộ'),
(4, 'RCPT-OUT-002', 1500000.00, 'EXPENSE', N'Kho Sài Gòn', N'Thuê kho bãi tháng 10', GETDATE(), 4, N'Chi phí kho');
SET IDENTITY_INSERT receipts OFF;
GO

-- ================================================================
-- Insert Commissions
-- ================================================================
SET IDENTITY_INSERT commissions ON;
INSERT INTO commissions (id, shipper_id, order_id, base_amount, commission_rate, commission_amount, commission_type, status, calculated_date, notes) VALUES
-- Paid commissions
(1, 1, 1, 50000.00, 10.00, 5000.00, 'DELIVERY', 'PAID', CAST(DATEADD(DAY, -1, GETDATE()) AS DATE), N'Hoa hồng giao hàng'),
(2, 1, 11, 45000.00, 10.00, 4500.00, 'DELIVERY', 'PAID', CAST(DATEADD(DAY, -2, GETDATE()) AS DATE), N'Hoa hồng giao hàng ORD-2024-011'),
(3, 2, 12, 60000.00, 10.00, 6000.00, 'DELIVERY', 'PAID', CAST(DATEADD(DAY, -3, GETDATE()) AS DATE), N'Hoa hồng giao hàng ORD-2024-012'),
-- Approved commissions
(4, 1, 2, 75000.00, 10.00, 7500.00, 'DELIVERY', 'APPROVED', CAST(DATEADD(DAY, -1, GETDATE()) AS DATE), N'Hoa hồng giao hàng'),
(5, 3, 13, 75000.00, 10.00, 7500.00, 'DELIVERY', 'APPROVED', CAST(DATEADD(DAY, -4, GETDATE()) AS DATE), N'Hoa hồng giao hàng ORD-2024-013'),
(6, 1, 14, 50000.00, 10.00, 5000.00, 'DELIVERY', 'APPROVED', CAST(DATEADD(DAY, -5, GETDATE()) AS DATE), N'Hoa hồng giao hàng ORD-2024-014'),
-- Pending commissions
(7, 1, 3, 120000.00, 10.00, 12000.00, 'DELIVERY', 'PENDING', CAST(GETDATE() AS DATE), N'Hoa hồng giao hàng'),
(8, 2, 15, 85000.00, 10.00, 8500.00, 'DELIVERY', 'PENDING', CAST(GETDATE() AS DATE), N'Hoa hồng giao hàng ORD-2024-015'),
-- Bonus commissions
(9, 1, NULL, 500000.00, 100.00, 500000.00, 'BONUS', 'PAID', CAST(DATEADD(DAY, -10, GETDATE()) AS DATE), N'Thưởng xuất sắc tháng 10'),
(10, 2, NULL, 300000.00, 100.00, 300000.00, 'BONUS', 'APPROVED', CAST(DATEADD(DAY, -8, GETDATE()) AS DATE), N'Thưởng hoàn thành KPI'),
(11, 3, NULL, 200000.00, 100.00, 200000.00, 'BONUS', 'PENDING', CAST(GETDATE() AS DATE), N'Thưởng giao hàng nhanh');
SET IDENTITY_INSERT commissions OFF;
GO

-- ================================================================
-- Insert Payrolls
-- ================================================================
SET IDENTITY_INSERT payrolls ON;
INSERT INTO payrolls (id, employee_id, month, year, base_salary, bonus, deduction, total_salary, status, payment_date, notes) VALUES
(1, 3, 9, 2024, 8000000.00, 500000.00, 200000.00, 8300000.00, 'PAID', CAST(DATEADD(DAY, -20, GETDATE()) AS DATE), N'Lương tháng 9 - Shipper'),
(2, 4, 9, 2024, 12000000.00, 1000000.00, 300000.00, 12700000.00, 'PAID', CAST(DATEADD(DAY, -20, GETDATE()) AS DATE), N'Lương tháng 9 - Kế toán'),
(3, 3, 10, 2024, 8000000.00, 600000.00, 200000.00, 8400000.00, 'APPROVED', NULL, N'Lương tháng 10 - Shipper'),
(4, 4, 10, 2024, 12000000.00, 1200000.00, 300000.00, 12900000.00, 'PENDING', NULL, N'Lương tháng 10 - Kế toán');
SET IDENTITY_INSERT payrolls OFF;
GO

-- ================================================================
-- Insert Bank Reconciliations
-- ================================================================
SET IDENTITY_INSERT bank_reconciliations ON;
INSERT INTO bank_reconciliations (id, bank_name, account_number, reconciliation_date, statement_balance, book_balance, difference_amount, status, notes) VALUES
(1, 'Vietcombank', '0123456789', CAST(DATEADD(DAY, -1, GETDATE()) AS DATE), 50000000.00, 50000000.00, 0.00, 'MATCHED', N'Đối soát khớp hoàn toàn'),
(2, 'Techcombank', '9876543210', CAST(DATEADD(DAY, -1, GETDATE()) AS DATE), 30000000.00, 29500000.00, 500000.00, 'UNMATCHED', N'Chênh lệch 500k - cần kiểm tra'),
(3, 'BIDV', '1122334455', CAST(GETDATE() AS DATE), 75000000.00, 75000000.00, 0.00, 'PENDING', N'Chưa đối soát');
SET IDENTITY_INSERT bank_reconciliations OFF;
GO

-- ================================================================
-- Insert Financial Reports
-- ================================================================
SET IDENTITY_INSERT financial_reports ON;
INSERT INTO financial_reports (id, report_name, report_type, report_date, start_date, end_date, total_revenue, total_expenses, net_profit, shipping_revenue, commission_expenses, operational_expenses, total_orders) VALUES
(1, N'Báo cáo tháng 9/2024', 'MONTHLY', '2024-09-30', '2024-09-01', '2024-09-30', 50000000.00, 30000000.00, 20000000.00, 45000000.00, 5000000.00, 25000000.00, 150),
(2, N'Báo cáo tuần 40/2024', 'WEEKLY', '2024-10-07', '2024-10-01', '2024-10-07', 8000000.00, 5000000.00, 3000000.00, 7000000.00, 1000000.00, 4000000.00, 25),
(3, N'Báo cáo quý 3/2024', 'QUARTERLY', '2024-09-30', '2024-07-01', '2024-09-30', 140000000.00, 85000000.00, 55000000.00, 125000000.00, 15000000.00, 70000000.00, 420);
SET IDENTITY_INSERT financial_reports OFF;
GO

-- ================================================================
-- Insert Notifications
-- ================================================================
SET IDENTITY_INSERT notifications ON;
INSERT INTO notifications (id, recipient_type, recipient_id, title, message, type, order_id, is_read, read_at) VALUES
(1, 'CUSTOMER', 2, N'Đơn hàng đã được giao', N'Đơn hàng ORD-2024-001 đã được giao thành công', 'SUCCESS', 1, 1, DATEADD(DAY, -1, GETDATE())),
(2, 'CUSTOMER', 2, N'Đơn hàng đang vận chuyển', N'Đơn hàng ORD-2024-008 đang được vận chuyển', 'INFO', 8, 0, NULL),
(3, 'CUSTOMER', 2, N'Đơn hàng đã được giao', N'Đơn hàng ORD-2024-011 đã được giao thành công', 'SUCCESS', 11, 1, DATEADD(DAY, -2, GETDATE())),
(4, 'CUSTOMER', 7, N'Đơn hàng đã được giao', N'Đơn hàng ORD-2024-012 đã được giao thành công', 'SUCCESS', 12, 1, DATEADD(DAY, -3, GETDATE())),
(5, 'CUSTOMER', 8, N'Đơn hàng đang vận chuyển', N'Đơn hàng ORD-2024-016 đang được vận chuyển', 'INFO', 16, 0, NULL),
(6, 'SHIPPER', 3, N'Có đơn hàng mới', N'Bạn có đơn hàng mới cần nhận: ORD-2024-006', 'INFO', 6, 1, GETDATE()),
(7, 'SHIPPER', 5, N'Có đơn hàng mới', N'Bạn có đơn hàng mới cần nhận: ORD-2024-012', 'INFO', 12, 1, DATEADD(DAY, -4, GETDATE())),
(8, 'SHIPPER', 6, N'Hoa hồng đã được duyệt', N'Hoa hồng 300,000đ đã được duyệt', 'SUCCESS', NULL, 0, NULL),
(9, 'ALL', NULL, N'Thông báo bảo trì hệ thống', N'Hệ thống sẽ bảo trì từ 2h-4h sáng ngày mai', 'WARNING', NULL, 0, NULL),
(10, 'ALL', NULL, N'Khuyến mãi tháng 11', N'Giảm 20% phí vận chuyển cho đơn hàng đầu tiên', 'INFO', NULL, 0, NULL),
(11, 'CUSTOMER', 2, N'Đơn hàng giao thất bại', N'Đơn hàng ORD-2024-010 giao không thành công', 'ERROR', 10, 1, DATEADD(DAY, -19, GETDATE()));
SET IDENTITY_INSERT notifications OFF;
GO

-- ================================================================
-- Insert Chat Messages
-- ================================================================
SET IDENTITY_INSERT chat_messages ON;
INSERT INTO chat_messages (id, content, sender_id, receiver_id, is_from_customer, is_read, read_at) VALUES
(1, N'Xin chào, tôi muốn hỏi về đơn hàng ORD-2024-006', 2, 1, 1, 1, DATEADD(HOUR, -2, GETDATE())),
(2, N'Chào bạn! Đơn hàng của bạn đang được xử lý. Dự kiến giao trong hôm nay.', 1, 2, 0, 1, DATEADD(HOUR, -1, GETDATE())),
(3, N'Cảm ơn bạn! Vậy khoảng mấy giờ ship đến ạ?', 2, 1, 1, 1, DATEADD(MINUTE, -30, GETDATE())),
(4, N'Dự kiến từ 14h-16h chiều nay. Shipper sẽ gọi điện trước khi đến.', 1, 2, 0, 0, NULL),
(5, N'Shipper có thể giao vào buổi tối được không?', 2, 3, 1, 1, DATEADD(HOUR, -3, GETDATE())),
(6, N'Được ạ, tôi sẽ giao vào khoảng 18h-19h tối nay.', 3, 2, 0, 1, DATEADD(HOUR, -2, GETDATE()));
SET IDENTITY_INSERT chat_messages OFF;
GO

-- ================================================================
-- Insert Reviews
-- ================================================================
SET IDENTITY_INSERT reviews ON;
INSERT INTO reviews (id, order_id, customer_id, rating, comment) VALUES
(1, 1, 2, 5, N'Dịch vụ tuyệt vời! Giao hàng nhanh, shipper thân thiện.'),
(2, 2, 2, 4, N'Tốt, nhưng giao hơi chậm so với dự kiến.'),
(3, 3, 2, 5, N'Rất hài lòng, sẽ sử dụng lại dịch vụ.'),
(4, 4, 2, 3, N'Bình thường, cần cải thiện thời gian giao hàng.'),
(5, 10, 2, 1, N'Giao hàng thất bại, không thể liên lạc được shipper.'),
(6, 11, 2, 5, N'Dịch vụ tuyệt vời! Shipper rất nhiệt tình.'),
(7, 12, 7, 4, N'Giao hàng nhanh nhưng đóng gói hơi sơ sài.'),
(8, 13, 8, 5, N'Rất hài lòng! Sẽ tiếp tục sử dụng dịch vụ.'),
(9, 14, 2, 3, N'Tạm được, thời gian giao hàng hơi chậm.');
SET IDENTITY_INSERT reviews OFF;
GO

PRINT 'Sample data inserted successfully!';
GO

-- ================================================================

