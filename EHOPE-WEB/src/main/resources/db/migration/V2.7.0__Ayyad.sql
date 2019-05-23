CREATE TABLE "public"."lkp_print_format" (
"rid" int8 NOT NULL,
"code" varchar(50) NOT NULL,
"name" varchar(4000) NOT NULL,
"description" varchar(4000),
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
PRIMARY KEY ("rid")
);

CREATE TABLE "public"."lkp_print_format_aud" (
"rid" int8 NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
"code" varchar(50),
"name" varchar(4000),
"description" varchar(4000),
"version" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
PRIMARY KEY ("rid", "rev")
);

CREATE SEQUENCE "public"."lkp_print_format_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

ALTER TABLE "public"."lkp_print_format" OWNER TO "postgres";

alter table lkp_print_format alter COLUMN rid set DEFAULT nextval('lkp_print_format_seq');


INSERT INTO "public"."lkp_master" ("code", "entity", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") 
VALUES ('VISIT_TYPE', 'LkpPrintFormat', '{"en_us":"Print Format","ar_jo":"Print Format"}', '{"en_us":"Print Format","ar_jo":"Print Format"}', '0', '2019-04-04 11:51:12.015', NULL, '2', NULL);


INSERT INTO "public"."com_tenant_messages" ("rid", "tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1864', '1', '0', '2019-04-11 09:53:26.028', NULL, NULL, 'printFormat', '{"en_us":"Print Format","ar_jo":"تنسيق الطباعة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("rid", "tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1863', '0', '0', '2019-04-11 09:53:26.018', NULL, NULL, 'printFormat', '{"en_us":"Print Format","ar_jo":"تنسيق الطباعة"}', '59', '0');


ALTER TABLE "public"."sec_tenant"
ADD COLUMN "print_format" int8,
ADD CONSTRAINT "sec_tenant_print_format_fk" FOREIGN KEY ("print_format") REFERENCES "public"."lkp_print_format" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "public"."sec_tenant_aud"
ADD COLUMN "print_format_id" int8;

update sec_tenant 
set print_format_id = (select rid
from lkp_print_format
where code = "PDF");


UPDATE "public"."lkp_print_format" 
SET "code"='RTF', "name"='{"en_us":"RTF","ar_jo":"RTF"}', "description"='{"en_us":"RTF","ar_jo":"RTF"}'
WHERE ("code"='WORD');
