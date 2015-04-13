# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  constraint pk_account primary key (id))
;

create table workflow (
  id                        bigint not null,
  name                      varchar(255),
  content                   varchar(255),
  owner_id                  bigint,
  constraint pk_workflow primary key (id))
;

create sequence account_seq;

create sequence workflow_seq;

alter table workflow add constraint fk_workflow_owner_1 foreign key (owner_id) references account (id) on delete restrict on update restrict;
create index ix_workflow_owner_1 on workflow (owner_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists account;

drop table if exists workflow;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists account_seq;

drop sequence if exists workflow_seq;
