create table if not exists users (
  id bigserial primary key,
  email varchar(100) not null unique,
  password_hash text not null
);

create table if not exists roles (
  id bigserial primary key,
  name varchar(50) not null unique
);

create table if not exists user_roles (
  id bigserial primary key,
  user_id bigint not null,
  role_id bigint not null,
  assigned_at timestamptz not null default now(),
  constraint fk_user_roles_user foreign key (user_id) references users(id) on delete cascade,
  constraint fk_user_roles_role foreign key (role_id) references roles(id) on delete cascade,
  constraint uq_user_roles_user_role unique (user_id, role_id)
);

create index if not exists idx_user_roles_user_id on user_roles(user_id);
create index if not exists idx_user_roles_role_id on user_roles(role_id);