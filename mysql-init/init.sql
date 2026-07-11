-- Este script se ejecuta automaticamente la PRIMERA vez que se crea el contenedor de MySQL.
-- Crea una base de datos separada para cada microservicio (cada uno con su propia BD).
CREATE DATABASE IF NOT EXISTS primer_proyecto_db;
CREATE DATABASE IF NOT EXISTS ms_inventario_db;
CREATE DATABASE IF NOT EXISTS ms_cliente_db;
CREATE DATABASE IF NOT EXISTS ms_pedido_db;
CREATE DATABASE IF NOT EXISTS ms_pago_db;
CREATE DATABASE IF NOT EXISTS ms_categoria_db;
CREATE DATABASE IF NOT EXISTS ms_envio_db;
CREATE DATABASE IF NOT EXISTS ms_cupon_db;
CREATE DATABASE IF NOT EXISTS ms_opinion_db;
CREATE DATABASE IF NOT EXISTS ms_notificacion_db;
