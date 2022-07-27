
ALTER TABLE edc_contract_negotiation RENAME COLUMN contract_agreement_id TO agreement_id;

ALTER TABLE edc_contract_negotiation ALTER COLUMN contract_offers TYPE JSON USING contract_offers::json;
ALTER TABLE edc_contract_negotiation ALTER COLUMN trace_context TYPE JSON USING trace_context::json;