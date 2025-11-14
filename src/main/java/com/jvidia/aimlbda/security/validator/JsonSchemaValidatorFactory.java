/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.validator;

import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JsonSchemaValidatorFactory {

	private static final String JSON_SCHEMAS_DIR = "/jsonschemas/";

	private Schema schema;

	public boolean validateJsonPaylaod(String schema, String payload) {
		try {
			InputStream schemaStream = getClass().getResourceAsStream(JSON_SCHEMAS_DIR + schema);
			JSONObject rawSchemaJSONObject = new JSONObject(new JSONTokener(schemaStream));
			this.schema = SchemaLoader.load(rawSchemaJSONObject);

			this.schema.validate(new JSONObject(payload));
			return true;
		}
		catch (JSONException ex) {
			log.error("Error validateJsonPaylaod schema {} payload {}", schema, payload, ex);
		}

		return false;
	}

}
