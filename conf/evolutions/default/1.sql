# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table service (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  url                       varchar(255),
  user_id                   bigint,
  constraint pk_service primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  password                  varchar(255),
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create table workflow (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  content                   varchar(255),
  user_id                   bigint,
  constraint pk_workflow primary key (id))
;

alter table service add constraint fk_service_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_service_user_1 on service (user_id);
alter table workflow add constraint fk_workflow_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_workflow_user_2 on workflow (user_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table service;

drop table user;

drop table workflow;

SET FOREIGN_KEY_CHECKS=1;

