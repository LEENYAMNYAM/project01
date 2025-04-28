-- Test data for the 'bab' database

-- Insert test users
INSERT INTO user (username, password, email, phone, name, gender, role, address, point, regdate, updatedate)
VALUES 
('admin', '$2a$10$8jBGVl3r1DCCHqQgv.9ILuHvvKNMsQdUgZ9C0HQWgNQXVJRUFbcgS', 'admin@example.com', '010-1234-5678', 'Admin User', 'Male', 'ROLE_ADMIN', '서울시 강남구', 1000, NOW(), NOW()),
('user1', '$2a$10$8jBGVl3r1DCCHqQgv.9ILuHvvKNMsQdUgZ9C0HQWgNQXVJRUFbcgS', 'user1@example.com', '010-2345-6789', '홍길동', 'Male', 'ROLE_USER', '서울시 서초구', 500, NOW(), NOW()),
('user2', '$2a$10$8jBGVl3r1DCCHqQgv.9ILuHvvKNMsQdUgZ9C0HQWgNQXVJRUFbcgS', 'user2@example.com', '010-3456-7890', '김철수', 'Male', 'ROLE_USER', '서울시 송파구', 300, NOW(), NOW()),
('user3', '$2a$10$8jBGVl3r1DCCHqQgv.9ILuHvvKNMsQdUgZ9C0HQWgNQXVJRUFbcgS', 'user3@example.com', '010-4567-8901', '이영희', 'Female', 'ROLE_USER', '서울시 마포구', 700, NOW(), NOW());

-- Insert test ingredients
INSERT INTO ingredient (ingredient_name, price, detail, image_url)
VALUES 
('쌀',  10000, 'Food Mall', 'https://example.com/images/rice.jpg'),
('소고기', 25000, 'Meat Shop', 'https://example.com/images/beef.jpg'),
('돼지고기', 15000, 'Meat Shop', 'https://example.com/images/pork.jpg'),
('닭고기', 8000, 'Meat Shop', 'https://example.com/images/chicken.jpg'),
('양파', 3000, 'Veggie Market', 'https://example.com/images/onion.jpg'),
('마늘', 2000, 'Veggie Market', 'https://example.com/images/garlic.jpg'),
('당근', 2500, 'Veggie Market', 'https://example.com/images/carrot.jpg'),
('감자', 4000, 'Veggie Market', 'https://example.com/images/potato.jpg'),
('고추장', 5000, 'Sauce Shop', 'https://example.com/images/gochujang.jpg'),
('간장', 4500, 'Sauce Shop', 'https://example.com/images/soysauce.jpg');

-- Insert test recipes
INSERT INTO recipe (title, category, main_image, youtube_link, username, created_at, like_count)
VALUES
('맛있는 김치찌개', '국', 'kimchi_stew.jpg', 'https://www.youtube.com/embed/example1', 1, NOW(), 120),
('소고기 불고기', '메인반찬', 'bulgogi.jpg', 'https://www.youtube.com/embed/example2', 2, NOW(), 85),
('간단한 비빔밥', '밥', 'bibimbap.jpg', 'https://www.youtube.com/embed/example3', 3, NOW(), 150),
('매콤한 떡볶이', '면', 'tteokbokki.jpg', 'https://www.youtube.com/embed/example4', 4, NOW(), 95);

-- Insert test recipe steps for 김치찌개
INSERT INTO recipe_step (recipe_id, step_number, content, image_name)
VALUES
(1, 1, '냄비에 물을 넣고 끓입니다.', 'kimchi_step1.jpg'),
(1, 2, '김치를 넣고 끓입니다.', 'kimchi_step2.jpg'),
(1, 3, '돼지고기를 넣고 끓입니다.', 'kimchi_step3.jpg'),
(1, 4, '두부를 넣고 5분간 더 끓입니다.', 'kimchi_step4.jpg'),
(1, 5, '파를 넣고 완성합니다.', 'kimchi_step5.jpg');

-- Insert test recipe steps for 불고기
INSERT INTO recipe_step (recipe_id, step_number, content, image_name)
VALUES 
(2, 1, '소고기를 얇게 썰어 양념합니다.', 'bulgogi_step1.jpg'),
(2, 2, '양파, 당근을 썰어 준비합니다.', 'bulgogi_step2.jpg'),
(2, 3, '팬에 고기와 야채를 넣고 볶습니다.', 'bulgogi_step3.jpg'),
(2, 4, '간장 소스를 넣고 조금 더 볶습니다.', 'bulgogi_step4.jpg');

-- Insert test recipe steps for 비빔밥
INSERT INTO recipe_step (recipe_id, step_number, content, image_name)
VALUES 
(3, 1, '밥을 짓습니다.', 'bibimbap_step1.jpg'),
(3, 2, '각종 나물을 준비합니다.', 'bibimbap_step2.jpg'),
(3, 3, '고기를 볶습니다.', 'bibimbap_step3.jpg'),
(3, 4, '밥 위에 나물과 고기를 올립니다.', 'bibimbap_step4.jpg'),
(3, 5, '고추장을 넣고 비벼 먹습니다.', 'bibimbap_step5.jpg');

-- Insert test recipe steps for 떡볶이
INSERT INTO recipe_step (recipe_id, step_number, content, image_name)
VALUES 
(4, 1, '떡을 물에 불립니다.', 'tteokbokki_step1.jpg'),
(4, 2, '냄비에 물을 넣고 끓입니다.', 'tteokbokki_step2.jpg'),
(4, 3, '고추장과 양념을 넣습니다.', 'tteokbokki_step3.jpg'),
(4, 4, '떡을 넣고 끓입니다.', 'tteokbokki_step4.jpg'),
(4, 5, '어묵을 넣고 완성합니다.', 'tteokbokki_step5.jpg');

-- Insert test recipe ingredients for 김치찌개
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity)
VALUES 
(1, 3, 300), -- 돼지고기 300g
(1, 5, 1),  -- 양파 1개
(1, 6, 2),  -- 마늘 2쪽
(1, 9, 1);  -- 고추장 1큰술

-- Insert test recipe ingredients for 불고기
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity)
VALUES 
(2, 2, 500), -- 소고기 500g
(2, 5, 1),   -- 양파 1개
(2, 7, 1),   -- 당근 1개
(2, 10, 3);  -- 간장 3큰술

-- Insert test recipe ingredients for 비빔밥
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity)
VALUES 
(3, 1, 2),   -- 쌀 2컵
(3, 2, 100), -- 소고기 100g
(3, 7, 1),   -- 당근 1개
(3, 9, 2);   -- 고추장 2큰술

-- Insert test recipe ingredients for 떡볶이
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity)
VALUES 
(4, 9, 3),   -- 고추장 3큰술
(4, 10, 1);  -- 간장 1큰술
