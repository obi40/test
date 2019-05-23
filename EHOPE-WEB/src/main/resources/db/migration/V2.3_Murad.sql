INSERT INTO "public"."lkp_result_value_type" ("code", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('ORG', '{"en_us":"Organism","ar_jo":"جسيم"}', '{"en_us":"Organism","ar_jo":"جسيم"}', '1', '2018-12-02 14:42:33.173', '2018-12-02 14:42:49.305', '0', '0');

CREATE TABLE "public"."organism" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"code" varchar(50) NOT NULL,
"name" varchar(255) NOT NULL,
"type_id" int8 NOT NULL,
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;



ALTER TABLE "public"."organism"
ADD CONSTRAINT "organism_code_uk" UNIQUE ("tenant_id", "code");

CREATE TABLE "public"."organism_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"code" varchar(50) COLLATE "default",
"name" varchar(255) COLLATE "default",
"type_id" int8,
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_organism_type" (
"rid" serial8 NOT NULL,
"code" varchar(50) COLLATE "default" NOT NULL,
"name" varchar(4000) COLLATE "default" NOT NULL,
"description" varchar(4000) COLLATE "default",
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_organism_type_aud" (
"rid" int8 NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
"tenant_id" int8,
"created_by" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"code" varchar(50) COLLATE "default",
"description" varchar(4000) COLLATE "default",
"name" varchar(4000) COLLATE "default",
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."organism"
ADD CONSTRAINT "organism_type_id_fk" FOREIGN KEY ("type_id") REFERENCES "public"."lkp_organism_type" ("rid");

ALTER TABLE "public"."bill_balance_aud"
DROP COLUMN "version";

CREATE TABLE "public"."anti_microbial" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"code" varchar(50) COLLATE "default" NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."anti_microbial"
ADD CONSTRAINT "anti_microbial_code_uk" UNIQUE ("tenant_id", "code");

CREATE TABLE "public"."anti_microbial_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"code" varchar(50) COLLATE "default",
"name" varchar(255) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_anti_microbial_type" (
"rid" serial8 NOT NULL,
"code" varchar(50) COLLATE "default" NOT NULL,
"name" varchar(4000) COLLATE "default" NOT NULL,
"description" varchar(4000) COLLATE "default",
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_anti_microbial_type_aud" (
"rid" int8 NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
"tenant_id" int8,
"created_by" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"code" varchar(50) COLLATE "default",
"description" varchar(4000) COLLATE "default",
"name" varchar(4000) COLLATE "default",
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."anti_microbial_type_mapping" (
"rid" serial8 NOT NULL,
"anti_microbial_id" int8 NOT NULL,
"type_id" int8 NOT NULL,
"version" int8 NOT NULL,
"tenant_id" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"created_by" int8 NOT NULL,
"update_date" timestamp(6),
"updated_by" int8,
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."anti_microbial_type_mapping"
ADD CONSTRAINT "anti_microbial_type_mapping_anti_microbial_id_fk" FOREIGN KEY ("anti_microbial_id") REFERENCES "public"."anti_microbial" ("rid"),
ADD CONSTRAINT "anti_microbial_type_mapping_type_id_fk" FOREIGN KEY ("type_id") REFERENCES "public"."lkp_anti_microbial_type" ("rid");

CREATE TABLE "public"."anti_microbial_type_mapping_aud" (
"rid" int8 NOT NULL,
"anti_microbial_id" int8,
"type_id" int8,
"tenant_id" int8,
"creation_date" timestamp(6),
"created_by" int8,
"update_date" timestamp(6),
"updated_by" int8,
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_section_type" (
"rid" serial8 NOT NULL,
"code" varchar(50) COLLATE "default" NOT NULL,
"name" varchar(4000) COLLATE "default" NOT NULL,
"description" varchar(4000) COLLATE "default",
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6) DEFAULT now(),
"created_by" int8 NOT NULL,
"updated_by" int8,
PRIMARY KEY ("rid"),
CONSTRAINT "lkp_section_type_code_uk" UNIQUE ("code")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_section_type_aud" (
"rid" int8 NOT NULL,
"code" varchar(50) COLLATE "default",
"name" varchar(4000) COLLATE "default",
"description" varchar(4000) COLLATE "default",
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."lab_section"
ADD COLUMN "type_id" int8,
ADD CONSTRAINT "lab_section_type_id_fk" FOREIGN KEY ("type_id") REFERENCES "public"."lkp_section_type" ("rid");

INSERT INTO "public"."lkp_master" ("code", "entity", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('SECTION_TYPE', 'LkpSectionType', '{"en_us":"Section Type"}', '{"en_us":"Section Type"}', '0', '2018-12-03 11:17:33', NULL, '0', NULL);
INSERT INTO "public"."lkp_master" ("code", "entity", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('ORGANISM_TYPE', 'LkpOrganismType', '{"en_us":"Organism Type"}', '{"en_us":"Organism Type"}', '0', '2018-12-03 11:18:32', NULL, '0', NULL);
INSERT INTO "public"."lkp_master" ("code", "entity", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('ANTI_MICROBIAL_TYPE', 'LkpAntiMicrobialType', '{"en_us":"Anti-Microbial Type"}', '{"en_us":"Anti-Microbial Type"}', '0', '2018-12-03 11:19:16', NULL, '0', NULL);

INSERT INTO "public"."lkp_section_type" ("code", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('MICROBIOLOGY', '{"en_us":"MICROBIOLOGY","ar_jo":"MICROBIOLOGY"}', '{"en_us":"MICROBIOLOGY","ar_jo":"MICROBIOLOGY"}', '0', '2018-12-03 09:27:48.681', NULL, '0', NULL);

ALTER TABLE "public"."lab_section_aud"
ADD COLUMN "type_id" int8;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-03 11:36:47.742', NULL, NULL, 'changeSectionTypeFromMicrobiology', '{"en_us":"Changing the section type from \"Microbiology\" might cause test-definition issues!","ar_jo":"قد يتسبب تغيير نوع القسم من \"علم الأحياء الدقيقة\" في حدوث مشكلات في تعريف الاختبار!"}', '60', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-03 11:36:47.734', NULL, NULL, 'changeSectionTypeFromMicrobiology', '{"en_us":"Changing the section type from \"Microbiology\" might cause test-definition issues!","ar_jo":"قد يتسبب تغيير نوع القسم من \"علم الأحياء الدقيقة\" في حدوث مشكلات في تعريف الاختبار!"}', '60', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-03 11:43:15.416', NULL, NULL, 'sections', '{"en_us":"Sections","ar_jo":"الأقسام"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-03 11:43:15.411', NULL, NULL, 'sections', '{"en_us":"Sections","ar_jo":"الأقسام"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-03 11:45:33.709', NULL, NULL, 'labUnits', '{"en_us":"Lab Units","ar_jo":"وحدات المختبر"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-03 11:45:33.705', NULL, NULL, 'labUnits', '{"en_us":"Lab Units","ar_jo":"وحدات المختبر"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-03 13:10:36.036', NULL, NULL, 'organisms', '{"en_us":"Organisms","ar_jo":"الجسيمات"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-03 13:10:36.03', NULL, NULL, 'organisms', '{"en_us":"Organisms","ar_jo":"الجسيمات"}', '59', '0');

INSERT INTO "public"."sys_page" ("rid", "module_id", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('34', '1', '{"en_us":"Organisms","ar_jo":"الجسيمات"}', '{"en_us":"Organisms","ar_jo":"الجسيمات"}', '0', '2018-12-03 15:43:51', NULL, '0', NULL);
INSERT INTO "public"."sys_page" ("rid", "module_id", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('35', '1', '{"en_us":"AntiMicrobials","ar_jo":"مضادات الميكروبات"}', '{"en_us":"AntiMicrobials","ar_jo":"مضادات الميكروبات"}', '0', '2018-12-03 15:43:51', NULL, '0', NULL);


INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"View Organism"}', '2018-12-03 15:44:30', '2018-12-03 15:43:56.493748', '0', NULL, '0', 'VIEW_ORGANISM', '{"en_us":"View Organism"}', '34');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Add Organism"}', '2018-12-03 15:46:49', '2018-12-03 15:46:23.910906', '0', NULL, '0', 'ADD_ORGANISM', '{"en_us":"Add Organism"}', '34');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Update Organism"}', '2018-12-03 16:12:30', '2018-12-03 16:11:56.115256', '0', NULL, '0', 'UPD_ORGANISM', '{"en_us":"Update Organism"}', '34');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Delete Organism"}', '2018-12-03 16:14:01', '2018-12-03 16:13:38.082107', '0', NULL, '0', 'DEL_ORGANISM', '{"en_us":"Delete Organism"}', '34');

INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"View Anti-Microbial"}', '2018-12-03 15:44:30', '2018-12-03 15:43:56.493748', '0', NULL, '0', 'VIEW_ANTI_MICROBIAL', '{"en_us":"View Anti-Microbial"}', '35');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Add Anti-Microbial"}', '2018-12-03 15:46:49', '2018-12-03 15:46:23.910906', '0', NULL, '0', 'ADD_ANTI_MICROBIAL', '{"en_us":"Add Anti-Microbial"}', '35');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Update Anti-Microbial"}', '2018-12-03 16:12:30', '2018-12-03 16:11:56.115256', '0', NULL, '0', 'UPD_ANTI_MICROBIAL', '{"en_us":"Update Anti-Microbial"}', '35');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Delete Anti-Microbial"}', '2018-12-03 16:14:01', '2018-12-03 16:13:38.082107', '0', NULL, '0', 'DEL_ANTI_MICROBIAL', '{"en_us":"Delete Anti-Microbial"}', '35');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-04 07:35:50.455', NULL, NULL, 'antiMicrobials', '{"en_us":"Anti-Microbials","ar_jo":"مضادات الميكروبات"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-04 07:35:50.355', NULL, NULL, 'antiMicrobials', '{"en_us":"Anti-Microbials","ar_jo":"مضادات الميكروبات"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-04 08:17:10.11', NULL, NULL, 'medicalSettings', '{"en_us":"Medical Settings","ar_jo":"الإعدادات الطبية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-04 08:17:10.104', NULL, NULL, 'medicalSettings', '{"en_us":"Medical Settings","ar_jo":"الإعدادات الطبية"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-04 09:05:05.2', NULL, NULL, 'types', '{"en_us":"Types","ar_jo":"الانواع"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-04 09:05:05.109', NULL, NULL, 'types', '{"en_us":"Types","ar_jo":"الانواع"}', '59', '0');

ALTER TABLE "public"."test_normal_ranges"
ALTER COLUMN "max_value_comparator" TYPE varchar(6) COLLATE "default";

ALTER TABLE "public"."test_normal_ranges_aud"
ALTER COLUMN "max_value_comparator" TYPE varchar(6) COLLATE "default";

CREATE TABLE "public"."actual_organism" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"organism_id" int8 NOT NULL,
"colony_count" varchar(255),
"actual_result_id" int8 NOT NULL,
PRIMARY KEY ("rid"),
CONSTRAINT "actual_organism_organism_id_fk" FOREIGN KEY ("organism_id") REFERENCES "public"."organism" ("rid"),
CONSTRAINT "actual_organism_actual_result_id_fk" FOREIGN KEY ("actual_result_id") REFERENCES "public"."lab_test_actual_result" ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."actual_organism_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"organism_id" int8,
"colony_count" varchar(255) COLLATE "default",
"actual_result_id" int8,
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_organism_detection" (
"rid" serial8 NOT NULL,
"name" varchar(4000) COLLATE "default" NOT NULL,
"description" varchar(4000) COLLATE "default",
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"code" varchar(50) COLLATE "default" NOT NULL,
"version" int8 NOT NULL,
"updated_by" int8,
PRIMARY KEY ("rid"),
CONSTRAINT "lkp_organism_detection_code_uk" UNIQUE ("code")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_organism_detection_aud" (
"rid" int8 NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
"created_by" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"code" varchar(50) COLLATE "default",
"description" varchar(4000) COLLATE "default",
"name" varchar(4000) COLLATE "default",
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."lab_test_actual_result"
ADD COLUMN "organism_detection_id" int8;

ALTER TABLE "public"."lab_test_actual_result"
ADD CONSTRAINT "lab_test_actual_result_organism_detection_fk" FOREIGN KEY ("organism_detection_id") REFERENCES "public"."lkp_organism_detection" ("rid");

ALTER TABLE "public"."lab_test_actual_result_aud"
ADD COLUMN "organism_detection_id" int8;

INSERT INTO "public"."lkp_master" ("code", "entity", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('ORGANISM_DETECTION', 'LkpOrganismDetection', '{"en_us":"Organism Detection","ar_jo":"Organism Detection"}', '{"en_us":"Organism Detection","ar_jo":"Organism Detection"}', '0', '2018-12-10 10:33:05', NULL, '0', NULL);


CREATE TABLE "public"."lkp_organism_sensitivity" (
"rid" serial8 NOT NULL,
"name" varchar(4000) COLLATE "default" NOT NULL,
"description" varchar(4000) COLLATE "default",
"created_by" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"code" varchar(50) COLLATE "default" NOT NULL,
"version" int8 NOT NULL,
"updated_by" int8,
PRIMARY KEY ("rid"),
CONSTRAINT "lkp_organism_sensitivity_code_uk" UNIQUE ("code")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."lkp_organism_sensitivity_aud" (
"rid" int8 NOT NULL,
"rev" int4 NOT NULL,
"revtype" int2,
"created_by" int8,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"updated_by" int8,
"code" varchar(50) COLLATE "default",
"description" varchar(4000) COLLATE "default",
"name" varchar(4000) COLLATE "default",
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

INSERT INTO "public"."lkp_master" ("code", "entity", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('ORGANISM_SENSITIVITY', 'LkpOrganismSensitivity', '{"en_us":"Organism Sensitivity","ar_jo":"Organism Sensitivity"}', '{"en_us":"Organism Sensitivity","ar_jo":"Organism Sensitivity"}', '0', '2018-12-10 10:50:11', NULL, '0', NULL);

CREATE TABLE "public"."actual_anti_microbial" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"anti_microbial_id" int8 NOT NULL,
"actual_result_id" int8 NOT NULL,
"organism_sensitivity_id" int8,
PRIMARY KEY ("rid"),
CONSTRAINT "actual_anti_microbial_anti_microbial_id_fk" FOREIGN KEY ("anti_microbial_id") REFERENCES "public"."anti_microbial" ("rid"),
CONSTRAINT "actual_anti_microbial_actual_result_id_fk" FOREIGN KEY ("actual_result_id") REFERENCES "public"."lab_test_actual_result" ("rid"),
CONSTRAINT "actual_anti_microbial_organism_sensitivity_id_fk" FOREIGN KEY ("organism_sensitivity_id") REFERENCES "public"."lkp_organism_sensitivity" ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."actual_anti_microbial_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"anti_microbial_id" int8,
"actual_result_id" int8,
"organism_sensitivity_id" int8,
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

INSERT INTO "public"."lkp_organism_detection" ("name", "description", "created_by", "creation_date", "update_date", "code", "version", "updated_by") VALUES ('{"en_us":"Growth Detected"}', '{"en_us":"Growth Detected"}', '0', '2018-12-10 13:03:02.798', NULL, 'GROWTH', '0', NULL);
INSERT INTO "public"."lkp_organism_detection" ("name", "description", "created_by", "creation_date", "update_date", "code", "version", "updated_by") VALUES ('{"en_us":"No Growth"}', '{"en_us":"No Growth"}', '0', '2018-12-10 13:03:30.566', NULL, 'NO_GROWTH', '0', NULL);

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-10 13:22:42.655', NULL, NULL, 'organismDetection', '{"en_us":"Organism Detection","ar_jo":"كشف الجسيم"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-10 13:22:42.649', NULL, NULL, 'organismDetection', '{"en_us":"Organism Detection","ar_jo":"كشف الجسيم"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 09:13:06.359', NULL, NULL, 'antiMicrobial', '{"en_us":"Anti-Microbial","ar_jo":"مضادات الميكروبات"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 09:13:06.354', NULL, NULL, 'antiMicrobial', '{"en_us":"Anti-Microbial","ar_jo":"مضادات الميكروبات"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 09:15:08.51', NULL, NULL, 'notTested', '{"en_us":"Not Tested","ar_jo":"لم تختبر"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 09:15:08.507', NULL, NULL, 'notTested', '{"en_us":"Not Tested","ar_jo":"لم تختبر"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 09:14:37.378', NULL, NULL, 'resistant', '{"en_us":"Resistant","ar_jo":"Resistant"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 09:14:37.375', NULL, NULL, 'resistant', '{"en_us":"Resistant","ar_jo":"Resistant"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 09:14:24.062', NULL, NULL, 'intermediate', '{"en_us":"Intermediate","ar_jo":"Intermediate"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 09:14:24.059', NULL, NULL, 'intermediate', '{"en_us":"Intermediate","ar_jo":"Intermediate"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 09:14:08.523', NULL, NULL, 'susceptible', '{"en_us":"Susceptible","ar_jo":"Susceptible"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 09:14:08.519', NULL, NULL, 'susceptible', '{"en_us":"Susceptible","ar_jo":"Susceptible"}', '59', '0');

INSERT INTO "public"."lkp_organism_sensitivity" ("name", "description", "created_by", "creation_date", "update_date", "code", "version", "updated_by") VALUES ('{"en_us":"Susceptible","ar_jo":"Susceptible"}', '{"en_us":"Susceptible","ar_jo":"Susceptible"}', '0', '2018-12-11 10:13:17.75', NULL, 'S', '0', NULL);
INSERT INTO "public"."lkp_organism_sensitivity" ("name", "description", "created_by", "creation_date", "update_date", "code", "version", "updated_by") VALUES ('{"en_us":"Intermediate","ar_jo":"Intermediate"}', '{"en_us":"Intermediate","ar_jo":"Intermediate"}', '0', '2018-12-11 10:13:32.776', NULL, 'I', '0', NULL);
INSERT INTO "public"."lkp_organism_sensitivity" ("name", "description", "created_by", "creation_date", "update_date", "code", "version", "updated_by") VALUES ('{"en_us":"Resistant","ar_jo":"Resistant"}', '{"en_us":"Resistant","ar_jo":"Resistant"}', '0', '2018-12-11 10:13:45.368', NULL, 'R', '0', NULL);

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 15:10:15.115', NULL, NULL, 'deleteOrganism', '{"en_us":"Delete Organism","ar_jo":"حذف الجسيم"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 15:10:15.11', NULL, NULL, 'deleteOrganism', '{"en_us":"Delete Organism","ar_jo":"حذف الجسيم"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 15:09:14.439', NULL, NULL, 'colonyCount', '{"en_us":"Colony Count","ar_jo":"عدد المستعمرة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 15:09:14.434', NULL, NULL, 'colonyCount', '{"en_us":"Colony Count","ar_jo":"عدد المستعمرة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-11 15:07:57.86', NULL, NULL, 'organism', '{"en_us":"Organism","ar_jo":"الجسيم"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-11 15:07:57.85', NULL, NULL, 'organism', '{"en_us":"Organism","ar_jo":"الجسيم"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-12 14:03:10.566', NULL, NULL, 'narrativeTemplates', '{"en_us":"Narrative Templates","ar_jo":"النصوص المعرفة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-12 14:03:10.563', NULL, NULL, 'narrativeTemplates', '{"en_us":"Narrative Templates","ar_jo":"النصوص المعرفة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-12 14:02:27.812', NULL, NULL, 'text', '{"en_us":"Text","ar_jo":"النص"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-12 14:02:27.8', NULL, NULL, 'text', '{"en_us":"Text","ar_jo":"النص"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-13 09:09:14', NULL, NULL, 'deleteNarrativeTemplate', '{"en_us":"Delete Narrative Template","ar_jo":"حذف النص المعرف"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-13 09:09:13.995', NULL, NULL, 'deleteNarrativeTemplate', '{"en_us":"Delete Narrative Template","ar_jo":"حذف النص المعرف"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-13 09:08:38.97', NULL, NULL, 'addNarrativeTemplate', '{"en_us":"Add Narrative Template","ar_jo":"اضافة النص المعرف"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-13 09:08:38.958', NULL, NULL, 'addNarrativeTemplate', '{"en_us":"Add Narrative Template","ar_jo":"اضافة النص المعرف"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-13 09:12:14.423', NULL, NULL, 'addResult', '{"en_us":"Add Result","ar_jo":"اضافة نتيجة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-13 09:12:14.419', NULL, NULL, 'addResult', '{"en_us":"Add Result","ar_jo":"اضافة نتيجة"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-13 11:10:07.582', NULL, NULL, 'showHide', '{"en_us":"Show/Hide","ar_jo":"إظهار/إخفاء"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-13 11:10:07.577', NULL, NULL, 'showHide', '{"en_us":"Show/Hide","ar_jo":"إظهار/إخفاء"}', '59', '0');

ALTER TABLE "public"."organism"
ALTER COLUMN "type_id" DROP NOT NULL;

INSERT INTO "public"."lkp_section_type" ("code", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('ALLERGY', '{"en_us":"ALLERGY","ar_jo":"ALLERGY"}', '{"en_us":"ALLERGY","ar_jo":"ALLERGY"}', '0', '2018-12-16 08:46:05.507', NULL, '0', NULL);

CREATE TABLE "public"."interpretation" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"min_concentration_value" numeric(15,5),
"max_concentration_value" numeric(15,5),
"min_concentration_comparator" varchar(2) COLLATE "default",
"max_concentration_comparator" varchar(2) COLLATE "default",
"interpretation_class" varchar(50) NOT NULL,
"explanation" varchar(4000) NOT NULL,
PRIMARY KEY ("rid")
)
WITH (OIDS=FALSE)
;
ALTER TABLE "public"."interpretation"
ADD COLUMN "test_id" int8 NOT NULL,
ADD CONSTRAINT "interpretation_test_id_fk" FOREIGN KEY ("test_id") REFERENCES "public"."test_definition" ("rid");


CREATE TABLE "public"."interpretation_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"min_concentration_value" numeric(15,5),
"max_concentration_value" numeric(15,5),
"min_concentration_comparator" varchar(2) COLLATE "default",
"max_concentration_comparator" varchar(2) COLLATE "default",
"interpretation_class" varchar(50) COLLATE "default",
"explanation" varchar(4000) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;
ALTER TABLE "public"."interpretation_aud"
ADD COLUMN "test_id" int8;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-16 13:41:18.983', NULL, NULL, 'explanation', '{"en_us":"Explanation","ar_jo":"الشرح"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-16 13:41:18.967', NULL, NULL, 'explanation', '{"en_us":"Explanation","ar_jo":"الشرح"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-16 13:40:35.917', NULL, NULL, 'class', '{"en_us":"Class","ar_jo":"الفئة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-16 13:40:35.912', NULL, NULL, 'class', '{"en_us":"Class","ar_jo":"الفئة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-16 13:39:19.139', NULL, NULL, 'maxConcentration', '{"en_us":"Max Concentration","ar_jo":"أقصى تركيز"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-16 13:39:19.135', NULL, NULL, 'maxConcentration', '{"en_us":"Max Concentration","ar_jo":"أقصى تركيز"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-16 13:38:44.663', NULL, NULL, 'minConcentration', '{"en_us":"Min Concentration","ar_jo":"أدنى تركيز"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-16 13:38:44.585', NULL, NULL, 'minConcentration', '{"en_us":"Min Concentration","ar_jo":"أدنى تركيز"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-16 15:49:28.515', NULL, NULL, 'concentration', '{"en_us":"Concentration","ar_jo":"التركيز"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-16 15:49:28.511', NULL, NULL, 'concentration', '{"en_us":"Concentration","ar_jo":"التركيز"}', '59', '0');


INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-17 07:45:24.734', NULL, NULL, 'minOrMaxConcentrationValueShouldBeFilled', '{"en_us":"\"Min Concentration Value\" or \"Max Concentration Value\" should be filled","ar_jo":"يجب ملء \"الحد الأدنى للتركيز\" أو \"الحد الأعلى للتركيز\""}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-17 07:45:24.727', NULL, NULL, 'minOrMaxConcentrationValueShouldBeFilled', '{"en_us":"\"Min Concentration Value\" or \"Max Concentration Value\" should be filled","ar_jo":"يجب ملء \"الحد الأدنى للتركيز\" أو \"الحد الأعلى للتركيز\""}', '61', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-17 14:05:54.711', NULL, NULL, 'unitDecimals', '{"en_us":"Unit Decimals","ar_jo":"الأرقام العشرية للوحدة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-17 14:05:54.593', NULL, NULL, 'unitDecimals', '{"en_us":"Unit Decimals","ar_jo":"الأرقام العشرية للوحدة"}', '59', '0');

ALTER TABLE "public"."test_definition"
ADD COLUMN "allergy_decimals" int2,
ADD COLUMN "allergy_unit_id" int8,
ADD CONSTRAINT "test_definition_allergy_unit_id_fk" FOREIGN KEY ("allergy_unit_id") REFERENCES "public"."lab_units" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "public"."test_definition_aud"
ADD COLUMN "allergy_decimals" int2,
ADD COLUMN "allergy_unit_id" int8;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-18 11:26:01.749', NULL, NULL, 'showHideInterpretationTable', '{"en_us":"Show/Hide Interpretation Table","ar_jo":"إظهار/إخفاء جدول التفسير"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-18 11:26:01.729', NULL, NULL, 'showHideInterpretationTable', '{"en_us":"Show/Hide Interpretation Table","ar_jo":"إظهار/إخفاء جدول التفسير"}', '59', '0');

--Dec/27/2018
ALTER TABLE "public"."test_destination"
ADD COLUMN "is_active" int2 DEFAULT 1 NOT NULL;

ALTER TABLE "public"."test_destination_aud"
ADD COLUMN "is_active" int2 DEFAULT 1;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-27 13:45:55.945', NULL, NULL, 'sourceAndDestinationAlreadyExist', '{"en_us":"Source and destination already exist!","ar_jo":"المصدر و الوجهة موجودتان سابقا!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-27 13:45:55.824', NULL, NULL, 'sourceAndDestinationAlreadyExist', '{"en_us":"Source and destination already exist!","ar_jo":"المصدر و الوجهة موجودتان سابقا!"}', '61', '0');

--Dec/30/2018
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2018-12-30 15:31:10.55', NULL, NULL, 'noValidDestination', '{"en_us":"No valid destination","ar_jo":"لا توجد وجهة صالحة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2018-12-30 15:31:10.471', NULL, NULL, 'noValidDestination', '{"en_us":"No valid destination","ar_jo":"لا توجد وجهة صالحة"}', '59', '0');

--Dec/31/2018
ALTER TABLE "public"."test_normal_ranges"
ADD COLUMN "destination_id" int8,
ADD CONSTRAINT "test_normal_ranges_destination_id_fk" FOREIGN KEY ("destination_id") REFERENCES "public"."test_destination" ("rid");

ALTER TABLE "public"."test_normal_ranges_aud"
ADD COLUMN "destination_id" int8;

--Jan/3/2018
ALTER TABLE "public"."lab_test_actual_result"
ADD COLUMN "normal_range_text" text;

ALTER TABLE "public"."lab_test_actual_result_aud"
ADD COLUMN "normal_range_text" text COLLATE "default";

--Jan/6/2018
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-01-06 09:35:31.471', NULL, NULL, 'namePrimary', '{"en_us":"Name Primary","ar_jo":"أساسي للإسم"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-01-06 09:35:31.351', NULL, NULL, 'namePrimary', '{"en_us":"Name Primary","ar_jo":"أساسي للإسم"}', '59', '0');

--Jan/8/2018
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-01-08 13:37:17.321', NULL, NULL, 'maxDate', '{"en_us":"Maximum allowed date exceeded","ar_jo":"تم تجاوز الحد الأقصى للتاريخ المسموح به"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-01-08 13:37:17.317', NULL, NULL, 'maxDate', '{"en_us":"Maximum allowed date exceeded","ar_jo":"تم تجاوز الحد الأقصى للتاريخ المسموح به"}', '61', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-01-08 13:39:36.467', NULL, NULL, 'minDate', '{"en_us":"Minimum allowed date not met","ar_jo":"لم يتم استيفاء الحد الأدنى للتاريخ المسموح به"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-01-08 13:39:36.463', NULL, NULL, 'minDate', '{"en_us":"Minimum allowed date not met","ar_jo":"لم يتم استيفاء الحد الأدنى للتاريخ المسموح به"}', '61', '0');

--Jan/9/2018
INSERT INTO "public"."sys_page" ("rid", "module_id", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('37', '6', '{"en_us":"Workbenches","ar_jo":"اماكن العمل"}', '{"en_us":"Workbenches","ar_jo":"اماكن العمل"}', '0', '2019-01-09 16:01:08', NULL, '0', NULL);


INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"View Workbench"}', '2018-12-03 15:44:30', '2018-12-03 15:43:56.493748', '0', NULL, '0', 'VIEW_WORKBENCH', '{"en_us":"View Workbench"}', '37');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Add Workbench"}', '2018-12-03 15:46:49', '2018-12-03 15:46:23.910906', '0', NULL, '0', 'ADD_WORKBENCH', '{"en_us":"Add Workbench"}', '37');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Update Workbench"}', '2018-12-03 16:12:30', '2018-12-03 16:11:56.115256', '0', NULL, '0', 'UPD_WORKBENCH', '{"en_us":"Update Workbench"}', '37');
INSERT INTO "public"."sec_rights" ("name", "creation_date", "update_date", "created_by", "updated_by", "version", "code", "description", "page_id") VALUES ('{"en_us":"Delete Workbench"}', '2018-12-03 16:14:01', '2018-12-03 16:13:38.082107', '0', NULL, '0', 'DEL_WORKBENCH', '{"en_us":"Delete Workbench"}', '37');

--Jan/21/2019
ALTER TABLE "public"."actual_organism"
ADD COLUMN "organism" varchar(255);

ALTER TABLE "public"."actual_organism_aud"
ADD COLUMN "organism" varchar(255);

update actual_organism ao set organism = (select name from organism where rid = ao.organism_id);

ALTER TABLE "public"."actual_organism"
DROP COLUMN "organism_id";

ALTER TABLE "public"."actual_organism_aud"
DROP COLUMN "organism_id";

ALTER TABLE "public"."actual_organism"
ALTER COLUMN "organism" SET NOT NULL;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-01-21 12:34:00.781', NULL, NULL, 'destinationMustHaveAPriceList', '{"en_us":"Destination {0} must have a pricelist associated with test {1}!","ar_jo":"يجب أن يكون لدى الوجهة {0} قائمة أسعار مرتبطة بالاختبار {1}!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-01-21 12:34:00.769', '2019-01-21 13:26:27.503', '0', 'destinationMustHaveAPriceList', '{"en_us":"Destination {0} must have a pricelist associated with test {1}!","ar_jo":"يجب أن يكون لدى الوجهة {0} قائمة أسعار مرتبطة بالاختبار {1}!"}', '61', '2');

--Jan/27/2019
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-01-27 08:20:15.031', NULL, NULL, 'selectFiles', '{"en_us":"Select files...","ar_jo":"اختر الملفات..."}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-01-27 08:20:14.926', NULL, NULL, 'selectFiles', '{"en_us":"Select files...","ar_jo":"اختر الملفات..."}', '59', '0');

--Jan/30/2019
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-01-30 11:53:15.765', NULL, NULL, 'download', '{"en_us":"Download","ar_jo":"تحميل"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-01-30 11:53:15.741', NULL, NULL, 'download', '{"en_us":"Download","ar_jo":"تحميل"}', '59', '0');


--Jan/31/2019
CREATE TABLE "public"."patient_artifact" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"patient_id" int8 NOT NULL,
"content" bytea NOT NULL,
"extension" varchar(50) COLLATE "default" NOT NULL,
"file_name" varchar(255) COLLATE "default" NOT NULL,
"size" int8 NOT NULL,
"content_type" varchar(255) COLLATE "default" NOT NULL,
PRIMARY KEY ("rid"),
CONSTRAINT "patient_artifact_patient_id_fk" FOREIGN KEY ("patient_id") REFERENCES "public"."emr_patient_info" ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."patient_artifact_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"patient_id" int8,
"content" bytea,
"extension" varchar(50) COLLATE "default",
"file_name" varchar(255) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2,
"size" int8,
"content_type" varchar(255) COLLATE "default",
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."order_artifact" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"order_id" int8 NOT NULL,
"content" bytea NOT NULL,
"extension" varchar(50) COLLATE "default" NOT NULL,
"file_name" varchar(255) COLLATE "default" NOT NULL,
"size" int8 NOT NULL,
"content_type" varchar(255) COLLATE "default" NOT NULL,
PRIMARY KEY ("rid"),
CONSTRAINT "order_artifact_order_id_fk" FOREIGN KEY ("order_id") REFERENCES "public"."emr_visits" ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."order_artifact_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"order_id" int8,
"content" bytea,
"extension" varchar(50) COLLATE "default",
"file_name" varchar(255) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2,
"size" int8,
"content_type" varchar(255) COLLATE "default",
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."actual_test_artifact" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"actual_test_id" int8 NOT NULL,
"content" bytea NOT NULL,
"extension" varchar(50) COLLATE "default" NOT NULL,
"file_name" varchar(255) COLLATE "default" NOT NULL,
"size" int8 NOT NULL,
"content_type" varchar(255) COLLATE "default" NOT NULL,
PRIMARY KEY ("rid"),
CONSTRAINT "actual_test_artifact_actual_test_id_fk" FOREIGN KEY ("actual_test_id") REFERENCES "public"."lab_test_actual" ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."actual_test_artifact_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"actual_test_id" int8,
"content" bytea,
"extension" varchar(50) COLLATE "default",
"file_name" varchar(255) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2,
"size" int8,
"content_type" varchar(255) COLLATE "default",
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-01-31 13:41:56.605', NULL, NULL, 'files', '{"en_us":"Files","ar_jo":"الملفات"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-01-31 13:41:56.592', NULL, NULL, 'files', '{"en_us":"Files","ar_jo":"الملفات"}', '59', '0');

--Feb/6/2019
ALTER TABLE "public"."lab_test_actual_result"
ADD COLUMN "is_amended" int2,
ADD COLUMN "amendment_reason" varchar(4000);

ALTER TABLE "public"."lab_test_actual_result_aud"
ADD COLUMN "is_amended" int2,
ADD COLUMN "amendment_reason" varchar(4000) COLLATE "default";

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-02-06 10:01:54.766', NULL, NULL, 'amendmentReason', '{"en_us":"Amendment Reason","ar_jo":"سبب التعديل"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-02-06 10:01:54.7', NULL, NULL, 'amendmentReason', '{"en_us":"Amendment Reason","ar_jo":"سبب التعديل"}', '59', '0');


--Feb/10/2019
ALTER TABLE "public"."actual_anti_microbial"
ALTER COLUMN "organism_sensitivity_id" SET NOT NULL;

CREATE TABLE "public"."amended_actual_result" (
"rid" serial8 NOT NULL,
"tenant_id" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"created_by" int8 NOT NULL,
"primary_result_value" varchar(64) COLLATE "default",
"normality" int8,
"is_confirmed" int2 DEFAULT 1,
"result_source_id" int8,
"narrative_text" varchar(4000) COLLATE "default",
"primary_result_parsed" numeric(15,5),
"secondary_result_parsed" numeric(15,5),
"comments" varchar(4000) COLLATE "default",
"test_coded_result_id" int8,
"organism_detection_id" int8,
"normal_range_text" text COLLATE "default",
"is_amended" int2,
"amendment_reason" varchar(4000) COLLATE "default",
"actual_result_id" int8 NOT NULL,
"version" int8 NOT NULL,
PRIMARY KEY ("rid"),
FOREIGN KEY ("organism_detection_id") REFERENCES "public"."lkp_organism_detection" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
FOREIGN KEY ("test_coded_result_id") REFERENCES "public"."test_coded_result" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
FOREIGN KEY ("actual_result_id") REFERENCES "public"."lab_test_actual_result" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."amended_actual_anti_microbial" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"created_by" int8 NOT NULL,
"tenant_id" int8 NOT NULL,
"anti_microbial_id" int8 NOT NULL,
"amended_actual_result_id" int8 NOT NULL,
"organism_sensitivity_id" int8 NOT NULL,
PRIMARY KEY ("rid"),
CONSTRAINT "amended_actual_anti_microbial_anti_microbial_id_fk" FOREIGN KEY ("anti_microbial_id") REFERENCES "public"."anti_microbial" ("rid"),
CONSTRAINT "amended_actual_anti_microbial_amended_actual_result_id_fk" FOREIGN KEY ("amended_actual_result_id") REFERENCES "public"."amended_actual_result" ("rid"),
CONSTRAINT "amended_actual_anti_microbial_organism_sensitivity_id_fk" FOREIGN KEY ("organism_sensitivity_id") REFERENCES "public"."lkp_organism_sensitivity" ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."amended_actual_organism" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"created_by" int8 NOT NULL,
"tenant_id" int8 NOT NULL,
"colony_count" varchar(255) COLLATE "default",
"amended_actual_result_id" int8 NOT NULL,
"organism" varchar(255) COLLATE "default" NOT NULL,
PRIMARY KEY ("rid"),
CONSTRAINT "amended_actual_organism_amended_actual_result_id_fk" FOREIGN KEY ("amended_actual_result_id") REFERENCES "public"."amended_actual_result" ("rid")
)
WITH (OIDS=FALSE)
;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-02-10 14:34:37.133', NULL, NULL, 'amendResults', '{"en_us":"Amend Results","ar_jo":"تعديل النتائج"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-02-10 14:34:37.127', NULL, NULL, 'amendResults', '{"en_us":"Amend Results","ar_jo":"تعديل النتائج"}', '59', '0');

--Feb/13/2019
ALTER TABLE "public"."anti_microbial"
ADD CONSTRAINT "anti_microbial_name_uk" UNIQUE ("tenant_id", "name");

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-02-13 08:47:00.097', NULL, NULL, 'antiMicrobialNameExists', '{"en_us":"Anti-Microbial with same NAME exists!","ar_jo":"يوجد مضاد للميكروبات بنفس الاسم!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-02-13 08:47:00.086', NULL, NULL, 'antiMicrobialNameExists', '{"en_us":"Anti-Microbial with same NAME exists!","ar_jo":"يوجد مضاد للميكروبات بنفس الاسم!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-02-13 08:46:09.482', NULL, NULL, 'antiMicrobialCodeExists', '{"en_us":"Anti-Microbial with same CODE exists!","ar_jo":"يوجد مضاد للميكروبات بنفس الكود!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-02-13 08:46:09.371', NULL, NULL, 'antiMicrobialCodeExists', '{"en_us":"Anti-Microbial with same CODE exists!","ar_jo":"يوجد مضاد للميكروبات بنفس الكود!"}', '61', '0');

ALTER TABLE "public"."interpretation"
ADD COLUMN "print_order" int2 DEFAULT 1 NOT NULL;
ALTER TABLE "public"."interpretation_aud"
ADD COLUMN "print_order" int2;

ALTER TABLE "public"."test_definition"
ADD COLUMN "is_allow_repetition" int2 DEFAULT 0 NOT NULL;

ALTER TABLE "public"."test_definition_aud"
ADD COLUMN "is_allow_repetition" int2;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-02-13 10:20:42.269', NULL, NULL, 'allowRepetition', '{"en_us":"Allow Repetition","ar_jo":"السماح بالتكرار"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-02-13 10:20:42.175', NULL, NULL, 'allowRepetition', '{"en_us":"Allow Repetition","ar_jo":"السماح بالتكرار"}', '59', '0');

--Mar/6/2019
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-06 14:14:24.359', NULL, NULL, 'searchByFingerprint', '{"en_us":"Search By Fingerprint","ar_jo":"البحث عن طريق البصمة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-06 14:14:24.347', NULL, NULL, 'searchByFingerprint', '{"en_us":"Search By Fingerprint","ar_jo":"البحث عن طريق البصمة"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-06 14:19:33.849', NULL, NULL, 'readFingerprint', '{"en_us":"Read Fingerprint","ar_jo":"قراءة البصمة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-06 14:19:33.844', NULL, NULL, 'readFingerprint', '{"en_us":"Read Fingerprint","ar_jo":"قراءة البصمة"}', '59', '0');

--Mar/12/2019
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-12 08:50:49.045', NULL, NULL, 'confirmed', '{"en_us":"Confirmed","ar_jo":"مؤكد"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-12 08:50:49.034', NULL, NULL, 'confirmed', '{"en_us":"Confirmed","ar_jo":"مؤكد"}', '59', '0');

--Mar/13/2019
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-13 10:43:48.474', NULL, NULL, 'welcome', '{"en_us":"Welcome","ar_jo":"أهلاً و سهلاً"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-13 10:43:48.462', NULL, NULL, 'welcome', '{"en_us":"Welcome","ar_jo":"أهلاً و سهلاً"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-13 10:46:42.116', NULL, NULL, 'lis', '{"en_us":"LABORATORY INFORMATION SYSTEM","ar_jo":"نظام المعلومات المختبرية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-13 10:46:42.103', NULL, NULL, 'lis', '{"en_us":"LABORATORY INFORMATION SYSTEM","ar_jo":"نظام المعلومات المختبرية"}', '59', '0');

--Mar/18/2019
ALTER TABLE "public"."sec_tenant"
ADD COLUMN "is_document_auto_download" int2 DEFAULT 0 NOT NULL;

ALTER TABLE "public"."sec_tenant_aud"
ADD COLUMN "is_document_auto_download" int2;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-18 15:59:33.253', NULL, NULL, 'openInNewTabNoFileName', '{"en_us":"Open in new tab (file-name cannot be specified)","ar_jo":"فتح في علامة تبويب جديدة (لا يمكن تحديد اسم الملف)"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-18 15:59:33.234', NULL, NULL, 'openInNewTabNoFileName', '{"en_us":"Open in new tab (file-name cannot be specified)","ar_jo":"فتح في علامة تبويب جديدة (لا يمكن تحديد اسم الملف)"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-18 15:58:29.003', NULL, NULL, 'documentDownloadAction', '{"en_us":"Document Download Action","ar_jo":"إجراء تحميل الوثيقة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-18 15:58:28.994', NULL, NULL, 'documentDownloadAction', '{"en_us":"Document Download Action","ar_jo":"إجراء تحميل الوثيقة"}', '59', '0');

--Mar/21/2019
CREATE TABLE "public"."patient_fingerprint" (
"rid" serial8 NOT NULL,
"version" int8 NOT NULL,
"creation_date" timestamp(6) NOT NULL,
"update_date" timestamp(6),
"created_by" int8 NOT NULL,
"updated_by" int8,
"tenant_id" int8 NOT NULL,
"patient_id" int8 NOT NULL,
"image" bytea NOT NULL,
"template" varchar(4000) COLLATE "default" NOT NULL,
PRIMARY KEY ("rid"),
CONSTRAINT "patient_fingerprint_patient_id_fk" FOREIGN KEY ("patient_id") REFERENCES "public"."emr_patient_info" ("rid")
)
WITH (OIDS=FALSE)
;

CREATE TABLE "public"."patient_fingerprint_aud" (
"rid" int8 NOT NULL,
"creation_date" timestamp(6),
"update_date" timestamp(6),
"created_by" int8,
"updated_by" int8,
"tenant_id" int8,
"patient_id" int8,
"image" bytea,
"template" varchar(4000) COLLATE "default",
"rev" int4 NOT NULL,
"revtype" int2,
PRIMARY KEY ("rid", "rev")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."emr_patient_info_aud"
DROP COLUMN "fingerprint";

ALTER TABLE "public"."emr_patient_info"
DROP COLUMN "fingerprint";

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-25 07:41:50.827', NULL, NULL, 'failedToConnectToFingerprintReader', '{"en_us":"Failed to connect to fingerprint reader!","ar_jo":"فشل الاتصال بقارئ بصمات الأصابع!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-25 07:41:50.723', NULL, NULL, 'failedToConnectToFingerprintReader', '{"en_us":"Failed to connect to fingerprint reader!","ar_jo":"فشل الاتصال بقارئ بصمات الأصابع!"}', '61', '0');

--Mar/25/2019
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-25 12:50:29.194', NULL, NULL, 'idCardReader', '{"en_us":"ID Card Reader","ar_jo":"قارئ بطاقة الهوية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-25 12:50:29.19', NULL, NULL, 'idCardReader', '{"en_us":"ID Card Reader","ar_jo":"قارئ بطاقة الهوية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-25 12:49:54.805', NULL, NULL, 'idNo', '{"en_us":"ID Number","ar_jo":"رقم الهوية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-25 12:49:54.8', NULL, NULL, 'idNo', '{"en_us":"ID Number","ar_jo":"رقم الهوية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-25 12:49:31.492', NULL, NULL, 'importUsingIdCard', '{"en_us":"Import Using ID Card","ar_jo":"استيراد باستخدام بطاقة الهوية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-25 12:49:31.406', NULL, NULL, 'importUsingIdCard', '{"en_us":"Import Using ID Card","ar_jo":"استيراد باستخدام بطاقة الهوية"}', '59', '0');

--Mar/31/2019
ALTER TABLE "public"."test_normal_ranges"
ADD COLUMN "is_active" int2 DEFAULT 1 NOT NULL,
ADD COLUMN "state_change_date" timestamp SET DEFAULT NOW();
update test_normal_ranges set state_change_date = NOW() where state_change_date = null;
ALTER TABLE "public"."test_normal_ranges"
ALTER COLUMN "state_change_date" SET NOT NULL;


ALTER TABLE "public"."test_normal_ranges_aud"
ADD COLUMN "is_active" int2,
ADD COLUMN "state_change_date" timestamp(6);

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-31 14:04:44.694', NULL, NULL, 'activationDate', '{"en_us":"Activation Date","ar_jo":"تاريخ التفعيل"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-31 14:04:44.436', NULL, NULL, 'activationDate', '{"en_us":"Activation Date","ar_jo":"تاريخ التفعيل"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-03-31 14:06:06.825', NULL, NULL, 'deactivationDate', '{"en_us":"Deactivation Date","ar_jo":"تاريخ التعطيل"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-03-31 14:06:06.808', NULL, NULL, 'deactivationDate', '{"en_us":"Deactivation Date","ar_jo":"تاريخ التعطيل"}', '59', '0');

--Apr/01/2019
ALTER TABLE "public"."test_result"
ADD COLUMN "is_comprehensive" int2 DEFAULT 0 NOT NULL,
ADD COLUMN "is_differential" int2 DEFAULT 0 NOT NULL,
ADD COLUMN "comprehensive_result_id" int8,
ADD CONSTRAINT "test_result_comprehensive_result_id_fk" FOREIGN KEY ("comprehensive_result_id") REFERENCES "public"."test_result" ("rid");

ALTER TABLE "public"."test_result"
DROP CONSTRAINT "test_result_primary_unit_id_fkey",
ADD CONSTRAINT "test_result_primary_unit_id_fk" FOREIGN KEY ("primary_unit_id") REFERENCES "public"."lab_units" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "public"."test_result"
DROP CONSTRAINT "test_result_secondary_unit_id_fkey",
DROP CONSTRAINT "test_result_primary_unit_type_id_fkey",
ADD CONSTRAINT "test_result_secondary_unit_id_fk" FOREIGN KEY ("secondary_unit_id") REFERENCES "public"."lab_units" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT "test_result_primary_unit_type_id_fk" FOREIGN KEY ("primary_unit_type_id") REFERENCES "public"."lkp_unit_type" ("rid") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "public"."test_result_aud"
ADD COLUMN "is_comprehensive" int2,
ADD COLUMN "is_differential" int2,
ADD COLUMN "comprehensive_result_id" int8;

--Apr/02/2019
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-02 12:33:11.58', NULL, NULL, 'differential', '{"en_us":"Differential","ar_jo":"تفاضلي"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-02 12:33:11.574', NULL, NULL, 'differential', '{"en_us":"Differential","ar_jo":"تفاضلي"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-02 12:32:42.026', NULL, NULL, 'comprehensive', '{"en_us":"Comprehensive","ar_jo":"شامل"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-02 12:32:42.009', NULL, NULL, 'comprehensive', '{"en_us":"Comprehensive","ar_jo":"شامل"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-02 12:31:37.672', '2019-04-04 08:05:17.316', '0', 'differentialValidation', '{"en_us":"All differential result percentages must be filled and their sum equal 100!","ar_jo":"يجب ملء جميع النسب المئوية للنتائج التفاضلية ويساوي مجموعها 100!"}', '61', '1');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-02 12:31:37.672', '2019-04-04 08:05:17.316', '0', 'differentialValidation', '{"en_us":"All differential result percentages must be filled and their sum equal 100!","ar_jo":"يجب ملء جميع النسب المئوية للنتائج التفاضلية ويساوي مجموعها 100!"}', '61', '1');


ALTER TABLE "public"."lab_test_actual_result"
ADD COLUMN "percentage" int2;

ALTER TABLE "public"."lab_test_actual_result_aud"
ADD COLUMN "percentage" int2;