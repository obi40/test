delete FROM "public"."com_tenant_messages" where code = 'standardCodeExists';
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-07 07:57:32.124', NULL, NULL, 'testStandardCodeExists', '{"en_us":"Test Standard Code [{0}] Exists!","ar_jo":"الرمز الاساسي للفحص [{0}] موجود!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-07 07:57:31.971', NULL, NULL, 'testStandardCodeExists', '{"en_us":"Test Standard Code [{0}] Exists!","ar_jo":"الرمز الاساسي للفحص [{0}] موجود!"}', '61', '0');

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-07 09:16:44.211', NULL, NULL, 'duplicateResultStandardCode', '{"en_us":"Duplicate Result Standard Code [{0}]!","ar_jo":"الرمز الاساسي للنتيجة مكرر [{0}]!"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-07 09:16:44.063', NULL, NULL, 'duplicateResultStandardCode', '{"en_us":"Duplicate Result Standard Code [{0}]!","ar_jo":"الرمز الاساسي للنتيجة مكرر [{0}]!"}', '61', '0');

ALTER TABLE "public"."historical_result"
ADD COLUMN "normal_range_prefix" varchar(4000),
ADD COLUMN "conv_normal_range" varchar(4000),
ADD COLUMN "si_normal_range" varchar(4000),
ADD COLUMN "conv_unit" varchar(255),
ADD COLUMN "si_unit" varchar(255);

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-08 06:43:28.898', NULL, NULL, 'siNormalRange', '{"en_us":"SI Normal Range","ar_jo":"القيمة الطبيعية حسب النظام الدولي"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-08 06:43:28.89', NULL, NULL, 'siNormalRange', '{"en_us":"SI Normal Range","ar_jo":"القيمة الطبيعية حسب النظام الدولي"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-08 06:33:35.903', NULL, NULL, 'convNormalRange', '{"en_us":"Conventional Normal Range","ar_jo":"القيمة الطبيعية التقليدية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-08 06:33:35.896', NULL, NULL, 'convNormalRange', '{"en_us":"Conventional Normal Range","ar_jo":"القيمة الطبيعية التقليدية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-08 06:32:45.808', NULL, NULL, 'normalRangePrefix', '{"en_us":"Normal Range Prefix","ar_jo":"بادئة القيمة الطبيعية"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-08 06:32:45.653', NULL, NULL, 'normalRangePrefix', '{"en_us":"Normal Range Prefix","ar_jo":"بادئة القيمة الطبيعية"}', '59', '0');

ALTER TABLE "public"."lab_test_actual_result"
ADD COLUMN "branch_id" int8;

ALTER TABLE "public"."lab_test_actual_result_aud"
ADD COLUMN "branch_id" int8;

UPDATE lab_test_actual_result
SET branch_id = ta.branch_id
FROM
	(
		SELECT
			rid,
			branch_id
		FROM
			lab_test_actual
	) AS ta
WHERE
	lab_test_actual_result.actual_test_id = ta.rid;
	
ALTER TABLE "public"."lab_test_actual_result"
ALTER COLUMN "branch_id" SET NOT NULL;

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-08 14:55:46.894', NULL, NULL, 'totalPercentagesOfDifferentialResultsMustBe100', '{"en_us":"Total Percentages Of Differential Results Must Be 100","ar_jo":"يجب أن تكون النسب المئوية الإجمالية للنتائج التفاضلية 100"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-08 14:55:46.891', NULL, NULL, 'totalPercentagesOfDifferentialResultsMustBe100', '{"en_us":"Total Percentages Of Differential Results Must Be 100","ar_jo":"يجب أن تكون النسب المئوية الإجمالية للنتائج التفاضلية 100"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-08 14:55:08.622', NULL, NULL, 'comprehensiveResultMustBeFilled', '{"en_us":"Comprehensive Result Must Be Filled","ar_jo":"يجب ملء النتيجة الشاملة"}', '61', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-08 14:55:08.513', NULL, NULL, 'comprehensiveResultMustBeFilled', '{"en_us":"Comprehensive Result Must Be Filled","ar_jo":"يجب ملء النتيجة الشاملة"}', '61', '0');

INSERT INTO "public"."lkp_result_value_type" ("code", "name", "description", "version", "creation_date", "update_date", "created_by", "updated_by") VALUES ('RATIO', '{"en_us":"Ratio (Quantitative)","ar_jo":"نسبة (كمي)"}', '{"en_us":"Ratio (Quantitative)","ar_jo":"نسبة (كمي)"}', '0', '2019-04-09 09:49:51.985', NULL, '0', NULL);

ALTER TABLE "public"."test_normal_ranges"
ADD COLUMN "ratio" varchar(255);
ALTER TABLE "public"."test_normal_ranges_aud"
ADD COLUMN "ratio" varchar(255) COLLATE "default";

ALTER TABLE "public"."lab_test_actual_result"
ADD COLUMN "ratio" varchar(255);
ALTER TABLE "public"."lab_test_actual_result_aud"
ADD COLUMN "ratio" varchar(255) COLLATE "default";

INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('1', '0', '2019-04-09 11:44:56.991', NULL, NULL, 'ratio', '{"en_us":"Ratio","ar_jo":"النسبة"}', '59', '0');
INSERT INTO "public"."com_tenant_messages" ("tenant_id", "created_by", "creation_date", "update_date", "updated_by", "code", "description", "type_id", "version") VALUES ('0', '0', '2019-04-09 11:44:56.977', NULL, NULL, 'ratio', '{"en_us":"Ratio","ar_jo":"النسبة"}', '59', '0');
