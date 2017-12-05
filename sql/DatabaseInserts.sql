--inserts for the database


INSERT INTO employee VALUES 
('tdog','Anthony','Niehuser', '1996-09-09', 'tdog@gmail.com', 1000000.00, 1, 1),
('bro','Damon','George','1995-06-04', 'dlkj@ldkjf', 50000, 1, 0);

INSERT INTO enclosure VALUES 
(1, 'Arctic Enclosure', 'Arctic', 1);

INSERT INTO species VALUES 
('Spheniscidae', 'Penguin', 1, 'Pretty cute huh?');

INSERT INTO animal VALUES 
(1, 'Charlie', 'Charlie enjoys long walks on the beach', 'Spheniscidae', '2012-02-04', 3, '2017-11-15 12:00:00');

INSERT INTO food VALUES 
(1, 'Fruity Pebbles', 'Kellogs', 20.00, 50000, 0.5);

INSERT INTO employee_training VALUES 
('tdog', 'Spheniscidae', '2015-06-24', 4);

INSERT INTO species_eats VALUES 
('Spheniscidae', 1);
