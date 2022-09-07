create table projects(
    id int primary key auto_increment,
    description varchar(100) not null
    );


alter table task_groups add column project_id int null;
alter table task_groups add foreign key (project_id) references projects (id);


create table project_steps(
                        id int primary key auto_increment,
                        description varchar(100) not null,
                        project_id int not null,
                        days_to_deadline int,
                        foreign key (project_id) references projects(id)
);

