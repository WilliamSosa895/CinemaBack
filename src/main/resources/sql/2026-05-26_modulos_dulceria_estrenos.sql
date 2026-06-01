-- Migration compatible with current schema (users.id_user)
-- Creates candy-store and upcoming-releases tables requested by product scope.

CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    cantidad_disponible INTEGER NOT NULL DEFAULT 0 CHECK (cantidad_disponible >= 0),
    imagen_url TEXT
);

CREATE TABLE IF NOT EXISTS combos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    activo BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS combo_productos (
    id BIGSERIAL PRIMARY KEY,
    combo_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    CONSTRAINT fk_combo_productos_combo
        FOREIGN KEY (combo_id)
        REFERENCES combos(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_combo_productos_producto
        FOREIGN KEY (producto_id)
        REFERENCES productos(id)
        ON DELETE RESTRICT,
    CONSTRAINT uk_combo_producto UNIQUE (combo_id, producto_id)
);

CREATE INDEX IF NOT EXISTS idx_combo_productos_combo_id ON combo_productos(combo_id);
CREATE INDEX IF NOT EXISTS idx_combo_productos_producto_id ON combo_productos(producto_id);

CREATE TABLE IF NOT EXISTS compras_productos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha TIMESTAMPTZ NOT NULL DEFAULT now(),
    total NUMERIC(10,2) NOT NULL CHECK (total >= 0),
    codigo_qr TEXT,
    CONSTRAINT fk_compras_productos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES users(id_user)
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_compras_productos_usuario_id ON compras_productos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_compras_productos_fecha ON compras_productos(fecha);

CREATE TABLE IF NOT EXISTS compras_productos_items (
    id BIGSERIAL PRIMARY KEY,
    compra_producto_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    CONSTRAINT fk_compras_productos_items_compra
        FOREIGN KEY (compra_producto_id) REFERENCES compras_productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_compras_productos_items_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_compras_productos_items_compra_id ON compras_productos_items(compra_producto_id);
CREATE INDEX IF NOT EXISTS idx_compras_productos_items_producto_id ON compras_productos_items(producto_id);

CREATE TABLE IF NOT EXISTS estrenos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    fecha_estreno DATE NOT NULL,
    sinopsis TEXT,
    imagen_url TEXT
);

CREATE INDEX IF NOT EXISTS idx_estrenos_fecha_estreno ON estrenos(fecha_estreno);
