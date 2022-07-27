


-- TODO Document that all agreements need to be dropped

ALTER TABLE edc_contract_agreement RENAME COLUMN agreement_id TO agr_id;
ALTER TABLE edc_contract_agreement ADD COLUMN policy JSON;
ALTER TABLE edc_contract_agreement DROP COLUMN policy_id;
