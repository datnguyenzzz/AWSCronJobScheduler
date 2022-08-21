package com.github.datnguyenzzz.Factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.AWSPublisher;

/** 
 * @implSpec Currently have bean:
 * - awsSQSPublisher
 * - awsKinesisPubliser
 * - awsS3Publisher
 * 
 * implement interface AWSPublisher
*/
@Component("awsPublisherFactory")
@Scope("prototype")
public class AWSPublisherFactory{

    private final String PUBLISHER = "Publisher";
    private final String AWS = "aws";
    
    @Autowired
    private ApplicationContext ctx;

    /**
     * 
     * @param usedService
     * @return Bean correctsponding to used AWS service
     */
    public AWSPublisher getObject(String usedService) {
        AWSPublisher targetBean = ctx.getBean(getTargetBeanName(usedService), AWSPublisher.class);
        return targetBean;
    }

    private String getTargetBeanName(String usedService) {
        StringBuilder sb = new StringBuilder();
        sb.append(AWS);
        sb.append(usedService);
        sb.append(PUBLISHER);

        return sb.toString();
    }

}
