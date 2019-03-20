package org.consumerreports.pagespeed.repositories;

import org.bson.types.ObjectId;
import org.consumerreports.pagespeed.models.Emails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailsRepository extends MongoRepository<Emails, String> {
    Emails findBy_id(ObjectId _id);

    Emails findFirstByEmail(
            String email
    );
}