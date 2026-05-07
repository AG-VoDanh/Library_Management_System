CREATE DATABASE library_db 
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE library_db;

CREATE TABLE users ( 
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_code VARCHAR(20) NULL,
    username VARCHAR(16) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(50) NULL,
    age TINYINT UNSIGNED NULL,
    gender VARCHAR(10) NULL,
    address VARCHAR(255) NULL,
    email VARCHAR(255) UNIQUE NULL,
    phone_number VARCHAR(10) UNIQUE NULL,
    role VARCHAR(20) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_code VARCHAR(20) NULL,
    book_name VARCHAR(255) NOT NULL,
    author VARCHAR(255) NULL,
    publication_year YEAR NULL,
    category VARCHAR(255) NULL,
    quantity INT UNSIGNED NOT NULL,
    image VARCHAR(255) NULL,
    available_quantity INT UNSIGNED NOT NULL
);

CREATE TABLE members ( 
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_code VARCHAR(20) NULL,
    member_name VARCHAR(50) NOT NULL,
    age TINYINT UNSIGNED NULL,
    gender VARCHAR(10) NULL,
    address VARCHAR(255) NULL,
    phone_number VARCHAR(10) UNIQUE NULL,
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE book_borrows (
    id INT AUTO_INCREMENT PRIMARY KEY,
    borrow_code VARCHAR(20) NULL,
    member_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NULL,
    return_date DATE,
    late_fee INT UNSIGNED,
    
    borrower_code VARCHAR(20) NOT NULL,
    borrower_name VARCHAR(50) NULL,
    borrower_age TINYINT UNSIGNED NULL,
    borrower_gender VARCHAR(10) NULL,
    borrower_address VARCHAR(255) NULL,
    borrower_phone_number VARCHAR(10) NULL,

    CONSTRAINT fk_book_borrows_members_id FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE borrow_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    borrow_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT UNSIGNED NOT NULL,
    
    borrowed_book_code VARCHAR(20) NOT NULL,
    borrowed_book_name VARCHAR(255) NOT NULL,
    borrowed_author VARCHAR(255) NULL,
    borrowed_publication_year YEAR NULL,
    borrowed_category VARCHAR(255) NULL,
    borrowed_image VARCHAR(255) NULL,

    CONSTRAINT fk_borrow_details_book_borrows_id FOREIGN KEY (borrow_id) REFERENCES book_borrows(id),
    CONSTRAINT fk_borrow_details_books_id FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE book_imports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    import_code VARCHAR(20) NULL,
    user_id INT NOT NULL,
    import_date DATE NOT NULL,
    
    importer_code  VARCHAR(20) NOT NULL,
    importer_username VARCHAR(16) NOT NULL,
    importer_full_name VARCHAR(50) NULL,
    importer_age TINYINT UNSIGNED NULL,
    importer_gender VARCHAR(10) NULL,
    importer_address VARCHAR(255) NULL,
    importer_email VARCHAR(255) NULL,
    importer_phone_number VARCHAR(10) NULL,
    importer_role VARCHAR(20) NOT NULL,

    CONSTRAINT fk_book_imports_users_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE import_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    import_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT UNSIGNED NOT NULL,
    
    imported_book_code VARCHAR(20) NOT NULL,
    imported_book_name VARCHAR(255) NOT NULL,
    imported_author VARCHAR(255) NULL,
    imported_publication_year YEAR NULL,
    imported_category VARCHAR(255) NULL,
    imported_image VARCHAR(255) NULL,

    CONSTRAINT fk_import_details_book_imports_id FOREIGN KEY (import_id) REFERENCES book_imports(id),    
    CONSTRAINT fk_import_details_books_id FOREIGN KEY (book_id) REFERENCES books(id)
);

-- DỮ LIỆU MẪU --

INSERT INTO users (user_code , username , password , full_name , age , gender , address , email , phone_number , role )
VALUES
('USR_00001','admin123','admin123','Quản trị hệ thống',30,'NAM','HA_NOI','admin@gmail.com','0900000001','QUAN_TRI'),
('USR_00002','thuthu01','thuthu123456','Nguyễn Thị Lan',25,'NU','HA_NOI','lan@gmail.com','0900000002','NHAN_VIEN'),
('USR_00003','vienalpha1','Pass1234','Nguyen Van An',21,'NAM','HA_NOI','an.nguyen1@gmail.com','0910000001','NHAN_VIEN'),
('USR_00004','vienalpha2','Pass1234','Tran Thi Binh',22,'NU','TP_HO_CHI_MINH','binh.tran2@gmail.com','0910000002','NHAN_VIEN'),
('USR_00005','vienalpha3','Pass1234','Le Van Cuong',23,'NAM','DA_NANG','cuong.le3@gmail.com','0910000003','NHAN_VIEN'),
('USR_00006','vienalpha4','Pass1234','Pham Thi Dao',20,'NU','HAI_PHONG','dao.pham4@gmail.com','0910000004','NHAN_VIEN'),
('USR_00007','vienalpha5','Pass1234','Hoang Van Duc',24,'NAM','CAN_THO','duc.hoang5@gmail.com','0910000005','NHAN_VIEN'),
('USR_00008','vienbeta01','Pass1234','Vu Thi Hanh',21,'NU','HAI_DUONG','hanh.vu6@gmail.com','0910000006','NHAN_VIEN'),
('USR_00009','vienbeta02','Pass1234','Dang Van Kien',22,'NAM','HA_NOI','kien.dang7@gmail.com','0910000007','NHAN_VIEN'),
('USR_00010','vienbeta03','Pass1234','Bui Thi Lan',23,'NU','TP_HO_CHI_MINH','lan.bui8@gmail.com','0910000008','NHAN_VIEN'),
('USR_00011','vienbeta04','Pass1234','Do Van Minh',24,'NAM','DA_NANG','minh.do9@gmail.com','0910000009','NHAN_VIEN'),
('USR_00012','vienbeta05','Pass1234','Ngo Thi Nga',25,'NU','HAI_PHONG','nga.ngo10@gmail.com','0910000010','NHAN_VIEN'),
('USR_00013','vientheta1','Pass1234','Truong Van Phuc',26,'NAM','CAN_THO','phuc.truong11@gmail.com','0910000011','NHAN_VIEN'),
('USR_00014','vientheta2','Pass1234','Mai Thi Quyen',21,'NU','HAI_DUONG','quyen.mai12@gmail.com','0910000012','NHAN_VIEN'),
('USR_00015','vientheta3','Pass1234','Luong Van Son',22,'NAM','HA_NOI','son.luong13@gmail.com','0910000013','NHAN_VIEN'),
('USR_00016','vientheta4','Pass1234','Phan Thi Tam',23,'NU','TP_HO_CHI_MINH','tam.phan14@gmail.com','0910000014','NHAN_VIEN'),
('USR_00017','vientheta5','Pass1234','Nguyen Van Thang',24,'NAM','DA_NANG','thang.nguyen15@gmail.com','0910000015','NHAN_VIEN'),
('USR_00018','vienomega1','Pass1234','Le Thi Uyen',22,'NU','HAI_PHONG','uyen.le16@gmail.com','0910000016','NHAN_VIEN'),
('USR_00019','vienomega2','Pass1234','Tran Van Vinh',23,'NAM','CAN_THO','vinh.tran17@gmail.com','0910000017','NHAN_VIEN'),
('USR_00020','vienomega3','Pass1234','Pham Thi Xuan',24,'NU','HAI_DUONG','xuan.pham18@gmail.com','0910000018','NHAN_VIEN'),
('USR_00021','vienomega4','Pass1234','Ho Van Yen',25,'NAM','HA_NOI','yen.ho19@gmail.com','0910000019','NHAN_VIEN'),
('USR_00022','vienomega5','Pass1234','Dang Thi Zung',26,'NU','TP_HO_CHI_MINH','zung.dang20@gmail.com','0910000020','NHAN_VIEN'),
('USR_00023','rootroot1','QuanTri1234','Quan Tri Vien Mot',30,'NAM','HA_NOI','admin1@gmail.com','0910000021','QUAN_TRI'),
('USR_00024','rootroot2','QuanTri1234','Quan Tri Vien Hai',32,'NU','TP_HO_CHI_MINH','admin2@gmail.com','0910000022','QUAN_TRI'),
('USR_00025','rootroot3','QuanTri1234','Quan Tri Vien Ba',35,'NAM','DA_NANG','admin3@gmail.com','0910000023','QUAN_TRI'),
('USR_00026','rootroot4','QuanTri1234','Quan Tri Vien Bon',33,'NU','HAI_PHONG','admin4@gmail.com','0910000024','QUAN_TRI'),
('USR_00027','rootroot5','QuanTri1234','Quan Tri Vien Nam',34,'NAM','CAN_THO','admin5@gmail.com','0910000025','QUAN_TRI');
    
-- ĐÃ SỬA: Thêm available_quantity vào danh sách cột INSERT
INSERT INTO books (book_code , book_name , author , publication_year , category , quantity , image, available_quantity)
VALUES
('BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin',10,'java.jpg',10),
('BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin',8,'java_adv.jpg',8),
('BK_00003','Spring Boot thực chiến','Trần Minh B',2022,'Cong_Nghe_Thong_Tin',6,'spring.jpg',6),
('BK_00004','Cấu trúc dữ liệu và giải thuật','Trần Văn B',2019,'Cong_Nghe_Thong_Tin',8,'ctdl.jpg',8),
('BK_00005','Giải thuật nâng cao','Hoàng Văn C',2020,'Cong_Nghe_Thong_Tin',5,'giaithuat.jpg',5),
('BK_00006','Nhập môn cơ sở dữ liệu','Lê Văn C',2021,'Cong_Nghe_Thong_Tin',5,'csdl.jpg',5),
('BK_00007','SQL cho người mới bắt đầu','Phạm Văn D',2022,'Cong_Nghe_Thong_Tin',7,'sql.jpg',7),
('BK_00008','Hệ điều hành','Nguyễn Thị E',2018,'Cong_Nghe_Thong_Tin',4,'hdh.jpg',4),
('BK_00009','Mạng máy tính','Trần Văn F',2017,'Cong_Nghe_Thong_Tin',6,'network.jpg',6),
('BK_00010','Lập trình hướng đối tượng','Hoàng Văn G',2019,'Cong_Nghe_Thong_Tin',9,'oop.jpg',9),
('BK_00011','Dế mèn phiêu lưu ký','Tô Hoài',2015,'Van_Hoc',12,'demen.jpg',12),
('BK_00012','Tắt đèn','Ngô Tất Tố',2014,'Van_Hoc',7,'tatden.jpg',7),
('BK_00013','Chí Phèo','Nam Cao',2016,'Van_Hoc',10,'chipheo.jpg',10),
('BK_00014','Lão Hạc','Nam Cao',2016,'Van_Hoc',8,'laohac.jpg',8),
('BK_00015','Vợ nhặt','Kim Lân',2017,'Van_Hoc',6,'vonhat.jpg',6),
('BK_00016','Tuổi thơ dữ dội','Phùng Quán',2018,'Van_Hoc',5,'tuoitho.jpg',5),
('BK_00017','Không gia đình','Hector Malot',2015,'Van_Hoc',9,'khonggiadinh.jpg',9),
('BK_00018','Những người khốn khổ','Victor Hugo',2014,'Van_Hoc',4,'nhungnguoi.jpg',4),
('BK_00019','Đắc nhân tâm','Dale Carnegie',2019,'Ky_Nang_Song',15,'dacnhantam.jpg',15),
('BK_00020','Quẳng gánh lo đi','Dale Carnegie',2018,'Ky_Nang_Song',10,'quangganhlo.jpg',10),
('BK_00021','7 thói quen hiệu quả','Stephen Covey',2020,'Ky_Nang_Song',8,'7habits.jpg',8),
('BK_00022','Tư duy nhanh và chậm','Daniel Kahneman',2021,'Ky_Nang_Song',6,'thinking.jpg',6),
('BK_00023','Sapiens','Yuval Noah Harari',2020,'Khoa_Hoc',7,'sapiens.jpg',7),
('BK_00024','Lược sử thời gian','Stephen Hawking',2019,'Khoa_Hoc',5,'thoigian.jpg',5),
('BK_00025','Vũ trụ trong vỏ hạt dẻ','Stephen Hawking',2018,'Khoa_Hoc',4,'vutru.jpg',4),
('BK_00026','Vật lý vui','George Gamow',2017,'Khoa_Hoc',6,'vatly.jpg',6),
('BK_00027','Kinh tế học cơ bản','Paul Krugman',2021,'Kinh_Te',5,'kinhte.jpg',5),
('BK_00028','Cha giàu cha nghèo','Robert Kiyosaki',2020,'Kinh_Te',12,'chagiau.jpg',12),
('BK_00029','sach test',null,null,null,12,null,12);


INSERT INTO members (member_code , member_name , age , gender , address , phone_number )
VALUES
    ('MEM_00001','Phạm Trung Hiếu', 22, 'NAM', 'HA_NOI', '0911111111'),
    ('MEM_00002','Nguyễn Thị Mai', 20, 'NU', 'HAI_PHONG', '0922222222'),
    ('MEM_00003','Trần Văn Nam', 25, 'NAM', 'DA_NANG', '0933333333'),
    ('MEM_00004','Lê Minh Tuấn', 23, 'NAM', 'HA_NOI', '0944444444'),
    ('MEM_00005','Hoàng Thị Hương', 21, 'NU', 'DA_NANG', '0955555555'),
    ('MEM_00006',"Hiếu",null,null,null,'0911111112'),
    ('MEM_00007',"Hải",null,null,null,'0911111113'),
    ('MEM_00008',"Hoàng",null,null,null,'0911111114'),
    ('MEM_00009',"My",null,null,null,'0911111115'),
    ('MEM_00010',"Trang",null,null,null,'0911111116'),
    ('MEM_00011',"Thảo",null,null,null,'0911111117'),
    ('MEM_00012',"Thắng",null,null,null,'0911111118'),
    ('MEM_00013',"Hiệp",null,null,null,'0911111119'),
    ('MEM_00014',"Vũ",null,null,null,'0911111120'),
    ('MEM_00015',"Quang",null,null,null,'0911111121'),
    ('MEM_00016',"Hợp",null,null,null,'0911111122'),
    ('MEM_00017',"Dũng",null,null,null,'0911111123'),
    ('MEM_00018',"Anh",null,null,null,'0911111124'),
    ('MEM_00019',"Nam",null,null,null,'0911111125'),
    ('MEM_00020',"Hường",null,null,null,'0911111126');
    
INSERT INTO book_borrows 
(borrow_code, member_id, borrow_date, due_date, return_date, late_fee,
 borrower_code,borrower_name, borrower_age, borrower_gender, borrower_address, borrower_phone_number)
VALUES
('BR_00001',1,'2026-01-05','2026-01-12','2026-01-12',0,'MEM_00001','Phạm Trung Hiếu',22,'NAM','HA_NOI','0911111111'),
('BR_00002',2,'2026-01-10','2026-01-17','2026-01-17',0,'MEM_00002','Nguyễn Thị Mai',20,'NU','HAI_PHONG','0922222222'),
('BR_00003',3,'2026-01-02','2026-01-09','2026-01-15',20000,'MEM_00003','Trần Văn Nam',25,'NAM','DA_NANG','0933333333'),
('BR_00004',1,'2026-01-15','2026-01-22','2026-01-22',0,'MEM_00001','Phạm Trung Hiếu',22,'NAM','HA_NOI','0911111111'),
('BR_00005',2,'2026-01-18','2026-01-25','2026-01-25',0,'MEM_00002','Nguyễn Thị Mai',20,'NU','HAI_PHONG','0922222222'),
('BR_00006',1,'2026-03-08','2026-03-15','2026-03-14',0,'MEM_00001','Phạm Trung Hiếu',22,'NAM','HA_NOI','0911111111'),
('BR_00007',2,'2026-03-10','2026-03-17','2026-03-17',0,'MEM_00002','Nguyễn Thị Mai',20,'NU','HAI_PHONG','0922222222'),
('BR_00008',3,'2026-03-12','2026-03-19','2026-03-25',30000,'MEM_00003','Trần Văn Nam',25,'NAM','DA_NANG','0933333333'),
('BR_00009',4,'2026-03-15','2026-03-22','2026-04-01',50000,'MEM_00004','Lê Minh Tuấn',23,'NAM','HA_NOI','0944444444'),
('BR_00010',5,'2026-03-18','2026-03-25','2026-03-25',0,'MEM_00005','Hoàng Thị Hương',21,'NU','DA_NANG','0955555555'),
('BR_00011',1,'2026-03-20','2026-03-27','2026-03-27',0,'MEM_00001','Phạm Trung Hiếu',22,'NAM','HA_NOI','0911111111'),
('BR_00012',1,'2026-03-22','2026-04-01','2026-04-01',0,'MEM_00001','Phạm Trung Hiếu',22,'NAM','HA_NOI','0911111111');

INSERT INTO borrow_details
    (borrow_id, book_id, quantity,
     borrowed_book_code,borrowed_book_name, borrowed_author, borrowed_publication_year, borrowed_category, borrowed_image)
    VALUES
    (1,1,1,'BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin','java.jpg'),
		(1,2,1,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
(2,3,2,'BK_00003','Spring Boot thực chiến','Trần Minh B',2022,'Cong_Nghe_Thong_Tin','spring.jpg'),
    (3,1,1,'BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin','java.jpg'),
		(3,2,1,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
    (4,1,2,'BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin','java.jpg'),
(5,3,1,'BK_00003','Spring Boot thực chiến','Trần Minh B',2022,'Cong_Nghe_Thong_Tin','spring.jpg'),
    (6,1,1,'BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin','java.jpg'),
(6,4,1,'BK_00004','Cấu trúc dữ liệu và giải thuật','Trần Văn B',2019,'Cong_Nghe_Thong_Tin','ctdl.jpg'),
		(7,2,1,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
(7,3,1,'BK_00003','Spring Boot thực chiến','Trần Minh B',2022,'Cong_Nghe_Thong_Tin','spring.jpg'),
		(8,2,2,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
    (9,1,1,'BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin','java.jpg'),
(9,2,1,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
(10,4,1,'BK_00004','Cấu trúc dữ liệu và giải thuật','Trần Văn B',2019,'Cong_Nghe_Thong_Tin','ctdl.jpg'),
		(11,2,1,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
		(12,2,1,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
(12,5,1,'BK_00005','Giải thuật nâng cao','Hoàng Văn C',2020,'Cong_Nghe_Thong_Tin','giaithuat.jpg');

INSERT INTO book_imports
(import_code, user_id, import_date,
 importer_code,importer_username, importer_full_name, importer_age, importer_gender,
 importer_address, importer_email, importer_phone_number,importer_role)
VALUES
('IMP_00001',1,'2026-01-01','USR_00001','admin123','Quản trị hệ thống',30,'NAM','HA_NOI','admin@gmail.com','0900000001','QUAN_TRI'),
('IMP_00002',2,'2026-01-03','USR_00002','thuthu01','Nguyễn Thị Lan',25,'NU','HA_NOI','lan@gmail.com','0900000002','NHAN_VIEN'),
('IMP_00003',3,'2026-01-05','USR_00003','vienalpha1','Nguyen Van An',21,'NAM','HA_NOI','an.nguyen1@gmail.com','0910000001','NHAN_VIEN'),
('IMP_00004',4,'2026-01-07','USR_00004','vienalpha2','Tran Thi Binh',22,'NU','TP_HO_CHI_MINH','binh.tran2@gmail.com','0910000002','NHAN_VIEN'),
('IMP_00005',5,'2026-01-09','USR_00005','vienalpha3','Le Van Cuong',23,'NAM','DA_NANG','cuong.le3@gmail.com','0910000003','NHAN_VIEN'),
('IMP_00006',1,'2026-01-12','USR_00001','admin123','Quản trị hệ thống',30,'NAM','HA_NOI','admin@gmail.com','0900000001','QUAN_TRI'),
('IMP_00007',2,'2026-01-15','USR_00002','thuthu01','Nguyễn Thị Lan',25,'NU','HA_NOI','lan@gmail.com','0900000002','NHAN_VIEN'),
('IMP_00008',3,'2026-01-18','USR_00003','vienalpha1','Nguyen Van An',21,'NAM','HA_NOI','an.nguyen1@gmail.com','0910000001','NHAN_VIEN'),
('IMP_00009',4,'2026-01-21','USR_00004','vienalpha2','Tran Thi Binh',22,'NU','TP_HO_CHI_MINH','binh.tran2@gmail.com','0910000002','NHAN_VIEN'),
('IMP_00010',5,'2026-01-24','USR_00005','vienalpha3','Le Van Cuong',23,'NAM','DA_NANG','cuong.le3@gmail.com','0910000003','NHAN_VIEN'),
('IMP_00011',4,'2026-02-10','USR_00004','vienalpha2','Tran Thi Binh',22,'NU','TP_HO_CHI_MINH','binh.tran2@gmail.com','0910000002','NHAN_VIEN'),
('IMP_00012',5,'2026-02-12','USR_00005','vienalpha3','Le Van Cuong',23,'NAM','DA_NANG','cuong.le3@gmail.com','0910000003','NHAN_VIEN'),
('IMP_00013',1,'2026-01-05','USR_00001','admin123','Quản trị hệ thống',30,'NAM','HA_NOI','admin@gmail.com','0900000001','QUAN_TRI'),
('IMP_00014',2,'2026-01-08','USR_00002','thuthu01','Nguyễn Thị Lan',25,'NU','HA_NOI','lan@gmail.com','0900000002','NHAN_VIEN'),
('IMP_00015',3,'2026-01-11','USR_00003','vienalpha1','Nguyen Van An',21,'NAM','HA_NOI','an.nguyen1@gmail.com','0910000001','NHAN_VIEN'),
('IMP_00016',4,'2026-01-14','USR_00004','vienalpha2','Tran Thi Binh',22,'NU','TP_HO_CHI_MINH','binh.tran2@gmail.com','0910000002','NHAN_VIEN'),
('IMP_00017',5,'2026-01-17','USR_00005','vienalpha3','Le Van Cuong',23,'NAM','DA_NANG','cuong.le3@gmail.com','0910000003','NHAN_VIEN'),
('IMP_00018',1,'2026-01-20','USR_00001','admin123','Quản trị hệ thống',30,'NAM','HA_NOI','admin@gmail.com','0900000001','QUAN_TRI'),
('IMP_00019',2,'2026-01-23','USR_00002','thuthu01','Nguyễn Thị Lan',25,'NU','HA_NOI','lan@gmail.com','0900000002','NHAN_VIEN'),
('IMP_00020',3,'2026-01-26','USR_00003','vienalpha1','Nguyen Van An',21,'NAM','HA_NOI','an.nguyen1@gmail.com','0910000001','NHAN_VIEN');

INSERT INTO import_details
(import_id, book_id, quantity,
 imported_book_code,imported_book_name, imported_author, imported_publication_year,imported_category, imported_image)
VALUES
    (1,1,5,'BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin','java.jpg'),
		(1,2,3,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
(1,3,4,'BK_00003','Spring Boot thực chiến','Trần Minh B',2022,'Cong_Nghe_Thong_Tin','spring.jpg'),
(2,3,4,'BK_00003','Spring Boot thực chiến','Trần Minh B',2022,'Cong_Nghe_Thong_Tin','spring.jpg'),
(2,4,6,'BK_00004','Cấu trúc dữ liệu và giải thuật','Trần Văn B',2019,'Cong_Nghe_Thong_Tin','ctdl.jpg'),
(2,5,2,'BK_00005','Giải thuật nâng cao','Hoàng Văn C',2020,'Cong_Nghe_Thong_Tin','giaithuat.jpg'),
(3,6,5,'BK_00006','Nhập môn cơ sở dữ liệu','Lê Văn C',2021,'Cong_Nghe_Thong_Tin','csdl.jpg'),
(3,7,4,'BK_00007','SQL cho người mới bắt đầu','Phạm Văn D',2022,'Cong_Nghe_Thong_Tin','sql.jpg'),
(3,8,3,'BK_00008','Hệ điều hành','Nguyễn Thị E',2018,'Cong_Nghe_Thong_Tin','hdh.jpg'),
(4,9,6,'BK_00009','Mạng máy tính','Trần Văn F',2017,'Cong_Nghe_Thong_Tin','network.jpg'),
(4,10,2,'BK_00010','Lập trình hướng đối tượng','Hoàng Văn G',2019,'Cong_Nghe_Thong_Tin','oop.jpg'),
(5,11,7,'BK_00011','Dế mèn phiêu lưu ký','Tô Hoài',2015,'Van_Hoc','demen.jpg'),
(5,12,3,'BK_00012','Tắt đèn','Ngô Tất Tố',2014,'Van_Hoc','tatden.jpg'),
(6,13,4,'BK_00013','Chí Phèo','Nam Cao',2016,'Van_Hoc','chipheo.jpg'),
(6,14,5,'BK_00014','Lão Hạc','Nam Cao',2016,'Van_Hoc','laohac.jpg'),
(7,15,6,'BK_00015','Vợ nhặt','Kim Lân',2017,'Van_Hoc','vonhat.jpg'),
(7,16,3,'BK_00016','Tuổi thơ dữ dội','Phùng Quán',2018,'Van_Hoc','tuoitho.jpg'),
(7,17,2,'BK_00017','Không gia đình','Hector Malot',2015,'Van_Hoc','khonggiadinh.jpg'),
(8,18,4,'BK_00018','Những người khốn khổ','Victor Hugo',2014,'Van_Hoc','nhungnguoi.jpg'),
(8,19,6,'BK_00019','Đắc nhân tâm','Dale Carnegie',2019,'Ky_Nang_Song','dacnhantam.jpg'),
(9,20,8,'BK_00020','Quẳng gánh lo đi','Dale Carnegie',2018,'Ky_Nang_Song','quangganhlo.jpg'),
(9,21,3,'BK_00021','7 thói quen hiệu quả','Stephen Covey',2020,'Ky_Nang_Song','7habits.jpg'),
(10,22,5,'BK_00022','Tư duy nhanh và chậm','Daniel Kahneman',2021,'Ky_Nang_Song','thinking.jpg'),
(10,23,4,'BK_00023','Sapiens','Yuval Noah Harari',2020,'Khoa_Hoc','sapiens.jpg'),
    (11,1,6,'BK_00001','Lập trình Java cơ bản','Nguyễn Văn A',2020,'Cong_Nghe_Thong_Tin','java.jpg'),
	(11,2,3,'BK_00002','Java nâng cao','Nguyễn Văn A',2021,'Cong_Nghe_Thong_Tin','java_adv.jpg'),
(12,3,5,'BK_00003','Spring Boot thực chiến','Trần Minh B',2022,'Cong_Nghe_Thong_Tin','spring.jpg'),
(12,4,4,'BK_00004','Cấu trúc dữ liệu và giải thuật','Trần Văn B',2019,'Cong_Nghe_Thong_Tin','ctdl.jpg'),
(13,5,6,'BK_00005','Giải thuật nâng cao','Hoàng Văn C',2020,'Cong_Nghe_Thong_Tin','giaithuat.jpg'),
(13,6,3,'BK_00006','Nhập môn cơ sở dữ liệu','Lê Văn C',2021,'Cong_Nghe_Thong_Tin','csdl.jpg'),
(14,7,4,'BK_00007','SQL cho người mới bắt đầu','Phạm Văn D',2022,'Cong_Nghe_Thong_Tin','sql.jpg'),
(14,8,5,'BK_00008','Hệ điều hành','Nguyễn Thị E',2018,'Cong_Nghe_Thong_Tin','hdh.jpg'),
(14,9,2,'BK_00009','Mạng máy tính','Trần Văn F',2017,'Cong_Nghe_Thong_Tin','network.jpg'),
(15,10,6,'BK_00010','Lập trình hướng đối tượng','Hoàng Văn G',2019,'Cong_Nghe_Thong_Tin','oop.jpg'),
(15,11,3,'BK_00011','Dế mèn phiêu lưu ký','Tô Hoài',2015,'Van_Hoc','demen.jpg'),
(16,12,5,'BK_00012','Tắt đèn','Ngô Tất Tố',2014,'Van_Hoc','tatden.jpg'),
(16,13,4,'BK_00013','Chí Phèo','Nam Cao',2016,'Van_Hoc','chipheo.jpg'),
(17,14,6,'BK_00014','Lão Hạc','Nam Cao',2016,'Van_Hoc','laohac.jpg'),
(17,15,3,'BK_00015','Vợ nhặt','Kim Lân',2017,'Van_Hoc','vonhat.jpg'),
(18,16,4,'BK_00016','Tuổi thơ dữ dội','Phùng Quán',2018,'Van_Hoc','tuoitho.jpg'),
(18,17,5,'BK_00017','Không gia đình','Hector Malot',2015,'Van_Hoc','khonggiadinh.jpg'),
(19,18,6,'BK_00018','Những người khốn khổ','Victor Hugo',2014,'Van_Hoc','nhungnguoi.jpg'),
(19,19,3,'BK_00019','Đắc nhân tâm','Dale Carnegie',2019,'Ky_Nang_Song','dacnhantam.jpg'),
(19,20,2,'BK_00020','Quẳng gánh lo đi','Dale Carnegie',2018,'Ky_Nang_Song','quangganhlo.jpg'),
(20,21,7,'BK_00021','7 thói quen hiệu quả','Stephen Covey',2020,'Ky_Nang_Song','7habits.jpg'),
(20,22,4,'BK_00022','Tư duy nhanh và chậm','Daniel Kahneman',2021,'Ky_Nang_Song','thinking.jpg');

ALTER TABLE users ADD COLUMN session_token VARCHAR(255) NULL;