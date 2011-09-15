use test;
drop database if exists mhstk53ca;
create database if not exists mhstk53ca;
use mhstk53ca;

create table book (
bookid int not null auto_increment,
title nvarchar(100), 
author nvarchar(100),
rating float,
rateCount int,
info text character set utf8,
tags nvarchar(100), 
price int,
image blob,
bigImage longblob,
keypoint longblob,
descriptor longblob,
primary key(bookid)
);

create table shop (
shopid int not null auto_increment,
address nvarchar(200), 
title nvarchar(100),
x float,
y float,
phone varchar(15),
primary key(shopID)
);

create table book_shop (
bsId int not null auto_increment primary key,
bookid int not null,
shopid int not null,
price int,
foreign key (bookid) references book(bookid),
foreign key (shopid) references shop(shopid)
);


/*
create table Class(
classID int not null auto_increment, 
bookNo numeric(5),
histogram blob,
primary key (classID)
);

create table BookInfo(
bookID int not null auto_increment,
classID int,
bookName varchar(100), 
author varchar(100),
publisher varchar(100),
info longtext,
image blob,
primary key (bookID),
foreign key (classID) references Class(classID)
);

insert into Class values(3, 2, '3432425465447654');
select * from Class;
*/
