ALTER TABLE edc_policies ALTER COLUMN permissions TYPE JSON USING permissions::json;
ALTER TABLE edc_policies ALTER COLUMN prohibitions TYPE JSON USING prohibitions::json;
ALTER TABLE edc_policies ALTER COLUMN duties TYPE JSON USING duties::json;
ALTER TABLE edc_policies ALTER COLUMN extensible_properties TYPE JSON USING extensible_properties::json;
