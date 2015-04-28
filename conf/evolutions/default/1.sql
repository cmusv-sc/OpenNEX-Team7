# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table execution_result (
  id                        bigint not null,
  workflow_id               bigint,
  input                     TEXT,
  output                    TEXT,
  timestamp                 timestamp,
  executor_id               bigint,
  constraint pk_execution_result primary key (id))
;

create table notification (
  id                        bigint not null,
  user_id                   bigint,
  sender                    varchar(255),
  message                   varchar(255),
  timestamp                 timestamp,
  constraint pk_notification primary key (id))
;

create table service (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  type                      varchar(255),
  license                   varchar(255),
  version                   varchar(255),
  credits                   varchar(255),
  attributes                varchar(255),
  tags                      varchar(255),
  views                     varchar(255),
  url                       varchar(255),
  user_id                   bigint,
  constraint pk_service primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create table workflow (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  content                   TEXT,
  user_id                   bigint,
  constraint pk_workflow primary key (id))
;


create table user_service (
  user_id                        bigint not null,
  service_id                     bigint not null,
  constraint pk_user_service primary key (user_id, service_id))
;
create sequence execution_result_seq;

create sequence notification_seq;

create sequence service_seq;

create sequence user_seq;

create sequence workflow_seq;

alter table execution_result add constraint fk_execution_result_workflow_1 foreign key (workflow_id) references workflow (id) on delete restrict on update restrict;
create index ix_execution_result_workflow_1 on execution_result (workflow_id);
alter table execution_result add constraint fk_execution_result_executor_2 foreign key (executor_id) references user (id) on delete restrict on update restrict;
create index ix_execution_result_executor_2 on execution_result (executor_id);
alter table notification add constraint fk_notification_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_notification_user_3 on notification (user_id);
alter table service add constraint fk_service_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_service_user_4 on service (user_id);
alter table workflow add constraint fk_workflow_user_5 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_workflow_user_5 on workflow (user_id);



alter table user_service add constraint fk_user_service_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_service add constraint fk_user_service_service_02 foreign key (service_id) references service (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists execution_result;

drop table if exists notification;

drop table if exists service;

drop table if exists user_service;

drop table if exists user;

drop table if exists workflow;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists execution_result_seq;

drop sequence if exists notification_seq;

drop sequence if exists service_seq;

drop sequence if exists user_seq;

drop sequence if exists workflow_seq;

