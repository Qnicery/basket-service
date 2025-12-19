-- Создаём схему basket
CREATE SCHEMA IF NOT EXISTS basket;

-- Таблица корзин
CREATE TABLE IF NOT EXISTS basket.cart (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Таблица элементов корзины
CREATE TABLE IF NOT EXISTS basket.cart_items (
    cart_id BIGINT NOT NULL REFERENCES basket.cart(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    added_at TIMESTAMP NOT NULL,
    PRIMARY KEY (cart_id, product_id)
);

-- Таблица заказов
CREATE TABLE IF NOT EXISTS basket.orders (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Таблица элементов заказа
CREATE TABLE IF NOT EXISTS basket.order_items (
    order_id BIGINT NOT NULL REFERENCES basket.orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

-- Вставим дефолтную корзину для user_id=0, чтобы addItemNoAuth сразу работал
INSERT INTO basket.cart (user_id, created_at, updated_at)
VALUES (0, NOW(), NOW())
ON CONFLICT DO NOTHING;
