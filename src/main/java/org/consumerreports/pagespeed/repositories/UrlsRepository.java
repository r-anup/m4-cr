package org.consumerreports.pagespeed.repositories;

import org.consumerreports.pagespeed.models.Urls;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UrlsRepository extends MongoRepository<Urls, String> {
    Urls findBy_id(ObjectId _id);

    Urls findFirstByUrl(
            String url
    );


    @Query(value="{}", fields="{url: 1}")
    List<Urls> findAllUrls();
}