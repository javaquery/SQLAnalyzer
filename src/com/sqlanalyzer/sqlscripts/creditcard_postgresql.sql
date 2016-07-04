CREATE TABLE public.creditcard
(
   id bigserial not null, 
   user_id bigint, 
   credit_card_number bigint, 
   CONSTRAINT creditcard_primary_key PRIMARY KEY (id), 
   CONSTRAINT fk_creditcard_user_master FOREIGN KEY (user_id) REFERENCES user_master (id) ON UPDATE NO ACTION ON DELETE NO ACTION
) 