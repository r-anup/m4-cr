package org.consumerreports.pagespeed.repositories;

import org.bson.types.ObjectId;
import org.consumerreports.pagespeed.models.Metrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Date;
import java.util.List;


public interface MetricsRepository extends MongoRepository<Metrics, String> {

    Metrics findBy_id(ObjectId _id);

    @Query("{url: ?0 }")
    List<Metrics> findByURL(
            String url
    );

    //{url: 'consumerreports.org' , fetchTime: {$gte: '2019-03-04T22:05:14.868Z', $lte: '2019-03-04T22:05:14.868Z' }}
    Metrics findFirstByUrlOrderByFetchTimeDesc(
            String url
    );


    Metrics findFirstByUrlContainingAndDeviceTypeEqualsAndFetchTimeBetweenOrderByFetchTimeDesc(
            String url,
            String deviceType,
            Date fetchTime,
            Date fetchTimeTo
    );

}