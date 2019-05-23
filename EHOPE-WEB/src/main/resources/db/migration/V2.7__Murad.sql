CREATE TABLE "public"."actual_result_normal_range" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"branch_id" int8 NOT NULL,
"actual_result_id" int8 NOT NULL,
"normal_range_id" int8 NOT NULL,
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;
ALTER TABLE "public"."actual_result_normal_range"
ADD CONSTRAINT "actual_result_normal_range_actual_result_id_fk" FOREIGN KEY ("actual_result_id") REFERENCES "public"."lab_test_actual_result" ("rid"),
ADD CONSTRAINT "actual_result_normal_range_normal_range_id_fk" FOREIGN KEY ("normal_range_id") REFERENCES "public"."test_normal_ranges" ("rid");

CREATE TABLE "public"."actual_result_normal_range_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"branch_id" int8,
"actual_result_id" int8,
"normal_range_id" int8,
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;