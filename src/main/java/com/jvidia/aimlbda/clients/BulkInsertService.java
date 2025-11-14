/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.clients;

import com.jvidia.aimlbda.entity.TestUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BulkInsertService {

    public void bulkInsertEntities(EntityManager entityManager, List<TestUser> entities, int batchSize) {
		EntityTransaction transaction = entityManager.getTransaction();
		try (entityManager) {
			transaction.begin();
			for (int i = 0; i < entities.size(); i++) {
				entityManager.persist(entities.get(i));

				if ((i + 1) % batchSize == 0) {
					// Flush the batch and clear the persistence context
					entityManager.flush();
					entityManager.clear();
					// Begin a new transaction for the next batch
					transaction.commit();
					transaction.begin();
				}
			}
			// Commit any remaining entities in the last batch
			transaction.commit();
		}
		catch (RuntimeException e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		}
	}

}
