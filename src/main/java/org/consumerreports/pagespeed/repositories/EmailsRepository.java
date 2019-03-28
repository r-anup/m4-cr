package org.consumerreports.pagespeed.repositories;

import org.bson.types.ObjectId;
import org.consumerreports.pagespeed.models.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailsRepository extends MongoRepository<Email, String> {
    Email findBy_id(ObjectId _id);

    Email findFirstByEmail(
            String email
    );
}