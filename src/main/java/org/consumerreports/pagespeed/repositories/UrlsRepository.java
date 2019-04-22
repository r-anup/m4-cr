package org.consumerreports.pagespeed.repositories;

import org.consumerreports.pagespeed.models.CroUrl;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UrlsRepository extends MongoRepository<CroUrl, String> {
    CroUrl findBy_id(ObjectId _id);

    CroUrl findFirstByUrl(
            String url
    );


    @Query(value="{}", fields="{url: 1}", sort = "{sortOrder: 1}")
    List<CroUrl> findAllUrls();


    List<CroUrl> findAllByCompetitorUrlIsNotNullOrderBySortOrderAsc();


    CroUrl findFirstByCompetitorUrlValue(
            String competitorUrl
    );
}