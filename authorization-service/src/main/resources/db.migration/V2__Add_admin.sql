-- используем для добавления в пустую базу юзера админа иначе ничего н сможем делать
insert into player (id, nickName, password, active)
    values (1, 'admin', '12345', true);

insert into player_role (player_id, roles)
    values (1, 'USER'), (1, 'ADMIN');