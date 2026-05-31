-- Clear existing data to avoid constraint violations during testing
TRUNCATE TABLE reviews, inventories, cart_items, carts, order_line_items, orders, products, categories CASCADE;

-- 1. Insert Categories
INSERT INTO categories (id, name, slug) VALUES
                                            ('c1111111-e89b-12d3-a456-426614174000', 'Electronics', 'electronics'),
                                            ('c2222222-e89b-12d3-a456-426614174000', 'Apparel & Clothing', 'apparel-clothing'),
                                            ('c3333333-e89b-12d3-a456-426614174000', 'Home & Kitchen', 'home-kitchen');

-- 2. Insert Products
INSERT INTO products (id, sku, name, description, price, category_id) VALUES
                                                                          ('p1111111-e89b-12d3-a456-426614174000', 'SKU-MACBOOK-14', 'MacBook Pro 14 Inch', 'M3 Pro chip, 18GB Unified Memory, 512GB SSD', 1999.99, 'c1111111-e89b-12d3-a456-426614174000'),
                                                                          ('p2222222-e89b-12d3-a456-426614174000', 'SKU-IPHONE-15', 'iPhone 15 Pro Max', 'Titanium body, A17 Pro Chip, 256GB Storage', 1199.50, 'c1111111-e89b-12d3-a456-426614174000'),
                                                                          ('p3333333-e89b-12d3-a456-426614174000', 'SKU-HOODIE-BLK', 'Classic Black Hoodie', '100% organic heavy cotton streetwear hoodie', 59.99, 'c2222222-e89b-12d3-a456-426614174000'),
                                                                          ('p4444444-e89b-12d3-a456-426614174000', 'SKU-COFFEE-MUG', 'Smart Temperature Mug', 'App-controlled heated ceramic coffee mug', 129.00, 'c3333333-e89b-12d3-a456-426614174000');

-- 3. Insert Inventory Details
INSERT INTO inventories (id, product_id, available_stock, warehouse_location, is_backorder_allowed) VALUES
                                                                                                        ('i1111111-e89b-12d3-a456-426614174000', 'p1111111-e89b-12d3-a456-426614174000', 45, 'WH-EAST-A3', false),
                                                                                                        ('i2222222-e89b-12d3-a456-426614174000', 'p2222222-e89b-12d3-a456-426614174000', 120, 'WH-EAST-A4', false),
                                                                                                        ('i3333333-e89b-12d3-a456-426614174000', 'p3333333-e89b-12d3-a456-426614174000', 350, 'WH-WEST-B1', true),
                                                                                                        ('i4444444-e89b-12d3-a456-426614174000', 'p4444444-e89b-12d3-a456-426614174000', 0, 'WH-CENTRAL-C2', true);

-- 4. Insert Initial Reviews
INSERT INTO reviews (id, product_id, rating, headline, comment, author_name, created_at) VALUES
    ('r1111111-e89b-12d3-a456-426614174000', 'p1111111-e89b-12d3-a456-426614174000', 5, 'Absolute Beast of a Machine!', 'The compilation speeds on this M3 Pro chip are unbelievable. Worth every single penny.', 'DevJane', NOW());