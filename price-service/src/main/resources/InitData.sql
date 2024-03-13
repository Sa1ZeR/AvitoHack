INSERT INTO users (name) VALUES
('Maks'),
('Jon'),
('Pi'),
('Mark'),
('Sergy'),
('Liskov');

INSERT INTO locations (name, parent_id) VALUES
('Все регионы', null),
('Алтайский край', 1),
('Амурская область', 1),
('Архангельская область', 1),
('Астраханская область', 1),
('Башкортостан', 1),
('Крутиха', 2),
('Курья', 2),
('Архара', 3),
('Белогорск', 3),
('Подюга', 4),
('Нарьян-Мар', 4),
('Яндыки', 5),
('Яксатово', 5),
('Энергетик', 6),
('Учалы', 6);

INSERT INTO category (name, parent_id) VALUES
('ROOT', null),
('Автомобили', 1),
('Audio', 2),
('Ferrary', 2),
('Q5', 3),
('E5', 3),
('Личные вещи', 1),
('Детская одежда и обувь', 7),
('Товары для детей и игрушки', 7);

INSERT INTO discount_segments (user_id, segment) VALUES
(1, 290),
(1, 320),
(1, 333),
(2, 100),
(3, 280),
(5, 350),
(6, 290);

INSERT INTO matrix (name) VALUES
('baseline_matrix_1'),
('baseline_matrix_2'),
('baseline_matrix_3'),
('baseline_matrix_4'),
('discount_matrix_1'),
('discount_matrix_2'),
('discount_matrix_3');