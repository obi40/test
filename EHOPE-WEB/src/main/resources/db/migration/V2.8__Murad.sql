ALTER TABLE "public"."test_definition"
ADD COLUMN "is_repetition_separate_sample" int2 DEFAULT 0 NOT NULL,
ADD COLUMN "is_repetition_chargeable" int2 DEFAULT 0 NOT NULL;

ALTER TABLE "public"."test_definition_aud"
ADD COLUMN "is_repetition_separate_sample" int2,
ADD COLUMN "is_repetition_chargeable" int2;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-18 08:08:58.272', NULL, NULL, 'repetitionSeparateSample', '{"en_us":"Use Separate Sample When Repeated","ar_jo":"استخدم عينة منفصلة عند التكرار"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-18 08:08:58.136', NULL, NULL, 'repetitionSeparateSample', '{"en_us":"Use Separate Sample When Repeated","ar_jo":"استخدم عينة منفصلة عند التكرار"}', '59', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-18 08:13:58.788', NULL, NULL, 'repetitionChargeable', '{"en_us":"Charge Each Repeated Test","ar_jo":"قبض مقابل كل اختبار متكرر"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-18 08:13:58.782', NULL, NULL, 'repetitionChargeable', '{"en_us":"Charge Each Repeated Test","ar_jo":"قبض مقابل كل اختبار متكرر"}', '59', '0');
