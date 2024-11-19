# project라는 유저 생성
CREATE USER 'project'@'localhost' IDENTIFIED BY 'qwer1234!';

# database project 삭제 및 생성 
DROP DATABASE project;
# database 기본 문자 셋 utf8으로 설
CREATE DATABASE project DEFAULT CHARACTER SET UTF8;
#프로젝트를 데이터베이스로 사용
USE project;
# project유저가 project데이터베이스에서 사용할 모든 권한 부여
GRANT ALL PRIVILEGES ON project.* TO 'project'@'localhost';



# 유저(소비자) 테이블
DROP TABLE User;
CREATE table User (
	idx BIGINT(10) primary key AUTO_INCREMENT,
	username VARCHAR(50) UNIQUE KEY not null,
	password varchar(100) not null,
	name varchar(50) not null,
	phoneNum varchar(15) not null,
	email varchar(50),
	phoneNum varchar(15) not null,
	postcode varchar(10),
  	address varchar(200),
  	detailAddress varchar(200),
  	regidate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	authority varchar(20) default 'ROLE_USER',
  	enabled INT(1) default 1,
  	provider varchar(20) default 'LOCAL',
  	providerId varchar(100),
  	isLocked INT(1) default 0,
  	failCount INT default 0,
  	lockTimes TIMESTAMP
);


-- user 테이블에 더미 데이터 추가
INSERT INTO User (username, password, email, name) VALUES
('user1', 'password1', 'user1@example.com', '사용자 1'),
('user2', 'password2', 'user2@example.com', '사용자 2'),
('user3', 'password3', 'user3@example.com', '사용자 3');

SELECT * FROM user;

# 셀러(판매자) 테이블
DROP TABLE seller;
CREATE table seller (
	idx BIGINT(10) primary key AUTO_INCREMENT,
	businessNum varchar(100) UNIQUE KEY NOT NULL,
	username VARCHAR(50) UNIQUE KEY NOT NULL,
	password varchar(100) not null,
	name varchar(50) not null,
	businessName varchar(100) UNIQUE KEY not null,
	phoneNum varchar(15) not null,
	email varchar(50),
	postcode varchar(10),
  	address varchar(200),
  	detailAddress varchar(200),
  	regidate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	authority varchar(20) default 'ROLE_SELLER',
  	enabled INT(1) default 1,
  	provider varchar(20) default 'LOCAL',
  	providerId varchar(100),
  	isLocked INT(1) default 0,
  	failCount INT default 0,
  	lockTimes TIMESTAMP
);

INSERT INTO seller (businessNum,username,name,password,businessName) VALUES
('B001', 'user1','password1','사용자1','Business A'),
('B002', 'user2','password2','사용자2','Business B'),
('B003', 'user3','password3','사용자3','Business C');

select * from seller;

# 관리자 테이블 생성
DROP TABLE admin;
CREATE table admin (
	idx INT(10) primary key AUTO_INCREMENT,
	username varchar(50) UNIQUE KEY  not null,
	password varchar(100) not null,
	email varchar(100),
	authority varchar(20) default 'ROLE_ADMIN'
);
INSERT INTO admin (username, password, email) VALUES
('admin', '{bcrypt}$2a$10$CWM93JFRp7V36DAYtZM3wegLBlV9RDc0768jUEUhsEm0NEySsraTS', 'admin@admin.com');

DELETE
    FROM admin;
   
SELECT * FROM admin;

# 테이블에 초기 관리자 넣기
INSERT into admin (idx, username, password, email, authority)
	values (1, 'admin', 'qwer1234!', '', 'ROLE_ADMIN');
	
# 관리자 테이블 확인
SELECT * FROM admin;

###### 공원 목록 ######
drop table parkList;
CREATE TABLE parkList (
    parkId BIGINT(100) PRIMARY KEY AUTO_INCREMENT,
    parkNm VARCHAR(100) NOT NULL,
    parkSe VARCHAR(30),
    lnmadr VARCHAR(200),
    parkAr VARCHAR(30),
    latitude DOUBLE,
    longitude DOUBLE
);
select * from parkList;

######## 제품 테이블 ########
DROP TABLE product;
CREATE TABLE product (
    idx bigint(10) PRIMARY KEY AUTO_INCREMENT,
    productNum varchar(10) not null,
    businessName varchar(100) not null,
    productName varchar(50) not null,
    quantity int(10) not null,
    cost bigint(100),
    parkId BIGINT(100),
    FOREIGN KEY (businessName) REFERENCES seller (businessName)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY (parkId) REFERENCES parkList(parkId)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT uniqueProduct UNIQUE (productName, parkId)  -- 상품 이름과 공원 ID로 복합 UNIQUE 키 설정
);

SELECT * FROM Product;


# 예약 테이블 생성 
SELECT * FROM reservation;
DROP TABLE reservation;
CREATE TABLE reservation (
	idx BIGINT(10) PRIMARY KEY AUTO_INCREMENT,
	reserveNum varchar(50) UNIQUE KEY  not null,
	reserveDate date UNIQUE KEY  not null,
	reserveTime time UNIQUE KEY  not null,
	username VARCHAR(50) not null,
	isPaid int(1) not null,
	costAll bigint(10) UNIQUE KEY NOT NULL,
	businessName varchar(100) not null,
	productName varchar(50) not null
);

ALTER TABLE reservation DROP FOREIGN KEY reservation_ibfk_2;

# API 정보 가져와서 공원 리스트로 저장하기
drop table parkList;
CREATE TABLE parkList (
    parkId BIGINT(100) PRIMARY KEY AUTO_INCREMENT,
    parkNm VARCHAR(100) NOT NULL,
    parkSe VARCHAR(30),
    lnmadr VARCHAR(200),
    parkAr VARCHAR(30),
    latitude DOUBLE,
    longitude DOUBLE
);
select * from parkList;
######## 결제 ########
drop table transaction;
CREATE TABLE transaction (
	idx bigint(10) PRIMARY KEY AUTO_INCREMENT,
	reserveNum int not null,
	costAll BIGINT(100),
	methodOfPayment varchar(30) not null,
	transactionDate Date not null,
	FOREIGN KEY (costAll) REFERENCES reservation (costAll)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

######## 커뮤니티 게시판 테이블 ########
DROP TABLE community;
CREATE TABLE community (
	idx BIGINT(10) PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(50) not null,
	title varchar(50) not null,
	content varchar(1000) not null,
  	postdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	viewCount int(10) DEFAULT 0 NOT NULL,
  	ofile varchar(200),
  	sfile varchar(100),
  	FOREIGN KEY (username) REFERENCES user (username)
  	ON DELETE CASCADE
	ON UPDATE CASCADE
);

# 커뮤니티 댓글 테이블
DROP TABLE commuComment;
create table commuComment (
    cIdx int(10) primary key, -- 댓글 인덱스 번호
    writer VARCHAR(50), -- 댓글 작성자 아이디
    commentText VARCHAR(500), -- 댓글 내용
    refGroup BIGINT(6), -- 원글 번호
    deleted INT(1) default 0, -- 지워진 댓글 여부 1:yes 0 no
    postdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (refGroup) REFERENCES community(idx),
    FOREIGN KEY (writer) REFERENCES user(username)
    ON DELETE CASCADE
	ON UPDATE CASCADE
);

# 커뮤니티 게시판 좋아요 테이블
ß
drop table commuLike;

CREATE TABLE commuLike (
	likeCount INT(10) default 0,
	username VARCHAR(50) NOT NULL,
	boardIdx BIGINT(10) NOT NULL,
	FOREIGN KEY (boardIdx) REFERENCES community (idx),
	FOREIGN KEY (username) REFERENCES user (username)
    ON DELETE CASCADE
	ON UPDATE CASCADE
);


######## 공지사항 게시판 테이블 ########
DROP TABLE announcement;
CREATE TABLE announcement (
    idx BIGINT(10) PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    title VARCHAR(50) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    postdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    viewCount INT(10) NOT NULL DEFAULT 0,
    ofile VARCHAR(200),
    sfile VARCHAR(100),
    FOREIGN KEY (username) REFERENCES admin (username)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
DESCRIBE announcement;
ALTER TABLE announcement 
MODIFY COLUMN viewCount int(10) NOT NULL DEFAULT 0;
INSERT INTO announcement (username, title, content, postdate, ofile, sfile) VALUES
('user1', '공지사항 1', '첫 번째 공지사항 내용입니다.', '2024-10-01 10:00:00', 'original_file_1.pdf', 'stored_file_1.pdf'),
('user2', '공지사항 2', '두 번째 공지사항 내용입니다.', '2024-10-02 11:00:00', 'original_file_2.pdf', 'stored_file_2.pdf'),
('user3', '공지사항 3', '세 번째 공지사항 내용입니다.', '2024-10-03 12:30:00', 'original_file_3.pdf', 'stored_file_3.pdf');


######## 1대1 문의 게시판 ##########
DROP TABLE inquiry;


=======
CREATE TABLE inquiry (
	idx bigint(10) PRIMARY KEY AUTO_INCREMENT,
	originNo bigint(10) default 0,
	groupOrd int(10) DEFAULT 0 NOT NULL,
	groupLayer int(10) DEFAULT 0 NOT NULL,
	username varchar(100) not null,
 	title varchar(50) not null,
	content varchar(1000) not null,
	postdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	viewCount int(10) DEFAULT 0 NOT NULL,
	responses int(10) DEFAULT 0 NOT NULL,
  	ofile varchar(200),
  	sfile varchar(100),
  	inquiryPassword varchar(20) not null
);
DELETE FROM inquiry WHERE idx=7;

######## 장바구니 ##########
DROP table cart;
CREATE TABLE cart (
    idx BIGINT(10) PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,  
    productNum VARCHAR(10) NOT NULL, 
    productName VARCHAR(50) NOT NULL,
    productPrice BIGINT(20) NOT NULL,
    quantity INT(10) NOT NULL DEFAULT 1,
    parkId BIGINT(100) NOT NULL,
    FOREIGN KEY (username) REFERENCES user(username) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (productName, parkId) REFERENCES product(productName, parkId) 
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
## 이거 추가해야 카트 테이블 생성됨;; ##
ALTER TABLE product ADD INDEX (productNum);
CREATE INDEX idxProductNumParkId ON product (productNum, parkId);


SELECT productNum FROM cart;

DROP TABLE PwdResetToken;
CREATE TABLE PwdResetToken (
    idx BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
   	userIdx BIGINT NOT NULL,
    expirationDate TIMESTAMP NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (userIdx) REFERENCES user(idx)
        ON DELETE CASCADE
);

SELECT * from PwdResetToken;