
CREATE TABLE class (
    id INT AUTO_INCREMENT PRIMARY KEY,        
    name VARCHAR(255) NOT NULL,                
    code VARCHAR(50) NOT NULL UNIQUE,         
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
);

CREATE TABLE student (
    id INT AUTO_INCREMENT PRIMARY KEY,         
    name VARCHAR(255) NOT NULL,                
    code VARCHAR(50) NOT NULL UNIQUE,          
    class_id INT,                              
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
    FOREIGN KEY (class_id) REFERENCES class(id) ON DELETE SET NULL 
);

CREATE TABLE homeroom_teacher (
    id INT AUTO_INCREMENT PRIMARY KEY,         
    name VARCHAR(255) NOT NULL,                
    code VARCHAR(50) NOT NULL UNIQUE,          
    class_id INT UNIQUE,                       
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
    FOREIGN KEY (class_id) REFERENCES class(id) ON DELETE CASCADE 
);


INSERT INTO class (name, code) VALUES
('Class A', 'CLA001'),
('Class B', 'CLB002'),
('Class C', 'CLC003'),
('Class D', 'CLD004'),
('Class E', 'CLE005'),
('Class F', 'CLF006'),
('Class G', 'CLG007'),
('Class H', 'CLH008'),
('Class I', 'CLI009'),
('Class J', 'CLJ010'),
('Class K', 'CLK011'),
('Class L', 'CLL012'),
('Class M', 'CLM013'),
('Class N', 'CLN014'),
('Class O', 'CLO015');

INSERT INTO student (name, code, class_id) VALUES
('Student 1', 'STD001', 1),
('Student 2', 'STD002', 1),
('Student 3', 'STD003', 2),
('Student 4', 'STD004', 2),
('Student 5', 'STD005', 3),
('Student 6', 'STD006', 3),
('Student 7', 'STD007', 4),
('Student 8', 'STD008', 4),
('Student 9', 'STD009', 5),
('Student 10', 'STD010', 5),
('Student 11', 'STD011', 6),
('Student 12', 'STD012', 6),
('Student 13', 'STD013', 7),
('Student 14', 'STD014', 7),
('Student 15', 'STD015', 8);

INSERT INTO homeroom_teacher (name, code, class_id) VALUES
('Teacher A', 'TCH001', 1),
('Teacher B', 'TCH002', 2),
('Teacher C', 'TCH003', 3),
('Teacher D', 'TCH004', 4),
('Teacher E', 'TCH005', 5),
('Teacher F', 'TCH006', 6),
('Teacher G', 'TCH007', 7),
('Teacher H', 'TCH008', 8),
('Teacher I', 'TCH009', 9),
('Teacher J', 'TCH010', 10),
('Teacher K', 'TCH011', 11),
('Teacher L', 'TCH012', 12),
('Teacher M', 'TCH013', 13),
('Teacher N', 'TCH014', 14),
('Teacher O', 'TCH015', 15);
