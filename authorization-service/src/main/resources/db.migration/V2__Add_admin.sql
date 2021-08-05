-- используем для добавления в пустую базу юзера админа иначе ничего н сможем делать
INSERT INTO players (nickName, email, password, active)
    values ('admin', '12345', 'admin@admin.com', true);

insert into player_role (player_id, roles)
    values (1, 'ROLE_GAMER'), (1, 'ROLE_ADMIN'), (1, 'ROLE_OWNER');

