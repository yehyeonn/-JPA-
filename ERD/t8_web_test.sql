SELECT TABLE_NAME FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'db326'
  AND TABLE_NAME LIKE 't8_%'
;

SELECT * FROM t8_authority;
SELECT * FROM t8_user ORDER BY id DESC;
SELECT * FROM t8_user_authorities;
SELECT * FROM t8_post ORDER BY id DESC;
SELECT * FROM t8_comment ORDER BY id DESC;
SELECT * FROM t8_attachment ORDER BY id DESC;



# 테이블 삭제 (순서 주의)
DROP TABLE IF EXISTS t8_comment;
DROP TABLE IF EXISTS t8_attachment;
DROP TABLE IF EXISTS t8_post;
DROP TABLE IF EXISTS t8_user_authorities;
DROP TABLE IF EXISTS t8_user;
DROP TABLE IF EXISTS t8_authority;

