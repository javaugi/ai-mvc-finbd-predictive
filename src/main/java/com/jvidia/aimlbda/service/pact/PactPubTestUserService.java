/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service.pact;

// Manual Pact Publisher (if needed)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// import au.com.dius.pact.core.pactbroker.PactBrokerClient;
// import au.com.dius.pact.core.pactbroker.Publish;
@Component
public class PactPubTestUserService {

	@Value("${pact.broker.url:http://localhost:9292}")
	private String brokerUrl;

    @Value("${pact.broker.auth.username:admin}")
	private String username;

    @Value("${pact.broker.auth.password:password}")
	private String password;

	/*
	 * public static void main(String[] args) { String brokerBaseUrl =
	 * "http://localhost:9292"; String pactDir = "target/pacts"; String version = "1.0.0";
	 * 
	 * PactBrokerClient brokerClient = new PactBrokerClient(brokerBaseUrl);
	 * 
	 * // For publishing verification results Publish publish = new Publish(brokerBaseUrl,
	 * version, pactDir); publish.setBrokerUsername("admin");
	 * publish.setBrokerPassword("password");
	 * 
	 * try { publish.uploadPacts(); System.out.println("Pacts published successfully"); }
	 * catch (Exception e) { System.err.println("Failed to publish pacts: " +
	 * e.getMessage()); } }
	 * 
	 * 
	 * public void publishPacts(String pactDirectory) { try { PactBroker broker = new
	 * PactBroker(brokerUrl); broker.setAuthentication(new Authentication(username,
	 * password));
	 * 
	 * File pactDir = new File(pactDirectory); File[] pactFiles = pactDir.listFiles((dir,
	 * name) -> name.endsWith(".json"));
	 * 
	 * if (pactFiles != null) { for (File pactFile : pactFiles) {
	 * broker.publishPact(pactFile, "1.0.0"); // Your version } } } catch (Exception e) {
	 * throw new RuntimeException("Failed to publish pacts", e); } } //
	 */

}
