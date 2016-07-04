CREATE TABLE public.user_master
(
   id bigserial NOT NULL, 
   username character(50), 
   password character(50), 
   email character(100), 
   CONSTRAINT "user_master_primary key" PRIMARY KEY (id)
) 