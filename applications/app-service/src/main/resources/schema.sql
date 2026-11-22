DROP TABLE IF EXISTS tournaments;
DROP TABLE IF EXISTS category;

-- Create the category table first (referenced by tournaments)
CREATE TABLE category (
                          id INT PRIMARY KEY,
                          name VARCHAR(255),
                          capacity INT
);

-- Create the tournaments table
CREATE TABLE tournaments (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             description VARCHAR(255),
                             name VARCHAR(255),
                             category_id INT,
                             start_date DATE,
                             end_date DATE,
                             user_id VARCHAR(255),
                             ticket_price DOUBLE,
                             remaining_capacity INT,
                             is_free BOOLEAN,
                             CONSTRAINT fk_tournament_category FOREIGN KEY (category_id) REFERENCES category(id)
);