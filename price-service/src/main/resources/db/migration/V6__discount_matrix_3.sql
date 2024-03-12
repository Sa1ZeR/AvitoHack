create table if not exists discount_matrix_3(
     microcategory_id int,
     location_id int,
     price int
);

insert into discount_matrix_3 (microcategory_id, location_id, price)
values  (1, 1, 1);