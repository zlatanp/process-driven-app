INSERT INTO `aukcije`.`job_category`(`name`) VALUES("Usluge smestaja i ishrane"), ("Informacione tehnologije i komunikacije"), ("Saobracaj i skladistenje"), ("Trgovina"), ("Umetnost, zabava i rekreacija");

INSERT INTO `aukcije`.`custom_user`(`adresa`,`email`,`firstname`,`is_active`,`lastname`,`mesto`,`password`,`ptt`,`tip`,`username`,`name`,`job_category_id`, `max_distance`, `ocena`) VALUES ("Takovska 10","gigatron@localhost","Gigatron",1,"Gigatronovic","Beograd","gigatron", "11000","PRAVNO","gigatron","Gigatron", 2, 100, 0.0), ("Takovska 12","winwin@localhost","Win",1,"Winner","Beograd","winwin", "11000","PRAVNO","winwin","WinWin", 2, 200, 0.0), ("Zmaj Jovina 24","caribic@localhost","Jovan",1,"Jovanovic","Novi Sad","caribic", "21000","PRAVNO","caribic","Picerija Caribic", 1, 100, 0.0), ("Cara Dusana 102","golub@localhost","Golub",1,"Golubovic","Beograd","golub", "11000","PRAVNO","golub","Restoran Golub", 1, 5000, 0.0), ("Janka Veselinovica 3","viva@localhost","Viva",1,"Vivic","Sabac","viva", "15000","PRAVNO","viva","Viva Travel", 3, 500, 0.0), ("Kralja Petra 90","nisekspres@localhost","Nis",1,"Nisic","Nis","nisekspres", "18000","PRAVNO","nisekspres","Nisekspres d.o.o", 3, 800, 0.0),("London street 1","interkop@localhost","Mark",1,"Twen","London","interkop", "1105","PRAVNO","interkop","Interkop", 3, 300, 0.0), ("Vojvodjanska 3","test@localhost","Test",1,"Test","Sabac","test", "15000","FIZICKO","test","Tester",2, 0.0, 0.0), ("Street Art Museum Amsterdam 5","pera@localhost","Pera",1,"Peric","Amsterdam","pera", "1025","FIZICKO","pera", "Pera Tester", 3, 0.0, 0.0); 

INSERT INTO `aukcije`.`location`(`latitude`,`longitude`,`user`)VALUES(44.786568,20.448922,"gigatron"),(44.786568,20.448922,"winwin"), (45.267135,19.833550,"caribic"),(44.786568,20.448922,"golub"),(44.756960,19.694090,"viva"),(43.320902,21.895759,"nisekspres"),(51.507351,-0.127758,"interkop"),(44.756960,19.694090,"test"), (52.370216,4.895168,"zika");