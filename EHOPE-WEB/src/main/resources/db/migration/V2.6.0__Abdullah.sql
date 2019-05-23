
CREATE SEQUENCE "public"."tenant_email_history_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 57
 CACHE 1;
ALTER TABLE "public"."tenant_email_history_seq" OWNER TO "postgres";
SELECT setval('"public"."tenant_email_history_seq"', 1, true);

CREATE TABLE "public"."tenant_email_history" (
"rid" int8 DEFAULT nextval('tenant_email_history_seq'::regclass) NOT NULL,
"tenant_id" int8 NOT NULL,
"email" varchar(255) NOT NULL,
"version" int8,
"update_date" timestamp(6),
"creation_date" timestamp(6) NOT NULL,
"updated_by" int8,
"created_by" int8 NOT NULL
)
WITH (OIDS=FALSE);
CREATE TABLE "public"."tenant_email_history_aud" (
"rid" int8 NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
"tenant_id" int8,
"email" varchar(255),
"version" int8,
"update_date" timestamp(6),
"creation_date" timestamp(6),
"updated_by" int8,
"created_by" int8
)
WITH (OIDS=FALSE);
CREATE INDEX "tenant_email_history_idx" ON "public"."tenant_email_history" USING btree ("tenant_id");
ALTER TABLE "public"."tenant_email_history" CLUSTER ON "tenant_email_history_idx";
ALTER TABLE "public"."tenant_email_history" ADD PRIMARY KEY ("rid");
ALTER TABLE "public"."tenant_email_history_aud" ADD PRIMARY KEY ("rid", "rev");
ALTER TABLE "public"."tenant_email_history_aud" ADD FOREIGN KEY ("rev") REFERENCES "public"."revinfo" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
alter table tenant_email_history add constraint "tenant_email_history_email_tenant_id_uk" UNIQUE(tenant_id,email);