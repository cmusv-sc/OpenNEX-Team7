# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table software (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  url                       varchar(255),
  owner_id                  bigint,
  constraint pk_software primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (id))
;

create table workflow (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  content                   varchar(255),
  owner_id                  bigint,
  constraint pk_workflow primary key (id))
;

create sequence software_seq;

create sequence user_seq;

create sequence workflow_seq;

alter table software add constraint fk_software_owner_1 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_software_owner_1 on software (owner_id);
alter table workflow add constraint fk_workflow_owner_2 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_workflow_owner_2 on workflow (owner_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists software;

drop table if exists user;

drop table if exists workflow;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists software_seq;

drop sequence if exists user_seq;

drop sequence if exists workflow_seq;

