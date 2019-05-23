update emr_patient_info set medical_information = 'medical information...' where medical_information is null;
alter table emr_patient_info alter column medical_information set not null;

update ins_provider set is_net_amount = 0 where tenant_id = 1;