create table users
(
    id int not null auto_increment,
    name varchar(100) not null,
    primary key (id)
);

create table services
(
    id int not null auto_increment,
    user_id int not null,
    name varchar(100) not null,
    url varchar(250) not null,
    status varchar(10) not null default 'UNKNOWN',
    created_at datetime default current_timestamp not null,
    updated_at datetime default current_timestamp not null,
    primary key (id),
    foreign key (user_id) references users(id)
);
