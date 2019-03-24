package org.consumerreports.pagespeed.repositories;

import org.bson.types.ObjectId;
import org.consumerreports.pagespeed.models.CompetitorUrls;
import org.consumerreports.pagespeed.models.Urls;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CompetitorsRepository extends MongoRepository<CompetitorUrls, String> {
    CompetitorUrls findBy_id(ObjectId _id);

    CompetitorUrls findFirstByUrl(
            String url
    );


    @Query(value="{}", fields="{url: 1}")
    List<CompetitorUrls> findAllUrls();
}