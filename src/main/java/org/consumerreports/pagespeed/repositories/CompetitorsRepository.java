package org.consumerreports.pagespeed.repositories;

import org.bson.types.ObjectId;
import org.consumerreports.pagespeed.models.CompetitorUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;

public interface CompetitorsRepository extends MongoRepository<CompetitorUrl, String> {
    CompetitorUrl findBy_id(ObjectId _id);

    CompetitorUrl findFirstByUrl(
            String url
    );


    @Query(value="{}", fields="{url: 1}")
    List<CompetitorUrl> findAllUrls();
}